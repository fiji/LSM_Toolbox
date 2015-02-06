
package org.imagearchive.lsm.toolbox;

import ij.ImagePlus;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.imagearchive.lsm.reader.info.ImageDirectory;
import org.imagearchive.lsm.reader.info.LSMFileInfo;
import org.imagearchive.lsm.toolbox.info.CZLSMInfoExtended;
import org.imagearchive.lsm.toolbox.info.scaninfo.DataType;
import org.imagearchive.lsm.toolbox.info.scaninfo.Recording;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DomXmlExporter {

	private Document doc;
	private static String comment =
		"Created with LSM_Toolbox v4.0g (C) Patrick Pirrotte 2008";

	public String getXML(final String filename, final boolean filter) {
		final Reader reader = ServiceMediator.getReader();
		final ImagePlus imp = reader.open(filename, false);
		reader.updateMetadata(imp);
		final LSMFileInfo lsm = (LSMFileInfo) imp.getOriginalFileInfo();
		final ImageDirectory imDir = (ImageDirectory) lsm.imageDirectories.get(0);
		final CZLSMInfoExtended cz = (CZLSMInfoExtended) imDir.TIF_CZ_LSMINFO;
		return buildTree(cz, filter);
	}

	public String buildTree(final CZLSMInfoExtended cz, final boolean filter) {
		try {
			final ArrayList recordings = cz.scanInfo.recordings;
			final DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			this.doc = docBuilder.newDocument();
			this.doc.appendChild(this.doc.createComment(comment));
			final Element root =
				this.doc.createElement("org.imagearchive.lsm.toolbox");

			final Element child = this.doc.createElement("CarlZeiss");
			addNode(child, "DimensionX", DataType.LONG, Long.valueOf(cz.DimensionX));
			addNode(child, "DimensionY", DataType.LONG, Long.valueOf(cz.DimensionY));
			addNode(child, "DimensionZ", DataType.LONG, Long.valueOf(cz.DimensionZ));
			addNode(child, "DimensionChannels", DataType.LONG, Long
				.valueOf(cz.DimensionChannels));
			addNode(child, "DimensionTime", DataType.LONG, Long
				.valueOf(cz.DimensionTime));
			addNode(child, "IntensityDataType", DataType.LONG, Long
				.valueOf(cz.IntensityDataType));
			addNode(child, "ThumbnailX", DataType.LONG, Long.valueOf(cz.ThumbnailX));
			addNode(child, "ThumbnailY", DataType.LONG, Long.valueOf(cz.ThumbnailY));
			addNode(child, "VoxelSizeX", DataType.DOUBLE, Double
				.valueOf(cz.VoxelSizeX));
			addNode(child, "VoxelSizeY", DataType.DOUBLE, Double
				.valueOf(cz.VoxelSizeY));
			addNode(child, "VoxelSizeZ", DataType.DOUBLE, Double
				.valueOf(cz.VoxelSizeZ));
			addNode(child, "OriginX", DataType.DOUBLE, Double.valueOf(cz.OriginX));
			addNode(child, "OriginY", DataType.DOUBLE, Double.valueOf(cz.OriginY));
			addNode(child, "OriginZ", DataType.DOUBLE, Double.valueOf(cz.OriginZ));
			addNode(child, "ScanType", DataType.INTEGER, Double.valueOf(cz.OriginZ));
			addNode(child, "SpectralScan", DataType.INTEGER, Integer
				.valueOf(cz.SpectralScan));
			addNode(child, "DataType", DataType.LONG, Long.valueOf(cz.DataType));
			addNode(child, "TimeIntervall", DataType.DOUBLE, Double
				.valueOf(cz.TimeIntervall));
			addNode(child, "DisplayAspectX", DataType.DOUBLE, Double
				.valueOf(cz.DisplayAspectX));
			addNode(child, "DisplayAspectY", DataType.DOUBLE, Double
				.valueOf(cz.DisplayAspectY));
			addNode(child, "DisplayAspectZ", DataType.DOUBLE, Double
				.valueOf(cz.DisplayAspectZ));
			addNode(child, "DisplayAspectTime", DataType.DOUBLE, Double
				.valueOf(cz.DisplayAspectTime));
			addNode(child, "ToolbarFlags", DataType.DOUBLE, Double
				.valueOf(cz.OriginZ));

			root.appendChild(child);

			for (int i = 0; i < recordings.size(); i++) {
				final Recording recording = (Recording) recordings.get(i);
				Element rec = this.doc.createElement("Recording");
				rec = populateNode(rec, Recording.data, recording.records);
				if (recording.lasers != null) {
					final Element las = this.doc.createElement("Lasers");
					for (int j = 0; j < recording.lasers.length; j++) {
						final LinkedHashMap records = recording.lasers[j].records;
						final LinkedHashMap m = records;
						final Object o = m.get("LASER_ACQUIRE");
						if ((filter) && (o != null)) {
							if (!(o != null & o.toString().equals("-1"))) ;
						}
						else {
							Element lasNode = this.doc.createElement("Laser");
							lasNode =
								populateNode(lasNode, recording.lasers[j].data, records);
							las.appendChild(lasNode);
						}
					}
					rec.appendChild(las);
				}

				if (recording.tracks != null) {
					final Element tra = this.doc.createElement("Tracks");
					for (int j = 0; j < recording.tracks.length; j++) {
						Element traNode = this.doc.createElement("Track");
						traNode =
							populateNode(traNode, recording.tracks[j].data,
								recording.tracks[j].records);
						if ((recording.tracks[j].detectionChannels != null) &&
							(recording.tracks[j].detectionChannels.length > 0))
						{
							final Element cha = this.doc.createElement("DetectionChannels");
							for (int k = 0; k < recording.tracks[j].detectionChannels.length; k++)
							{
								final LinkedHashMap m =
									recording.tracks[j].detectionChannels[k].records;
								final Object o = m.get("ACQUIRE");
								if ((filter) && (o != null)) {
									if (!(o != null & o.toString().equals("-1"))) ;
								}
								else {
									Element chaNode = this.doc.createElement("DetectionChannel");
									chaNode =
										populateNode(chaNode,
											recording.tracks[j].detectionChannels[k].data,
											recording.tracks[j].detectionChannels[k].records);
									cha.appendChild(chaNode);
								}
							}
							traNode.appendChild(cha);
						}

						if ((recording.tracks[j].illuminationChannels != null) &&
							(recording.tracks[j].illuminationChannels.length > 0))
						{
							final Element ill =
								this.doc.createElement("IlluminationChannels");
							for (int k = 0; k < recording.tracks[j].illuminationChannels.length; k++)
							{
								final LinkedHashMap m =
									recording.tracks[j].illuminationChannels[k].records;
								final Object o = m.get("ACQUIRE");
								if ((filter) && (o != null)) {
									if (!(o != null & o.toString().equals("-1"))) ;
								}
								else {
									Element illNode =
										this.doc.createElement("IlluminationChannel");
									illNode =
										populateNode(illNode,
											recording.tracks[j].illuminationChannels[k].data,
											recording.tracks[j].illuminationChannels[k].records);
									ill.appendChild(illNode);
								}
							}
							traNode.appendChild(ill);
						}

						if ((recording.tracks[j].beamSplitters != null) &&
							(recording.tracks[j].beamSplitters.length > 0))
						{
							final Element bsp = this.doc.createElement("BeamSplitters");
							for (int k = 0; k < recording.tracks[j].beamSplitters.length; k++)
							{
								Element bspNode = this.doc.createElement("BeamSplitter");
								bspNode =
									populateNode(bspNode,
										recording.tracks[j].beamSplitters[k].data,
										recording.tracks[j].beamSplitters[k].records);
								bsp.appendChild(bspNode);
							}
							traNode.appendChild(bsp);
						}

						if ((recording.tracks[j].dataChannels != null) &&
							(recording.tracks[j].dataChannels.length > 0))
						{
							final Element dch = this.doc.createElement("DataChannels");
							for (int k = 0; k < recording.tracks[j].dataChannels.length; k++)
							{
								final LinkedHashMap m =
									recording.tracks[j].dataChannels[k].records;
								final Object o = m.get("ACQUIRE");
								if ((filter) && (o != null)) {
									if (!(o != null & o.toString().equals("-1"))) ;
								}
								else {
									Element dchNode = this.doc.createElement("DataChannel");
									dchNode =
										populateNode(dchNode,
											recording.tracks[j].dataChannels[k].data,
											recording.tracks[j].dataChannels[k].records);
									dch.appendChild(dchNode);
								}
							}
							traNode.appendChild(dch);
						}

						if (traNode.hasChildNodes()) tra.appendChild(traNode);
					}
					rec.appendChild(tra);
				}

				if (recording.markers != null) {
					final Element mar = this.doc.createElement("Markers");
					for (int j = 0; j < recording.markers.length; j++) {
						Element marNode = this.doc.createElement("Marker");
						marNode =
							populateNode(marNode, recording.markers[j].data,
								recording.markers[j].records);
						mar.appendChild(marNode);
					}
					rec.appendChild(mar);
				}
				if (recording.timers != null) {
					final Element tim = this.doc.createElement("Timers");
					for (int j = 0; j < recording.timers.length; j++) {
						Element timNode = this.doc.createElement("Timer");
						timNode =
							populateNode(timNode, recording.markers[j].data,
								recording.markers[j].records);
						tim.appendChild(timNode);
					}
					rec.appendChild(tim);
				}
				root.appendChild(rec);
			}

			this.doc.appendChild(root);

			final TransformerFactory transfac = TransformerFactory.newInstance();
			final Transformer trans = transfac.newTransformer();
			trans.setOutputProperty("omit-xml-declaration", "no");
			trans.setOutputProperty("indent", "yes");

			final StringWriter sw = new StringWriter();
			final StreamResult result = new StreamResult(sw);
			final DOMSource source = new DOMSource(this.doc);
			trans.transform(source, result);
			return sw.toString();
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Element populateNode(final Element pNode, final Object[][] data,
		final LinkedHashMap<String, Object> records)
	{
		for (int i = 0; i < data.length; i++) {
			final Object o = records.get(data[i][2]);
			if (o != null) {
				String s = "";
				if ((Enum) data[i][1] == DataType.STRING) s = o.toString();
				if ((Enum) data[i][1] == DataType.INTEGER) s =
					Integer.toString(((Integer) o).intValue());
				if ((Enum) data[i][1] == DataType.LONG) s =
					Long.toString(((Long) o).longValue());
				if ((Enum) data[i][1] == DataType.DOUBLE) {
					s = Double.toString(((Double) o).doubleValue());
				}
				final String id = Long.toString(((Long) data[i][0]).longValue());
				final Element node = this.doc.createElement((String) data[i][2]);

				node.setAttribute("id", id);
				node.setAttribute("type", ((Enum) data[i][1]).toString());
				node.appendChild(this.doc.createTextNode(s));
				pNode.appendChild(node);
			}
		}
		return pNode;
	}

	private Element addNode(final Element pNode, final String name, final Enum e,
		final Object value)
	{
		final Element node = this.doc.createElement(name);

		node.setAttribute("type", e.toString());

		node.appendChild(this.doc.createTextNode(value.toString()));
		pNode.appendChild(node);
		return pNode;
	}

	private String getDataType(final Enum e) {
		return e.toString();
	}
}
