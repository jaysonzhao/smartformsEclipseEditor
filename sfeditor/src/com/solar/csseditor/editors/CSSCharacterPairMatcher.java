package com.solar.csseditor.editors;

import org.eclipse.jface.text.IDocument;

import com.solar.htmleditor.HTMLUtil;
import com.solar.htmleditor.editors.AbstractCharacterPairMatcher;

/**
 * @author Naoki Takezoe
 */
public class CSSCharacterPairMatcher extends AbstractCharacterPairMatcher {

	public CSSCharacterPairMatcher() {
		addBlockCharacter('{', '}');
	}
	
	@Override protected String getSource(IDocument document){
		return HTMLUtil.cssComment2space(document.get());
	}

}
