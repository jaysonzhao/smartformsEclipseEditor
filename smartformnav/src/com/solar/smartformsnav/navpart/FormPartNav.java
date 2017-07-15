/*
 * Created on Jul 13, 2006
 *
 */
package com.solar.smartformsnav.navpart;

/**
 * 
 * ���ֱ������֣���ͷ�����塢���ԡ�ACL�ȡ�
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
