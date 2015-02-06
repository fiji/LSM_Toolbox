
package org.imagearchive.lsm.toolbox.info.scaninfo;

import java.util.LinkedHashMap;

public class IlluminationChannel {

	public LinkedHashMap<String, Object> records = new LinkedHashMap();

	public Object[][] data = {
		{ new Long(-1879048191L), DataType.STRING, "ILL_NAME" },
		{ new Long(-1879048190L), DataType.DOUBLE, "POWER" },
		{ new Long(-1879048189L), DataType.DOUBLE, "WAVELENGTH" },
		{ new Long(-1879048188L), DataType.LONG, "ACQUIRE" },
		{ new Long(-1879048187L), DataType.STRING, "DETCHANNEL_NAME" },
		{ new Long(-1879048186L), DataType.DOUBLE, "POWER_BC1" },
		{ new Long(-1879048185L), DataType.DOUBLE, "POWER_BC2" } };

	public static boolean isIlluminationChannels(final long tagEntry) {
		if (tagEntry == -2147483648L) {
			return true;
		}
		return false;
	}

	public static boolean isIlluminationChannel(final long tagEntry) {
		if (tagEntry == -1879048192L) {
			return true;
		}
		return false;
	}
}
