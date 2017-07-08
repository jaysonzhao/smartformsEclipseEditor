package com.solar.htmleditor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

import com.solar.htmleditor.assist.SOTFormsSync;
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
	private SmartformInfo apps; //应用库列表
	private SmartformInfo forms; //表单列表，由应用库选择更新
	private SmartformInfo formdata; //取选择的表单内容取数据库表单体

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
		if(formName!=null){//没有PULL下来不给PUSH回去
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
		SOTFormsSync formsync = new SOTFormsSync();
		HTMLSourceEditor editor = getActiveEditor();
		if (editor != null) {
			try {
				FileInputStream formdata = new FileInputStream(editor.getFile());
				int flen = (int) editor.getFile().length();
				byte[] strBuffer = new byte[flen];
				formdata.read(strBuffer, 0, flen);
				formdata.close();
				formsync.updateFormDatabyFormId(formId, new String(strBuffer));
				consoleStream.println("updated form content with form ID: " + formId);
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
