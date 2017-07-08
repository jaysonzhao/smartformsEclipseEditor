package com.solar.htmleditor.assist;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.solar.htmleditor.HTMLPlugin;

public class SOTFormsSync {

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

		Connection con = null;// ����һ�����ݿ�����
		PreparedStatement pre = null;// ����Ԥ����������һ�㶼�������������Statement
		ResultSet result = null; // ����һ�����������
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");// ����Oracle��������
			// consoleStream.println("��ʼ�����������ݿ⣡");
			String url = store.getString(HTMLPlugin.FORMS_SERVER_DB);// "jdbc:oracle:thin:"
																		// +
																		// "@mail.gzsolartech.com:1521:smartformsdb";
			String user = store.getString(HTMLPlugin.FORMS_SERVER_DBUSER);// "csmart";//
																			// �û���,ϵͳĬ�ϵ��˻���
			String password = store.getString(HTMLPlugin.FORMS_SERVER_DBPASS);// "csmart";//
																				// �㰲װʱѡ���õ�����
			con = DriverManager.getConnection(url, user, password);// ��ȡ����
			con.setAutoCommit(false);
			// consoleStream.println("���ӳɹ���");
			pre = con.prepareStatement(sql);// ʵ����Ԥ�������
			if (this.UPDATEFORMSDATA.equals(actionType)) { // ������±���
				/*
				 * Clob clob = result.getClob("form_content"); Writer outStream
				 * = clob.setCharacterStream(0); char[] c =
				 * apps.getFormdata().toCharArray(); outStream.write(c, 0,
				 * c.length); outStream.flush(); outStream.close();
				 */ // ����д������ɲ��ָ���

				StringReader clob = new StringReader(apps.getFormdata());
				pre.setCharacterStream(1, clob, apps.getFormdata().length());
				Calendar cal = Calendar.getInstance();
				java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime());
				pre.setDate(2, sqlDate);
				pre.executeUpdate();
				con.commit();

			} else {//�����ѯ��Ĳ���
				result = pre.executeQuery();// ִ�в�ѯ��ע�������в���Ҫ�ټӲ���
				while (result.next()) {
					if (this.GETAPPORFORMS.equals(actionType)) {
						apps.getNames().add(result.getString(namefield));
						apps.getIds().add(result.getString(idfield));
					}

					if (this.GETFORMSDATA.equals(actionType)) { // �����ȡ����
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
			consoleStream.println("���ݿ����Ӵ�����������smarforms ����ҳ");

		} finally {
			try {
				// ��һ������ļ�������رգ���Ϊ���رյĻ���Ӱ�����ܡ�����ռ����Դ
				// ע��رյ�˳�����ʹ�õ����ȹر�
				if (result != null)
					result.close();
				if (pre != null)
					pre.close();
				if (con != null)
					con.close();
				// consoleStream.println("���ݿ������ѹرգ�");
			} catch (Exception e) {
				consoleStream.println(e.getMessage());

			}

		}
	}

}
