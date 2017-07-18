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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * @since 3.2
 *
 */
public class PropertyActionProvider extends CommonActionProvider {
	
	private OpenPropertyAction openAction;

	/**
	 * Construct Property Action provider.
	 */
	public PropertyActionProvider() { 		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.navigator.CommonActionProvider#init(org.eclipse.ui.navigator.ICommonActionExtensionSite)
	 */
	public void init(ICommonActionExtensionSite aSite) {
		super.init(aSite);
		ICommonViewerSite viewSite = aSite.getViewSite();
		if(viewSite instanceof ICommonViewerWorkbenchSite) {
			ICommonViewerWorkbenchSite workbenchSite = 
				(ICommonViewerWorkbenchSite) viewSite;
			openAction = 
				new OpenPropertyAction(workbenchSite.getPage(), 
										workbenchSite.getSelectionProvider());
		}
		IWorkbench workbench = Activator.getDefault().getWorkbench();
		IDoubleClickListener doubleclick = new IDoubleClickListener() {
	        @Override
	        public void doubleClick(DoubleClickEvent event) {//暂时不实现双击
	            //System.out.println("in double click");
	        }
	    };
	    aSite.getStructuredViewer().addDoubleClickListener(doubleclick);
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
	 */
	public void fillActionBars(IActionBars actionBars) { 
		/* Set up the property open action when enabled. */
		if(openAction.isEnabled())
			actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openAction);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillContextMenu(IMenuManager menu) {
		if(openAction.isEnabled())
			menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, openAction);		
	}
	
	
}
