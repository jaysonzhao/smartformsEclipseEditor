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
import com.solar.smartformsnav.navpart.APPNav;
import com.solar.smartformsnav.navpart.FormBodyNav;
import com.solar.smartformsnav.navpart.FormHeadNav;
import com.solar.smartformsnav.navpart.FormListNav;
import com.solar.smartformsnav.navpart.FormPartNav;
import com.solar.smartformsnav.navpart.FormsNav;
import com.solar.smartformsnav.navpart.MainFormsNav;
import com.solar.smartformsnav.navpart.ShareSubFormsNav;
import com.solar.smartformsnav.navpart.SubFormsNav;
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
            FormsNav[] children = new FormsNav[3];
            children[0] = new MainFormsNav("表单", ((APPNav) parentElement).getSid());
            children[1] = new SubFormsNav("子表单", ((APPNav) parentElement).getSid());
            children[2] = new ShareSubFormsNav("共享子表单", ((APPNav) parentElement).getSid());
            return children;
        }
        else if(parentElement instanceof MainFormsNav){
        	MainFormsNav parent = (MainFormsNav) parentElement;
            SmartFormsSync formsync = new SmartFormsSync();
            SmartformInfo forms = formsync.getFormbyAPPId(parent.getSid());
    		FormListNav[] children = new FormListNav[forms.getNames().size()];
    		for(int i=0; i<forms.getNames().size(); i++)
    			children[i] = new FormListNav(forms.getNames().get(i), forms.getIds().get(i));
                    
            return children;
        	
        } else if(parentElement instanceof SubFormsNav){
        	SubFormsNav parent = (SubFormsNav) parentElement;
            SmartFormsSync formsync = new SmartFormsSync();
            SmartformInfo forms = formsync.getSubFormbyAPPId(parent.getSid());
    		FormListNav[] children = new FormListNav[forms.getNames().size()];
    		for(int i=0; i<forms.getNames().size(); i++)
    			children[i] = new FormListNav(forms.getNames().get(i), forms.getIds().get(i), true, false);
                    
            return children;
        	
        }
        else if(parentElement instanceof ShareSubFormsNav){
        	ShareSubFormsNav parent = (ShareSubFormsNav) parentElement;
            SmartFormsSync formsync = new SmartFormsSync();
            SmartformInfo forms = formsync.getShareSubForm();
    		FormListNav[] children = new FormListNav[forms.getNames().size()];
    		for(int i=0; i<forms.getNames().size(); i++)
    			children[i] = new FormListNav(forms.getNames().get(i), forms.getIds().get(i), false, true);
                    
            return children;
        	
        }
        else if(parentElement instanceof FormListNav)
        {
        	
        	FormListNav parent = (FormListNav) parentElement;
        	FormPartNav[] children = null;
           if(parent.isShareForm() || parent.isSubForm()){
        	   children = new FormPartNav[1];
               children[0] = new FormBodyNav("表单体", parent.getSid(),parent.isSubForm(),parent.isShareForm());
           }else{
        	   children = new FormPartNav[2];
        	   children[0] = new FormHeadNav("表单头", parent.getSid(),parent.isSubForm(),parent.isShareForm());
               children[1] = new FormBodyNav("表单体", parent.getSid(),parent.isSubForm(),parent.isShareForm());
           }
           
            
    	
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
