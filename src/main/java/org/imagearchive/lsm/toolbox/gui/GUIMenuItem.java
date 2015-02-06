
package org.imagearchive.lsm.toolbox.gui;

import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

public class GUIMenuItem extends JMenuItem {

	public GUIMenuItem(final String itemText, final String tooltipText) {
		Font font = new Font(null);
		final float fontsize = 11;
		font = font.deriveFont(fontsize);
		font = font.deriveFont(Font.BOLD);
		this.setFont(font);
		this.setText(itemText);
		this.setForeground(SystemColor.windowText);
		this.setToolTipText(tooltipText);
	}

	public GUIMenuItem(final String buttonText, final String imageResource,
		final String tooltipText)
	{
		Font font = new Font(null);
		final float fontsize = 11;
		font = font.deriveFont(fontsize);
		font = font.deriveFont(Font.BOLD);
		this.setIcon(new ImageIcon(getClass().getResource(imageResource)));
		this.setFont(font);
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setText(buttonText);
		this.setForeground(SystemColor.windowText);
		this.setToolTipText(tooltipText);
	}
}
