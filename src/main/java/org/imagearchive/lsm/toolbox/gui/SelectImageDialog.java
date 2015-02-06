
package org.imagearchive.lsm.toolbox.gui;

import ij.WindowManager;
import ij.io.FileInfo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.imagearchive.lsm.reader.info.ImageDirectory;
import org.imagearchive.lsm.reader.info.LSMFileInfo;
import org.imagearchive.lsm.toolbox.MasterModel;
import org.imagearchive.lsm.toolbox.info.CZLSMInfoExtended;

public class SelectImageDialog extends JDialog {

	private JPanel panel;
	private JList imageList;
	private final MasterModel masterModel = MasterModel.getMasterModel();
	private Vector<ListBoxImage> fileInfos;
	private Vector<String> images;
	private String label = "Please select:";
	private JButton okButton;
	private JButton cancelButton;
	private int returnVal = -1;

	private int[] values = null;
	public static final int OK_OPTION = 0;
	public static final int CANCEL_OPTION = 2;
	private boolean channel = false;

	public SelectImageDialog(final JFrame parent, final String label,
		final boolean channel, final byte filter)
	{
		super(parent, true);
		this.label = label;
		this.channel = channel;
		initiliazeGUI();
		fillList(filter);
	}

	public SelectImageDialog(final JFrame parent, final String label,
		final boolean channel)
	{
		super(parent, true);
		this.label = label;
		this.channel = channel;
		initiliazeGUI();
		fillList(MasterModel.NONE);
	}

	private void initiliazeGUI() {
		this.panel = new JPanel();
		this.imageList = new JList();
		this.okButton =
			new JButton("OK", new ImageIcon(getClass().getResource("images/ok.png")));
		this.cancelButton =
			new JButton("Cancel", new ImageIcon(getClass().getResource(
				"images/cancel.png")));
		this.panel.setLayout(new GridBagLayout());
		final GridBagConstraints constraints = new GridBagConstraints();
		setSize(new Dimension(200, 300));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.fill = 2;

		constraints.ipadx = 0;
		constraints.ipady = 10;
		constraints.weightx = 0.25D;
		constraints.weighty = 0.25D;
		this.panel.add(new JLabel(this.label), constraints);
		constraints.gridy = 1;
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.weightx = 1.0D;
		constraints.weighty = 1.0D;
		constraints.fill = 1;
		this.panel.add(this.imageList, constraints);
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.ipadx = 0;
		constraints.ipady = 10;
		constraints.weightx = 0.25D;
		constraints.weighty = 0.25D;
		constraints.fill = 2;
		this.panel.add(this.okButton, constraints);
		constraints.gridx = 1;
		this.panel.add(this.cancelButton, constraints);
		getContentPane().add(this.panel);
		setTitle("Select...");
		if (this.channel) this.imageList.setSelectionMode(2);
		else this.imageList.setSelectionMode(0);
		setListeners();
		centerWindow();
	}

	private void fillList(final byte filter) {
		final int[] imagesIDs = WindowManager.getIDList();
		this.images = new Vector();
		this.fileInfos = new Vector();
		if (imagesIDs == null) return;
		for (int i = 0; i < imagesIDs.length; i++) {
			if (WindowManager.getImage(imagesIDs[i]) != null) {
				final FileInfo fi =
					WindowManager.getImage(imagesIDs[i]).getOriginalFileInfo();
				boolean add = false;
				if ((fi != null) && ((fi instanceof LSMFileInfo))) {
					final LSMFileInfo lsm = (LSMFileInfo) fi;
					final CZLSMInfoExtended cz =
						(CZLSMInfoExtended) ((ImageDirectory) lsm.imageDirectories.get(0)).TIF_CZ_LSMINFO;
					if ((filter == MasterModel.TIME) && (cz.DimensionTime > 1L)) add =
						true;
					if ((filter == MasterModel.DEPTH) && (cz.DimensionZ > 1L)) add = true;
					if ((filter == MasterModel.CHANNEL) && (cz.SpectralScan == 1) &&
						(cz.channelWavelength != null) &&
						(cz.channelWavelength.Channels >= 1L)) add = true;
					if (filter == MasterModel.NONE) add = true;
					if (add) {
						this.images.add(lsm.fileName);
						this.fileInfos
							.add(new ListBoxImage(lsm.fileName, lsm, imagesIDs[i]));
					}
				}
			}
		}
		final ComboBoxModel cbm = new DefaultComboBoxModel(this.images);
		this.imageList.setModel(cbm);
	}

	private void setListeners() {
		this.okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (SelectImageDialog.this.imageList.getModel().getSize() == 0) return;
				final int[] selectedIndices =
					SelectImageDialog.this.imageList.getSelectedIndices();
				SelectImageDialog.this.values = new int[selectedIndices.length];
				for (int i = 0; i < selectedIndices.length; i++) {
					final ListBoxImage im =
						SelectImageDialog.this.fileInfos.get(selectedIndices[i]);
					SelectImageDialog.this.values[i] = im.imageIndex;
				}
				SelectImageDialog.this.returnVal = 0;
				SelectImageDialog.this.setVisible(false);
			}
		});
		this.cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				SelectImageDialog.this.returnVal = 2;
				SelectImageDialog.this.setVisible(false);
			}
		});
	}

	public int showDialog() {
		setVisible(true);
		dispose();
		return this.returnVal;
	}

	public int[] getSelected() {
		return this.values;
	}

	public void centerWindow() {
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2,
			(screenSize.height - getHeight()) / 2);
	}
}
