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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }        
}
