
package org.imagearchive.lsm.toolbox.gui;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.text.html.HTMLEditorKit;

import org.imagearchive.lsm.toolbox.MasterModel;

public class AboutDialog extends JDialog {

	private MasterModel masterModel;
	private JEditorPane about;
	private JEditorPane changelog;
	private JEditorPane iconset;
	private JEditorPane license;
	private JEditorPane help;
	private JScrollPane aboutScroller;
	private JScrollPane changelogScroller;
	private JScrollPane helpScroller;
	private JScrollPane licenseScroller;
	private JScrollPane iconsetScroller;
	private JTabbedPane tabber;
	private JButton okButton;
	private final Dimension ScreenDimension = Toolkit.getDefaultToolkit()
		.getScreenSize();

	private final int ScreenX = (int) this.ScreenDimension.getWidth();

	private final int ScreenY = (int) this.ScreenDimension.getHeight();
	private JLabel infoTitle;
	private final String infoText =
		"<html><center>LSM_Toolbox ver 4.0g (C) 2003-2009 Patrick Pirrotte </center></html>";

	public AboutDialog(final JFrame parent, final MasterModel masterModel)
		throws HeadlessException
	{
		super(parent, true);
		this.masterModel = masterModel;
		initializeGUI();
		loadPages();
	}

	public AboutDialog() throws HeadlessException {
		initializeGUI();
	}

	public void initializeGUI() {
		setTitle("About");
		this.tabber = new JTabbedPane();
		this.okButton =
			new JButton(new ImageIcon(getClass().getResource("images/ok.png")));
		this.aboutScroller = new JScrollPane();
		this.changelogScroller = new JScrollPane();
		this.about = new JEditorPane();
		this.changelog = new JEditorPane();
		this.license = new JEditorPane();
		this.iconset = new JEditorPane();
		this.help = new JEditorPane();
		this.helpScroller = new JScrollPane();
		this.licenseScroller = new JScrollPane();
		this.iconsetScroller = new JScrollPane();
		this.infoTitle = new JLabel();
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent evt) {
				AboutDialog.this.dispose();
			}
		});
		this.tabber.setPreferredSize(new Dimension(this.ScreenX / 2,
			this.ScreenY / 2));
		this.infoTitle.setText(this.infoText);
		this.aboutScroller.setViewportView(this.about);
		this.changelogScroller.setViewportView(this.changelog);
		this.help.setEditorKit(new HTMLEditorKit());
		this.iconsetScroller.setViewportView(this.iconset);
		this.helpScroller.setViewportView(this.help);
		this.licenseScroller.setViewportView(this.license);

		this.tabber.addTab("About", this.aboutScroller);
		this.tabber.addTab("Changelog", this.changelogScroller);
		this.tabber.addTab("Help", this.helpScroller);
		this.tabber.addTab("LSM_Toolbox Licence", this.licenseScroller);
		this.tabber.addTab("Nuvola Iconset License", this.iconsetScroller);

		final String loadingText = "Loading... please wait...";
		this.about.setText(loadingText);
		this.changelog.setText(loadingText);
		this.help.setText(loadingText);
		this.license.setText(loadingText);
		this.iconset.setText(loadingText);

		this.about.setContentType("text/html");
		this.about.setEditable(false);
		this.changelog.setContentType("text/html");
		this.changelog.setEditable(false);
		this.iconset.setContentType("text/html");
		this.iconset.setEditable(false);
		this.license.setContentType("text/html");
		this.license.setEditable(false);
		this.help.setContentType("text/html");
		this.help.setEditable(false);

		this.infoTitle.setBorder(BorderFactory.createEtchedBorder());
		this.infoTitle.setHorizontalAlignment(0);

		getContentPane().add(this.infoTitle, "North");
		getContentPane().add(this.tabber, "Center");
		getContentPane().add(this.okButton, "South");
		this.okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				AboutDialog.this.dispose();
			}
		});
		pack();
		centerWindow();
	}

	public void loadPages() {
		final Object[][] pages =
			{ { this.about, "html/about.htm" },
				{ this.changelog, "html/changelog.htm" },
				{ this.iconset, "html/lgpl.txt" },
				{ this.license, "html/licence.txt" }, { this.help, "html/help.htm" } };
		final HtmlPageLoader loader = new HtmlPageLoader(this, pages);
		loader.start();
	}

	public void centerWindow() {
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2,
			(screenSize.height - getHeight()) / 2);
	}

	public static void main(final String[] args) {
		new AboutDialog(new JFrame(), null).setVisible(true);
		System.exit(-1);
	}
}
