/*
 * Created on Jul 13, 2006
 *
 */
package com.solar.smartformsnav.navpart;

/**
 * 
 * 区分表单各部分：表单头、表单体、属性、ACL等。
 * 
 * @author Jayson
 */
public class FormPartNav
{
	private boolean isSubForm=false;
	public synchronized boolean isSubForm() {
		return isSubForm;
	}

	public synchronized void setSubForm(boolean isSubForm) {
		this.isSubForm = isSubForm;
	}

	public synchronized boolean isShareForm() {
		return isShareForm;
	}

	public synchronized void setShareForm(boolean isShareForm) {
		this.isShareForm = isShareForm;
	}

	private boolean isShareForm=false;
    private String name = null;
    public synchronized String getSid() {
		return sid;
	}

	public synchronized void setSid(String sid) {
		this.sid = sid;
	}

	private String sid = null;
    
    
    public FormPartNav(String name)
    {
        this.name = name;
    }
    
    public FormPartNav(String name, String sid)
    {
        this.name = name;
        this.sid = sid;
    }
    
    public FormPartNav(String name, String sid,boolean isSubForm, boolean isShareSubForm)
    {
        this.name = name;
        this.sid = sid;
        this.isSubForm = isSubForm;
        this.isShareForm = isShareSubForm;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }        
}
