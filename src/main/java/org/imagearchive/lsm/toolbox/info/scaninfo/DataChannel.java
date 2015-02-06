
package org.imagearchive.lsm.toolbox.info.scaninfo;

import java.util.LinkedHashMap;

public class DataChannel {

	public LinkedHashMap<String, Object> records = new LinkedHashMap();

	public Object[][] data = {
		{ new Long(-805306367L), DataType.STRING, "DATA_NAME" },
		{ new Long(-805306364L), DataType.LONG, "COLOR" },
		{ new Long(-805306363L), DataType.LONG, "SAMPLETYPE" },
		{ new Long(-805306362L), DataType.LONG, "BITS_PER_SAMPLE" },
		{ new Long(-805306361L), DataType.LONG, "RATIO_TYPE" },
		{ new Long(-805306360L), DataType.LONG, "RATIO_TRACK1" },
		{ new Long(-805306359L), DataType.LONG, "RATIO_TRACK2" },
		{ new Long(-805306358L), DataType.STRING, "RATIO_CHANNEL1" },
		{ new Long(-805306357L), DataType.STRING, "RATIO_CHANNEL2" },
		{ new Long(-805306356L), DataType.DOUBLE, "RATIO_CONST1" },
		{ new Long(-805306355L), DataType.DOUBLE, "RATIO_CONST2" },
		{ new Long(-805306354L), DataType.DOUBLE, "RATIO_CONST3" },
		{ new Long(-805306353L), DataType.DOUBLE, "RATIO_CONST4" },
		{ new Long(-805306352L), DataType.DOUBLE, "RATIO_CONST5" },
		{ new Long(-805306351L), DataType.DOUBLE, "RATIO_CONST6" },
		{ new Long(-805306350L), DataType.LONG, "RATIO_FIRST_IMAGES1" },
		{ new Long(-805306349L), DataType.LONG, "RATIO_FIRST_IMAGES2" },
		{ new Long(-805306348L), DataType.STRING, "DYE_NAME" },
		{ new Long(-805306347L), DataType.STRING, "DYE_FOLDER" },
		{ new Long(-805306346L), DataType.STRING, "SPECTRUM" },
		{ new Long(-805306345L), DataType.LONG, "ACQUIRE" } };

	public static boolean isDataChannels(final long tagEntry) {
		if (tagEntry == -1073741824L) {
			return true;
		}
		return false;
	}

	public static boolean isDataChannel(final long tagEntry) {
		if (tagEntry == -805306368L) {
			return true;
		}
		return false;
	}
}
