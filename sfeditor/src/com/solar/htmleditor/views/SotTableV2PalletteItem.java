package com.solar.htmleditor.views;

import com.solar.htmleditor.HTMLPlugin;

public class SotTableV2PalletteItem extends DefaultPaletteItem {

	
	public SotTableV2PalletteItem() {
		super("TableV2", HTMLPlugin.getDefault().getImageRegistry().getDescriptor(HTMLPlugin.ICON_TEXT),
				"<sot:tableV2 id=\"tht\" >"
				+ " <sot:theadV2>"
				+ "   <tr>"
				+ "	<th>1</th>"
				+ "	<th>2</th>"
				+ "	<th>3</th>"
				+ "	<th>4</th>"
				+ "	<th>5</th>"
				+ "  </tr>"
				+ " </sot:theadV2>"
				+ " <sot:tbodyV2>"
				+ "   <tr>"
				+ "	<td><input name=\"file\"></td>"
				+ "    <td><input name=\"Fruit\" type=\"checkbox\" value=\"apple\" />Æ»¹û"
				+ "	<input name=\"Fruit\" type=\"checkbox\" value=\"pea\" />ÌÒ×Ó "
				+ "    </td>"
				+ "<td><select name=\"size\"> "
				+ "	<option value=\"0\">DIVCSS5</option> "
				+ "	<option value=\"1\">DI</option> "
				+ "	</select> "
				+ "    </td>"
				+ "    <td>"
				+ "	<input name=\"version\" type=\"radio\" value=\"1\" />ver1"
				+ "	<input name=\"version\" type=\"radio\" value=\"2\" />ver2  "
						+ "	<input name=\"version\" type=\"radio\" value=\"3\" />ver3 "
								+ "	<input name=\"version\" type=\"radio\" value=\"4\" />ver4"
										+ "    </td>"
										+ "    <td><input name=\"operate\">"
										+ "</td>"
										+ "     <td><input name=\"file\"></td>"
										+ "   </tr>  </sot:tbodyV2>"
										+ "</sot:tableV2>");
	
	}

}
