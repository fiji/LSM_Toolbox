
package org.imagearchive.lsm.toolbox.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import org.imagearchive.lsm.toolbox.ServiceMediator;

public class ImageFocusListener implements WindowFocusListener {

	public ImageFocusListener() {}

	@Override
	public void windowGainedFocus(final WindowEvent e) {
		final DetailsFrame details = ServiceMediator.getDetailsFrame();
		final InfoFrame info = ServiceMediator.getInfoFrame();
		if (info != null) info.updateInfoFrame();
		if (details != null) details.updateTreeAndLabels();
	}

	@Override
	public void windowLostFocus(final WindowEvent e) {}
}
