
package org.imagearchive.lsm.toolbox.gui;

import ij.ImagePlus;
import ij.WindowManager;
import ij.text.TextWindow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.imagearchive.lsm.reader.info.ImageDirectory;
import org.imagearchive.lsm.reader.info.LSMFileInfo;
import org.imagearchive.lsm.toolbox.DomXmlExporter;
import org.imagearchive.lsm.toolbox.MasterModel;
import org.imagearchive.lsm.toolbox.Reader;
import org.imagearchive.lsm.toolbox.ServiceMediator;
import org.imagearchive.lsm.toolbox.info.CZLSMInfoExtended;
import org.imagearchive.lsm.toolbox.info.scaninfo.Recording;
import org.imagearchive.lsm.toolbox.info.scaninfo.ScanInfo;

public class DetailsFrame extends JFrame {

	private final MasterModel masterModel = MasterModel.getMasterModel();

	private final int detailsFrameXsize = 260;

	private final int detailsFrameYsize = 400;

	private final int detailsFrameXlocation = 0;

	private final int detailsFrameYlocation = 200;
	public JTree detailsTree;
	private JTable table;
	private JToolBar toolBar;
	private JButton exitButton;
	private JButton dumpButton;
	private JButton xmlButton;
	private JToggleButton filterButton;
	private JButton searchButton;
	private JTextField searchTF;
	private DefaultTreeModel treemodel;
	private TreeTableModel tablemodel;
	private JMenuItem expandAllItem;
	private JMenuItem collapseAllItem;
	private JCheckBoxMenuItem filterCBItem;
	private JPopupMenu detailsTreePopupMenu;
	private final String frameTitle = "Details";

	private final String title = "Image acquisition properties";

	private DefaultMutableTreeNode lastNodeResult = null;
	private Point searchCoordinates;
	private LSMFileInfo lsm = null;

	public DetailsFrame() throws HeadlessException {
		initializeGUI();
		ServiceMediator.registerDetailsFrame(this);
	}

	public void initializeGUI() {
		setTitle(this.frameTitle);
		setSize(this.detailsFrameXsize, this.detailsFrameYsize);
		setLocation(this.detailsFrameXlocation, this.detailsFrameYlocation);
		this.treemodel =
			new DefaultTreeModel(new DefaultMutableTreeNode("LSM File Information"));
		this.detailsTree = new JTree(this.treemodel);
		this.detailsTree.putClientProperty("JTree.lineStyle", "Angled");
		this.detailsTree.getSelectionModel().setSelectionMode(1);
		this.detailsTree.setShowsRootHandles(true);
		this.tablemodel = new TreeTableModel();
		this.table = new JTable(this.tablemodel);
		this.table.setCellSelectionEnabled(true);
		final JScrollPane treepane = new JScrollPane(this.detailsTree);
		final JScrollPane detailspane = new JScrollPane(this.table);
		final JSplitPane splitPane = new JSplitPane(1, treepane, detailspane);
		splitPane.setBorder(null);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(300);
		final Dimension minimumSize = new Dimension(1, -48);
		treepane.setMinimumSize(minimumSize);
		detailspane.setMinimumSize(minimumSize);
		this.exitButton =
			new JButton(new ImageIcon(getClass().getResource("images/exit.png")));
		this.exitButton.setToolTipText("Close this window");
		this.searchButton =
			new JButton(new ImageIcon(getClass().getResource("images/find.png")));
		this.searchButton.setToolTipText("Find tag, property or value");
		this.filterButton =
			new JToggleButton(new ImageIcon(getClass().getResource(
				"images/filter.png")));
		this.filterButton.setToolTipText("Filter unused tags");
		this.dumpButton =
			new JButton(new ImageIcon(getClass().getResource("images/dump.png")));
		this.dumpButton
			.setToolTipText("Dump data to textwindow, saving to text file is possible");
		this.xmlButton =
			new JButton(new ImageIcon(getClass().getResource("images/xml.png")));
		this.xmlButton
			.setToolTipText("Dump xml data to textwindow, saving to text file is possible");

		this.searchTF = new JTextField("");
		this.detailsTreePopupMenu = new JPopupMenu();
		this.expandAllItem =
			new JMenuItem("Expand all", new ImageIcon(getClass().getResource(
				"images/plus.png")));
		this.collapseAllItem =
			new JMenuItem("Collapse all", new ImageIcon(getClass().getResource(
				"images/minus.png")));
		this.filterCBItem =
			new JCheckBoxMenuItem("Filtered", new ImageIcon(getClass().getResource(
				"images/filter.png")));
		this.detailsTreePopupMenu.add(this.expandAllItem);
		this.detailsTreePopupMenu.add(this.collapseAllItem);
		this.detailsTreePopupMenu.add(new JSeparator());
		this.detailsTreePopupMenu.add(this.filterCBItem);
		this.detailsTreePopupMenu.setOpaque(true);
		this.detailsTree.add(this.detailsTreePopupMenu);
		this.detailsTree.setExpandsSelectedPaths(true);
		this.toolBar = new JToolBar();

		this.toolBar.add(this.exitButton);
		this.toolBar.add(this.dumpButton);
		this.toolBar.add(this.xmlButton);
		this.toolBar.add(this.filterButton);
		this.toolBar.add(new JSeparator());
		this.toolBar.add(new JLabel("  Search: "));
		this.toolBar.add(this.searchTF);
		this.toolBar.add(this.searchButton);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(this.toolBar, "North");
		getContentPane().add(splitPane, "Center");

		pack();
		centerWindow();
		setListeners();
	}

	public void setListeners() {
		addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowGainedFocus(final WindowEvent e) {
				DetailsFrame.this.updateTreeAndLabels();
			}

			@Override
			public void windowLostFocus(final WindowEvent e) {}
		});
		this.dumpButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				DetailsFrame.this.dumpData();
			}
		});
		this.xmlButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				DetailsFrame.this.dumpXmlData();
			}
		});
		this.expandAllItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				DetailsFrame.this.expandAll();
			}
		});
		this.collapseAllItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				DetailsFrame.this.collapseAll();
			}
		});
		this.detailsTree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (e.isPopupTrigger()) DetailsFrame.this.detailsTreePopupMenu.show(
					(JComponent) e.getSource(), e.getX(), e.getY());
			}
		});
		this.exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				DetailsFrame.this.dispose();
			}
		});
		this.filterCBItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent e) {
				DetailsFrame.this.filterButton
					.setSelected(DetailsFrame.this.filterCBItem.isSelected());
				DetailsFrame.this.updateTreeAndLabels();
			}
		});
		this.filterButton.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent e) {
				DetailsFrame.this.filterCBItem
					.setSelected(DetailsFrame.this.filterButton.isSelected());
				DetailsFrame.this.updateTreeAndLabels();
			}
		});
		this.searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (!DetailsFrame.this.searchTF.getText().equals("")) {
					DetailsFrame.this.lastNodeResult =
						DetailsFrame.this.findNode(DetailsFrame.this.searchTF.getText(),
							((DefaultMutableTreeNode) DetailsFrame.this.detailsTree
								.getModel().getRoot()).breadthFirstEnumeration(),
							DetailsFrame.this.lastNodeResult);
					if (DetailsFrame.this.lastNodeResult != null) {
						final TreePath tp =
							new TreePath(DetailsFrame.this.lastNodeResult.getPath());
						DetailsFrame.this.detailsTree.setSelectionPath(tp);
						DetailsFrame.this.detailsTree.scrollPathToVisible(tp);
						if (((DetailsFrame.this.lastNodeResult instanceof InfoNode)) &&
							(DetailsFrame.this.searchCoordinates != null))
						{
							DetailsFrame.this.table.changeSelection(
								DetailsFrame.this.searchCoordinates.x,
								DetailsFrame.this.searchCoordinates.y, false, false);
						}
					}
				}
			}
		});
		this.detailsTree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(final TreeSelectionEvent e) {
				if ((DetailsFrame.this.detailsTree.getLastSelectedPathComponent() instanceof InfoNode))
				{
					final InfoNode node =
						(InfoNode) DetailsFrame.this.detailsTree
							.getLastSelectedPathComponent();
					if (node == null) return;
					final Object nodeInfo = node.getUserObject();
					if (nodeInfo == null) return;
					if ((nodeInfo instanceof LinkedHashMap)) {
						final LinkedHashMap info = (LinkedHashMap) nodeInfo;
						((TreeTableModel) DetailsFrame.this.table.getModel()).setData(info);
					}
				}
				else {
					((TreeTableModel) DetailsFrame.this.table.getModel()).setData(null);
				}
			}
		});
		updateTreeAndLabels();
	}

	public DefaultMutableTreeNode findNode(String string,
		final Enumeration<DefaultMutableTreeNode> nodes,
		final DefaultMutableTreeNode last)
	{
		DefaultMutableTreeNode result = null;
		DefaultMutableTreeNode node = null;
		Object[][] data = null;
		string = string.toLowerCase();
		if (last != null) {
			if ((last instanceof InfoNode)) {
				final InfoNode info = (InfoNode) last;
				LinkedHashMap dataMap = (LinkedHashMap) info.data;
				if (this.filterCBItem.isSelected()) dataMap = getFilteredMap(dataMap);
				final Iterator iterator = dataMap.keySet().iterator();

				data = new Object[dataMap.size()][2];
				for (int i = 0; iterator.hasNext(); i++) {
					final String tag = (String) iterator.next();
					data[i][0] = tag;
					data[i][1] = dataMap.get(tag);
				}

				boolean pointCreated = false;
				if (this.searchCoordinates == null) {
					this.searchCoordinates = new Point(0, 0);
					pointCreated = true;
				}

				int i = this.searchCoordinates.x;
				int j = this.searchCoordinates.y;
				while (i < data.length) {
					while (j < 2) {
						if ((pointCreated) ||
							(!this.searchCoordinates.equals(new Point(i, j))))
						{
							final Object property = data[i][j];
							if (property.toString().toLowerCase().indexOf(string) > 0) {
								this.searchCoordinates = new Point(i, j);
								return last;
							}
						}

						j++;
					}
					j = 0;
					i++;
				}
			}

			while ((nodes.hasMoreElements()) && (!nodes.nextElement().equals(last)));
		}

		this.searchCoordinates = null;

		while ((nodes.hasMoreElements()) && (result == null)) {
			node = nodes.nextElement();
			final String nodeTitle = node.getUserObject().toString();

			if (nodeTitle.toLowerCase().indexOf(string) > 0) {
				result = node;
			}
		}
		if (result == null) {
			JOptionPane
				.showMessageDialog(
					this,
					"End of metadata reached. I could not find any tags, properties or values. The next search will start from the beginning.",
					"Find...", 1);
		}
		return result;
	}

	public LinkedHashMap<String, Object> getFilteredMap(
		final LinkedHashMap<String, Object> dataMap)
	{
		final LinkedHashMap filteredMap = new LinkedHashMap();
		final Iterator iterator = dataMap.keySet().iterator();

		for (int i = 0; iterator.hasNext(); i++) {
			final String tag = (String) iterator.next();
			if (tag.indexOf("<UNKNOWN@") == -1) filteredMap
				.put(tag, dataMap.get(tag));
		}
		return filteredMap;
	}

	public void expandAll() {
		int row = 0;
		while (row < this.detailsTree.getRowCount()) {
			this.detailsTree.expandRow(row);
			row++;
		}
	}

	public void collapseAll() {
		int row = this.detailsTree.getRowCount() - 1;
		while (row >= 0) {
			this.detailsTree.collapseRow(row);
			row--;
		}
	}

	public void updateTreeAndLabels() {
		final ImagePlus imp = WindowManager.getCurrentImage();
		if (imp == null) return;
		final Reader reader = ServiceMediator.getReader();
		reader.updateMetadata(imp);
		if ((imp.getOriginalFileInfo() instanceof LSMFileInfo)) {
			this.lsm = ((LSMFileInfo) imp.getOriginalFileInfo());
			setTitle(this.title + " - " + this.lsm.fileName);
			this.detailsTree.clearSelection();
			this.table.clearSelection();
			collapseAll();
			if (this.filterCBItem.isSelected()) updateFilteredTree(true);
			else updateFilteredTree(false);
		}
	}

	public void dumpData() {
		final String header = new String("Data dump");
		new TextWindow("SCANINFO DUMP", header, getTreeAsStringBuffer().toString(),
			250, 400);
	}

	protected void dumpXmlData() {
		if (this.lsm == null) return;
		final ImageDirectory imDir =
			(ImageDirectory) this.lsm.imageDirectories.get(0);
		final CZLSMInfoExtended cz = (CZLSMInfoExtended) imDir.TIF_CZ_LSMINFO;
		new TextWindow("XML SCANINFO DUMP", new DomXmlExporter().buildTree(cz,
			this.filterButton.isSelected()), 250, 400);
	}

	public void updateFilteredTree(final boolean filter) {
		((DefaultMutableTreeNode) this.detailsTree.getModel().getRoot())
			.removeAllChildren();
		final ImageDirectory imDir =
			(ImageDirectory) this.lsm.imageDirectories.get(0);
		final CZLSMInfoExtended cz = (CZLSMInfoExtended) imDir.TIF_CZ_LSMINFO;
		final InfoNode czNode = new InfoNode("CarlZeiss", convertCZ(cz));
		((DefaultMutableTreeNode) this.treemodel.getRoot()).add(czNode);
		if (cz.scanInfo == null) return;
		final ScanInfo scanInfo = cz.scanInfo;
		final ArrayList recordings = scanInfo.recordings;
		for (int i = 0; i < recordings.size(); i++) {
			final Recording recording = (Recording) recordings.get(i);
			final InfoNode recordingsNode =
				new InfoNode("Recordings", recording.records);

			if (recording.lasers != null) {
				final DefaultMutableTreeNode lasersNode =
					new DefaultMutableTreeNode("Lasers");

				for (int j = 0; j < recording.lasers.length; j++) {
					final LinkedHashMap map = recording.lasers[j].records;
					final Object o = map.get("LASER_ACQUIRE");
					if ((filter) && (o != null)) {
						if (!(o != null & o.toString().equals("-1"))) ;
					}
					else lasersNode.add(new InfoNode("Laser " + j,
						recording.lasers[j].records));
				}
				recordingsNode.add(lasersNode);
			}
			if (recording.tracks != null) {
				final DefaultMutableTreeNode tracksNode =
					new DefaultMutableTreeNode("Tracks");

				for (int j = 0; j < recording.tracks.length; j++) {
					final DefaultMutableTreeNode trackNode =
						new InfoNode("Track " + j, recording.tracks[j].records);
					if ((recording.tracks[j].detectionChannels != null) &&
						(recording.tracks[j].detectionChannels.length > 0))
					{
						final DefaultMutableTreeNode detectionChannelsNode =
							new DefaultMutableTreeNode("DetectionChannels");
						for (int k = 0; k < recording.tracks[j].detectionChannels.length; k++)
						{
							final LinkedHashMap map =
								recording.tracks[j].detectionChannels[k].records;
							final Object o = map.get("ACQUIRE");
							if ((filter) && (o != null)) {
								if (!(o != null & o.toString().equals("-1"))) ;
							}
							else detectionChannelsNode.add(new InfoNode("DetectionChannel " +
								k, recording.tracks[j].detectionChannels[k].records));
						}
						trackNode.add(detectionChannelsNode);
					}

					if ((recording.tracks[j].illuminationChannels != null) &&
						(recording.tracks[j].illuminationChannels.length > 0))
					{
						final DefaultMutableTreeNode illuminationChannelsNode =
							new DefaultMutableTreeNode("IlluminationChannels");
						for (int k = 0; k < recording.tracks[j].illuminationChannels.length; k++)
						{
							final LinkedHashMap map =
								recording.tracks[j].illuminationChannels[k].records;
							final Object o = map.get("ACQUIRE");
							if ((filter) && (o != null)) {
								if (!(o != null & o.toString().equals("-1"))) ;
							}
							else illuminationChannelsNode.add(new InfoNode(
								"IlluminationChannel " + k,
								recording.tracks[j].illuminationChannels[k].records));
						}
						trackNode.add(illuminationChannelsNode);
					}

					if ((recording.tracks[j].beamSplitters != null) &&
						(recording.tracks[j].beamSplitters.length > 0))
					{
						final DefaultMutableTreeNode beamSplittersNode =
							new DefaultMutableTreeNode("BeamSplitters");

						for (int k = 0; k < recording.tracks[j].beamSplitters.length; k++) {
							final InfoNode bsNode =
								new InfoNode("Beamsplitter " + k,
									recording.tracks[j].beamSplitters[k].records);
							beamSplittersNode.add(bsNode);
						}
						trackNode.add(beamSplittersNode);
					}

					if ((recording.tracks[j].dataChannels != null) &&
						(recording.tracks[j].dataChannels.length > 0))
					{
						final DefaultMutableTreeNode dataChannelsNode =
							new DefaultMutableTreeNode("DataChannels");
						for (int k = 0; k < recording.tracks[j].dataChannels.length; k++) {
							final LinkedHashMap map =
								recording.tracks[j].dataChannels[k].records;
							final Object o = map.get("ACQUIRE");
							if ((filter) && (o != null)) {
								if (!(o != null & o.toString().equals("-1"))) ;
							}
							else dataChannelsNode.add(new InfoNode("DataChannel " + k,
								recording.tracks[j].dataChannels[k].records));
						}
						trackNode.add(dataChannelsNode);
					}

					tracksNode.add(trackNode);
				}
				if (tracksNode.getChildCount() > 0) {
					recordingsNode.add(tracksNode);
				}
			}
			if (recording.markers != null) {
				final DefaultMutableTreeNode markersNode =
					new DefaultMutableTreeNode("Markers");

				for (int j = 0; j < recording.markers.length; j++) {
					markersNode.add(new InfoNode("Marker " + j,
						recording.markers[j].records));
				}
				recordingsNode.add(markersNode);
			}
			if (recording.timers != null) {
				final DefaultMutableTreeNode timersNode =
					new DefaultMutableTreeNode("Timers");

				for (int j = 0; j < recording.timers.length; j++) {
					timersNode
						.add(new InfoNode("Timer " + j, recording.timers[j].records));
				}
				recordingsNode.add(timersNode);
			}

			((DefaultMutableTreeNode) this.treemodel.getRoot()).add(recordingsNode);
		}
		this.lastNodeResult = null;
		this.searchCoordinates = null;
		((TreeTableModel) this.table.getModel()).setFiltered(filter);
		this.treemodel.reload();
		expandAll();
	}

	public void expandTree() {
		expandEntireTree((DefaultMutableTreeNode) this.treemodel.getRoot());
	}

	private void expandEntireTree(final DefaultMutableTreeNode tNode) {
		final TreePath tp = new TreePath(tNode.getPath());
		this.detailsTree.expandPath(tp);

		for (int i = 0; i < tNode.getChildCount(); i++)
			expandEntireTree((DefaultMutableTreeNode) tNode.getChildAt(i));
	}

	private StringBuffer getTreeAsStringBuffer() {
		final ImageDirectory imDir =
			(ImageDirectory) this.lsm.imageDirectories.get(0);
		final CZLSMInfoExtended cz = (CZLSMInfoExtended) imDir.TIF_CZ_LSMINFO;
		final StringBuffer sb = new StringBuffer();

		sb.append("CarlZeiss\t\n");
		sb.append(getRecordAsString(convertCZ(cz)));

		final ScanInfo scanInfo = cz.scanInfo;
		final ArrayList recordings = scanInfo.recordings;
		for (int i = 0; i < recordings.size(); i++) {
			final Recording recording = (Recording) recordings.get(i);
			sb.append("Recording " + i + "\t\n");
			sb.append(getRecordAsString(recording.records));
			if (recording.lasers != null) {
				for (int j = 0; j < recording.lasers.length; j++) {
					sb.append("Laser " + j + "\t\n");
					sb.append(getRecordAsString(recording.lasers[j].records));
				}
			}
			if (recording.tracks != null) {
				for (int j = 0; j < recording.tracks.length; j++) {
					sb.append("Track" + j + "\t\n");
					sb.append(getRecordAsString(recording.tracks[j].records));
					if (recording.tracks[j].dataChannels != null) for (int k = 0; k < recording.tracks[j].dataChannels.length; k++)
					{
						sb.append("DataChannel " + k + "\t\n");

						sb.append(getRecordAsString(recording.tracks[j].dataChannels[k].records));
					}
					if (recording.tracks[j].beamSplitters != null) for (int k = 0; k < recording.tracks[j].beamSplitters.length; k++)
					{
						sb.append("BeamSplitter " + k + "\t\n");
						sb.append(getRecordAsString(recording.tracks[j].beamSplitters[k].records));
					}
					if (recording.tracks[j].detectionChannels != null) for (int k = 0; k < recording.tracks[j].detectionChannels.length; k++)
					{
						sb.append("DetectionChannel " + k + "\t\n");
						sb.append(getRecordAsString(recording.tracks[j].detectionChannels[k].records));
					}
					if (recording.tracks[j].illuminationChannels != null) {
						for (int k = 0; k < recording.tracks[j].illuminationChannels.length; k++)
						{
							sb.append("IlluminationChannel " + k + "\t\n");
							sb.append(getRecordAsString(recording.tracks[j].illuminationChannels[k].records));
						}
					}
				}
			}
			if (recording.markers != null) {
				for (int j = 0; j < recording.markers.length; j++) {
					sb.append("Marker " + j + "\t\n");
					sb.append(getRecordAsString(recording.markers[j].records));
				}
			}
			if (recording.timers != null) for (int j = 0; j < recording.timers.length; j++)
			{
				sb.append("Timer " + j + "\t\n");
				sb.append(getRecordAsString(recording.timers[j].records));
			}
		}
		return sb;
	}

	private StringBuffer
		getRecordAsString(final LinkedHashMap<String, Object> hm)
	{
		final StringBuffer sb = new StringBuffer();
		if (hm != null) {
			final Iterator iterator = hm.keySet().iterator();
			for (int i = 0; iterator.hasNext(); i++) {
				final String tag = (String) iterator.next();
				sb.append(tag + ":\t" + hm.get(tag) + "\n");
			}
		}
		return sb;
	}

	public static LinkedHashMap<String, Object> convertCZ(
		final CZLSMInfoExtended cz)
	{
		final LinkedHashMap map = new LinkedHashMap();
		map.put("DimensionX", new Long(cz.DimensionX));
		map.put("DimensionY", new Long(cz.DimensionY));
		map.put("DimensionZ", new Long(cz.DimensionZ));
		map.put("DimensionChannels", new Long(cz.DimensionChannels));
		map.put("DimensionTime", new Long(cz.DimensionTime));
		map.put("IntensityDataType", new Long(cz.IntensityDataType));
		map.put("ThumbnailX", new Long(cz.ThumbnailX));
		map.put("ThumbnailY", new Long(cz.ThumbnailY));
		map.put("VoxelSizeX", new Double(cz.VoxelSizeX));
		map.put("VoxelSizeY", new Double(cz.VoxelSizeY));
		map.put("VoxelSizeZ", new Double(cz.VoxelSizeZ));
		map.put("OriginX", new Double(cz.OriginX));
		map.put("OriginY", new Double(cz.OriginY));
		map.put("OriginZ", new Double(cz.OriginZ));
		map.put("ScanType", new Integer(cz.ScanType));
		map.put("SpectralScan", new Integer(cz.SpectralScan));
		map.put("DataType", new Long(cz.DataType));
		map.put("TimeIntervall", new Double(cz.TimeIntervall));
		map.put("DisplayAspectX", new Double(cz.DisplayAspectX));
		map.put("DisplayAspectY", new Double(cz.DisplayAspectY));
		map.put("DisplayAspectZ", new Double(cz.DisplayAspectZ));
		map.put("DisplayAspectTime", new Double(cz.DisplayAspectTime));
		map.put("ToolbarFlags", new Long(cz.ToolbarFlags));
		return map;
	}

	public void centerWindow() {
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2,
			(screenSize.height - getHeight()) / 2);
	}
}
