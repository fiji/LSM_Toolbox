
package org.imagearchive.lsm.toolbox.info.scaninfo;

import java.util.LinkedHashMap;

public class Track {

	public LinkedHashMap<String, Object> records = new LinkedHashMap();

	public Object[][] data = {
		{ new Long(1073741825L), DataType.LONG, "MULTIPLEX_TYPE" },
		{ new Long(1073741826L), DataType.LONG, "MULTIPLEX_ORDER" },
		{ new Long(1073741827L), DataType.LONG, "SAMPLING_MODE" },
		{ new Long(1073741828L), DataType.LONG, "SAMPLING_METHOD" },
		{ new Long(1073741829L), DataType.LONG, "SAMPLING_NUMBER" },
		{ new Long(1073741830L), DataType.LONG, "ACQUIRE" },
		{ new Long(1073741831L), DataType.DOUBLE, "OBSERVATION_TIME" },
		{ new Long(1073741835L), DataType.DOUBLE, "TIME_BETWEEN_STACKS" },
		{ new Long(1073741836L), DataType.STRING, "TRACK_NAME" },
		{ new Long(1073741837L), DataType.STRING, "COLLIMATOR1_NAME" },
		{ new Long(1073741838L), DataType.LONG, "COLLIMATOR1_POSITION" },
		{ new Long(1073741839L), DataType.STRING, "COLLIMATOR2_NAME" },
		{ new Long(1073741840L), DataType.STRING, "COLLIMATOR2_POSITION" },
		{ new Long(1073741841L), DataType.LONG, "BLEACH_TRACK" },
		{ new Long(1073741842L), DataType.LONG, "BLEACH_AFTER_SCAN_NUMBER" },
		{ new Long(1073741843L), DataType.LONG, "BLEACH_SCAN_NUMBER" },
		{ new Long(1073741844L), DataType.STRING, "TRIGGER_IN" },
		{ new Long(1073741845L), DataType.STRING, "TRIGGER_OUT" },
		{ new Long(1073741846L), DataType.LONG, "IS_RATIO_TRACK" },
		{ new Long(1073741847L), DataType.LONG, "BLEACH_COUNT" },
		{ new Long(1073741848L), DataType.DOUBLE, "SPI_CENTER_WAVELENGTH" },
		{ new Long(1073741849L), DataType.DOUBLE, "PIXEL_TIME" },
		{ new Long(1073741856L), DataType.STRING, "ID_CONDENSOR_FRONTLENS" },
		{ new Long(1073741857L), DataType.LONG, "CONDENSOR_FRONTLENS" },
		{ new Long(1073741858L), DataType.STRING, "ID_FIELD_STOP" },
		{ new Long(1073741859L), DataType.DOUBLE, "FIELD_STOP_VALUE" },
		{ new Long(1073741860L), DataType.STRING, "ID_CONDENSOR_APERTURE" },
		{ new Long(1073741861L), DataType.DOUBLE, "CONDENSOR_APERTURE" },
		{ new Long(1073741862L), DataType.STRING, "ID_CONDENSOR_REVOLVER" },
		{ new Long(1073741863L), DataType.STRING, "CONDENSOR_FILTER" },
		{ new Long(1073741864L), DataType.DOUBLE, "ID_TRANSMISSION_FILTER1" },
		{ new Long(1073741865L), DataType.STRING, "ID_TRANSMISSION1" },
		{ new Long(1073741872L), DataType.DOUBLE, "ID_TRANSMISSION_FILTER2" },
		{ new Long(1073741873L), DataType.STRING, "ID_TRANSMISSION2" },
		{ new Long(1073741874L), DataType.LONG, "REPEAT_BLEACH" },
		{ new Long(1073741875L), DataType.LONG, "ENABLE_SPOT_BLEACH_POS" },
		{ new Long(1073741876L), DataType.DOUBLE, "SPOT_BLEACH_POSX" },
		{ new Long(1073741877L), DataType.DOUBLE, "SPOT_BLEACH_POSY" },
		{ new Long(1073741878L), DataType.DOUBLE, "BLEACH_POSITION_Z" },
		{ new Long(1073741879L), DataType.STRING, "ID_TUBELENS" },
		{ new Long(1073741880L), DataType.STRING, "ID_TUBELENS_POSITION" },
		{ new Long(1073741881L), DataType.DOUBLE, "TRANSMITTED_LIGHT" },
		{ new Long(1073741882L), DataType.DOUBLE, "REFLECTED_LIGHT" },
		{ new Long(1073741883L), DataType.LONG, "TRACK_SIMULTAN_GRAB_AND_BLEACH" },
		{ new Long(1073741884L), DataType.DOUBLE, "BLEACH_PIXEL_TIME" } };
	public BeamSplitter[] beamSplitters;
	public DataChannel[] dataChannels;
	public DetectionChannel[] detectionChannels;
	public IlluminationChannel[] illuminationChannels;

	public static boolean isTracks(final long tagEntry) {
		if (tagEntry == 536870912L) {
			return true;
		}
		return false;
	}

	public static boolean isTrack(final long tagEntry) {
		if (tagEntry == 1073741824L) {
			return true;
		}
		return false;
	}
}
