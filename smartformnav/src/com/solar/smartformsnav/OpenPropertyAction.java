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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.ui.ide.IDE;

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
	private String formPart;

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
				formPart = "Head";
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
				formPart = "Body";
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

				return ((FormPartNav) sSelection.getFirstElement()).isSubForm();
			}
		}
		return false;
	}

	private boolean isShareSubForm() {
		ISelection selection = provider.getSelection();
		if (!selection.isEmpty()) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			if (sSelection.size() == 1 && sSelection.getFirstElement() instanceof FormPartNav) {

				return ((FormPartNav) sSelection.getFirstElement()).isShareForm();
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
				if (isSubForm()) {
					formdata = formsync.getSubFormDatabyId(formId);
				} else if (isShareSubForm()) {
					formdata = formsync.getShareSubFormDatabyId(formId);
				} else if (isBody()) {// 主表单
					formdata = formsync.getFormDatabyFormId(formId);
				} else if (isHead()) {// 主表单头
					formdata = formsync.getFormHeadbyFormId(formId);
				}

				
				if (formdata != null) {
					

						IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(HTMLPlugin.getDefault()
								.getPreferenceStore().getString(HTMLPlugin.FORMS_WORKING_PROJECT));
						if (project != null) {
 
							 String formFileName = formName + formId.substring(7, 11) + formPart + ".xsp";
							 IFile file = project.getFile(new Path(formFileName));
						     InputStream inputStreamJava = new ByteArrayInputStream(formdata.getFormdata().getBytes());
							 if (!file.exists()){
							     file.create(inputStreamJava, false, null);
							 }else{
								 FileOutputStream update = new FileOutputStream(file.getLocation().toOSString());
									update.write(formdata.getFormdata().getBytes());
									update.flush();
									update.close();
							 }
							
							IDE.openEditor(page, file);
						}

						HTMLPlugin.getDefault().setFormId(formId);
						HTMLPlugin.getDefault().setFormName(formdata.getNames().get(0));
						HTMLPlugin.getDefault().setSubForm(isSubForm());
						HTMLPlugin.getDefault().setShareForm(isShareSubForm());
						HTMLPlugin.getDefault().setHead(isHead());
					

				} else {
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
