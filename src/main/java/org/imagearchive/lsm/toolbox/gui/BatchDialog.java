
package org.imagearchive.lsm.toolbox.gui;

import ij.IJ;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.imagearchive.lsm.toolbox.BatchConverter;
import org.imagearchive.lsm.toolbox.MasterModel;

public class BatchDialog extends JDialog {

	private JButton okButton;
	private JButton cancelButton;
	private final MasterModel masterModel;
	private final String title = "Zeiss LSM batch conversion";
	private JPanel buttonsPanel;
	private JPanel mainPanel;
	private JLabel sourceLabel;
	private JLabel outputLabel;
	private JLabel formatLabel;
	private JTable sourceTable;
	private JScrollPane sourcePane;
	private JTextField outputTF;
	private JButton sourceButton;
	private JButton outputButton;
	private JButton resetButton;
	private JComboBox formatCombo;
	private JCheckBox verboseCB;
	private JCheckBox dirCB;
	private final JFrame parent;

	public BatchDialog(final Object parent, final MasterModel masterModel) {
		super((JFrame) parent, true);
		this.parent = ((JFrame) parent);
		this.masterModel = masterModel;
		initComponents();
		setGUI();
		centerWindow();
		setListeners();
	}

	public void initComponents() {
		this.okButton =
			new JButton("Run batch", new ImageIcon(getClass().getResource(
				"images/ok.png")));
		this.cancelButton =
			new JButton("Cancel", new ImageIcon(getClass().getResource(
				"images/cancel.png")));
		this.sourceLabel = new JLabel("Select source folder");
		this.outputLabel = new JLabel("Select output folder");
		this.sourceTable = new JTable();
		this.sourcePane = new JScrollPane();
		this.outputTF = new JTextField();
		this.sourceButton = new JButton("Browse");
		this.outputButton = new JButton("Browse");
		this.resetButton = new JButton("Reset list");
		this.formatLabel = new JLabel("Save as type:");
		this.formatCombo = new JComboBox(this.masterModel.supportedBatchTypes);
		this.verboseCB = new JCheckBox("Verbose (popups on error!)");
		this.dirCB = new JCheckBox("Output each image to separate directory");
		this.buttonsPanel = new JPanel();
		this.mainPanel = new JPanel();
	}

	public void setGUI() {
		getContentPane().setLayout(new BorderLayout());

		this.mainPanel.setLayout(new GridBagLayout());

		this.sourcePane.setViewportView(this.sourceTable);

		this.mainPanel =
			((JPanel) GUIMaker.addComponentToGrid(this.sourceLabel, this.mainPanel,
				0, 0, 1, 1, 2, 11, 0.125D, 1.0D));

		this.sourcePane.setMinimumSize(new Dimension(400, 200));

		this.mainPanel =
			((JPanel) GUIMaker.addComponentToGrid(this.sourcePane, this.mainPanel, 1,
				0, 1, 4, 1, 10, 1.0D, 1.0D));

		this.mainPanel =
			((JPanel) GUIMaker.addComponentToGrid(this.sourceButton, this.mainPanel,
				2, 0, 1, 1, 2, 11, 0.125D, 0.5D));

		this.mainPanel =
			((JPanel) GUIMaker.addComponentToGrid(this.outputLabel, this.mainPanel,
				0, 5, 1, 1, 1, 10, 0.125D, 1.0D));

		this.mainPanel =
			((JPanel) GUIMaker.addComponentToGrid(this.outputTF, this.mainPanel, 1,
				5, 1, 1, 2, 10, 0.125D, 1.0D));

		this.mainPanel =
			((JPanel) GUIMaker.addComponentToGrid(this.outputButton, this.mainPanel,
				2, 5, 1, 1, 2, 10, 0.125D, 0.5D));

		this.mainPanel =
			((JPanel) GUIMaker.addComponentToGrid(this.formatLabel, this.mainPanel,
				0, 6, 1, 1, 2, 10, 0.125D, 0.5D));

		this.mainPanel =
			((JPanel) GUIMaker.addComponentToGrid(this.formatCombo, this.mainPanel,
				1, 6, 1, 1, 2, 10, 0.125D, 0.5D));

		this.mainPanel =
			((JPanel) GUIMaker.addComponentToGrid(this.dirCB, this.mainPanel, 0, 7,
				3, 1, 2, 10, 0.125D, 0.5D));

		this.mainPanel =
			((JPanel) GUIMaker.addComponentToGrid(this.verboseCB, this.mainPanel, 0,
				8, 3, 1, 2, 10, 0.125D, 0.5D));

		this.buttonsPanel.add(this.resetButton);
		this.buttonsPanel.add(this.okButton);
		this.buttonsPanel.add(this.cancelButton);
		this.verboseCB.setSelected(true);
		getContentPane().add(this.mainPanel, "Center");
		getContentPane().add(this.buttonsPanel, "South");
		pack();
		setTitle(this.title);
	}

	public void setListeners() {
		this.okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final File outputDir = new File(BatchDialog.this.outputTF.getText());
				if (BatchDialog.this.sourceTable.getModel().getRowCount() <= 0) {
					IJ.error("You have to select some files or a directory containing images first!");
					return;
				}
				final ArrayList list =
					((LsmImageTableModel) BatchDialog.this.sourceTable.getModel())
						.getFiles();
				if ((BatchDialog.this.outputTF.getText() != "" | (outputDir
					.isDirectory() | outputDir.exists()))) if (!outputDir.exists()) {
					final int result =
						JOptionPane
							.showConfirmDialog(
								new JFrame(),
								"The output directory does not exist. Do you want to create it and continue the processing?",
								"Create directory", 0);
					if ((result == 0) && (outputDir.mkdirs())) BatchDialog.this
						.doConvert(list, outputDir);
				}
				else {
					BatchDialog.this.doConvert(list, outputDir);
				}

			}
		});
		this.cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				BatchDialog.this.dispose();
			}
		});
		this.sourceButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(2);
				fc.setMultiSelectionEnabled(true);
				fc.setDialogTitle("Select a source directory or multiselect files");
				fc.addChoosableFileFilter(new ImageFilter());
				final int returnVal = fc.showDialog(null, "Select source");
				if (returnVal == 0) {
					final File[] files = fc.getSelectedFiles();
					final LsmImageTableModel tm = new LsmImageTableModel();
					for (int i = 0; i < files.length; i++) {
						BatchDialog.this.processPath(tm, files[i]);
					}
					BatchDialog.this.sourceTable.setModel(tm);
				}
			}
		});
		this.outputButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Select a output directory");
				fc.setFileSelectionMode(1);
				fc.setAcceptAllFileFilterUsed(false);
				final int returnVal = fc.showDialog(null, "Select target directory");
				if (returnVal == 0) {
					final File file = fc.getSelectedFile();
					BatchDialog.this.outputTF.setText(file.getAbsolutePath());
				}
			}
		});
		this.resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				((LsmImageTableModel) BatchDialog.this.sourceTable.getModel())
					.removeAllFiles();
			}
		});
	}

	public void doConvert(final ArrayList list, final File outputDir) {
		final BatchConverter converter = new BatchConverter(this.masterModel);
		IJ.showStatus("Conversion started");
		for (int i = 0; i < list.size(); i++) {
			IJ.showStatus("Converting " + i + "/" + list.size());
			converter.convertFile(((File) list.get(i)).getAbsolutePath(), outputDir
				.getAbsolutePath(), (String) this.formatCombo.getSelectedItem(),
				this.verboseCB.isSelected(), this.dirCB.isSelected());
		}
		IJ.showProgress(1.0D);
		IJ.showStatus("Conversion done");
		IJ.showMessage("Conversion done");
		dispose();
	}

	public LsmImageTableModel processPath(LsmImageTableModel tm, final File path)
	{
		if (path.isDirectory()) {
			final String[] children = path.list();
			for (int i = 0; i < children.length; i++) {
				tm = processPath(tm, new File(path, children[i]));
			}
		}
		else if (ImageFilter.getExtension(path).equals("lsm")) {
			tm.addFile(path);
		}
		return tm;
	}

	public void centerWindow() {
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2,
			(screenSize.height - getHeight()) / 2);
	}
}
