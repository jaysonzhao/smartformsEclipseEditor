/*
 * Created on Jul 13, 2006
 *
 */
package com.solar.smartformsnav;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.solar.htmleditor.assist.SmartFormsSync;
import com.solar.htmleditor.assist.SmartformInfo;
/**
 * 
 * 
 * 
 * @author Jayson
 */
public class NRContentProvider implements ITreeContentProvider
{
    private static Object[] EMPTY_OBJ_ARRAY = {}; 
    private final List<APPNav> appsnav = new ArrayList<APPNav>();
    
    
    public Object[] getChildren(Object parentElement)
    {
        if(parentElement instanceof IProject)
        {
            NRParent parent = new NRParent("智能表单平台应用库");
            return new NRParent[]{parent};
        }
        else if(parentElement instanceof NRParent)
        {
        	SmartFormsSync formsync = new SmartFormsSync();
        	SmartformInfo apps = formsync.getAPPs();
        	
            for(int i=0;i<apps.getNames().size();i++)
            {
                appsnav.add(new APPNav(apps.getNames().get(i), apps.getIds().get(i)));
            }
            return appsnav.toArray();
        }
        else if(parentElement instanceof APPNav)
        {
            FormsNav[] children = new FormsNav[2];
            children[0] = new FormsNav("表单", ((APPNav) parentElement).getSid());
            children[1] = new SubFormsNav("子表单", ((APPNav) parentElement).getSid());
            return children;
        }
        else if(parentElement instanceof FormsNav)
        {
        	
        	FormsNav parent = (FormsNav) parentElement;
            SmartFormsSync formsync = new SmartFormsSync();
            SmartformInfo forms = formsync.getFormbyAPPId(parent.getSid());
    		FormListNav[] children = new FormListNav[forms.getNames().size()];
    		for(int i=0; i<forms.getNames().size(); i++)
    			children[i] = new FormListNav(forms.getNames().get(i), forms.getIds().get(i));
                    
            return children;
        }
        else
        {
            return EMPTY_OBJ_ARRAY;
        }
    }
    
    public Object[] getElements(Object inputElement)
    {
        return getChildren(inputElement);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element)
    {
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {}

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {}

}
