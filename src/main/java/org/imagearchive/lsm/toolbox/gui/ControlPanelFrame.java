
package org.imagearchive.lsm.toolbox.gui;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.gui.Toolbar;
import ij.io.FileInfo;
import ij.measure.Calibration;
import ij.plugin.MacroInstaller;
import ij.process.ImageProcessor;
import ij.text.TextWindow;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ColorModel;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.imagearchive.lsm.reader.info.ImageDirectory;
import org.imagearchive.lsm.reader.info.LSMFileInfo;
import org.imagearchive.lsm.toolbox.MasterModel;
import org.imagearchive.lsm.toolbox.Reader;
import org.imagearchive.lsm.toolbox.ServiceMediator;
import org.imagearchive.lsm.toolbox.info.CZLSMInfoExtended;
import org.imagearchive.lsm.toolbox.info.scaninfo.Recording;

public class ControlPanelFrame extends JFrame {

	public InfoFrame infoFrame;
	private JPanel pan;
	private final GridBagLayout gridBagLayout = new GridBagLayout();

	private final GUIButton openLSMButton = new GUIButton(" Open LSM ",
		"images/fileopen.png", "Opens a LSM image, image stack or a batch file");

	private final GUIButton closeWindowsButton = new GUIButton(
		" Close all Windows ", "images/fileclose.png",
		"Closes all opened Image Windows");

	private final GUIButton exitButton = new GUIButton(" Exit ",
		"images/exit.png", "Exits the LSM Toolbox");

	private final GUIButton showInfoButton = new GUIButton(" Show Infos ",
		"images/info.png", "Brings the infos panel to front");

	private final GUIButton browseButton = new GUIButton(" Browse ",
		"images/browse.png", "Browse Hypervolume, needs HyperVolume_Browser");

	private final GUIButton fmButton = new GUIButton(" Fuse/Merge images",
		"images/blend.png",
		"Fuses or merges lsm images, needs LSM_Fusion and/or LSM_Merge");

	private final GUIButton applyStampButton = new GUIButton(" Apply stamps ",
		"images/display.png", "Apply stamps to each image of a stack");

	private final GUIMenuItem applyTStampItem = new GUIMenuItem(
		" Apply t-stamp ", "images/tstamp.png",
		"Apply timestamp to each image of a time series stack");

	private final GUIMenuItem applyZStampItem = new GUIMenuItem(
		" Apply z-stamp ", "images/zstamp.png",
		"Apply z-stamp to each image of a z series stack");

	private final GUIMenuItem applyLStampItem = new GUIMenuItem(
		" Apply l-stamp ", "images/lstamp.png",
		"Apply lambda-stamp to each image of a spectral series");

	private final GUIButton editPaletteButton = new GUIButton(" Edit Palette ",
		"images/palette.png", "Edit Palette, needs Lut_Panel");

	private final GUIButton batchConvertButton = new GUIButton(" Batch convert ",
		"images/batch.png", "Converts LSM files to other file formats");

	private final GUIButton helpButton = new GUIButton(" Help ",
		"images/help.png", "About, Help and Licensing");

	private final GUIButton macroButton = new GUIButton(" Install M&Ms ",
		"images/macro.png", "Install Magic Montage Macros ");

	private final JToggleButton rbButton = new JToggleButton();

	private final String title = " LSM Toolbox ";

	private final JLabel titleLabel = new JLabel("", 0);

	private final JPopupMenu stampsPM = new JPopupMenu();

	private final JPopupMenu hyperVolumePM = new JPopupMenu();

	private final JPopupMenu fmPM = new JPopupMenu();

	private final JMenuItem hyperVolumeItem = new JMenuItem(
		"Browse with HyperVolumeBrowser");

	private final JMenuItem image5DItem = new JMenuItem("Browse with Image5D");

	private final JMenuItem fuseItem = new JMenuItem("Fuse images");

	private final JMenuItem mergeItem = new JMenuItem("Merge images");

	public String[] LSMinfoText = new String[22];
	public long timestamps_count;
	private final Dimension ScreenDimension = Toolkit.getDefaultToolkit()
		.getScreenSize();

	private final int ScreenX = (int) this.ScreenDimension.getWidth();

	private final int ScreenY = (int) this.ScreenDimension.getHeight();

	private int baseFrameXlocation = 0;

	private int baseFrameYlocation = 0;

	private int selectedToolBarButtonID = -1;
	private final MasterModel masterModel;

	public ControlPanelFrame(final MasterModel masterModel)
		throws HeadlessException
	{
		this.masterModel = masterModel;
		ServiceMediator.registerControlPanelFrame(this);
	}

	public void initializeGUI() {
		setTitle("LSM Toolbox 4.0g");
		setResizable(false);
		addExitListener(this.exitButton, this);
		addShowHideInfolistener(this.showInfoButton, this);
		addOpenListener(this.openLSMButton, this);
		addCloseWinListener(this.closeWindowsButton, this);
		addStampsListener(this.applyStampButton, this);
		addApplyZStampListener(this.applyZStampItem, this);
		addApplyTStampListener(this.applyTStampItem, this);
		addApplyLambdaStampListener(this.applyLStampItem, this);
		addLUTListener(this.editPaletteButton, this);
		addBatchConvertListener(this.batchConvertButton, this);
		addHelpListener(this.helpButton, this);
		addMacroButtonListener(this.macroButton, this);
		addHyperVolumeBrowseListener(this.hyperVolumeItem, this);
		addImage5DListener(this.image5DItem, this);
		addBrowseListener(this.browseButton, this);
		addFuseListener(this.fuseItem, this);
		addMergeListener(this.mergeItem, this);
		addFMListener(this.fmButton, this);
		this.pan = new JPanel();
		this.pan.setForeground(SystemColor.window);
		this.pan.setLayout(this.gridBagLayout);
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = 1;
		this.titleLabel.setText(this.title + " ver " + "4.0g");
		this.pan.add(this.titleLabel, constraints);
		constraints.gridy += 1;
		this.pan.add(this.openLSMButton, constraints);
		constraints.gridy += 1;
		this.pan.add(this.showInfoButton, constraints);
		constraints.gridy += 1;
		this.pan.add(this.macroButton, constraints);
		constraints.gridy += 1;
		this.pan.add(this.applyStampButton, constraints);
		this.stampsPM.add(this.applyZStampItem);
		this.stampsPM.add(this.applyTStampItem);
		this.stampsPM.add(this.applyLStampItem);

		if ((isPluginInstalled("HyperVolume_Browser")) || (isImage5DInstalled())) {
			constraints.gridy += 1;
			this.pan.add(this.browseButton, constraints);
			this.hyperVolumePM.add(this.hyperVolumeItem);
			this.hyperVolumePM.add(this.image5DItem);
			if (!isPluginInstalled("HyperVolume_Browser")) this.hyperVolumeItem
				.setEnabled(false);
			if (!isImage5DInstalled()) {
				this.image5DItem.setEnabled(false);
			}
		}
		if ((isPluginInstalled("LSM_Fusion")) || (isPluginInstalled("LSM_Merge"))) {
			constraints.gridy += 1;
			this.pan.add(this.fmButton, constraints);
			this.fmPM.add(this.fuseItem);
			this.fmPM.add(this.mergeItem);
			if (!isPluginInstalled("LSM_Fusion")) this.fuseItem.setEnabled(false);
			if (!isPluginInstalled("LSM_Merge")) {
				this.mergeItem.setEnabled(false);
			}
		}
		if (isPluginInstalled("Lut_Panel")) {
			constraints.gridy += 1;
			this.pan.add(this.editPaletteButton, constraints);
		}
		constraints.gridy += 1;
		this.pan.add(this.closeWindowsButton, constraints);
		constraints.gridy += 1;
		this.pan.add(this.batchConvertButton, constraints);
		constraints.gridy += 1;
		this.pan.add(this.helpButton, constraints);
		constraints.gridy += 1;
		this.pan.add(this.exitButton, constraints);
		getContentPane().add(this.pan);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				ControlPanelFrame.this.dispose();
			}
		});
		initInfoFrame();

		new CPDragAndDrop(this);
		invalidate();
		pack();
		this.baseFrameXlocation = (this.ScreenX - getWidth());
		this.baseFrameYlocation = (this.ScreenY / 2 - getHeight());
		setLocation(this.baseFrameXlocation, this.baseFrameYlocation);
		setVisible(true);
	}

	private void closeFrames() {
		this.infoFrame.dispose();
		dispose();
	}

	public void initInfoFrame() {
		this.infoFrame = new InfoFrame();
	}

	private void addExitListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				WindowManager.closeAllWindows();
				ControlPanelFrame.this.closeFrames();
			}
		});
	}

	private void addBrowseListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Component c = (Component) e.getSource();
				if (!ControlPanelFrame.this.hyperVolumePM.isShowing()) ControlPanelFrame.this.hyperVolumePM
					.show(c, 0, 2 + c.getHeight());
				else ControlPanelFrame.this.hyperVolumePM.setVisible(false);
			}
		});
	}

	private void addOpenListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Reader reader = ServiceMediator.getReader();
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						try {
							IJ.showStatus("Loading image");
							final ImagePlus imp = reader.open("", true);
							IJ.showStatus("Image loaded");
							if (imp == null) return;
							imp.setPosition(1, 1, 1);
							imp.show();
						}
						catch (final OutOfMemoryError e) {
							IJ.outOfMemory("Could not load lsm image.");
						}
					}
				});
			}
		});
	}

	private void
		addBatchConvertListener(final JButton button, final JFrame parent)
	{
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				new BatchDialog(parent, ControlPanelFrame.this.masterModel)
					.setVisible(true);
			}
		});
	}

	private void addHelpListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				new AboutDialog(parent, ControlPanelFrame.this.masterModel)
					.setVisible(true);
			}
		});
	}

	private void
		addMacroButtonListener(final JButton button, final JFrame parent)
	{
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				new MacroInstaller().install(ControlPanelFrame.this.masterModel
					.getMagicMontaqe());
			}
		});
	}

	private void addCloseWinListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				WindowManager.closeAllWindows();
				ControlPanelFrame.this.setLSMinfoText(new String[22]);
				ControlPanelFrame.this.infoFrame.getDetailsFrame().dispose();
				ControlPanelFrame.this.infoFrame.dispose();
				ControlPanelFrame.this.showInfoButton.setEnabled(false);
			}
		});
	}

	private void
		addShowHideInfolistener(final JButton button, final JFrame parent)
	{
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (!ControlPanelFrame.this.infoFrame.isShowing()) ControlPanelFrame.this.infoFrame
					.setVisible(true);
				else ControlPanelFrame.this.infoFrame.setVisible(false);
			}
		});
	}

	private void addStampsListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Component c = (Component) e.getSource();
				if (!ControlPanelFrame.this.stampsPM.isShowing()) ControlPanelFrame.this.stampsPM
					.show(c, 0, 2 + c.getHeight());
				else ControlPanelFrame.this.stampsPM.setVisible(false);
			}
		});
	}

	private void addFMListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Component c = (Component) e.getSource();
				if (!ControlPanelFrame.this.fmPM.isShowing()) ControlPanelFrame.this.fmPM
					.show(c, 0, 2 + c.getHeight());
				else ControlPanelFrame.this.fmPM.setVisible(false);
			}
		});
	}

	private void
		addApplyZStampListener(final JMenuItem item, final JFrame parent)
	{
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final SelectImageDialog id =
					new SelectImageDialog(parent,
						"Select an lsm image to apply Z stamps to", false,
						MasterModel.DEPTH);
				final int returnVal = id.showDialog();
				if (returnVal == 0) {
					final int[] imageVals = id.getSelected();
					if (((imageVals == null ? 1 : 0) & (imageVals.length > 0 ? 1 : 0)) != 0)
					{
						JOptionPane.showMessageDialog(parent, "No image has been selected",
							"Error", 0);
						return;
					}
					final String[] choices = { "Dump to textfile", "Apply to image" };
					final GenericDialog gd = new GenericDialog(" Z stamps");
					gd.addChoice("Stamps destination : ", choices, "Apply to image");
					gd.showDialog();
					if (gd.wasCanceled()) {
						return;
					}
					final String choice = gd.getNextChoice();
					final Reader reader = ServiceMediator.getReader();
					for (int i = 0; i < imageVals.length; i++) {
						final ImagePlus imp = WindowManager.getImage(imageVals[i]);
						reader.updateMetadata(imp);
						final LSMFileInfo openLSM = (LSMFileInfo) imp.getOriginalFileInfo();
						final CZLSMInfoExtended cz =
							(CZLSMInfoExtended) ((ImageDirectory) openLSM.imageDirectories
								.get(0)).TIF_CZ_LSMINFO;
						final Recording r = cz.scanInfo.recordings.get(0);
						final double planeSpacing =
							((Double) r.records.get("PLANE_SPACING")).doubleValue();
						if (choice.equals("Dump to textfile")) {
							String twstr = new String("");
							double ps = 0.0D;
							for (int k = 1; i <= cz.DimensionZ; k++) {
								final String s = IJ.d2s(ps, 2) + " " + MasterModel.micrometer;
								ps += planeSpacing;
								twstr = twstr + s + "\n";
							}
							new TextWindow("Z-stamps", "Z-stamps", twstr, 200, 400);
						}
						else {
							ControlPanelFrame.this.applyZSTAMP(imp, (LSMFileInfo) imp
								.getOriginalFileInfo());
						}
					}
				}
			}
		});
	}

	private void
		addApplyTStampListener(final JMenuItem item, final JFrame parent)
	{
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final SelectImageDialog id =
					new SelectImageDialog(parent,
						"Select an lsm image to apply time stamps to", false,
						MasterModel.TIME);
				final int returnVal = id.showDialog();
				if (returnVal == 0) {
					final int[] imageVals = id.getSelected();
					if (((imageVals == null ? 1 : 0) & (imageVals.length > 0 ? 1 : 0)) != 0)
					{
						JOptionPane.showMessageDialog(parent, "No image has been selected",
							"Error", 0);
						return;
					}
					final String[] choices = { "Dump to textfile", "Apply to image" };
					final GenericDialog gd = new GenericDialog("Time stamps");
					gd.addChoice("Stamps destination : ", choices, "Apply to image");
					gd.showDialog();
					if (gd.wasCanceled()) {
						return;
					}
					final String choice = gd.getNextChoice();
					final Reader reader = ServiceMediator.getReader();
					for (int i = 0; i < imageVals.length; i++) {
						final ImagePlus imp = WindowManager.getImage(imageVals[i]);
						reader.updateMetadata(imp);
						final LSMFileInfo openLSM = (LSMFileInfo) imp.getOriginalFileInfo();
						final CZLSMInfoExtended cz =
							(CZLSMInfoExtended) ((ImageDirectory) openLSM.imageDirectories
								.get(0)).TIF_CZ_LSMINFO;
						if (choice.equals("Dump to textfile")) {
							String twstr = new String("");
							for (int k = 0; k < cz.timeStamps.NumberTimeStamps; k++)
								twstr =
									twstr + Double.toString(cz.timeStamps.TimeStamps[k]) + "\n";
							new TextWindow("Timestamps", "Timestamps", twstr, 200, 400);
						}
						else {
							ControlPanelFrame.this.applyTSTAMP(imp, (LSMFileInfo) imp
								.getOriginalFileInfo());
						}
					}
				}
			}
		});
	}

	private void addApplyLambdaStampListener(final JMenuItem item,
		final JFrame parent)
	{
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final SelectImageDialog id =
					new SelectImageDialog(parent,
						"Select an lsm image to apply lambda stamps to", false,
						MasterModel.CHANNEL);
				final int returnVal = id.showDialog();
				if (returnVal == 0) {
					final int[] imageVals = id.getSelected();
					if (((imageVals == null ? 1 : 0) & (imageVals.length > 0 ? 1 : 0)) != 0)
					{
						JOptionPane.showMessageDialog(parent, "No image has been selected",
							"Error", 0);
						return;
					}
					final String[] choices = { "Dump to textfile", "Apply to image" };
					final GenericDialog gd = new GenericDialog("Lambdastamps");
					gd.addChoice("Stamps destination : ", choices, "Apply to image");
					gd.showDialog();
					if (gd.wasCanceled()) {
						return;
					}
					final String choice = gd.getNextChoice();
					final Reader reader = ServiceMediator.getReader();
					for (int i = 0; i < imageVals.length; i++) {
						final ImagePlus imp = WindowManager.getImage(imageVals[i]);
						reader.updateMetadata(imp);
						final LSMFileInfo openLSM = (LSMFileInfo) imp.getOriginalFileInfo();
						final CZLSMInfoExtended cz =
							(CZLSMInfoExtended) ((ImageDirectory) openLSM.imageDirectories
								.get(0)).TIF_CZ_LSMINFO;

						if (cz.SpectralScan != 1) {
							IJ.error("Image not issued from spectral scan. Lambda stamp obsolete!");
							return;
						}

						if (choice.equals("Dump to textfile")) {
							String twstr = new String("");
							for (int k = 0; k < cz.channelWavelength.Channels; k++) {
								twstr =
									twstr +
										Double.toString(cz.channelWavelength.LambdaStamps[k]) +
										"\n";
							}
							new TextWindow("Lambdastamps", "Lambdastamps", twstr, 200, 400);
						}
						else {
							ControlPanelFrame.this.applyLSTAMP(imp, (LSMFileInfo) imp
								.getOriginalFileInfo());
						}
					}
				}
			}
		});
	}

	private void addImage5DListener(final JMenuItem item, final JFrame parent) {
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final SelectImageDialog id =
					new SelectImageDialog(parent, "Select an image to open with Image5D",
						false);
				final int returnVal = id.showDialog();
				if (returnVal == 0) {
					final int[] imageVals = id.getSelected();
					if (((imageVals == null ? 1 : 0) & (imageVals.length > 0 ? 1 : 0)) != 0)
					{
						JOptionPane.showMessageDialog(parent, "No image has been selected",
							"Error", 0);
						return;
					}
					final Reader reader = ServiceMediator.getReader();
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							try {
								for (int i = 0; i < imageVals.length; i++) {
									final ImagePlus imp = WindowManager.getImage(imageVals[i]);
									reader.updateMetadata(imp);
									final LSMFileInfo openLSM =
										(LSMFileInfo) imp.getOriginalFileInfo();
									final CZLSMInfoExtended cz =
										(CZLSMInfoExtended) ((ImageDirectory) openLSM.imageDirectories
											.get(0)).TIF_CZ_LSMINFO;
									Class i5Dc = null;
									if ((imp == null) || (imp.getStackSize() == 0)) {
										IJ.error("Could not open file.");
										return;
									}
									try {
										i5Dc = Class.forName("i5d.Image5D");
									}
									catch (final ClassNotFoundException e1) {
										try {
											i5Dc = Class.forName("Image5D");
										}
										catch (final ClassNotFoundException e2) {
											e2.printStackTrace();
										}
									}
									Constructor i5Dcon = null;

									Object o = null;
									try {
										i5Dcon =
											i5Dc.getConstructor(new Class[] { String.class,
												Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE,
												Integer.TYPE, Integer.TYPE, Boolean.TYPE });
										o =
											i5Dcon
												.newInstance(new Object[] { openLSM.fileName,
													new Integer(imp.getType()),
													new Integer(imp.getWidth()),
													new Integer(imp.getHeight()),
													new Integer((int) cz.DimensionChannels),
													new Integer((int) cz.DimensionZ),
													new Integer((int) cz.DimensionTime),
													new Boolean(false) });

										final Method i5DsetCurrentPosition =
											o.getClass().getMethod(
												"setCurrentPosition",
												new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE,
													Integer.TYPE, Integer.TYPE });
										final Method i5DsetPixels =
											o.getClass().getMethod("setPixels",
												new Class[] { Object.class });
										final Method i5DsetCalibration =
											o.getClass().getMethod("setCalibration",
												new Class[] { Calibration.class });
										final Method i5Dshow =
											o.getClass().getMethod("show", new Class[0]);
										final Method i5DgetWindow =
											o.getClass().getMethod("getWindow", new Class[0]);
										final Method i5DsetChannelColorModel =
											o.getClass().getMethod("setChannelColorModel",
												new Class[] { Integer.TYPE, ColorModel.class });

										final Method i5DsetFileInfo =
											o.getClass().getMethod("setFileInfo",
												new Class[] { FileInfo.class });

										int position = 1;
										for (int t = 0; t < cz.DimensionTime; t++) {
											for (int z = 0; z < cz.DimensionZ; z++) {
												for (int c = 0; c < cz.DimensionChannels; c++) {
													i5DsetCurrentPosition.invoke(o, new Object[] {
														new Integer(0), new Integer(0), new Integer(c),
														new Integer(z), new Integer(t) });
													imp.setSlice(position++);
													i5DsetPixels.invoke(o, new Object[] { imp
														.getProcessor().getPixels() });
												}
											}

											for (int c = 0; c < cz.DimensionChannels; c++) {
												i5DsetChannelColorModel.invoke(o, new Object[] {
													new Integer(c + 1),
													imp.getProcessor().getColorModel() });
											}
										}
										i5DsetCalibration.invoke(o, new Object[] { imp
											.getCalibration().copy() });
										i5DsetFileInfo.invoke(o, new Object[] { (LSMFileInfo) imp
											.getOriginalFileInfo() });
										i5Dshow.invoke(o, new Object[0]);
										((ImageWindow) i5DgetWindow.invoke(o, new Object[0]))
											.addWindowFocusListener(new ImageFocusListener());

										ServiceMediator.getInfoFrame().updateInfoFrame();
										ServiceMediator.getDetailsFrame().updateTreeAndLabels();
									}
									catch (final IllegalArgumentException ex) {
										ex.printStackTrace();
									}
									catch (final InstantiationException ex) {
										ex.printStackTrace();
									}
									catch (final IllegalAccessException ex) {
										ex.printStackTrace();
									}
									catch (final InvocationTargetException ex) {
										ex.printStackTrace();
									}
									catch (final SecurityException ex) {
										ex.printStackTrace();
									}
									catch (final NoSuchMethodException ex) {
										ex.printStackTrace();
									}
								}
							}
							catch (final OutOfMemoryError e) {
								IJ.outOfMemory("Could not load lsm image.");
							}
						}
					});
				}
			}
		});
	}

	private void addHyperVolumeBrowseListener(final JMenuItem item,
		final JFrame parent)
	{
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final SelectImageDialog id =
					new SelectImageDialog(parent,
						"Select one or more channels to open with HyperVolume_Browser",
						true);
				final int returnVal = id.showDialog();
				if (returnVal == 0) {
					final int[] imageVals = id.getSelected();

					if (((imageVals == null ? 1 : 0) & (imageVals.length > 0 ? 1 : 0)) != 0)
					{
						JOptionPane.showMessageDialog(parent, "No image has been selected",
							"Error", 0);
						return;
					}
					final Reader reader = ServiceMediator.getReader();
					for (int i = 0; i < imageVals.length; i++) {
						final ImagePlus imp = WindowManager.getImage(imageVals[i]);
						reader.updateMetadata(imp);
						final FileInfo fi = imp.getOriginalFileInfo();
						if ((fi != null) && ((fi instanceof LSMFileInfo))) {
							final LSMFileInfo lsm = (LSMFileInfo) fi;
							final CZLSMInfoExtended cz =
								(CZLSMInfoExtended) ((ImageDirectory) lsm.imageDirectories
									.get(0)).TIF_CZ_LSMINFO;

							final long depth = cz.DimensionZ;
							IJ.selectWindow(imageVals[i]);
							IJ.runPlugIn("HyperVolume_Browser", "3rd=z depth=" + depth +
								" 4th=t");
						}
					}
				}
			}
		});
	}

	private void addLUTListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				IJ.runPlugIn("Lut_Panel", "");
			}
		});
	}

	private void addFuseListener(final JMenuItem item, final JFrame parent) {
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				IJ.runPlugIn("LSM_Fusion", "");
			}
		});
	}

	private void addMergeListener(final JMenuItem item, final JFrame parent) {
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				IJ.runPlugIn("LSM_Merge", "");
			}
		});
	}

	public void setLSMinfoText(final String[] str) {
		this.LSMinfoText = str;
	}

	public void applyZSTAMP(final ImagePlus imp, final LSMFileInfo lfi) {
		int x = 2;
		int y = 40;
		double ps = 0.0D;
		final ImageStack stack = imp.getStack();
		final Font font = new Font("SansSerif", 0, 20);
		ImageProcessor ip = imp.getProcessor();
		final Rectangle roi = ip.getRoi();
		if ((roi.width < ip.getWidth()) || (roi.height < ip.getHeight())) {
			x = roi.x;
			y = roi.y + roi.height;
		}
		final Color c = Toolbar.getForegroundColor();
		final CZLSMInfoExtended cz =
			(CZLSMInfoExtended) ((ImageDirectory) lfi.imageDirectories.get(0)).TIF_CZ_LSMINFO;
		if (cz.DimensionZ != 1L) {
			final Recording r = cz.scanInfo.recordings.get(0);
			final double planeSpacing =
				((Double) r.records.get("PLANE_SPACING")).doubleValue();
			int stackPosition = 1;
			for (int i = 1; i <= cz.DimensionTime; i++) {
				ps = 0.0D;
				for (int j = 1; j <= cz.DimensionZ; j++) {
					for (int k = 1; k <= cz.DimensionChannels; k++) {
						if (stackPosition <= imp.getStackSize()) {
							IJ.showStatus("MinMax: " + j + "/" + cz.DimensionZ);
							final String s = IJ.d2s(ps, 2) + " " + MasterModel.micrometer;

							ip = stack.getProcessor(stackPosition++);
							ip.setFont(font);
							final float[] hsb =
								Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
							ip.setColor(Color.getHSBColor(255.0F, 255.0F, 255.0F - hsb[2]));
							ip.moveTo(x, y);
							ip.drawString(s);
						}
					}
					ps += planeSpacing;
				}
			}
		}
		imp.updateAndRepaintWindow();
	}

	public void applyTSTAMP(final ImagePlus imp, final LSMFileInfo lfi) {
		int x = 2;
		int y = 20;

		final ImageStack stack = imp.getStack();
		final Font font = new Font("SansSerif", 0, 20);
		ImageProcessor ip = imp.getProcessor();
		final Rectangle roi = ip.getRoi();
		if ((roi.width < ip.getWidth()) || (roi.height < ip.getHeight())) {
			x = roi.x;
			y = roi.y + roi.height;
		}
		final Color c = Toolbar.getForegroundColor();
		final CZLSMInfoExtended cz =
			(CZLSMInfoExtended) ((ImageDirectory) lfi.imageDirectories.get(0)).TIF_CZ_LSMINFO;
		if (cz.DimensionTime > 1L) {
			int stackPosition = 1;
			for (int i = 1; i <= cz.DimensionTime; i++) {
				for (int j = 1; j <= cz.DimensionZ; j++)
					for (int k = 1; k <= cz.DimensionChannels; k++)
						if (stackPosition <= imp.getStackSize()) {
							IJ.showStatus("MinMax: " + stackPosition + "/" +
								cz.timeStamps.NumberTimeStamps);
							final String s =
								IJ.d2s(cz.timeStamps.TimeStamps[(i - 1)], 2) + " s";
							ip = stack.getProcessor(stackPosition++);
							ip.setFont(font);
							final float[] hsb =
								Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
							ip.setColor(Color.getHSBColor(255.0F, 255.0F, 255.0F - hsb[2]));
							ip.moveTo(x, y);
							ip.drawString(s);
						}
			}
		}
		imp.updateAndRepaintWindow();
	}

	public void applyLSTAMP(final ImagePlus imp, final LSMFileInfo lfi) {
		int x = 2;
		int y = 60;
		ImageProcessor ip = imp.getProcessor();
		final Rectangle roi = ip.getRoi();
		if ((roi.width < ip.getWidth()) || (roi.height < ip.getHeight())) {
			x = roi.x;
			y = roi.y + roi.height;
		}
		final CZLSMInfoExtended cz =
			(CZLSMInfoExtended) ((ImageDirectory) lfi.imageDirectories.get(0)).TIF_CZ_LSMINFO;
		final ImageStack stack = imp.getStack();
		final Font font = new Font("SansSerif", 0, 20);
		final Color c = Toolbar.getForegroundColor();
		if ((cz.DimensionChannels > 1L) && (cz.SpectralScan == 1)) {
			int stackPosition = 1;
			for (int i = 1; i <= cz.DimensionTime; i++) {
				for (int j = 1; j <= cz.DimensionZ; j++) {
					for (int k = 1; k <= cz.DimensionChannels; k++) {
						if (stackPosition <= imp.getStackSize()) {
							final double channelWaveLength =
								cz.channelWavelength.LambdaStamps[(k - 1)];
							final String s =
								IJ.d2s(channelWaveLength * 1000000000.0D, 2) + " nm";
							ip = stack.getProcessor(stackPosition++);
							ip.setFont(font);
							final float[] hsb =
								Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
							ip.setColor(Color.getHSBColor(255.0F, 255.0F, 255.0F - hsb[2]));
							ip.moveTo(x, y);
							ip.drawString(s);
						}
					}
				}
			}
		}
		imp.updateAndRepaintWindow();
	}

	public boolean isPluginInstalled(final String className) {
		boolean found = false;
		try {
			Class.forName(className);
			found = true;
		}
		catch (final ClassNotFoundException localClassNotFoundException) {}
		return found;
	}

	public boolean isImage5DInstalled() {
		boolean installed = false;
		try {
			Class.forName("Image5DWindow");
			installed = true;
		}
		catch (final ClassNotFoundException e1) {
			try {
				Class.forName("i5d.gui.Image5DWindow");
				installed = true;
			}
			catch (final ClassNotFoundException localClassNotFoundException1) {}
		}
		return installed;
	}

	public boolean isValidImage5D() {
		boolean installed = false;
		try {
			Class.forName("Image5DWindow");
			installed = true;
		}
		catch (final ClassNotFoundException e1) {
			try {
				Class.forName("i5d.gui.Image5DWindow");
				installed = true;
			}
			catch (final ClassNotFoundException localClassNotFoundException1) {}
		}
		return installed;
	}

	public int getSelectedToolBarButtonID() {
		return this.selectedToolBarButtonID;
	}

	public void resetToolbar() {
		this.rbButton.setSelected(true);
		this.selectedToolBarButtonID = -1;
	}
}
