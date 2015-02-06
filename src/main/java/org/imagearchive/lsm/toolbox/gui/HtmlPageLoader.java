
package org.imagearchive.lsm.toolbox.gui;

import java.awt.Cursor;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;

public class HtmlPageLoader extends Thread {

	private final Object[][] pages;
	private final JDialog c;

	public HtmlPageLoader(final JDialog c, final Object[][] pages) {
		this.pages = pages;
		this.c = c;
	}

	@Override
	public void run() {
		this.c.setCursor(Cursor.getPredefinedCursor(3));
		for (int i = 0; i < this.pages.length; i++) {
			try {
				final URL source = getClass().getResource((String) this.pages[i][1]);
				((JEditorPane) this.pages[i][0]).setPage(source);
			}
			catch (final Exception e) {
				JOptionPane.showMessageDialog(this.c,
					"Could not load page. Jar must be corrupted", "Warning", 2);
				System.err.println(e.getMessage());
			}
		}
		((JEditorPane) this.pages[0][0]).validate();
		this.c.setCursor(Cursor.getPredefinedCursor(0));
	}
}
