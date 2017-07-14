/*
 * Created on Jul 13, 2006
 *
 */
package com.solar.smartformsnav;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class SubFormsNav extends FormsNav
{
    private String name = null;
    public synchronized String getSid() {
		return sid;
	}

	public synchronized void setSid(String sid) {
		this.sid = sid;
	}

	private String sid = null;
    
    
    public SubFormsNav(String name)
    {
    	super(name);
    	this.name=name;
       
    }
    
    public SubFormsNav(String name, String sid)
    {
        super(name,sid);
        this.name=name;
        this.sid=sid;
        
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
