package com.solar.htmleditor.views;

import org.eclipse.jface.resource.ImageDescriptor;

import com.solar.htmleditor.editors.HTMLSourceEditor;

public interface IPaletteItem {
	
	public String getLabel();
	
	public ImageDescriptor getImageDescriptor();
	
	public void execute(HTMLSourceEditor editor);
	
}
