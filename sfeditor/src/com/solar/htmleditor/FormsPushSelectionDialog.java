package com.solar.htmleditor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.preference.IPreferenceStore;
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

public class FormsPushSelectionDialog extends Window {

	private Text xpath;
	private Combo appscombo;
	private Combo formscombo;
	private Button next;
	private Button prev;
	private Label status;
	private String formName;
	private String formId;
	private boolean isHead;
	private boolean isSubForm;
	private boolean isShareSubForm;
	private SmartformInfo apps; //Ӧ�ÿ��б�
	private SmartformInfo forms; //���б���Ӧ�ÿ�ѡ�����
	private SmartformInfo formdata; //ȡѡ��ı�����ȡ���ݿ����

	public FormsPushSelectionDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createContents(Composite parent) {
		MessageConsole console = HTMLPlugin.getDefault().getConsole();
		final MessageConsoleStream consoleStream = console.newMessageStream();
		getShell().setText(HTMLPlugin.getResourceString("HTMLEditor.PushForm"));
        formName = HTMLPlugin.getDefault().getFormName();
        formId = HTMLPlugin.getDefault().getFormId();
        isHead = HTMLPlugin.getDefault().isHead();
        isSubForm = HTMLPlugin.getDefault().isSubForm();
        isShareSubForm = HTMLPlugin.getDefault().isShareForm();
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
       
		Label label = new Label(composite, SWT.NULL);
		Label idlabel = new Label(composite, SWT.NULL);
		if(formName!=null){
		  label.setText("Form to push: "+formName);
		  idlabel.setText("id for ref: "+ formId);
		}else{
		  label.setText("You need to pull a form first.");
		}
			   		  
		Composite buttons = new Composite(composite, SWT.NULL);
		buttons.setLayout(new GridLayout(2, true));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		buttons.setLayoutData(gd);

		next = new Button(buttons, SWT.PUSH);
		next.setText(HTMLPlugin.getResourceString("Button.Push"));
		if(formName!=null){//û��PULL��������PUSH��ȥ
			next.setEnabled(true);
		}else{
			next.setEnabled(false);
		}
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
		MessageConsole console = HTMLPlugin.getDefault().getConsole();
		final MessageConsoleStream consoleStream = console.newMessageStream();
		consoleStream.println("forms to push: " + formName+" with id: "+formId);
		final SmartFormsSync formsync = new SmartFormsSync(); //used to execute update sql and refresh action
		HTMLSourceEditor editor = getActiveEditor();
		if (editor != null) {
			try {
				FileInputStream formdata = new FileInputStream(editor.getFile());
				int flen = (int) editor.getFile().length();
				byte[] strBuffer = new byte[flen];
				formdata.read(strBuffer, 0, flen);
				formdata.close(); 
				if(isShareSubForm){
					formsync.updateShareSubFormDatabyFormId(formId, new String(strBuffer));
				}else if(isSubForm){
					formsync.updateSubFormDatabyFormId(formId, new String(strBuffer));
				}else if(isHead){
					formsync.updateFormHeadbyFormId(formId, new String(strBuffer));
				}else{
					formsync.updateFormDatabyFormId(formId, new String(strBuffer));
				}
				
				consoleStream.println("updated form content with form ID: " + formId);
				//�Ż���ȡӦ�ÿ�Ϊ�첽
				Job retrieveJob = new Job("Refresing server forms JSP") 
			    {           
			        @Override
			        protected IStatus run(IProgressMonitor monitor) {
			        	IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
			        	if(formsync.exeuteRefreshXSP(store.getString(HTMLPlugin.FORMS_SERVER_URL),store.getString(HTMLPlugin.FORMS_SERVER_DEVUSER),store.getString(HTMLPlugin.FORMS_SERVER_DEVPASS)))
			        		return Status.OK_STATUS;  
			        	else return Status.CANCEL_STATUS;
			        }

					
			    };
			    retrieveJob.addJobChangeListener(new JobChangeAdapter() {

			        @Override
			        public void done(IJobChangeEvent event) {
			            if(event.getResult().isOK())
			            {
			            	consoleStream.println("server forms refreshed");
			            	 
			            }
			        }

					       
			    });
			    // this will run in a background thread 
			    // and nicely integrate with the UI
			    retrieveJob.schedule();
				
			} catch (IOException e) {
				consoleStream.println(e.getMessage());
			}
		} else {
			status.setText("Unsupported editor");

		}
	}

	@Override
	protected Point getInitialSize() {
		Point point = super.getInitialSize();
		point.x = 500;
		return point;
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	

}
