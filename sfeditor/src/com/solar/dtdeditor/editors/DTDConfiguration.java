package com.solar.dtdeditor.editors;

import com.solar.htmleditor.ColorProvider;
import com.solar.htmleditor.HTMLPlugin;
import com.solar.htmleditor.assist.HTMLAssistProcessor;
import com.solar.htmleditor.editors.HTMLConfiguration;
import com.solar.htmleditor.editors.HTMLTagScanner;

/**
 * @author Naoki Takezoe
 */
public class DTDConfiguration extends HTMLConfiguration {
	
	private HTMLTagScanner tagScanner;
	
	public DTDConfiguration(ColorProvider colorProvider) {
		super(colorProvider);
	}
	
	@Override protected HTMLTagScanner getTagScanner() {
		if (tagScanner == null) {
			tagScanner = new DTDTagScanner(getColorProvider());
			tagScanner.setDefaultReturnToken(
					getColorProvider().getToken(HTMLPlugin.PREF_COLOR_TAG));
		}
		return tagScanner;
	}
	
	@Override protected HTMLAssistProcessor createAssistProcessor() {
		return new DTDAssistProcessor();
	}
}
