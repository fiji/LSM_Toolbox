
package org.imagearchive.lsm.toolbox.info.scaninfo;

import java.util.LinkedHashMap;

public class Laser {

	public LinkedHashMap<String, Object> records = new LinkedHashMap();

	public Object[][] data = {
		{ new Long(1342177281L), DataType.STRING, "LASER_NAME" },
		{ new Long(1342177282L), DataType.LONG, "LASER_ACQUIRE" },
		{ new Long(1342177283L), DataType.DOUBLE, "LASER_POWER" } };

	public static boolean isLasers(final long tagEntry) {
		if (tagEntry == 805306368L) {
			return true;
		}
		return false;
	}

	public static boolean isLaser(final long tagEntry) {
		if (tagEntry == 1342177280L) {
			return true;
		}
		return false;
	}
}
