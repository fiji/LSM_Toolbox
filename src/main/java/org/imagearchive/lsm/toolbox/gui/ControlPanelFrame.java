
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

	private final JLabel titleLabel = new JLabel("", JLabel.CENTER);

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

	private final int ScreenX = (int) ScreenDimension.getWidth();

	private final int ScreenY = (int) ScreenDimension.getHeight();

	private int baseFrameXlocation = 0;

	private int baseFrameYlocation = 0;

	private int selectedToolBarButtonID = -1;

	private final MasterModel masterModel;

	public ControlPanelFrame(final MasterModel masterModel)
		throws HeadlessException
	{
		super();
		this.masterModel = masterModel;
		ServiceMediator.registerControlPanelFrame(this);
	}

	public void initializeGUI() {
		setTitle("LSM Toolbox " + MasterModel.VERSION);
		setResizable(false);
		addExitListener(exitButton, this);
		addShowHideInfolistener(showInfoButton, this);
		addOpenListener(openLSMButton, this);
		addCloseWinListener(closeWindowsButton, this);
		addStampsListener(applyStampButton, this);
		addApplyZStampListener(applyZStampItem, this);
		addApplyTStampListener(applyTStampItem, this);
		addApplyLambdaStampListener(applyLStampItem, this);
		addLUTListener(editPaletteButton, this);
		addBatchConvertListener(batchConvertButton, this);
		addHelpListener(helpButton, this);
		addMacroButtonListener(macroButton, this);
		addHyperVolumeBrowseListener(hyperVolumeItem, this);
		addImage5DListener(image5DItem, this);
		addBrowseListener(browseButton, this);
		addFuseListener(fuseItem, this);
		addMergeListener(mergeItem, this);
		addFMListener(fmButton, this);
		pan = new JPanel();
		pan.setForeground(SystemColor.window);
		pan.setLayout(gridBagLayout);
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		titleLabel.setText(title + " ver " + MasterModel.VERSION);
		pan.add(titleLabel, constraints);
		constraints.gridy++;
		pan.add(openLSMButton, constraints);
		constraints.gridy++;
		pan.add(showInfoButton, constraints);
		constraints.gridy++;
		pan.add(macroButton, constraints);
		constraints.gridy++;
		pan.add(applyStampButton, constraints);
		stampsPM.add(applyZStampItem);
		stampsPM.add(applyTStampItem);
		stampsPM.add(applyLStampItem);

		if (isPluginInstalled("HyperVolume_Browser") || isImage5DInstalled()) {
			constraints.gridy++;
			pan.add(browseButton, constraints);
			hyperVolumePM.add(hyperVolumeItem);
			hyperVolumePM.add(image5DItem);
			if (!isPluginInstalled("HyperVolume_Browser")) hyperVolumeItem
				.setEnabled(false);
			if (!isImage5DInstalled()) image5DItem.setEnabled(false);
		}

		if (isPluginInstalled("LSM_Fusion") || isPluginInstalled("LSM_Merge")) {
			constraints.gridy++;
			pan.add(fmButton, constraints);
			fmPM.add(fuseItem);
			fmPM.add(mergeItem);
			if (!isPluginInstalled("LSM_Fusion")) fuseItem.setEnabled(false);
			if (!isPluginInstalled("LSM_Merge")) mergeItem.setEnabled(false);
		}

		if (isPluginInstalled("Lut_Panel")) {
			constraints.gridy++;
			pan.add(editPaletteButton, constraints);
		}
		constraints.gridy++;
		pan.add(closeWindowsButton, constraints);
		constraints.gridy++;
		pan.add(batchConvertButton, constraints);
		constraints.gridy++;
		pan.add(helpButton, constraints);
		constraints.gridy++;
		pan.add(exitButton, constraints);
		getContentPane().add(pan);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				dispose();
			}
		});

		initInfoFrame();
		/*
		 * masterModel.addMasterModelListener(new MasterModelAdapter() { public
		 * void LSMFileInfoChanged(MasterModelEvent evt) { updateShowInfo(); if
		 * (masterModel.getCz().DimensionTime <= 1)
		 * applyTStampItem.setEnabled(false); else
		 * applyTStampItem.setEnabled(true); if (masterModel.getCz().DimensionZ
		 * <= 1) applyZStampItem.setEnabled(false); else
		 * applyZStampItem.setEnabled(true); if
		 * (masterModel.getCz().SpectralScan == 0)
		 * applyLStampItem.setEnabled(false); else if
		 * (masterModel.getCz().channelWavelength != null &&
		 * masterModel.getCz().channelWavelength.Channels >= 1)
		 * applyLStampItem.setEnabled(true); pack(); } });
		 */
		new CPDragAndDrop(this);
		invalidate();
		pack();
		baseFrameXlocation = (ScreenX) - (this.getWidth());
		baseFrameYlocation = (ScreenY / 2) - (this.getHeight());
		setLocation(baseFrameXlocation, baseFrameYlocation);
		setVisible(true);
	}

	private void closeFrames() {
		infoFrame.dispose();
		dispose();
	}

	public void initInfoFrame() {
		infoFrame = new InfoFrame();
	}

	private void addExitListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				ij.WindowManager.closeAllWindows();
				closeFrames();
			}
		});
	}

	private void addBrowseListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Component c = (Component) e.getSource();
				if (!hyperVolumePM.isShowing()) {
					hyperVolumePM.show(c, 0, 2 + c.getHeight());
				}
				else {
					hyperVolumePM.setVisible(false);
				}
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
				new BatchDialog(parent, masterModel).setVisible(true);
			}
		});
	}

	private void addHelpListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				new AboutDialog(parent, masterModel).setVisible(true);
			}
		});
	}

	private void
		addMacroButtonListener(final JButton button, final JFrame parent)
	{
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				new MacroInstaller().install(masterModel.getMagicMontaqe());
			}
		});
	}

	private void addCloseWinListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				ij.WindowManager.closeAllWindows();
				setLSMinfoText(new String[22]);
				infoFrame.getDetailsFrame().dispose();
				infoFrame.dispose();
				showInfoButton.setEnabled(false);
			}
		});
	}

	private void
		addShowHideInfolistener(final JButton button, final JFrame parent)
	{
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (infoFrame.isShowing() == false) infoFrame.setVisible(true);
				else infoFrame.setVisible(false);
			}
		});
	}

	private void addStampsListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Component c = (Component) e.getSource();
				if (!stampsPM.isShowing()) {
					stampsPM.show(c, 0, 2 + c.getHeight());
				}
				else {
					stampsPM.setVisible(false);
				}
			}

		});
	}

	private void addFMListener(final JButton button, final JFrame parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Component c = (Component) e.getSource();
				if (!fmPM.isShowing()) {
					fmPM.show(c, 0, 2 + c.getHeight());
				}
				else {
					fmPM.setVisible(false);
				}
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
				if (returnVal == SelectImageDialog.OK_OPTION) {
					final int[] imageVals = id.getSelected();
					if (imageVals == null & imageVals.length > 0) {
						JOptionPane.showMessageDialog(parent, "No image has been selected",
							"Error", JOptionPane.ERROR_MESSAGE);
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
							double ps = 0;
							for (int k = 1; i <= cz.DimensionZ; k++) {
								final String s = IJ.d2s(ps, 2) + " " + MasterModel.micrometer;
								ps += planeSpacing; // moved from line -2
								twstr = twstr + s + "\n";
							}
							new TextWindow("Z-stamps", "Z-stamps", twstr, 200, 400);

						}
						else {
							applyZSTAMP(imp, (LSMFileInfo) imp.getOriginalFileInfo());
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
				if (returnVal == SelectImageDialog.OK_OPTION) {
					final int[] imageVals = id.getSelected();
					if (imageVals == null & imageVals.length > 0) {
						JOptionPane.showMessageDialog(parent, "No image has been selected",
							"Error", JOptionPane.ERROR_MESSAGE);
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
							applyTSTAMP(imp, (LSMFileInfo) imp.getOriginalFileInfo());
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
				if (returnVal == SelectImageDialog.OK_OPTION) {
					final int[] imageVals = id.getSelected();
					if (imageVals == null & imageVals.length > 0) {
						JOptionPane.showMessageDialog(parent, "No image has been selected",
							"Error", JOptionPane.ERROR_MESSAGE);
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
							for (int k = 0; k < cz.channelWavelength.Channels; k++)
								twstr =
									twstr +
										Double.toString(cz.channelWavelength.LambdaStamps[k]) +
										"\n";

							new TextWindow("Lambdastamps", "Lambdastamps", twstr, 200, 400);

						}
						else {
							applyLSTAMP(imp, (LSMFileInfo) imp.getOriginalFileInfo());
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
				if (returnVal == SelectImageDialog.OK_OPTION) {
					final int[] imageVals = id.getSelected();
					if (imageVals == null & imageVals.length > 0) {
						JOptionPane.showMessageDialog(parent, "No image has been selected",
							"Error", JOptionPane.ERROR_MESSAGE);
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
									if (imp == null || imp.getStackSize() == 0) {
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
												int.class, int.class, int.class, int.class, int.class,
												int.class, boolean.class });
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
												new Class[] { int.class, int.class, int.class,
													int.class, int.class });
										final Method i5DsetPixels =
											o.getClass().getMethod("setPixels",
												new Class[] { Object.class });
										final Method i5DsetCalibration =
											o.getClass().getMethod("setCalibration",
												new Class[] { Calibration.class });
										final Method i5Dshow =
											o.getClass().getMethod("show", new Class[] {});
										final Method i5DgetWindow =
											o.getClass().getMethod("getWindow", new Class[] {});
										final Method i5DsetChannelColorModel =
											o.getClass().getMethod("setChannelColorModel",
												new Class[] { int.class, ColorModel.class });

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
										i5Dshow.invoke(o, new Object[] {});
										((ImageWindow) i5DgetWindow.invoke(o, new Object[] {}))
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
				if (returnVal == SelectImageDialog.OK_OPTION) {
					final int[] imageVals = id.getSelected();

					if (imageVals == null & imageVals.length > 0) {
						JOptionPane.showMessageDialog(parent, "No image has been selected",
							"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					final Reader reader = ServiceMediator.getReader();
					for (int i = 0; i < imageVals.length; i++) {
						final ImagePlus imp = WindowManager.getImage(imageVals[i]);
						reader.updateMetadata(imp);
						final FileInfo fi = imp.getOriginalFileInfo();
						if (fi != null && fi instanceof LSMFileInfo) {
							final LSMFileInfo lsm = (LSMFileInfo) fi;
							final CZLSMInfoExtended cz =
								(CZLSMInfoExtended) ((ImageDirectory) lsm.imageDirectories
									.get(0)).TIF_CZ_LSMINFO;
							// System.err.println("dimz:" + cz.DimensionZ);
							// if (cz.DimensionZ/imageVals.length)
							// long depth = (long) (cz.DimensionZ /
							// imageVals.length);
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
		LSMinfoText = str;
	}

	public void applyZSTAMP(final ImagePlus imp, final LSMFileInfo lfi) {
		int x = 2;
		int y = 40;
		double ps = 0;
		final ImageStack stack = imp.getStack();
		final Font font = new Font("SansSerif", Font.PLAIN, 20);
		ImageProcessor ip = imp.getProcessor();
		final Rectangle roi = ip.getRoi();
		if (roi.width < ip.getWidth() || roi.height < ip.getHeight()) {
			x = roi.x;
			y = roi.y + roi.height;
		}
		final Color c = Toolbar.getForegroundColor();
		final CZLSMInfoExtended cz =
			(CZLSMInfoExtended) ((ImageDirectory) lfi.imageDirectories.get(0)).TIF_CZ_LSMINFO;
		if (cz.DimensionZ != 1) {
			final Recording r = cz.scanInfo.recordings.get(0);
			final double planeSpacing =
				((Double) r.records.get("PLANE_SPACING")).doubleValue();
			int stackPosition = 1;
			for (int i = 1; i <= cz.DimensionTime; i++) {
				ps = 0;
				for (int j = 1; j <= cz.DimensionZ; j++) {
					for (int k = 1; k <= cz.DimensionChannels; k++) {
						if (stackPosition <= imp.getStackSize()) {
							IJ.showStatus("MinMax: " + j + "/" + cz.DimensionZ);
							final String s = IJ.d2s(ps, 2) + " " + MasterModel.micrometer;

							ip = stack.getProcessor(stackPosition++);
							ip.setFont(font);
							final float[] hsb =
								Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
							ip.setColor(Color.getHSBColor(255, 255, 255 - hsb[2]));
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
		final Font font = new Font("SansSerif", Font.PLAIN, 20);
		ImageProcessor ip = imp.getProcessor();
		final Rectangle roi = ip.getRoi();
		if (roi.width < ip.getWidth() || roi.height < ip.getHeight()) {
			x = roi.x;
			y = roi.y + roi.height;
		}
		final Color c = Toolbar.getForegroundColor();
		final CZLSMInfoExtended cz =
			(CZLSMInfoExtended) ((ImageDirectory) lfi.imageDirectories.get(0)).TIF_CZ_LSMINFO;
		if (cz.DimensionTime > 1) {
			int stackPosition = 1;
			for (int i = 1; i <= cz.DimensionTime; i++)
				for (int j = 1; j <= cz.DimensionZ; j++)
					for (int k = 1; k <= cz.DimensionChannels; k++) {
						if (stackPosition <= imp.getStackSize()) {
							IJ.showStatus("MinMax: " + stackPosition + "/" +
								cz.timeStamps.NumberTimeStamps);
							final String s =
								IJ.d2s(cz.timeStamps.TimeStamps[i - 1], 2) + " s";
							ip = stack.getProcessor(stackPosition++);
							ip.setFont(font);
							final float[] hsb =
								Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
							ip.setColor(Color.getHSBColor(255, 255, 255 - hsb[2]));
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
		if (roi.width < ip.getWidth() || roi.height < ip.getHeight()) {
			x = roi.x;
			y = roi.y + roi.height;
		}
		final CZLSMInfoExtended cz =
			(CZLSMInfoExtended) ((ImageDirectory) lfi.imageDirectories.get(0)).TIF_CZ_LSMINFO;
		final ImageStack stack = imp.getStack();
		final Font font = new Font("SansSerif", Font.PLAIN, 20);
		final Color c = Toolbar.getForegroundColor();
		if (cz.DimensionChannels > 1 && cz.SpectralScan == 1) {
			int stackPosition = 1;
			for (int i = 1; i <= cz.DimensionTime; i++) {
				for (int j = 1; j <= cz.DimensionZ; j++) {
					for (int k = 1; k <= cz.DimensionChannels; k++) {
						if (stackPosition <= imp.getStackSize()) {
							final double channelWaveLength =
								cz.channelWavelength.LambdaStamps[k - 1];
							final String s =
								IJ.d2s(channelWaveLength * 1000000000, 2) + " nm";
							ip = stack.getProcessor(stackPosition++);
							ip.setFont(font);
							final float[] hsb =
								Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
							ip.setColor(Color.getHSBColor(255, 255, 255 - hsb[2]));
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
		catch (final ClassNotFoundException e) {

		}
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
			catch (final ClassNotFoundException e2) {}
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
			catch (final ClassNotFoundException e2) {}
		}
		return installed;
	}

	public int getSelectedToolBarButtonID() {
		return selectedToolBarButtonID;
	}

	public void resetToolbar() {
		rbButton.setSelected(true);
		selectedToolBarButtonID = -1;

	}
}
