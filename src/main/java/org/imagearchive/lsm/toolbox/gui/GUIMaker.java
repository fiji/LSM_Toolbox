
package org.imagearchive.lsm.toolbox.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;

public class GUIMaker {

	public static Container addComponentToGrid(final Component component,
		final Container container, final int x, final int y, final int width,
		final int height, final int fill, final int anchor, final double weightx,
		final double weighty)
	{
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = x;
		constraints.gridy = y;
		constraints.gridwidth = width;
		constraints.gridheight = height;
		constraints.weightx = weightx;
		constraints.weighty = weighty;
		constraints.anchor = anchor;
		constraints.fill = fill;
		container.add(component, constraints);
		return container;
	}
}
