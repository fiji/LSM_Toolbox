
package org.imagearchive.lsm.toolbox.gui;

import ij.ImagePlus;
import ij.WindowManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
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
import org.imagearchive.lsm.toolbox.info.scaninfo.Recording;

public class NotesDialog extends JDialog {

	private JTextArea tsnotes;
	private JTextArea tdnotes;
	private JPanel panel;
	private final MasterModel masterModel = MasterModel.getMasterModel();

	public NotesDialog(final JFrame parent, final boolean modal) {
		super(parent, modal);
		initializeGUI();
	}

	private void initializeGUI() {
		setTitle("LSM Notes");
		getContentPane().setLayout(new BorderLayout());
		final JLabel snotes = new JLabel("Short Notes :");
		final JLabel dnotes = new JLabel("Detailed Notes :");
		this.tsnotes = new JTextArea("");
		this.tdnotes = new JTextArea("");
		this.tsnotes.setEditable(false);
		this.tsnotes.setEditable(false);
		this.tdnotes.setRows(4);
		this.tdnotes.setRows(4);
		this.tsnotes.setColumns(20);
		this.tdnotes.setColumns(20);
		this.panel = new JPanel();

		this.panel.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = 2;

		this.panel.add(snotes, gbc);
		gbc.gridy = 1;
		this.panel.add(dnotes, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = 1;
		this.panel.add(this.tsnotes, gbc);
		gbc.gridy = 1;
		this.panel.add(this.tdnotes, gbc);
		getContentPane().add(this.panel, "North");
		final JButton okb = new JButton("Ok");
		addokbListener(okb, this);
		getContentPane().add(okb, "South");
		pack();
		centerWindow();
	}

	public void setNotes() {
		final ImagePlus imp = WindowManager.getCurrentImage();
		final Reader reader = ServiceMediator.getReader();
		reader.updateMetadata(imp);
		if (imp == null) return;
		if ((imp.getOriginalFileInfo() instanceof LSMFileInfo)) {
			final LSMFileInfo lsm = (LSMFileInfo) imp.getOriginalFileInfo();

			final ArrayList imageDirectories = lsm.imageDirectories;
			final ImageDirectory imDir = (ImageDirectory) imageDirectories.get(0);
			if (imDir == null) return;
			final CZLSMInfoExtended cz = (CZLSMInfoExtended) imDir.TIF_CZ_LSMINFO;
			final Recording r = cz.scanInfo.recordings.get(0);
			if (r == null) return;
			final String shortNotes = (String) r.records.get("ENTRY_DESCRIPTION");
			final String detailedNotes = (String) r.records.get("ENTRY_NOTES");
			this.tsnotes.setText(shortNotes);
			this.tdnotes.setText(detailedNotes);
		}
	}

	private void addokbListener(final JButton button, final JDialog parent) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				NotesDialog.this.setVisible(false);
			}
		});
	}

	public void centerWindow() {
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2,
			(screenSize.height - getHeight()) / 2);
	}
}
