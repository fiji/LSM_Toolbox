
package org.imagearchive.lsm.toolbox.info.scaninfo;

import java.util.LinkedHashMap;

public class Marker {

	public LinkedHashMap<String, Object> records = new LinkedHashMap();

	public Object[][] data = {
		{ new Long(335544321L), DataType.STRING, "MARKER_NAME" },
		{ new Long(335544322L), DataType.STRING, "DESCRIPTION" },
		{ new Long(335544323L), DataType.STRING, "TRIGGER_IN" },
		{ new Long(335544324L), DataType.STRING, "TRIGGER_OUT" } };

	public static boolean isMarkers(final long tagEntry) {
		if (tagEntry == 318767104L) {
			return true;
		}
		return false;
	}

	public static boolean isMarker(final long tagEntry) {
		if (tagEntry == 335544320L) {
			return true;
		}
		return false;
	}
}
