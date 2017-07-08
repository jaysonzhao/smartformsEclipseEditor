package com.solar.htmleditor.assist;

import java.util.ArrayList;
import java.util.List;

public class SmartformInfo {
	private String formdata;
	public synchronized String getFormdata() {
		return formdata;
	}
	public synchronized void setFormdata(String formdata) {
		this.formdata = formdata;
	}
	private List<String> names;
	public synchronized List<String> getNames() {
		return names;
	}
	public synchronized void setNames(List<String> names) {
		this.names = names;
	}
	public synchronized List<String> getIds() {
		return ids;
	}
	public synchronized void setIds(List<String> ids) {
		this.ids = ids;
	}
	private List<String> ids;
	
	public SmartformInfo(){
		this.names = new ArrayList<String>();
		this.ids = new ArrayList<String>();
		
	}

}
