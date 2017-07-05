package com.solar.htmleditor;

import java.util.Map;

import com.solar.jspeditor.editors.JSPInfo;

import jp.aonir.fuzzyxml.FuzzyXMLNode;

/**
 * An interface to convert taglibs for HTML preview.
 * 
 * @author Naoki Takezoe
 */
public interface ICustomTagConverter {
	
	public String process(Map<String, String> attrs,FuzzyXMLNode[] children,
			JSPInfo info, boolean fixPath);
	
}
