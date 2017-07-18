/*
 * Created on Jul 13, 2006
 * 
 */
package com.solar.smartformsnav;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.solar.smartformsnav.navpart.APPNav;
import com.solar.smartformsnav.navpart.FormListNav;
import com.solar.smartformsnav.navpart.FormPartNav;
import com.solar.smartformsnav.navpart.FormsNav;
import com.solar.smartformsnav.navpart.SubFormsNav;


/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class NRLabelProvider implements ILabelProvider
{
    
    public String getText(Object element)
    {
        if(element instanceof NRParent)
        {
            NRParent parent = (NRParent)element;
            return parent.getName();
        }
        else if(element instanceof APPNav)
        {
            APPNav child = (APPNav) element;
            return child.getName();
        }
        else if(element instanceof FormsNav){
        	FormsNav child = (FormsNav) element;
        	return child.getName();
        }        	
        else if(element instanceof FormPartNav){
        	FormPartNav child = (FormPartNav) element;
        	return child.getName();
        }  
        else if(element instanceof FormListNav){
        	FormListNav child = (FormListNav) element;
        	return child.getName();
        }  
        else 
        {
            return "";
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    public Image getImage(Object element)
    {
    	if (element instanceof NRParent)
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_ETOOL_HOME_NAV); 
    	if(element instanceof APPNav)
       		return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_FOLDER); 
    	if(element instanceof FormsNav)
       		return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_FOLDER); 
    	if(element instanceof FormPartNav)
       		return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT); 
    	if(element instanceof FormListNav)
       		return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_FILE); 
        
    	return null;

    }

    

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener)
    {}

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose()
    {}

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     */
    public boolean isLabelProperty(Object element, String property)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener listener)
    {}

}
