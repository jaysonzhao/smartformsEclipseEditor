/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package com.solar.smartformsnav;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.solar.htmleditor.HTMLPlugin;
import com.solar.htmleditor.HTMLUtil;
import com.solar.htmleditor.assist.SmartFormsSync;
import com.solar.htmleditor.assist.SmartformInfo;
import com.solar.htmleditor.editors.HTMLSourceEditor;
import com.solar.htmleditor.views.IPaletteTarget;
import com.solar.smartformsnav.navpart.FormBodyNav;
import com.solar.smartformsnav.navpart.FormHeadNav;
import com.solar.smartformsnav.navpart.FormPartNav;

/**
 * @since 3.2
 *
 */
public class OpenPropertyAction extends Action {

	private IWorkbenchPage page;
	private String formId;
	private String formName;
	private ISelectionProvider provider;

	/**
	 * Construct the OpenPropertyAction with the given page.
	 * 
	 * @param p
	 *            The page to use as context to open the editor.
	 * @param selectionProvider
	 *            The selection provider
	 */
	public OpenPropertyAction(IWorkbenchPage p, ISelectionProvider selectionProvider) {
		setText("Pull Form"); //$NON-NLS-1$
		page = p;
		provider = selectionProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	public boolean isEnabled() {
		ISelection selection = provider.getSelection();
		if (!selection.isEmpty()) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			if (sSelection.size() == 1 && sSelection.getFirstElement() instanceof FormPartNav) {
				
				formName = ((FormPartNav) sSelection.getFirstElement()).getName();
				formId = ((FormPartNav) sSelection.getFirstElement()).getSid();
				return true;
			}
		}
		return false;
	}
	
	private boolean isHead() {
		ISelection selection = provider.getSelection();
		if (!selection.isEmpty()) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			if (sSelection.size() == 1 && sSelection.getFirstElement() instanceof FormHeadNav) {
			
				return true;
			}
		}
		return false;
	}
	
	private boolean isBody() {
		ISelection selection = provider.getSelection();
		if (!selection.isEmpty()) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			if (sSelection.size() == 1 && sSelection.getFirstElement() instanceof FormBodyNav) {
			
				return true;
			}
		}
		return false;
	}
	
	private boolean isSubForm() {
		ISelection selection = provider.getSelection();
		if (!selection.isEmpty()) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			if (sSelection.size() == 1 && sSelection.getFirstElement() instanceof FormPartNav) {
			    
				return ((FormPartNav)sSelection.getFirstElement()).isSubForm();
			}
		}
		return false;
	}
	private boolean isShareSubForm() {
		ISelection selection = provider.getSelection();
		if (!selection.isEmpty()) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			if (sSelection.size() == 1 && sSelection.getFirstElement() instanceof FormPartNav) {
			    
				return ((FormPartNav)sSelection.getFirstElement()).isShareForm();
			}
		}
		return false;
	}

	private HTMLSourceEditor getActiveEditor() {
		IEditorPart part = HTMLUtil.getActiveEditor();
		if (part instanceof HTMLSourceEditor) {
			return (HTMLSourceEditor) part;
		} else if (part instanceof IPaletteTarget) {
			return ((IPaletteTarget) part).getPaletteTarget();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		/*
		 * In production code, you should always externalize strings, but this
		 * is an example.
		 */

		try {
			if (isEnabled()) {
				// System.out.println(formId);
				SmartFormsSync formsync = new SmartFormsSync();
				SmartformInfo formdata = null;
				if(isSubForm()){
					 formdata = formsync.getSubFormDatabyId(formId);
				}else if (isShareSubForm()){
					 formdata = formsync.getShareSubFormDatabyId(formId);
				}else if(isBody()){//主表单
					 formdata = formsync.getFormDatabyFormId(formId);
				}else if(isHead()){//主表单头
					 formdata = formsync.getFormHeadbyFormId(formId);
				}
				
				HTMLSourceEditor editor = getActiveEditor();
				if (editor != null && formdata != null) {
					try {
						FileOutputStream update = new FileOutputStream(editor.getFile());
						update.write(formdata.getFormdata().getBytes());
						update.flush();
						update.close();
						editor.doSave(new NullProgressMonitor());
						editor.setFocus();
						HTMLPlugin.getDefault().setFormId(formId);
						HTMLPlugin.getDefault().setFormName(formdata.getNames().get(0));
						HTMLPlugin.getDefault().setSubForm(isSubForm());
						HTMLPlugin.getDefault().setShareForm(isShareSubForm());
						HTMLPlugin.getDefault().setHead(isHead());
					} catch (FileNotFoundException e) {
						Activator.logError(0, "Form not pull!", e);
					} catch (IOException e) {
						Activator.logError(0, "Local file open error!", e);
					}

				}
				else{
					MessageConsole console = HTMLPlugin.getDefault().getConsole();
					final MessageConsoleStream consoleStream = console.newMessageStream();
					consoleStream.println("Please Open a xsp file first.");
					
				}

			}
		} catch (Exception e) {
			Activator.logError(0, "Could not open!", e); //$NON-NLS-1$
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error Opening ", //$NON-NLS-1$
					"Could not open!"); //$NON-NLS-1$
		}
	}
}
