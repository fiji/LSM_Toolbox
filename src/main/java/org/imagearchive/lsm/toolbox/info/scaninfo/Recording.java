
package org.imagearchive.lsm.toolbox.info.scaninfo;

import java.util.LinkedHashMap;

public class Recording {

	public LinkedHashMap<String, Object> records = new LinkedHashMap();

	public static Object[][] data = {
		{ new Long(268435457L), DataType.STRING, "ENTRY_NAME" },
		{ new Long(268435458L), DataType.STRING, "ENTRY_DESCRIPTION" },
		{ new Long(268435459L), DataType.STRING, "ENTRY_NOTES" },
		{ new Long(268435460L), DataType.STRING, "ENTRY_OBJECTIVE" },
		{ new Long(268435461L), DataType.STRING, "PROCESSING_SUMMARY" },
		{ new Long(268435462L), DataType.STRING, "SPECIAL_SCAN" },
		{ new Long(268435463L), DataType.STRING, "SCAN_TYPE" },
		{ new Long(268435464L), DataType.STRING, "SCAN_MODE" },
		{ new Long(268435465L), DataType.LONG, "STACKS_COUNT" },
		{ new Long(268435466L), DataType.LONG, "LINES_PER_PLANE" },
		{ new Long(268435467L), DataType.LONG, "SAMPLES_PER_LINE" },
		{ new Long(268435468L), DataType.LONG, "PLANES_PER_VOLUME" },
		{ new Long(268435469L), DataType.LONG, "IMAGES_WIDTH" },
		{ new Long(268435470L), DataType.LONG, "IMAGES_HEIGHT" },
		{ new Long(268435471L), DataType.LONG, "NUMBER_OF_PLANES" },
		{ new Long(268435472L), DataType.LONG, "IMAGES_NUMBER_STACKS" },
		{ new Long(268435473L), DataType.LONG, "IMAGES_NUMBER_CHANNELS" },
		{ new Long(268435474L), DataType.LONG, "LINESCAN_XY" },
		{ new Long(268435475L), DataType.LONG, "SCAN_DIRECTION" },
		{ new Long(268435476L), DataType.LONG, "TIME_SERIES" },
		{ new Long(268435477L), DataType.LONG, "ORIGNAL_SCAN_DATA" },
		{ new Long(268435478L), DataType.DOUBLE, "ZOOM_X" },
		{ new Long(268435479L), DataType.DOUBLE, "ZOOM_Y" },
		{ new Long(268435480L), DataType.DOUBLE, "ZOOM_Z" },
		{ new Long(268435481L), DataType.DOUBLE, "SAMPLE_0X" },
		{ new Long(268435482L), DataType.DOUBLE, "SAMPLE_0Y" },
		{ new Long(268435483L), DataType.DOUBLE, "SAMPLE_0Z" },
		{ new Long(268435484L), DataType.DOUBLE, "SAMPLE_SPACING" },
		{ new Long(268435485L), DataType.DOUBLE, "LINE_SPACING" },
		{ new Long(268435486L), DataType.DOUBLE, "PLANE_SPACING" },
		{ new Long(268435487L), DataType.DOUBLE, "PLANE_WIDTH" },
		{ new Long(268435488L), DataType.DOUBLE, "PLANE_HEIGHT" },
		{ new Long(268435489L), DataType.DOUBLE, "VOLUME_DEPTH" },
		{ new Long(268435508L), DataType.DOUBLE, "ROTATION" },
		{ new Long(268435509L), DataType.DOUBLE, "PRECESSION" },
		{ new Long(268435510L), DataType.DOUBLE, "SAMPLE_0TIME" },
		{ new Long(268435511L), DataType.STRING, "START_SCAN_TRIGGER_IN" },
		{ new Long(268435512L), DataType.STRING, "START_SCAN_TRIGGER_OUT" },
		{ new Long(268435513L), DataType.LONG, "START_SCAN_EVENT" },
		{ new Long(268435520L), DataType.DOUBLE, "START_SCAN_TIME" },
		{ new Long(268435521L), DataType.STRING, "STOP_SCAN_TRIGGER_IN" },
		{ new Long(268435522L), DataType.STRING, "STOP_SCAN_TRIGGER_OUT" },
		{ new Long(268435523L), DataType.LONG, "STOP_SCAN_EVENT" },
		{ new Long(268435524L), DataType.DOUBLE, "START_SCAN_TIME2" },
		{ new Long(268435525L), DataType.LONG, "USE_ROIS" },
		{ new Long(268435526L), DataType.LONG, "USE_REDUCED_MEMORY_ROIS" },
		{ new Long(268435527L), DataType.STRING, "USER" },
		{ new Long(268435528L), DataType.LONG, "USE_BCCORECCTION" },
		{ new Long(268435529L), DataType.DOUBLE, "POSITION_BCCORRECTION1" },
		{ new Long(268435536L), DataType.DOUBLE, "POSITION_BCCORRECTION2" },
		{ new Long(268435537L), DataType.LONG, "INTERPOLATIONY" },
		{ new Long(268435538L), DataType.LONG, "CAMERA_BINNING" },
		{ new Long(268435539L), DataType.LONG, "CAMERA_SUPERSAMPLING" },
		{ new Long(268435540L), DataType.LONG, "CAMERA_FRAME_WIDTH" },
		{ new Long(268435541L), DataType.LONG, "CAMERA_FRAME_HEIGHT" },
		{ new Long(268435542L), DataType.DOUBLE, "CAMERA_OFFSETX" },
		{ new Long(268435543L), DataType.DOUBLE, "CAMERA_OFFSETY" } };
	public Track[] tracks;
	public Marker[] markers;
	public Timer[] timers;
	public Laser[] lasers;

	public static boolean isRecording(final long tagEntry) {
		if (tagEntry == 268435456L) {
			return true;
		}

		return false;
	}
}
