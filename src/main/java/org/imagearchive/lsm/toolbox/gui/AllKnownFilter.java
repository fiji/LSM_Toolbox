
package org.imagearchive.lsm.toolbox.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class AllKnownFilter extends FileFilter {

	@Override
	public boolean accept(final File f) {
		if (f.isDirectory()) {
			return true;
		}

		final String extension = getExtension(f);
		if (extension != null) {
			if (extension.equals("lsm") || extension.equals("csv")) return true;
			else return false;

		}

		return false;
	}

	@Override
	public String getDescription() {
		return "Show all known files (*.lsm, *.csv)";
	}

	public static String getExtension(final File f) {
		String ext = null;
		final String s = f.getName();
		final int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

}
