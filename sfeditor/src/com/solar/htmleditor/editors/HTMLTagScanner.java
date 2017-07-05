package com.solar.htmleditor.editors;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;

import com.solar.htmleditor.ColorProvider;
import com.solar.htmleditor.HTMLPlugin;

public class HTMLTagScanner extends RuleBasedScanner {

	public HTMLTagScanner(ColorProvider colorProvider, boolean bold) {
		IToken string = null;
		if(bold){
            string = colorProvider.getToken(HTMLPlugin.PREF_COLOR_TAGLIB_ATTR);
		} else {
		    string = colorProvider.getToken(HTMLPlugin.PREF_COLOR_STRING);
		}
		IRule[] rules = new IRule[3];
		
		rules[0] = new MultiLineRule("\"" , "\"" , string, '\\');
		rules[1] = new MultiLineRule("'"  , "'"  , string, '\\');
		rules[2] = new WhitespaceRule(new HTMLWhitespaceDetector());
		
		setRules(rules);
	}
}
