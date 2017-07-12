package com.solar.htmleditor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.solar.htmleditor.assist.SmartFormsSync;
import com.solar.htmleditor.assist.SmartformInfo;
import com.solar.htmleditor.editors.HTMLSourceEditor;
import com.solar.htmleditor.views.IPaletteTarget;

public class FormsPullSelectionDialog extends Window {

	private Text xpath;
	private Combo appscombo;
	private Combo formscombo;
	private Button next;
	private Button prev;
	private Label status;
	private SmartformInfo apps; //应用库列表
	private SmartformInfo forms; //表单列表，由应用库选择更新
	private SmartformInfo formdata; //取选择的表单内容取数据库表单体

	public FormsPullSelectionDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createContents(Composite parent) {
		MessageConsole console = HTMLPlugin.getDefault().getConsole();
		final MessageConsoleStream consoleStream = console.newMessageStream();
		getShell().setText(HTMLPlugin.getResourceString("HTMLEditor.PullForm"));

		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(composite, SWT.NULL);
		label.setText("APP: ");
		appscombo = new Combo(composite, SWT.NULL);
		
		Label formlabel = new Label(composite, SWT.NULL);
		formlabel.setText("FORMs: ");
		formscombo = new Combo(composite, SWT.NULL);
		appscombo.add("Loading......");
		appscombo.select(0);
		formscombo.add("Loading......");
		formscombo.select(0);
		appscombo.setSize(new Point(200,200));
		formscombo.setSize(new Point(200,200));
		//优化获取应用库为异步
		Job retrieveJob = new Job("Retrieving APPSData") 
	    {           
	        @Override
	        protected IStatus run(IProgressMonitor monitor) {
	        	getAppsData();
	            return Status.OK_STATUS;                
	        }

			
	    };
	    retrieveJob.addJobChangeListener(new JobChangeAdapter() {

	        @Override
	        public void done(IJobChangeEvent event) {
	            if(event.getResult().isOK())
	            {
	            	 updateAPPSList();
	            	 
	            }
	        }

			       
	    });
	    // this will run in a background thread 
	    // and nicely integrate with the UI
	    retrieveJob.schedule();
		
		
		  appscombo.addSelectionListener(new SelectionListener() {
		      public void widgetSelected(SelectionEvent e) {
		    	  //consoleStream.println("Selected index: " + appscombo.getSelectionIndex() + ", selected item: " + appscombo.getItem(appscombo.getSelectionIndex()) + ", text content in the text field: " + appscombo.getText());
		    	  setFormsList();
		      }

		      public void widgetDefaultSelected(SelectionEvent e) {
		    	 //do nothing
		      }
		    });
		  
		Composite buttons = new Composite(composite, SWT.NULL);
		buttons.setLayout(new GridLayout(2, true));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		buttons.setLayoutData(gd);

		next = new Button(buttons, SWT.PUSH);
		next.setText(HTMLPlugin.getResourceString("Button.Pull"));
		next.setEnabled(true);
		next.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		next.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateFormContent();
			}
		});

		

		status = new Label(composite, SWT.NULL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		status.setLayoutData(gd);

		return composite;
	}

	private HTMLSourceEditor getActiveEditor(){
		IEditorPart part = HTMLUtil.getActiveEditor();
		if(part instanceof HTMLSourceEditor){
			return (HTMLSourceEditor) part;
		} else if(part instanceof IPaletteTarget){
			return ((IPaletteTarget) part).getPaletteTarget();
		}
		return null;
	}

	private void updateFormContent(){
		status.setText("forms selected:" + formscombo.getText());
		SmartFormsSync formsync = new SmartFormsSync();
		formdata = formsync.getFormDatabyFormId(forms.getIds().get(formscombo.getSelectionIndex()));
		HTMLSourceEditor editor = getActiveEditor();
		if(editor != null){
			
			try {
				FileOutputStream update = new FileOutputStream(editor.getFile());
				update.write(formdata.getFormdata().getBytes());
				update.flush();
				update.close();
				editor.doSave(new NullProgressMonitor());
				editor.setFocus();
				status.setText("pull success with Form name:"+formscombo.getText());
				HTMLPlugin.getDefault().setFormId(forms.getIds().get(formscombo.getSelectionIndex()));
				HTMLPlugin.getDefault().setFormName(forms.getNames().get(formscombo.getSelectionIndex()));
			} catch (FileNotFoundException e) {
				status.setText(e.getMessage());
			} catch (IOException e) {
				status.setText(e.getMessage());
			}
			
		} else {
			status.setText("Unsupported editor");
			
		}
	}

	@Override
	protected Point getInitialSize() {
		Point point = super.getInitialSize();
		point.x = 400;
		return point;
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	private void setFormsList() {
		SmartFormsSync formsync = new SmartFormsSync();
		forms = formsync.getFormbyAPPId(apps.getIds().get(appscombo.getSelectionIndex()));
		formscombo.removeAll();
		  for(int i=0; i<forms.getNames().size(); i++)
		      formscombo.add(forms.getNames().get(i));
		  formscombo.setSize(new Point(200,200));
	}
	private void getAppsData() {
		
		SmartFormsSync formsync = new SmartFormsSync();
		apps = formsync.getAPPs();
		
	}
	private void updateAPPSList() {//异步更新APP列表
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				appscombo.removeAll();
				for(int i=0; i<apps.getNames().size(); i++)
				      appscombo.add(apps.getNames().get(i));
				appscombo.setSize(new Point(200,200));
			}
			});
		
	}    

}
