package com.solar.htmleditor;

import org.eclipse.core.resources.IFile;

import com.solar.htmleditor.assist.AssistInfo;

public interface IFileAssistProcessor {
	
	public void reload(IFile file);
	
	public AssistInfo[] getAssistInfo(String value);
	
}
