
package org.imagearchive.lsm.toolbox.gui;

import org.imagearchive.lsm.reader.info.LSMFileInfo;

public class ListBoxImage {

	public String title = "";

	public String fileName = "";

	public String masses = "";
	public int imageIndex;
	public LSMFileInfo lsmFi;

	public ListBoxImage(final String title, final LSMFileInfo lsmFi,
		final int imageIndex)
	{
		this.title = title;
		this.lsmFi = lsmFi;
		this.imageIndex = imageIndex;
	}

	@Override
	public String toString() {
		return this.title;
	}
}
