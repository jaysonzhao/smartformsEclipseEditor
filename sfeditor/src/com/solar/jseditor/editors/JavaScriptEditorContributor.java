package com.solar.jseditor.editors;

import com.solar.htmleditor.editors.HTMLSourceEditorContributer;

public class JavaScriptEditorContributor extends HTMLSourceEditorContributer {

	public JavaScriptEditorContributor(){
		addActionId(JavaScriptEditor.ACTION_COMMENT);
		addActionId(JavaScriptEditor.ACTION_OUTLINE);
	}

}
