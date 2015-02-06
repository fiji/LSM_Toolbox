
package org.imagearchive.lsm.toolbox;

import org.imagearchive.lsm.toolbox.gui.ControlPanelFrame;
import org.imagearchive.lsm.toolbox.gui.DetailsFrame;
import org.imagearchive.lsm.toolbox.gui.InfoFrame;

public class ServiceMediator {

	private static ControlPanelFrame controlPanelFrame;
	private static InfoFrame infoFrame;
	private static DetailsFrame detailsFrame;
	private static Reader reader;

	public static void registerReader(Reader reader) {
		reader = reader;
	}

	public static void registerControlPanelFrame(
		ControlPanelFrame controlPanelFrame)
	{
		controlPanelFrame = controlPanelFrame;
	}

	public static void registerInfoFrame(InfoFrame infoFrame) {
		infoFrame = infoFrame;
	}

	public static void registerDetailsFrame(DetailsFrame detailsFrame) {
		detailsFrame = detailsFrame;
	}

	public static ControlPanelFrame getControlPanelFrame() {
		return controlPanelFrame;
	}

	public static InfoFrame getInfoFrame() {
		return infoFrame;
	}

	public static DetailsFrame getDetailsFrame() {
		return detailsFrame;
	}

	public static Reader getReader() {
		if (reader == null) reader = new Reader();
		return reader;
	}
}
