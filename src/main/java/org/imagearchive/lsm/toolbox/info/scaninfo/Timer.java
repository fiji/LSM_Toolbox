
package org.imagearchive.lsm.toolbox.info.scaninfo;

import java.util.LinkedHashMap;

public class Timer {

	public LinkedHashMap<String, Object> records = new LinkedHashMap();

	public Object[][] data = {
		{ new Long(301989889L), DataType.STRING, "TIMER_NAME" },
		{ new Long(301989891L), DataType.DOUBLE, "INTERVAL" },
		{ new Long(301989892L), DataType.STRING, "TRIGGER_IN" },
		{ new Long(301989893L), DataType.STRING, "TRIGGER_OUT" } };

	public static boolean isTimers(final long tagEntry) {
		if (tagEntry == 285212672L) {
			return true;
		}
		return false;
	}

	public static boolean isTimer(final long tagEntry) {
		if (tagEntry == 301989888L) {
			return true;
		}
		return false;
	}
}
