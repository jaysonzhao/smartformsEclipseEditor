/*
 * Created on Jul 13, 2006
 *
 */
package com.solar.smartformsnav.navpart;

/**
 * 
 * 表单列表的ELEMENT
 * 
 * @author Jayson
 */
public class FormListNav
{
    private String name = null;
    public synchronized String getSid() {
		return sid;
	}

	public synchronized void setSid(String sid) {
		this.sid = sid;
	}

	private String sid = null;
    
    
    public FormListNav(String name)
    {
        this.name = name;
    }
    
    public FormListNav(String name, String sid)
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
