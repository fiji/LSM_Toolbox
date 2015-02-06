
package org.imagearchive.lsm.toolbox;

import ij.IJ;
import ij.ImagePlus;
import ij.io.OpenDialog;
import ij.io.RandomAccessStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.swing.JFileChooser;

import org.imagearchive.lsm.reader.info.ChannelNamesAndColors;
import org.imagearchive.lsm.reader.info.ImageDirectory;
import org.imagearchive.lsm.reader.info.LSMFileInfo;
import org.imagearchive.lsm.toolbox.gui.AllKnownFilter;
import org.imagearchive.lsm.toolbox.gui.BatchFilter;
import org.imagearchive.lsm.toolbox.gui.ImageFilter;
import org.imagearchive.lsm.toolbox.gui.ImagePreview;
import org.imagearchive.lsm.toolbox.info.CZLSMInfoExtended;
import org.imagearchive.lsm.toolbox.info.ChannelWavelengthRange;
import org.imagearchive.lsm.toolbox.info.Event;
import org.imagearchive.lsm.toolbox.info.EventList;
import org.imagearchive.lsm.toolbox.info.TimeStamps;
import org.imagearchive.lsm.toolbox.info.scaninfo.BeamSplitter;
import org.imagearchive.lsm.toolbox.info.scaninfo.DataChannel;
import org.imagearchive.lsm.toolbox.info.scaninfo.DetectionChannel;
import org.imagearchive.lsm.toolbox.info.scaninfo.IlluminationChannel;
import org.imagearchive.lsm.toolbox.info.scaninfo.Laser;
import org.imagearchive.lsm.toolbox.info.scaninfo.Marker;
import org.imagearchive.lsm.toolbox.info.scaninfo.Recording;
import org.imagearchive.lsm.toolbox.info.scaninfo.ScanInfo;
import org.imagearchive.lsm.toolbox.info.scaninfo.Timer;
import org.imagearchive.lsm.toolbox.info.scaninfo.Track;

public class Reader {

	private final MasterModel masterModel;

	public Reader() {
		this.masterModel = MasterModel.getMasterModel();
	}

	public static CZLSMInfoExtended getCZ(final String filename) {
		final Reader reader = ServiceMediator.getReader();
		final ImagePlus imp = reader.open(filename, false);
		if (imp == null) return null;
		reader.updateMetadata(imp);
		final LSMFileInfo lsm = (LSMFileInfo) imp.getOriginalFileInfo();
		final ImageDirectory imDir = (ImageDirectory) lsm.imageDirectories.get(0);
		final CZLSMInfoExtended cz = (CZLSMInfoExtended) imDir.TIF_CZ_LSMINFO;
		return cz;
	}

	public ImagePlus open(final String arg, final boolean verbose) {
		File file = null;
		ImagePlus imp = null;
		if (arg.equals("")) {
			final JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(new BatchFilter());
			fc.addChoosableFileFilter(new ImageFilter());
			fc.addChoosableFileFilter(new AllKnownFilter());
			fc.setAcceptAllFileFilterUsed(false);
			fc.setAccessory(new ImagePreview(fc));
			fc.setName("Open Zeiss LSM image");
			final String directory = OpenDialog.getDefaultDirectory();
			if (directory != null) {
				final File directoryHandler = new File(directory);
				if ((directoryHandler != null) && (directoryHandler.isDirectory())) fc
					.setCurrentDirectory(directoryHandler);
			}
			final int returnVal = fc.showOpenDialog(null);
			if (returnVal == 0) {
				file = fc.getSelectedFile();
				if (file == null) {
					IJ.error("no file selected");
					return null;
				}
				if (file.getAbsolutePath().endsWith(".csv")) {
					final BatchConverter converter = new BatchConverter(this.masterModel);
					converter.convertBatchFile(file.getAbsolutePath());
					return null;
				}
			}
		}
		else {
			file = new File(arg);
		}
		if (file != null) {
			imp = open(file.getParent(), file.getName(), verbose, false);
			updateMetadata(imp);
			final LSMFileInfo openLSM = (LSMFileInfo) imp.getOriginalFileInfo();

			OpenDialog.setDefaultDirectory(file.getParent());
		}
		return imp;
	}

	public ImagePlus open(final String directory, final String filename,
		final boolean verbose, final boolean thumb)
	{
		ImagePlus imp = null;
		final org.imagearchive.lsm.reader.Reader r =
			new org.imagearchive.lsm.reader.Reader();
		imp = r.open(directory, filename, false, false);
		return imp;
	}

	private void printImDirData(final LSMFileInfo lsmFi) {
		for (int i = 0; i < lsmFi.imageDirectories.size(); i++) {
			System.err.println("Imdir " + i);
			System.err.println("=============\n");
			final ImageDirectory imDir =
				(ImageDirectory) lsmFi.imageDirectories.get(i);
			System.err.println("ImDir data:\n" + imDir.toString());
			if (imDir.TIF_CZ_LSMINFO != null) System.err.println("CZ-Info data:\n" +
				imDir.TIF_CZ_LSMINFO.toString());
			else System.err.println("CZ-Info data is null (not set)");
			System.err.println("=================================================");
		}
	}

	public void
		readMetadata(final RandomAccessStream stream, final ImagePlus imp)
	{
		if ((imp.getOriginalFileInfo() instanceof LSMFileInfo)) {
			final LSMFileInfo lsm = (LSMFileInfo) imp.getOriginalFileInfo();
			if (lsm.fullyRead) return;
			final ImageDirectory imDir = (ImageDirectory) lsm.imageDirectories.get(0);
			if (imDir == null) return;
			final long offset = imDir.TIF_CZ_LSMINFO_OFFSET;
			imDir.TIF_CZ_LSMINFO = getCZ_LSMINFO(stream, offset, false);
			lsm.imageDirectories.set(0, imDir);
			imp.setFileInfo(lsm);
		}
	}

	public void updateMetadata(final ImagePlus imp) {
		if (imp == null) return;
		if ((imp.getOriginalFileInfo() instanceof LSMFileInfo)) {
			final LSMFileInfo lsm = (LSMFileInfo) imp.getOriginalFileInfo();
			if (lsm.fullyRead) return;
			try {
				final RandomAccessFile file =
					new RandomAccessFile(new File(lsm.directory +
						System.getProperty("file.separator") + lsm.fileName), "r");
				final RandomAccessStream stream = new RandomAccessStream(file);
				final ImageDirectory imDir =
					(ImageDirectory) lsm.imageDirectories.get(0);
				if (imDir == null) return;
				final long offset = imDir.TIF_CZ_LSMINFO_OFFSET;
				imDir.TIF_CZ_LSMINFO = getCZ_LSMINFO(stream, offset, false);
				lsm.fullyRead = true;
				lsm.imageDirectories.set(0, imDir);
				imp.setFileInfo(lsm);
			}
			catch (final FileNotFoundException e) {
				IJ.error("Could not update metadata.");
			}
		}
	}

	public boolean isLSMfile(final RandomAccessStream stream) {
		boolean identifier = false;
		long ID = 0L;
		try {
			stream.seek(2);
			ID = ReaderToolkit.swap(stream.readShort());
			if (ID == 42L) identifier = true;
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		return identifier;
	}

	private CZLSMInfoExtended getCZ_LSMINFO(final RandomAccessStream stream,
		final long position, final boolean thumb)
	{
		final CZLSMInfoExtended cz = new CZLSMInfoExtended();
		try {
			if (position == 0L) return cz;
			stream.seek((int) position + 8);

			cz.DimensionX = ReaderToolkit.swap(stream.readInt());
			cz.DimensionY = ReaderToolkit.swap(stream.readInt());
			cz.DimensionZ = ReaderToolkit.swap(stream.readInt());

			cz.DimensionChannels = ReaderToolkit.swap(stream.readInt());

			cz.DimensionTime = ReaderToolkit.swap(stream.readInt());

			cz.IntensityDataType = ReaderToolkit.swap(stream.readInt());

			cz.ThumbnailX = ReaderToolkit.swap(stream.readInt());

			cz.ThumbnailY = ReaderToolkit.swap(stream.readInt());

			cz.VoxelSizeX = ReaderToolkit.swap(stream.readDouble());
			cz.VoxelSizeY = ReaderToolkit.swap(stream.readDouble());
			cz.VoxelSizeZ = ReaderToolkit.swap(stream.readDouble());

			cz.OriginX = ReaderToolkit.swap(stream.readDouble());
			cz.OriginY = ReaderToolkit.swap(stream.readDouble());
			cz.OriginZ = ReaderToolkit.swap(stream.readDouble());

			cz.ScanType = ReaderToolkit.swap(stream.readShort());
			cz.SpectralScan = ReaderToolkit.swap(stream.readShort());
			cz.DataType = ReaderToolkit.swap(stream.readInt());
			cz.OffsetVectorOverlay = ReaderToolkit.swap(stream.readInt());
			cz.OffsetInputLut = ReaderToolkit.swap(stream.readInt());
			cz.OffsetOutputLut = ReaderToolkit.swap(stream.readInt());
			cz.OffsetChannelColors = ReaderToolkit.swap(stream.readInt());
			cz.TimeIntervall = ReaderToolkit.swap(stream.readDouble());

			cz.OffsetChannelDataTypes = ReaderToolkit.swap(stream.readInt());

			cz.OffsetScanInformation = ReaderToolkit.swap(stream.readInt());
			cz.OffsetKsData = ReaderToolkit.swap(stream.readInt());
			cz.OffsetTimeStamps = ReaderToolkit.swap(stream.readInt());
			cz.OffsetEventList = ReaderToolkit.swap(stream.readInt());
			cz.OffsetRoi = ReaderToolkit.swap(stream.readInt());
			cz.OffsetBleachRoi = ReaderToolkit.swap(stream.readInt());
			cz.OffsetNextRecording = ReaderToolkit.swap(stream.readInt());

			cz.DisplayAspectX = ReaderToolkit.swap(stream.readDouble());
			cz.DisplayAspectY = ReaderToolkit.swap(stream.readDouble());
			cz.DisplayAspectZ = ReaderToolkit.swap(stream.readDouble());
			cz.DisplayAspectTime = ReaderToolkit.swap(stream.readDouble());

			cz.OffsetMeanOfRoisOverlay = ReaderToolkit.swap(stream.readInt());
			cz.OffsetTopoIsolineOverlay = ReaderToolkit.swap(stream.readInt());
			cz.OffsetTopoProfileOverlay = ReaderToolkit.swap(stream.readInt());
			cz.OffsetLinescanOverlay = ReaderToolkit.swap(stream.readInt());

			cz.ToolbarFlags = ReaderToolkit.swap(stream.readInt());
			cz.OffsetChannelWavelength = ReaderToolkit.swap(stream.readInt());
			cz.OffsetChannelFactors = ReaderToolkit.swap(stream.readInt());
			cz.ObjectiveSphereCorrection = ReaderToolkit.swap(stream.readInt());
			cz.OffsetUnmixParameters = ReaderToolkit.swap(stream.readInt());

			if (cz.OffsetChannelDataTypes != 0L) {
				cz.OffsetChannelDataTypesValues =
					getOffsetChannelDataTypesValues(stream, cz.OffsetChannelDataTypes,
						cz.DimensionChannels);
			}
			if (cz.OffsetChannelColors != 0L) {
				final ChannelNamesAndColors channelNamesAndColors =
					getChannelNamesAndColors(stream, cz.OffsetChannelColors,
						cz.DimensionChannels);
				cz.channelNamesAndColors = channelNamesAndColors;
			}
			if (cz.OffsetChannelWavelength != 0L) {
				cz.channelWavelength =
					getLambdaStamps(stream, cz.OffsetChannelWavelength);
			}

			if (cz.OffsetTimeStamps != 0L) {
				cz.timeStamps = getTimeStamps(stream, cz.OffsetTimeStamps);
				if (((cz.ScanType == 3) || (cz.ScanType == 4) || (cz.ScanType == 5) ||
					(cz.ScanType == 6) || (cz.ScanType == 9) || (cz.ScanType == 10)) &&
					(cz.OffsetEventList != 0L))
				{
					cz.eventList =
						getEventList(stream, cz.OffsetEventList,
							cz.timeStamps.FirstTimeStamp);
				}
			}
			if ((cz.OffsetScanInformation != 0L) && (!thumb)) cz.scanInfo =
				getScanInfo(stream, cz.OffsetScanInformation);
		}
		catch (final IOException getCZ_LSMINFO_exception) {
			getCZ_LSMINFO_exception.printStackTrace();
		}
		return cz;
	}

	private int[] getOffsetChannelDataTypesValues(
		final RandomAccessStream stream, final long position,
		final long channelCount)
	{
		final int[] OffsetChannelDataTypesValues = new int[(int) channelCount];
		try {
			stream.seek((int) position);

			for (int i = 0; i < channelCount; i++)
				OffsetChannelDataTypesValues[i] = ReaderToolkit.swap(stream.readInt());
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		return OffsetChannelDataTypesValues;
	}

	private ChannelNamesAndColors getChannelNamesAndColors(
		final RandomAccessStream stream, final long position,
		final long channelCount)
	{
		final ChannelNamesAndColors channelNamesAndColors =
			new ChannelNamesAndColors();
		try {
			stream.seek((int) position);
			channelNamesAndColors.BlockSize = ReaderToolkit.swap(stream.readInt());
			channelNamesAndColors.NumberColors = ReaderToolkit.swap(stream.readInt());
			channelNamesAndColors.NumberNames = ReaderToolkit.swap(stream.readInt());
			channelNamesAndColors.ColorsOffset = ReaderToolkit.swap(stream.readInt());
			channelNamesAndColors.NamesOffset = ReaderToolkit.swap(stream.readInt());
			channelNamesAndColors.Mono = ReaderToolkit.swap(stream.readInt());

			stream.seek((int) channelNamesAndColors.NamesOffset + (int) position);
			channelNamesAndColors.ChannelNames = new String[(int) channelCount];

			for (int j = 0; j < channelCount; j++) {
				final long size = ReaderToolkit.swap(stream.readInt());
				channelNamesAndColors.ChannelNames[j] =
					ReaderToolkit.readSizedNULLASCII(stream, size);
			}
			stream.seek((int) channelNamesAndColors.ColorsOffset + (int) position);
			channelNamesAndColors.Colors =
				new int[(int) channelNamesAndColors.NumberColors];

			for (int j = 0; j < (int) channelNamesAndColors.NumberColors; j++)
				channelNamesAndColors.Colors[j] = ReaderToolkit.swap(stream.readInt());
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		return channelNamesAndColors;
	}

	private TimeStamps getTimeStamps(final RandomAccessStream stream,
		final long position)
	{
		final TimeStamps timeStamps = new TimeStamps();
		try {
			stream.seek((int) position);
			timeStamps.Size = ReaderToolkit.swap(stream.readInt());
			timeStamps.NumberTimeStamps = ReaderToolkit.swap(stream.readInt());
			timeStamps.Stamps = new double[(int) timeStamps.NumberTimeStamps];
			timeStamps.TimeStamps = new double[(int) timeStamps.NumberTimeStamps];

			for (int i = 0; i < timeStamps.NumberTimeStamps; i++) {
				timeStamps.Stamps[i] = ReaderToolkit.swap(stream.readDouble());
			}
			for (int i = 1; i < timeStamps.NumberTimeStamps; i++) {
				timeStamps.TimeStamps[i] =
					(timeStamps.Stamps[i] - timeStamps.Stamps[0]);
			}
			timeStamps.FirstTimeStamp = timeStamps.Stamps[0];
			timeStamps.TimeStamps[0] = 0.0D;
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		return timeStamps;
	}

	private ChannelWavelengthRange getLambdaStamps(
		final RandomAccessStream stream, final long position)
	{
		final ChannelWavelengthRange channelWavelength =
			new ChannelWavelengthRange();
		try {
			stream.seek((int) position);
			channelWavelength.Channels = ReaderToolkit.swap(stream.readInt());
			channelWavelength.StartWavelength =
				new double[(int) channelWavelength.Channels];
			channelWavelength.EndWavelength =
				new double[(int) channelWavelength.Channels];
			channelWavelength.LambdaStamps =
				new double[(int) channelWavelength.Channels];
			for (int i = 0; i < channelWavelength.Channels; i++) {
				channelWavelength.StartWavelength[i] =
					ReaderToolkit.swap(stream.readDouble());
				channelWavelength.EndWavelength[i] =
					ReaderToolkit.swap(stream.readDouble());
				channelWavelength.LambdaStamps[i] =
					((channelWavelength.StartWavelength[i] + channelWavelength.EndWavelength[i]) / 2.0D);
			}
		}
		catch (final IOException getLAMBDASTAMPS_exception) {
			getLAMBDASTAMPS_exception.printStackTrace();
		}
		return channelWavelength;
	}

	private EventList getEventList(final RandomAccessStream stream,
		final long position, final double firstTimeStamp)
	{
		final EventList eventList = new EventList();
		String EventType = "";
		String EventDescription = "";
		String EventNote = "";
		int pointer = 0;
		try {
			stream.seek((int) position);
			eventList.Size = ReaderToolkit.swap(stream.readInt());
			eventList.NumberEvents = ReaderToolkit.swap(stream.readInt());
			eventList.events = new Event[(int) eventList.NumberEvents];
			pointer = stream.getFilePointer();
			if (eventList.NumberEvents > 0L) for (int i = 0; i < eventList.NumberEvents; i++)
			{
				eventList.events[i] = new Event();
				eventList.events[i].SizeEventListEntry =
					ReaderToolkit.swap(stream.readInt());
				eventList.events[i].Time = ReaderToolkit.swap(stream.readDouble());
				eventList.events[i].EventType = ReaderToolkit.swap(stream.readInt());
				switch ((int) eventList.events[i].EventType) {
					case 0:
						EventType = "Marker";
						break;
					case 1:
						EventType = "Timer Change";
						break;
					case 2:
						EventType = "Bleach Start";
						break;
					case 3:
						EventType = "Bleach Stop";
						break;
					case 4:
						EventType = "Trigger";
						break;
					default:
						EventType = "Unknown";
				}

				ReaderToolkit.swap(stream.readInt());
				EventDescription =
					EventDescription + IJ.d2s(eventList.events[i].Time - firstTimeStamp) +
						"\t" + EventType + "\t";
				EventNote =
					ReaderToolkit.readNULLASCII2(stream,
						eventList.events[i].SizeEventListEntry - 16L);
				pointer += (int) eventList.events[i].SizeEventListEntry;
				stream.seek(pointer);
				EventDescription = EventDescription + EventNote + "\n";
			}
			eventList.Description = EventDescription;
		}
		catch (final IOException getEVENTLIST_exception) {
			IJ.log("IOException \nLast Offset: " + IJ.d2s(position, 0));
			getEVENTLIST_exception.printStackTrace();
		}
		return eventList;
	}

	private ScanInfo getScanInfo(final RandomAccessStream stream,
		final long position)
	{
		final ScanInfo scanInfo = new ScanInfo();
		ScanInfoTag tag = new ScanInfoTag();
		try {
			stream.seek((int) position);
			while (tag.entry != -1L) {
				tag = getScanInfoTag(stream);
				if (Recording.isRecording(tag.entry)) {
					final Recording recording = new Recording();
					while (tag.entry != -1L) {
						tag = getScanInfoTag(stream);
						if (Laser.isLasers(tag.entry)) {
							recording.lasers = getLaserBlock(stream);
							tag.entry = 0L;
						}
						if (Track.isTracks(tag.entry)) {
							recording.tracks = getTrackBlock(stream);
							tag.entry = 0L;
						}
						if (Marker.isMarkers(tag.entry)) {
							recording.markers = getMarkerBlock(stream);
							tag.entry = 0L;
						}
						if (Timer.isTimers(tag.entry)) {
							recording.timers = getTimerBlock(stream);
							tag.entry = 0L;
						}
						recording.records =
							getRecords(stream, tag, Recording.data, recording.records);
					}
					scanInfo.recordings.add(recording);
				}
			}
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		return scanInfo;
	}

	private Laser[] getLaserBlock(final RandomAccessStream stream) {
		ScanInfoTag tag = new ScanInfoTag();
		if (IJ.debugMode) IJ.log("Lasers");
		final Vector v = new Vector();
		while (tag.entry != -1L) {
			tag = getScanInfoTag(stream);
			if (Laser.isLaser(tag.entry)) {
				final Laser l = new Laser();
				while (tag.entry != -1L) {
					tag = getScanInfoTag(stream);
					if (IJ.debugMode) IJ.log("Lasertag: " + Long.toHexString(tag.entry));
					if (tag.entry != -1L) l.records =
						getRecords(stream, tag, l.data, l.records);
				}
				v.add(l);
				tag.entry = 0L;
			}

		}

		return (Laser[]) v.toArray(new Laser[v.size()]);
	}

	private IlluminationChannel[] getIlluminationBlock(
		final RandomAccessStream stream)
	{
		ScanInfoTag tag = new ScanInfoTag();
		final Vector vdc = new Vector();
		while (tag.entry != -1L) {
			tag = getScanInfoTag(stream);

			if (IlluminationChannel.isIlluminationChannel(tag.entry)) {
				final IlluminationChannel ic = new IlluminationChannel();

				while (tag.entry != -1L) {
					tag = getScanInfoTag(stream);
					ic.records = getRecords(stream, tag, ic.data, ic.records);
				}
				vdc.add(ic);
				tag.entry = 0L;
			}
		}

		return (IlluminationChannel[]) vdc.toArray(new IlluminationChannel[vdc
			.size()]);
	}

	private Track[] getTrackBlock(final RandomAccessStream stream) {
		ScanInfoTag tag = new ScanInfoTag();
		final Vector v = new Vector();
		int tracksnum = 0;
		while (tag.entry != -1L) {
			tag = getScanInfoTag(stream);
			if (Track.isTrack(tag.entry)) {
				tracksnum++;
				final Track t = new Track();
				while (tag.entry != -1L) {
					tag = getScanInfoTag(stream);

					if (IJ.debugMode) {
						IJ.log("Tracktag: " + Long.toHexString(tag.entry));
					}
					if (IlluminationChannel.isIlluminationChannels(tag.entry)) {
						t.illuminationChannels = getIlluminationBlock(stream);
						tag.entry = 0L;
					}
					if (DetectionChannel.isDetectionChannels(tag.entry)) {
						t.detectionChannels = getDetectionChannelBlock(stream);
						tag.entry = 0L;
					}
					if (BeamSplitter.isBeamSplitters(tag.entry)) {
						t.beamSplitters = getBeamSplitterBlock(stream);
						tag.entry = 0L;
					}
					if (DataChannel.isDataChannels(tag.entry)) {
						t.dataChannels = getDataChannelBlock(stream);
						tag.entry = 0L;
					}

					if (tag.entry != -1L) {
						t.records = getRecords(stream, tag, t.data, t.records);
					}
				}
				v.add(t);
				tag.entry = 0L;
			}
		}
		return (Track[]) v.toArray(new Track[v.size()]);
	}

	private DetectionChannel[] getDetectionChannelBlock(
		final RandomAccessStream stream)
	{
		ScanInfoTag tag = new ScanInfoTag();
		final Vector vdc = new Vector();
		while (tag.entry != -1L) {
			tag = getScanInfoTag(stream);
			if (DetectionChannel.isDetectionChannel(tag.entry)) {
				final DetectionChannel ic = new DetectionChannel();
				while (tag.entry != -1L) {
					tag = getScanInfoTag(stream);
					ic.records = getRecords(stream, tag, ic.data, ic.records);
				}
				vdc.add(ic);
				tag.entry = 0L;
			}
		}
		final DetectionChannel[] dc = new DetectionChannel[vdc.size()];
		for (int i = 0; i < vdc.size(); i++) {
			dc[i] = ((DetectionChannel) vdc.get(i));
		}
		return dc;
	}

	private BeamSplitter[] getBeamSplitterBlock(final RandomAccessStream stream) {
		ScanInfoTag tag = new ScanInfoTag();
		final Vector vdc = new Vector();
		while (tag.entry != -1L) {
			tag = getScanInfoTag(stream);
			if (BeamSplitter.isBeamSplitter(tag.entry)) {
				final BeamSplitter ic = new BeamSplitter();
				while (tag.entry != -1L) {
					tag = getScanInfoTag(stream);
					ic.records = getRecords(stream, tag, ic.data, ic.records);
				}
				vdc.add(ic);
				tag.entry = 0L;
			}
		}

		return (BeamSplitter[]) vdc.toArray(new BeamSplitter[vdc.size()]);
	}

	private Marker[] getMarkerBlock(final RandomAccessStream stream) {
		ScanInfoTag tag = new ScanInfoTag();
		final Vector v = new Vector();
		while (tag.entry != -1L) {
			tag = getScanInfoTag(stream);
			if (Marker.isMarker(tag.entry)) {
				final Marker m = new Marker();
				while (tag.entry != -1L) {
					tag = getScanInfoTag(stream);
					m.records = getRecords(stream, tag, m.data, m.records);
				}
				v.add(m);
				tag.entry = 0L;
			}
		}
		return (Marker[]) v.toArray(new Marker[v.size()]);
	}

	private Timer[] getTimerBlock(final RandomAccessStream stream) {
		ScanInfoTag tag = new ScanInfoTag();
		final Vector v = new Vector();
		while (tag.entry != -1L) {
			tag = getScanInfoTag(stream);
			if (Timer.isTimer(tag.entry)) {
				final Timer t = new Timer();

				while (tag.entry != -1L) {
					tag = getScanInfoTag(stream);
					t.records = getRecords(stream, tag, t.data, t.records);
				}
				v.add(t);
				tag.entry = 0L;
			}
		}
		return (Timer[]) v.toArray(new Timer[v.size()]);
	}

	private DataChannel[] getDataChannelBlock(final RandomAccessStream stream) {
		ScanInfoTag tag = new ScanInfoTag();
		final Vector vdc = new Vector();

		while (tag.entry != -1L) {
			tag = getScanInfoTag(stream);
			if (DataChannel.isDataChannel(tag.entry)) {
				final DataChannel ic = new DataChannel();
				while (tag.entry != -1L) {
					tag = getScanInfoTag(stream);
					ic.records = getRecords(stream, tag, ic.data, ic.records);
				}
				vdc.add(ic);
				tag.entry = 0L;
			}
		}

		return (DataChannel[]) vdc.toArray(new DataChannel[vdc.size()]);
	}

	private LinkedHashMap<String, Object> getRecords(
		final RandomAccessStream stream, final ScanInfoTag tag,
		final Object[][] data, final LinkedHashMap<String, Object> lhm)
	{
		try {
			String value = "";
			long l = 0L;
			double d = 0.0D;
			final long position = stream.getFilePointer();
			if (tag.type == 2L) value =
				ReaderToolkit.readSizedNULLASCII(stream, tag.size);
			if (tag.type == 4L) l = ReaderToolkit.swap(stream.readInt());
			if (tag.type == 5L) d = ReaderToolkit.swap(stream.readDouble());
			for (int i = 0; i < data.length; i++) {
				if (((Long) data[i][0]).longValue() == tag.entry) {
					if (tag.type == 2L) {
						if (IJ.debugMode) IJ.log("Tag recognized: [" +
							Long.toHexString(tag.entry) + "] -->" + (String) data[i][2]);
						lhm.put((String) data[i][2], value);
						return lhm;
					}
					if (tag.type == 4L) {
						lhm.put((String) data[i][2], new Long(l));
						if (IJ.debugMode) IJ.log("Tag recognized: [" +
							Long.toHexString(tag.entry) + "] -->" + (String) data[i][2]);
						return lhm;
					}
					if (tag.type == 5L) {
						lhm.put((String) data[i][2], new Double(d));
						if (IJ.debugMode) IJ.log("Tag recognized: [" +
							Long.toHexString(tag.entry) + "] -->" + (String) data[i][2]);
						return lhm;
					}
				}
			}
			if (tag.type == 2L) lhm.put("<UNKNOWN@" + (position - 12L) + ">", value);
			if (tag.type == 4L) lhm.put("<UNKNOWN@" + (position - 12L) + ">",
				new Long(l));
			if (tag.type == 5L) lhm.put("<UNKNOWN@" + (position - 12L) + ">",
				new Double(d));
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		return lhm;
	}

	private ScanInfoTag getScanInfoTag(final RandomAccessStream stream) {
		final ScanInfoTag sit = new ScanInfoTag();
		try {
			final int s1 = stream.read();
			final int s2 = stream.read();
			final int s3 = stream.read();
			final int s4 = stream.read();
			sit.entry = ((s4 << 24) + (s2 << 16) + (s3 << 8) + s1);
			sit.type = ReaderToolkit.swap(stream.readInt());
			sit.size = ReaderToolkit.swap(stream.readInt());
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		if (IJ.debugMode) IJ.log("Tag read: [" + Long.toHexString(sit.entry) + "]");
		return sit;
	}

	private class ScanInfoTag {

		public long entry = 0L;

		public long type = 0L;

		public long size = 0L;

		private ScanInfoTag() {}
	}
}
