
package org.imagearchive.lsm.toolbox;

import ij.IJ;
import ij.ImagePlus;

import org.imagearchive.lsm.reader.info.ImageDirectory;
import org.imagearchive.lsm.reader.info.LSMFileInfo;
import org.imagearchive.lsm.toolbox.info.CZLSMInfoExtended;
import org.imagearchive.lsm.toolbox.info.EventList;
import org.imagearchive.lsm.toolbox.info.scaninfo.Recording;

public class StampUtils {

	public static String getTStamps(final Reader reader, final ImagePlus imp) {
		reader.updateMetadata(imp);
		final LSMFileInfo openLSM = (LSMFileInfo) imp.getOriginalFileInfo();
		final CZLSMInfoExtended cz =
			(CZLSMInfoExtended) ((ImageDirectory) openLSM.imageDirectories.get(0)).TIF_CZ_LSMINFO;
		final int n = new Long(cz.timeStamps.NumberTimeStamps).intValue();
		final String[] stamps = new String[n];
		for (int k = 0; k < n; k++)
			stamps[k] = Double.toString(cz.timeStamps.TimeStamps[k]);
		return implode(stamps);
	}

	public static String getZStamps(final Reader reader, final ImagePlus imp) {
		reader.updateMetadata(imp);
		final LSMFileInfo openLSM = (LSMFileInfo) imp.getOriginalFileInfo();
		final CZLSMInfoExtended cz =
			(CZLSMInfoExtended) ((ImageDirectory) openLSM.imageDirectories.get(0)).TIF_CZ_LSMINFO;
		final Recording r = cz.scanInfo.recordings.get(0);
		final double planeSpacing =
			((Double) r.records.get("PLANE_SPACING")).doubleValue();
		double ps = 0.0D;
		final String[] stamps = new String[(int) cz.DimensionZ];
		for (int k = 0; k < cz.DimensionZ; k++) {
			stamps[k] = (IJ.d2s(ps, 2) + " " + MasterModel.micrometer);
			ps += planeSpacing;
		}
		return implode(stamps);
	}

	public static String getLStamps(final Reader reader, final ImagePlus imp) {
		reader.updateMetadata(imp);
		final LSMFileInfo openLSM = (LSMFileInfo) imp.getOriginalFileInfo();
		final CZLSMInfoExtended cz =
			(CZLSMInfoExtended) ((ImageDirectory) openLSM.imageDirectories.get(0)).TIF_CZ_LSMINFO;
		if (cz.SpectralScan != 1) {
			IJ.error("Image not issued from spectral scan. Lambda stamp obsolete!");
			return null;
		}
		final String[] stamps = new String[(int) cz.channelWavelength.Channels];
		for (int k = 0; k < cz.channelWavelength.Channels; k++) {
			stamps[k] = IJ.d2s(cz.channelWavelength.LambdaStamps[k], 2);
		}

		return implode(stamps);
	}

	public static String getEvents(final Reader reader, final ImagePlus imp) {
		reader.updateMetadata(imp);
		final LSMFileInfo openLSM = (LSMFileInfo) imp.getOriginalFileInfo();
		final CZLSMInfoExtended cz =
			(CZLSMInfoExtended) ((ImageDirectory) openLSM.imageDirectories.get(0)).TIF_CZ_LSMINFO;

		final EventList events = cz.eventList;
		final StringBuffer buffer = new StringBuffer();
		if (events != null) {
			buffer.append("Time (sec) \tEvent Type \tEvent Description");
			buffer.append(events.Description);
		}
		return buffer.toString();
	}

	private static String implode(final String[] input) {
		String result;
		if (input.length == 0) {
			result = "";
		}
		else {
			final StringBuffer sb = new StringBuffer();
			sb.append(input[0]);
			for (int i = 1; i < input.length; i++) {
				sb.append(",");
				sb.append(input[i]);
			}
			result = sb.toString();
		}
		return result;
	}
}
