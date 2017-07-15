/*
 * Created on Jul 13, 2006
 *
 */
package com.solar.smartformsnav.navpart;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class APPNav
{
    private String name = null;
    public synchronized String getSid() {
		return sid;
	}

	public synchronized void setSid(String sid) {
		this.sid = sid;
	}

	private String sid = null;
    
    
    public APPNav(String name)
    {
        this.name = name;
    }
    
    public APPNav(String name, String sid)
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
