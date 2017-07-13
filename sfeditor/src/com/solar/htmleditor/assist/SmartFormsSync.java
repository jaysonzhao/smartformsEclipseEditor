package com.solar.htmleditor.assist;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;

import com.solar.htmleditor.HTMLPlugin;

public class SmartFormsSync {

	public static final String GETAPPORFORMS = "_get_apps";
	public static final String GETFORMSDATA = "_get_formsdata";
	public static final String UPDATEFORMSDATA = "_update_formsdata";

	public SmartformInfo getAPPs() {
		SmartformInfo apps = new SmartformInfo();
		String sql = "select * from  DAT_APPLICATION";
		executeQuery(apps, sql, "app_Name", "app_Id", this.GETAPPORFORMS);
		return apps;
	}

	public SmartformInfo getFormbyAPPId(String appid) {
		SmartformInfo forms = new SmartformInfo();
		String sql = "select * from DET_FORM_DEFINE where APP_ID='" + appid + "'";
		executeQuery(forms, sql, "form_name", "form_id", this.GETAPPORFORMS);
		return forms;
	}

	public SmartformInfo getFormDatabyFormId(String formid) {
		SmartformInfo forms = new SmartformInfo();
		String sql = "select * from DET_FORM_DEFINE where FORM_ID='" + formid + "'";
		executeQuery(forms, sql, "form_name", "form_id", this.GETFORMSDATA);
		return forms;
	}

	public void updateFormDatabyFormId(String formid, String formdata) {
		SmartformInfo forms = new SmartformInfo();
		forms.setFormdata(formdata);
		String sql = "update DET_FORM_DEFINE set form_Content=? , update_time=? where FORM_ID='" + formid+"'";
		executeQuery(forms, sql, "form_name", "form_id", this.UPDATEFORMSDATA);

	}

	private void executeQuery(SmartformInfo apps, String sql, String namefield, String idfield, String actionType) {
		MessageConsole console = HTMLPlugin.getDefault().getConsole();
		MessageConsoleStream consoleStream = console.newMessageStream();
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		// consoleStream.println("connecting:"+store.getString(HTMLPlugin.FORMS_SERVER_DB));
		// store.getString(HTMLPlugin.FORMS_SERVER_DBUSER));
		// consoleStream.println(store.getString(HTMLPlugin.FORMS_SERVER_DBPASS));

		Connection con = null;// 创建一个数据库连接
		PreparedStatement pre = null;// 创建预编译语句对象，一般都是用这个而不用Statement
		ResultSet result = null; // 创建一个结果集对象
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");// 加载Oracle驱动程序
			// consoleStream.println("开始尝试连接数据库！");
			String url = store.getString(HTMLPlugin.FORMS_SERVER_DB);// "jdbc:oracle:thin:"
																		// +
																		// "@mail.gzsolartech.com:1521:smartformsdb";
			String user = store.getString(HTMLPlugin.FORMS_SERVER_DBUSER);// "csmart";//
																			// 用户名,系统默认的账户名
			String password = store.getString(HTMLPlugin.FORMS_SERVER_DBPASS);// "csmart";//
																				// 你安装时选设置的密码
			con = DriverManager.getConnection(url, user, password);// 获取连接
			con.setAutoCommit(false);
			// consoleStream.println("连接成功！");
			pre = con.prepareStatement(sql);// 实例化预编译语句
			if (this.UPDATEFORMSDATA.equals(actionType)) { // 处理更新表单体
				/*
				 * Clob clob = result.getClob("form_content"); Writer outStream
				 * = clob.setCharacterStream(0); char[] c =
				 * apps.getFormdata().toCharArray(); outStream.write(c, 0,
				 * c.length); outStream.flush(); outStream.close();
				 */ // 这种写法会造成部分更新

				StringReader clob = new StringReader(apps.getFormdata());
				pre.setCharacterStream(1, clob, apps.getFormdata().length());
				Calendar cal = Calendar.getInstance();
				java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime());
				pre.setDate(2, sqlDate);
				pre.executeUpdate();
				con.commit();

			} else {//处理查询类的操作
				result = pre.executeQuery();// 执行查询，注意括号中不需要再加参数
				while (result.next()) {
					if (this.GETAPPORFORMS.equals(actionType)) {
						apps.getNames().add(result.getString(namefield));
						apps.getIds().add(result.getString(idfield));
					}

					if (this.GETFORMSDATA.equals(actionType)) { // 处理获取表单体
						Clob clob = result.getClob("form_content");
						Reader inStream = clob.getCharacterStream();
						char[] c = new char[(int) clob.length()];
						inStream.read(c);
						apps.setFormdata(new String(c));
						inStream.close();
					}

				}
			}
		} catch (Exception e) {
			consoleStream.println(e.getMessage());
			consoleStream.println("数据库连接错误请先配置smarforms 参数页");

		} finally {
			try {
				// 逐一将上面的几个对象关闭，因为不关闭的话会影响性能、并且占用资源
				// 注意关闭的顺序，最后使用的最先关闭
				if (result != null)
					result.close();
				if (pre != null)
					pre.close();
				if (con != null)
					con.close();
				// consoleStream.println("数据库连接已关闭！");
			} catch (Exception e) {
				consoleStream.println(e.getMessage());

			}

		}
	}
	class FormAuthenticateFilter implements ClientRequestFilter {
		private ArrayList<Object> cookies = new ArrayList<>();

		public FormAuthenticateFilter(Client client, List<String> params) {

		    WebTarget baseTarget = client.target(params.get(0)+"/console/user/login.action");

		    Form form = new Form();
			form.param("username", params.get(1));
			form.param("password", params.get(2));
		
			
			Response rslogin =
					baseTarget.request(MediaType.TEXT_HTML)
			    .post(Entity.form(form));

		    Map<String, NewCookie> cr = rslogin.getCookies();

		    for (NewCookie cookie : cr.values()) {
		       cookies.add(cookie.toCookie());
		        
		    }
		}

		public void filter(ClientRequestContext requestContext) throws IOException {
		    if (cookies != null) {
		        requestContext.getHeaders().put("Cookie", cookies);
		    }
		}
		
	}
	public boolean exeuteRefreshXSP(String servercontext, String devuserrname, String devpassword){
		ClientConfig config = new ClientConfig();
		config.property(ApacheClientProperties.DISABLE_COOKIES, false);
		config.connectorProvider(new ApacheConnectorProvider());
		Client client = ClientBuilder.newClient(config);
	    List<String> params = new ArrayList<String>();
	    params.add(servercontext);
	    params.add(devuserrname);
	    params.add(devpassword);
		WebTarget target = client.target(servercontext+"/console/form/refresh/initAllForm.action").register(new FormAuthenticateFilter(client, params));;
		
		Response rs = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		
		String entity =  rs.readEntity(String.class);
		return entity.contains("true");
		
	}
	public static void main(String[] args) {
	     SmartFormsSync test = new SmartFormsSync();
	     System.out.println(test.exeuteRefreshXSP("http://oa.gzsolartech.com:7788/smartforms","lds","password"));
	    		 
	}

}
