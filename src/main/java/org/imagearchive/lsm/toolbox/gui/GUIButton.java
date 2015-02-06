
package org.imagearchive.lsm.toolbox.gui;

import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class GUIButton extends JButton {

	public GUIButton(final String buttonText, final String tooltipText) {
		Font font = new Font(null);
		final float fontsize = 11;
		font = font.deriveFont(fontsize);
		font = font.deriveFont(Font.BOLD);
		this.setFont(font);
		this.setText(buttonText);
		this.setForeground(SystemColor.windowText);
		this.setToolTipText(tooltipText);
	}

	public GUIButton(final String buttonText, final String imageResource,
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
