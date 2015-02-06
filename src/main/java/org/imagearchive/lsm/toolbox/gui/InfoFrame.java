
package org.imagearchive.lsm.toolbox.gui;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.text.TextWindow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.imagearchive.lsm.reader.info.ImageDirectory;
import org.imagearchive.lsm.reader.info.LSMFileInfo;
import org.imagearchive.lsm.toolbox.MasterModel;
import org.imagearchive.lsm.toolbox.Reader;
import org.imagearchive.lsm.toolbox.ServiceMediator;
import org.imagearchive.lsm.toolbox.info.CZLSMInfoExtended;
import org.imagearchive.lsm.toolbox.info.EventList;
import org.imagearchive.lsm.toolbox.info.scaninfo.Recording;

public class InfoFrame extends JFrame {

	private final MasterModel masterModel = MasterModel.getMasterModel();

	private final DetailsFrame detailsFrame;

	private final NotesDialog notesDialog;

	private final int infoFrameXsize = 300;

	private final int infoFrameYsize = 400;

	private final JLabel[] infolab = new JLabel[22];

	private final JTextArea[] area = new JTextArea[22];

	public InfoFrame() throws HeadlessException {
		super();
		detailsFrame = new DetailsFrame();
		notesDialog = new NotesDialog(this, true);
		initializeGUI();
		ServiceMediator.registerInfoFrame(this);
	}

	public void initializeGUI() {
		setTitle("General file information");
		setSize(infoFrameXsize, infoFrameYsize);
		getContentPane().setLayout(new BorderLayout());
		final String[] infolabels = new String[19];
		infolabels[0] = "File Name";
		infolabels[1] = "User";
		infolabels[2] = "Image Width";
		infolabels[3] = "Image Height";
		infolabels[4] = "Number of channels";
		infolabels[5] = "Z Stack size";
		infolabels[6] = "Time Stack size";
		infolabels[7] = "Scan Type";
		infolabels[8] = "Voxel X size";
		infolabels[9] = "Voxel Y size";
		infolabels[10] = "Voxel Z size";
		infolabels[11] = "Objective";
		infolabels[12] = "X zoom factor";
		infolabels[13] = "Y zoom factor";
		infolabels[14] = "Z zoom factor";
		infolabels[15] = "Plane width";
		infolabels[16] = "Plane height";
		infolabels[17] = "Volume depth";
		infolabels[18] = "Plane spacing";
		final JPanel infopanel = new JPanel(new GridLayout(19, 2, 3, 3));
		Font dafont = new Font(null);
		final float fontsize = 11;
		dafont = dafont.deriveFont(fontsize);
		final Font dafontbold = dafont.deriveFont(Font.BOLD);

		for (int i = 0; i < 19; i++) {
			infolab[i] = new JLabel("  " + infolabels[i]);
			infolab[i].setFont(dafontbold);
			infopanel.add(infolab[i]);
			area[i] = new JTextArea("");
			area[i].setEditable(false);
			area[i].setFont(dafont);
			infopanel.add(area[i]);
		}

		final JButton details_button =
			new JButton(new ImageIcon(getClass().getResource("images/plus.png")));
		details_button.setToolTipText("More details...");
		addDetailsListener(details_button, this);

		final JButton notes_button =
			new JButton(new ImageIcon(getClass().getResource("images/info.png")));
		notes_button.setToolTipText("Notes");
		addNotesButtonListener(notes_button, this);

		final JButton dumpinfos_button =
			new JButton(new ImageIcon(getClass().getResource("images/dump.png")));
		dumpinfos_button.setToolTipText("Dump to text file");
		addDumpInfosListener(dumpinfos_button, this);

		final JButton events_button =
			new JButton(new ImageIcon(getClass().getResource("images/events.png")));
		details_button.setToolTipText("Events...");
		addEventsListener(events_button, this);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(events_button);
		buttonPanel.add(notes_button);
		buttonPanel.add(dumpinfos_button);
		buttonPanel.add(details_button);

		getContentPane().add(buttonPanel, BorderLayout.NORTH);
		getContentPane().add(infopanel, BorderLayout.CENTER);

		addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowGainedFocus(final WindowEvent e) {

				updateInfoFrame();
			}

			@Override
			public void windowLostFocus(final WindowEvent e) {}
		});

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(final WindowEvent evt) {
				if (detailsFrame != null) detailsFrame.dispose();
			}
		});

		updateInfoFrame();
		pack();
		centerWindow();
	}

	public void updateInfoFrame() {
		final String[] str = getInfo();
		if (str == null) return;
		for (int i = 0; i < 19; i++) {
			if (str[i] != null) area[i].setText(str[i]);
		}
	}

	private void addDumpInfosListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				dumpInfo();
			}
		});
	}

	private void addEventsListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final ImagePlus imp = WindowManager.getCurrentImage();
				if (imp == null) {
					IJ.error("No open images.");
					return;
				};
				final Reader reader = ServiceMediator.getReader();
				reader.updateMetadata(imp);
				final LSMFileInfo openLSM = (LSMFileInfo) imp.getOriginalFileInfo();
				final CZLSMInfoExtended cz =
					(CZLSMInfoExtended) ((ImageDirectory) openLSM.imageDirectories.get(0)).TIF_CZ_LSMINFO;

				final EventList events = cz.eventList;
				if (events != null) {
					final String header =
						new String("Time (sec) \tEvent Type \tEvent Description");
					final TextWindow tw =
						new TextWindow("Time Events for " + imp.getTitle(), header,
							(String) null, 400, 200);
					tw.append(events.Description);
				}
				else IJ.error("No events defined in the LSM file.");
			}
		});
	}

	private void addDetailsListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (detailsFrame.isShowing() == false) {
					detailsFrame.setVisible(true);
				}
				else detailsFrame.setVisible(false);
			}
		});
	}

	private void
		addNotesButtonListener(final JButton button, final JFrame parent)
	{
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (notesDialog.isShowing() == false) {
					notesDialog.setNotes();
					notesDialog.setVisible(true);
				}
				else notesDialog.setVisible(false);
			}
		});
	}

	public String[] getInfo() {
		final ImagePlus imp = WindowManager.getCurrentImage();
		if (imp == null) return null;
		final Reader reader = ServiceMediator.getReader();
		reader.updateMetadata(imp);
		if (imp.getOriginalFileInfo() instanceof LSMFileInfo) {
			final LSMFileInfo lsm = (LSMFileInfo) imp.getOriginalFileInfo();

			final ArrayList<ImageDirectory> imageDirectories = lsm.imageDirectories;
			final ImageDirectory imDir = (imageDirectories.get(0));
			if (imDir == null) return null;
			final CZLSMInfoExtended cz = (CZLSMInfoExtended) imDir.TIF_CZ_LSMINFO;
			final String[] infos = new String[19];
			final String stacksize = IJ.d2s(cz.DimensionZ, 0);
			final String width = IJ.d2s(lsm.width, 0);
			final String height = IJ.d2s(lsm.height, 0);
			final String channels = IJ.d2s(cz.DimensionChannels, 0);
			String scantype = "";

			switch (cz.ScanType) {
				case 0:
					scantype = "Normal X-Y-Z scan";
					break;
				case 1:
					scantype = "Z scan";
					break;
				case 2:
					scantype = "Line scan";
					break;
				case 3:
					scantype = "Time series X-Y";
					break;
				case 4:
					scantype = "Time series X-Z";
					break;
				case 5:
					scantype = "Time series - Means of ROIs";
					break;
				case 6:
					scantype = "Time series X-Y-Z";
					break;
				case 10:
					scantype = "Point mode";
					break;
				default:
					scantype = "UNKNOWN !";
					break;
			}
			final Recording r = cz.scanInfo.recordings.get(0);
			final String objective = (String) r.records.get("ENTRY_OBJECTIVE");
			final String user = (String) r.records.get("USER");
			final double zoomx = ((Double) r.records.get("ZOOM_X")).doubleValue();
			final double zoomy = ((Double) r.records.get("ZOOM_Y")).doubleValue();

			final double zoomz = ((Double) r.records.get("ZOOM_Z")).doubleValue();

			final double planeSpacing =
				((Double) r.records.get("PLANE_SPACING")).doubleValue();

			final String voxelsize_x =
				IJ.d2s(cz.VoxelSizeX * 1000000, 2) + " " + MasterModel.micrometer;
			final String voxelsize_y =
				IJ.d2s(cz.VoxelSizeY * 1000000, 2) + " " + MasterModel.micrometer;
			final String voxelsize_z =
				IJ.d2s(cz.VoxelSizeZ * 1000000, 2) + " " + MasterModel.micrometer;
			final String timestacksize = IJ.d2s(cz.DimensionTime, 0);
			final String plane_spacing =
				IJ.d2s(planeSpacing * 1000000, 2) + " " + MasterModel.micrometer;;
			final String plane_width =
				IJ.d2s(cz.DimensionX * cz.VoxelSizeX, 2) + " " + MasterModel.micrometer;
			final String plane_height =
				IJ.d2s(cz.DimensionY * cz.VoxelSizeY, 2) + " " + MasterModel.micrometer;
			final String volume_depth =
				IJ.d2s(cz.DimensionZ * cz.VoxelSizeZ, 2) + " " + MasterModel.micrometer;

			infos[0] = lsm.fileName;
			infos[1] = user;
			infos[2] = width;
			infos[3] = height;
			infos[4] = channels;
			infos[5] = stacksize;
			infos[6] = timestacksize;
			infos[7] = scantype;
			infos[8] = voxelsize_x;
			infos[9] = voxelsize_y;
			infos[10] = voxelsize_z;
			infos[11] = objective;
			infos[12] = IJ.d2s(zoomx, 2);
			infos[13] = IJ.d2s(zoomy, 2);
			infos[14] = IJ.d2s(zoomz, 2);
			infos[15] = plane_width;
			infos[16] = plane_height;
			infos[17] = volume_depth;
			infos[18] = plane_spacing;
			return infos;
		}
		return null;
	}

	private void dumpInfo() {
		final String header = new String("Parameter\tValue");
		final TextWindow tw =
			new TextWindow("LSM Infos DUMP", header, (String) null, 280, 450);
		final String[] Parameters = new String[26];
		Parameters[0] = "File Name";
		Parameters[1] = "User";
		Parameters[2] = "Image Width";
		Parameters[3] = "Image Height";
		Parameters[4] = "Number of channels";
		Parameters[5] = "Z Stack size";
		Parameters[6] = "Time Stack size";
		Parameters[7] = "Scan Type";
		Parameters[8] = "Voxel X size";
		Parameters[9] = "Voxel Y size";
		Parameters[10] = "Voxel Z size";
		Parameters[11] = "Objective";
		Parameters[12] = "X Zoom factor";
		Parameters[13] = "Y Zoom factor";
		Parameters[14] = "Z Zoom factor";
		Parameters[15] = "Plane width";
		Parameters[16] = "Plane height";
		Parameters[17] = "Volume depth";
		Parameters[18] = "Plane spacing";
		final String[] infos = getInfo();
		if (infos != null) for (int i = 0; i < 19; i++)
			tw.append(Parameters[i] + "\t" + infos[i]);
	}

	public DetailsFrame getDetailsFrame() {
		return detailsFrame;
	}

	public void centerWindow() {
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - this.getWidth()) / 2,
			(screenSize.height - this.getHeight()) / 2);
	}
}
