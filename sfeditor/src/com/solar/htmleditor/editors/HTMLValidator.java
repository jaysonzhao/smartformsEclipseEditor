package com.solar.htmleditor.editors;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.w3c.tidy.Tidy;

import com.solar.htmleditor.HTMLPlugin;
import com.solar.htmleditor.HTMLUtil;
import com.solar.htmleditor.IOUtil;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLParser;
import jp.aonir.fuzzyxml.event.FuzzyXMLErrorEvent;
import jp.aonir.fuzzyxml.event.FuzzyXMLErrorListener;

/**
 * The HTML Validator that is called by HTMLSourceEditor.
 * <p>
 * This validator validates the HTML document using JTidy,
 * and provides some common features for validators.
 *
 * @author Naoki Takezoe
 * @author Tom Wickham-Jones
 */
public class HTMLValidator implements FuzzyXMLErrorListener {

	private String original;
	private String contents;
	private IFile file;

	private Pattern TIDY_ERROR = Pattern.compile("^line ([0-9]+?) column ([0-9]+?) - (.+?)$",Pattern.DOTALL);

	private boolean showXMLErrors;

	/**
	 * Constructor.
	 *
	 * @param file IFile that is validated by this class.
	 */
	public HTMLValidator(IFile file){
		this.file = file;
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		showXMLErrors = store.getBoolean( HTMLPlugin.PREF_SHOW_XML_ERRORS);
	}

	/**
	 * Validates the HTML document.
	 */
	public void doValidate(){
		try {
			// Validates using JTidy
			if(validateUsingTidy()){
				ByteArrayOutputStream out = null;
				try {
					out = new ByteArrayOutputStream();
					Tidy tidy = new Tidy();
					tidy.setXHTML(false);
					tidy.setQuiet(true);
					tidy.setInputEncoding(file.getCharset());
					tidy.setOutputEncoding("UTF-8");
					tidy.setErrout(new PrintWriter(out, true));
					Properties props=new Properties();
					props.setProperty("new-blocklevel-tags", 
							"sot:textfield sot:textarea sot:combobox sot:checkbox sot:radio sot:password sot:datefield sot:numberfield sot:fileupload sot:filedownload"
							+ " sot:encrypttextfield sot:encrypttextarea sot:subform sot:submit sot:userdlg sot:deptdlg"
							+ " sot:span sot:panel sot:btn sot:editor sot:openForm sot:text sot:param sot:sharesubform"
							+ " sot:list sot:currency sot:dataview sot:query sot:condition sot:columns sot:item"
							+ " sot:tablev2 sot:theadv2 sot:tbodyv2 sot:tableviewv2 sot:theadviewv2 sot:tbodyviewv2"
							+ " sot:bpmexecinfo sot:bpmrouting sot:bpmtoolbar sot:bpmtoolpanel sot:bpmnote sot:bpmauditlist sot:wfctrl");
					tidy.setConfigurationFromProps(props);
					tidy.parse(file.getContents(), (OutputStream) null);

					String errors = new String(out.toByteArray());

					errors = errors.replaceAll("\r\n","\n");
					errors = errors.replaceAll("\r"  ,"\n");

					String[] dim = errors.split("\n");
					for(int i=0;i<dim.length;i++){
						if(dim[i].startsWith("line")){
							Matcher matcher = TIDY_ERROR.matcher(dim[i]);
							if(matcher.matches()){
								String message = matcher.group(3);
								if(message.startsWith("Warning")){
									HTMLUtil.addMarker(file, IMarker.SEVERITY_INFO,
											Integer.parseInt(matcher.group(1)),
											matcher.group(3));
								} else {
									HTMLUtil.addMarker(file, IMarker.SEVERITY_ERROR,
											Integer.parseInt(matcher.group(1)),
											matcher.group(3));
								}
							}
						}
					}

				} finally {
					if(out!=null){
						out.close();
					}
				}
			}

			// Validates using FuzzyXML
			if(validateUsingFuzzyXML()){
				this.original = new String(IOUtil.readStream(file.getContents()), file.getCharset());
				String contents = filterContents(this.original, file);
				contents = HTMLUtil.scriptlet2space(contents,false);

				this.contents = contents;
				this.contents = this.contents.replaceAll("\r\n"," \n");
				this.contents = this.contents.replaceAll("\r"  ,"\n");

				FuzzyXMLParser parser = new FuzzyXMLParser();
				parser.addErrorListener( this);
//				parser.addErrorListener(new HTMLParseErrorListener());
				FuzzyXMLDocument doc = parser.parse(contents);
				validateDocument(doc);
			}
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		}
	}

	/**
	 * Returns true if this validator validates using JTidy.
	 * <p>
	 * <strong>Notice:</strong> Even if this method returns true, JTidy based validation isn't executed,
	 * when &quot;HTML Validation&quot; isn't checked in the project property page.
	 *
	 * @return
	 *   <ul>
	 *     <li>true - validate using JTidy</li>
	 *     <li>false - not validate using JTidy</li>
	 *   </ul>
	 */
	protected boolean validateUsingTidy(){
		// not valudate when the target document is XHTML
		if(file.getName().endsWith(".xhtml")){
			return false;
		}
		try {
			String source = new String(IOUtil.readStream(file.getContents()), file.getCharset());
			if(source.indexOf("xmlns=\"http://www.w3.org/1999/xhtml\"") >= 0){
				return false;
			}
			if(source.indexOf("<?xml version=\"1.0\"") >= 0){
				return false;
			}
		} catch(Exception ex){
		}

		return true;
	}

	/**
	 * Returns true if this validator validates using FuzzyXML.
	 * Validation logic is written in validateDocument().
	 *
	 * @return
	 *   <ul>
	 *     <li>true - validate using FuzzyXML</li>
	 *     <li>false - not validate using FuzzyXML</li>
	 *   </ul>
	 */
	protected boolean validateUsingFuzzyXML(){
		if(!validateUsingTidy()){
			return true;
		}
		return false;
	}

	/**
	 * Validates using FuzzyXML.
	 * This method is called only when validateUsingFuzzyXML() returns true.
	 *
	 * @param doc FuzzyXMLDocument
	 */
	protected void validateDocument(FuzzyXMLDocument doc){
		// Nothing to do in default.
	}

	/**
	 * Returns the targeted IFile object.
	 *
	 * @return the IFile object, target of this validator
	 */
	protected IFile getFile(){
		return this.file;
	}

	/**
	 * Returns the original source code.
	 *
	 * @return the original source code
	 */
	protected String getContent(){
		return this.original;
	}

	/**
	 * Returns the line number from the offset.
	 *
	 * @param offset the offset
	 * @return the line number.
	 *
	 * Needs to be offset+1 in the substring to make sure
	 * that the text is included in the substring.  Otherwise
	 * an offset at the start of the line is not included.
	 */
	protected int getLineAtOffset(int offset){
		String text = contents.substring(0,offset+1);
		return text.split("\n").length;
	}

	/**
	 * Filters the contents of the page.
	 * <p>
	 * This validator will validate returned contents.
	 *
	 * @param contents
	 * @return filtered contents
	 * @since 2.0.5
	 */
	protected String filterContents( String contents, IFile file) {
		return contents;
	}

	public void error(FuzzyXMLErrorEvent event) {
		if ( !showXMLErrors) {
			return;
		}
		int offset = event.getOffset();
		int length = event.getLength();
		String message = event.getMessage();
		HTMLUtil.addMarker(getFile(), IMarker.SEVERITY_ERROR, getLineAtOffset(offset), offset , length, message);
	}


}
