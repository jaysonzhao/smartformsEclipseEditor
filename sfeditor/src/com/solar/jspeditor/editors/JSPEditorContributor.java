package com.solar.jspeditor.editors;

import com.solar.htmleditor.editors.HTMLEditorContributor;

public class JSPEditorContributor extends HTMLEditorContributor {
	
	@Override protected void init(){
		super.init();
		contributer.addActionId(JSPSourceEditor.ACTION_JSP_COMMENT);
	}
	
}
