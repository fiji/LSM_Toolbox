
package org.imagearchive.lsm.toolbox.gui;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.ImageWindow;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import org.imagearchive.lsm.toolbox.Reader;
import org.imagearchive.lsm.toolbox.ServiceMediator;

public class CPDragAndDrop implements DropTargetListener {

	protected static ImageJ ij = null;
	private static boolean enableDND = true;
	protected DataFlavor dFlavor;
	private ControlPanelFrame cp;

	public CPDragAndDrop(final ControlPanelFrame cp) {
		final String vers = System.getProperty("java.version");
		if (vers.compareTo("1.3.1") < 0) {
			return;
		}
		this.cp = cp;

		final DropTarget dropTarget = new DropTarget(cp, this);
	}

	@Override
	public void drop(final DropTargetDropEvent dtde) {
		dtde.acceptDrop(1);
		try {
			final Transferable t = dtde.getTransferable();
			if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				final Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
				final Iterator iterator = ((List) data).iterator();

				while (iterator.hasNext()) {
					final File file = (File) iterator.next();

					final Reader reader = ServiceMediator.getReader();
					SwingUtilities.invokeLater(new Runnable() {

						ImageWindow iwc = null;

						@Override
						public void run() {
							try {
								IJ.showStatus("Loading image");
								final ImagePlus imp = reader.open(file.getAbsolutePath(), true);
								IJ.showStatus("Image loaded");
								if (imp == null) return;
								imp.setPosition(1, 1, 1);
								imp.show();
							}
							catch (final OutOfMemoryError e) {
								IJ.outOfMemory("Could not load lsm image.");
							}
						}
					});
				}
			}
		}
		catch (final Exception e) {
			dtde.dropComplete(false);
			return;
		}
		dtde.dropComplete(true);
	}

	@Override
	public void dragEnter(final DropTargetDragEvent dtde) {
		dtde.acceptDrag(1);
	}

	@Override
	public void dragOver(final DropTargetDragEvent e) {}

	@Override
	public void dragExit(final DropTargetEvent e) {}

	@Override
	public void dropActionChanged(final DropTargetDragEvent e) {}
}
