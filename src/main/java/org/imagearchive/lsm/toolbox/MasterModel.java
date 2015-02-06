
package org.imagearchive.lsm.toolbox;

import ij.IJ;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MasterModel {

	private static MasterModel masterModel;
	public static final String VERSION = "4.0g";
	public static boolean debugMode = false;

	public static char micro = '�';

	public static String micrometer = micro + "m";

	public static byte NONE = 0;

	public static byte CHANNEL = 1;

	public static byte DEPTH = 2;

	public static byte TIME = 3;

	public String[] supportedBatchTypes = { "Tiff", "8-bit Tiff", "Jpeg", "Zip",
		"Raw" };
	public String[] macroFiles = { "magic_montage.txt" };
	public String[] macros = new String[this.macroFiles.length];

	public static MasterModel getMasterModel() {
		if (masterModel == null) masterModel = new MasterModel();
		return masterModel;
	}

	public MasterModel() {
		initializeModel();
		registerServices();
		readMacros();
	}

	public void initializeModel() {}

	public void readMacros() {
		for (int i = 0; i < this.macroFiles.length; i++) {
			final InputStream in =
				getClass().getClassLoader().getResourceAsStream(
					"org/imagearchive/lsm/toolbox/macros/" + this.macroFiles[i]);
			try {
				if (in == null) throw new IOException();
				final BufferedReader reader =
					new BufferedReader(new InputStreamReader(in));
				final StringBuffer macroBuffer = new StringBuffer();
				String line;
				while ((line = reader.readLine()) != null) {
					macroBuffer.append(line + "\n");
				}
				this.macros[i] = macroBuffer.toString();
				reader.close();
			}
			catch (final IOException e) {
				this.macros[i] = null;
				IJ.error("Could not load internal macro.");
			}
		}
	}

	private void registerServices() {}

	public String getVersion() {
		return "4.0g";
	}

	public String getMacro(final int i) {
		if ((i >= 0) && (i < this.macros.length)) {
			return this.macros[i];
		}
		return null;
	}

	public String getMagicMontaqe() {
		final StringBuffer sb = new StringBuffer();
		String ext_macro = null;
		float ext_ver = 0.0F;
		float int_ver = 0.0F;
		String int_macro = new String();
		final String toolsSetDir =
			IJ.getDirectory("macros") + File.separator + "toolsets";
		try {
			final File f =
				new File(toolsSetDir + File.separator + "magic_montage.txt");
			final BufferedReader input = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = input.readLine()) != null) {
				sb.append(line);
				sb.append(System.getProperty("line.separator"));
			}
			input.close();
			ext_macro = sb.toString();
		}
		catch (final IOException localIOException) {}
		if (ext_macro != null) try {
			ext_ver =
				Float.parseFloat(ext_macro.substring(
					ext_macro.indexOf("//--version--") + 13, ext_macro.indexOf("\n")));
		}
		catch (final NumberFormatException localNumberFormatException) {}
		int_macro = getMacro(0);
		if (int_macro != null) try {
			int_ver =
				Float.parseFloat(int_macro.substring(
					int_macro.indexOf("//--version--") + 13, int_macro.indexOf("\n")));
		}
		catch (final NumberFormatException localNumberFormatException1) {}
		if (int_ver < ext_ver) return ext_macro;
		return int_macro;
	}
}
