
package org.imagearchive.lsm.toolbox.info.scaninfo;

import java.util.LinkedHashMap;

public class DetectionChannel {

	public LinkedHashMap<String, Object> records = new LinkedHashMap();

	public Object[][] data = {
		{ new Long(1879048195L), DataType.DOUBLE, "DETECTOR_GAIN" },
		{ new Long(1879048197L), DataType.DOUBLE, "AMPLIFIER_GAIN" },
		{ new Long(1879048199L), DataType.DOUBLE, "AMPLIFIER_OFFSET" },
		{ new Long(1879048201L), DataType.DOUBLE, "PINHOLE_DIAMETER" },
		{ new Long(1879048203L), DataType.LONG, "ACQUIRE" },
		{ new Long(1879048204L), DataType.STRING, "DETECTOR_NAME" },
		{ new Long(1879048205L), DataType.STRING, "AMPLIFIER_NAME" },
		{ new Long(1879048206L), DataType.STRING, "PINHOLE_NAME" },
		{ new Long(1879048207L), DataType.STRING, "FILTER_SET_NAME" },
		{ new Long(1879048208L), DataType.STRING, "FILTER_NAME" },
		{ new Long(1879048211L), DataType.STRING, "INTEGRATOR_NAME" },
		{ new Long(1879048212L), DataType.STRING, "DETECTION_CHANNEL_NAME" },
		{ new Long(1879048213L), DataType.DOUBLE, "DETECTOR_GAIN_BC1" },
		{ new Long(1879048214L), DataType.DOUBLE, "DETECTOR_GAIN_BC2" },
		{ new Long(1879048215L), DataType.DOUBLE, "AMPLIFIER_GAIN_BC1" },
		{ new Long(1879048216L), DataType.DOUBLE, "AMPLIFIER_GAIN_BC2" },
		{ new Long(1879048217L), DataType.DOUBLE, "AMPLIFIER_OFFSET_BC1" },
		{ new Long(1879048224L), DataType.DOUBLE, "AMPLIFIER_OFFSET_BC2" },
		{ new Long(1879048225L), DataType.LONG, "SPECTRAL_SCAN_CHANNELS" },
		{ new Long(1879048226L), DataType.DOUBLE, "SPI_WAVE_LENGTH_START" },
		{ new Long(1879048227L), DataType.DOUBLE, "SPI_WAVELENGTH_END" },
		{ new Long(1879048230L), DataType.STRING, "DYE_NAME" },
		{ new Long(1879048231L), DataType.STRING, "DYE_FOLDER" } };

	public static boolean isDetectionChannels(final long tagEntry) {
		if (tagEntry == 1610612736L) {
			return true;
		}
		return false;
	}

	public static boolean isDetectionChannel(final long tagEntry) {
		if (tagEntry == 1879048192L) {
			return true;
		}
		return false;
	}
}
