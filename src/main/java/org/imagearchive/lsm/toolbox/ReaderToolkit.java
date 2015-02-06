
package org.imagearchive.lsm.toolbox;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.RandomAccessStream;

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.io.IOException;

public class ReaderToolkit {

	public static short swap(final short x) {
		return (short) ((x << 8) | ((x >> 8) & 0xff));
	}

	public static char swap(final char x) {
		return (char) ((x << 8) | ((x >> 8) & 0xff));
	}

	public static int swap(final int x) {
		return (swap((short) x) << 16) | (swap((short) (x >> 16)) & 0xffff);
	}

	public static long swap(final long x) {
		return ((long) swap((int) (x)) << 32) |
			(swap((int) (x >> 32)) & 0xffffffffL);
	}

	public static float swap(final float x) {
		return Float.intBitsToFloat(swap(Float.floatToIntBits(x)));
	}

	public static double swap(final double x) {
		return Double.longBitsToDouble(swap(Double.doubleToLongBits(x)));
	}

	public static String readSizedNULLASCII(final RandomAccessStream stream,
		final long s)
	{
		int offset = 0;
		String tempstr = new String("");
		int in = 0;
		char ch;
		boolean addchar = true;
		try {
			while (offset < s) {
				in = stream.read();
				if (in == -1) break;
				ch = (char) in;
				if (addchar == true) {
					final String achar = new Character(ch).toString();
					if (ch != 0x00) tempstr += achar;
					else addchar = false;
				}
				offset++;
			}
		}

		catch (final IOException Read_ASCII_exception) {
			Read_ASCII_exception.printStackTrace();
		}
		return tempstr;
	}

	public static String readASCII(final RandomAccessStream stream,
		final long length)
	{
		String rtn = "";
		try {
			for (int i = 0; i < length; i++)
				rtn += (char) (stream.read());
		}
		catch (final IOException Read_ASCII_exception) {
			Read_ASCII_exception.printStackTrace();
		}
		return rtn;
	}

	public static String readNULLASCII(final RandomAccessStream stream) {
		String rtn = "";
		try {
			char ch;

			do {
				ch = (char) (stream.read());
				if (ch != 0) {
					rtn += ch;
				}
			}
			while (ch != 0);
		}
		catch (final IOException Read_ASCII_exception) {
			Read_ASCII_exception.printStackTrace();
		}
		return rtn;
	}

	public static String readNULLASCII2(final RandomAccessStream stream,
		final long s)
	{
		int offset = 0;
		String tempstr = new String("");
		int in = 0;
		char ch;
		try {
			while (offset < s) {
				in = stream.read();
				if (in == -1) break;
				ch = (char) in;
				if (ch != 0) tempstr += Character.toString(ch);
				else return tempstr;
				offset++;
			}
		}
		catch (final IOException Read_ASCII_exception) {
			Read_ASCII_exception.printStackTrace();
		}
		return tempstr;
	}

	/*
	* applyColors, applies color gradient; function taken out from Lut_Panel
	* plugin
	*/

	public static void applyColors(final ImagePlus imp, final int channel,
		final Color[] gc, final int i)
	{
		final FileInfo fi = new FileInfo();
		final int size = 256;
		fi.reds = new byte[size];
		fi.greens = new byte[size];
		fi.blues = new byte[size];
		fi.lutSize = size;
		float nColorsfl = size;
		final float interval = size;
		float iR = gc[0].getRed();
		float iG = gc[0].getGreen();
		float iB = gc[0].getBlue();
		float idR = gc[1].getRed() - gc[0].getRed();
		float idG = gc[1].getGreen() - gc[0].getGreen();
		float idB = gc[1].getBlue() - gc[0].getBlue();
		idR = (idR / interval);
		idG = (idG / interval);
		idB = (idB / interval);
		int a = 0;
		for (a = (int) (interval * 0); a < (int) (interval * (0) + interval); a++, iR +=
			idR, iG += idG, iB += idB)
		{
			fi.reds[a] = (byte) (iR);
			fi.greens[a] = (byte) (iG);
			fi.blues[a] = (byte) (iB);
		}
		final int b = (int) (interval * 0 + interval) - 1;
		fi.reds[b] = (byte) (gc[1].getRed());
		fi.greens[b] = (byte) (gc[1].getGreen());
		fi.blues[b] = (byte) (gc[1].getBlue());
		nColorsfl = size;
		if (nColorsfl > 0) {
			if (nColorsfl < size) interpolate(size, fi.reds, fi.greens, fi.blues,
				(int) nColorsfl);
			showLut(imp, channel, fi, true);
			return;
		}
	}

	/*
	 * interpolate, modified from the ImageJ method by Wayne Rasband.
	 */

	private static void interpolate(final int size, final byte[] reds,
		final byte[] greens, final byte[] blues, final int nColors)
	{
		final byte[] r = new byte[nColors];
		final byte[] g = new byte[nColors];
		final byte[] b = new byte[nColors];
		System.arraycopy(reds, 0, r, 0, nColors);
		System.arraycopy(greens, 0, g, 0, nColors);
		System.arraycopy(blues, 0, b, 0, nColors);
		final double scale = nColors / (float) size;
		int i1, i2;
		double fraction;
		for (int i = 0; i < size; i++) {
			i1 = (int) (i * scale);
			i2 = i1 + 1;
			if (i2 == nColors) i2 = nColors - 1;
			fraction = i * scale - i1;
			reds[i] =
				(byte) ((1.0 - fraction) * (r[i1] & 255) + fraction * (r[i2] & 255));
			greens[i] =
				(byte) ((1.0 - fraction) * (g[i1] & 255) + fraction * (g[i2] & 255));
			blues[i] =
				(byte) ((1.0 - fraction) * (b[i1] & 255) + fraction * (b[i2] & 255));
		}
	}

	/*
	 * showLut, applies the new Lut on the actual image
	 */

	public static void showLut(final ImagePlus imp, final int channel,
		final FileInfo fi, final boolean showImage)
	{
		if (imp != null) {
			if (imp.getType() == ImagePlus.COLOR_RGB) IJ
				.error("Color tables cannot be assiged to RGB Images.");
			else {
				IndexColorModel cm = null;
				cm = new IndexColorModel(8, 256, fi.reds, fi.greens, fi.blues);
				imp.setPosition(channel + 1, imp.getSlice(), imp.getFrame());
				if (imp.isComposite()) {
					((CompositeImage) imp).setChannelColorModel(cm);
					((CompositeImage) imp).updateChannelAndDraw();
				}
				else {
					imp.getProcessor().setColorModel(cm);
					imp.updateAndDraw();
				}
			}
		}
	}
}
