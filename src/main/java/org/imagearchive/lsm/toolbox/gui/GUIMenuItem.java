
package org.imagearchive.lsm.toolbox.gui;

import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

public class GUIMenuItem extends JMenuItem {

	public GUIMenuItem(final String itemText, final String tooltipText) {
		Font font = new Font(null);
		final float fontsize = 11.0F;
		font = font.deriveFont(fontsize);
		font = font.deriveFont(1);
		setFont(font);
		setText(itemText);
		setForeground(SystemColor.windowText);
		setToolTipText(tooltipText);
	}

	public GUIMenuItem(final String buttonText, final String imageResource,
		final String tooltipText)
	{
		Font font = new Font(null);
		final float fontsize = 11.0F;
		font = font.deriveFont(fontsize);
		font = font.deriveFont(1);
		setIcon(new ImageIcon(getClass().getResource(imageResource)));
		setFont(font);
		setHorizontalAlignment(2);
		setText(buttonText);
		setForeground(SystemColor.windowText);
		setToolTipText(tooltipText);
	}
}
