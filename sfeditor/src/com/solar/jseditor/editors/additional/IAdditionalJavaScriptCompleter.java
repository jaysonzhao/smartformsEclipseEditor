package com.solar.jseditor.editors.additional;

import java.util.List;

import com.solar.jseditor.editors.model.JavaScriptModel;

/**
 * Provides additional JavaScript completion proposals.
 * 
 * @author Naoki Takezoe
 * @author shinsuke
 */
public interface IAdditionalJavaScriptCompleter {

	/**
	 * Load JS file and put the created model to the list.
	 * 
	 * @param libModelList
	 */
	List<JavaScriptModel> loadModel(List<JavaScriptModel> libModelList);
}
