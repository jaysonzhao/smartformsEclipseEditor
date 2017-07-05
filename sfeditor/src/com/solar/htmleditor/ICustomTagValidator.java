package com.solar.htmleditor;

import java.util.Map;

import com.solar.jspeditor.editors.IJSPValidationMarkerCreator;
import com.solar.jspeditor.editors.JSPInfo;

import jp.aonir.fuzzyxml.FuzzyXMLElement;

/**
 * An interface to convert taglibs for HTML preview.
 * 
 * @author Naoki Takezoe
 */
public interface ICustomTagValidator {
	
	public void validate(IJSPValidationMarkerCreator creator, 
			Map<String, String> attrs,FuzzyXMLElement element,JSPInfo info);
	
}