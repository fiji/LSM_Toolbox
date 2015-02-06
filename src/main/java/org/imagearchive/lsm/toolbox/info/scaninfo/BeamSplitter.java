
package org.imagearchive.lsm.toolbox.info.scaninfo;

import java.util.LinkedHashMap;

public class BeamSplitter {

	public LinkedHashMap<String, Object> records = new LinkedHashMap();

	public Object[][] data = {
		{ new Long(-1342177279L), DataType.STRING, "FILTER_SET" },
		{ new Long(-1342177278L), DataType.STRING, "FILTER" },
		{ new Long(-1342177277L), DataType.STRING, "BS_NAME" } };

	public static boolean isBeamSplitters(final long tagEntry) {
		if (tagEntry == -1610612736L) {
			return true;
		}
		return false;
	}

	public static boolean isBeamSplitter(final long tagEntry) {
		if (tagEntry == -1342177280L) {
			return true;
		}
		return false;
	}
}
