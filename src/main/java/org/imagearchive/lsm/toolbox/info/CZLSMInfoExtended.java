
package org.imagearchive.lsm.toolbox.info;

import org.imagearchive.lsm.reader.info.CZLSMInfo;
import org.imagearchive.lsm.toolbox.info.scaninfo.ScanInfo;

public class CZLSMInfoExtended extends CZLSMInfo {

	public long MagicNumber = 0L;

	public long StructureSize = 0L;

	public double OriginX = 0.0D;

	public double OriginY = 0.0D;

	public double OriginZ = 0.0D;

	public int SpectralScan = 0;

	public long DataType = 0L;

	public long OffsetVectorOverlay = 0L;

	public long OffsetInputLut = 0L;

	public long OffsetOutputLut = 0L;

	public double TimeIntervall = 0.0D;

	public long OffsetScanInformation = 0L;

	public long OffsetKsData = 0L;

	public long OffsetTimeStamps = 0L;

	public long OffsetEventList = 0L;

	public long OffsetRoi = 0L;

	public long OffsetBleachRoi = 0L;

	public long OffsetNextRecording = 0L;

	public double DisplayAspectX = 0.0D;

	public double DisplayAspectY = 0.0D;

	public double DisplayAspectZ = 0.0D;

	public double DisplayAspectTime = 0.0D;

	public long OffsetMeanOfRoisOverlay = 0L;

	public long OffsetTopoIsolineOverlay = 0L;

	public long OffsetTopoProfileOverlay = 0L;

	public long OffsetLinescanOverlay = 0L;

	public long ToolbarFlags = 0L;

	public long OffsetChannelWavelength = 0L;

	public long OffsetChannelFactors = 0L;

	public double ObjectiveSphereCorrection = 0.0D;

	public long OffsetUnmixParameters = 0L;
	public long[] Reserved;
	public TimeStamps timeStamps;
	public ChannelWavelengthRange channelWavelength;
	public EventList eventList;
	public ScanInfo scanInfo;

	@Override
	public String toString() {
		return new String("DimensionX:  " + this.DimensionX + "\n" +
			"DimensionY:  " + this.DimensionY + "\n" + "DimensionZ:  " +
			this.DimensionZ + "\n" + "DimensionChannels:  " + this.DimensionChannels +
			"\n" + "ScanType:  " + getScanTypeText(this.ScanType) + "(" +
			this.ScanType + ")\n" + "DataType:  " + this.DataType);
	}

	public String getScanTypeText(final int scanType) {
		switch (scanType) {
			case 0:
				return "normal x-y-z-scan";
			case 1:
				return "z-Scan (x-z-plane)";
			case 2:
				return "line scan";
			case 3:
				return "time series x-y";
			case 4:
				return "time series x-z (release 2.0  or later)";
			case 5:
				return "time series ?Mean of ROIs?";
			case 6:
				return "time series x-y-z";
			case 7:
				return "spline scan";
			case 8:
				return "spline plane x-z";
			case 9:
				return "time series spline plane x-z";
			case 10:
				return "point mode";
		}
		return "normal x-y-z-scan";
	}
}
