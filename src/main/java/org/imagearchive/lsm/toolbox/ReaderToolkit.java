
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
		return (short) (x << 8 | x >> 8 & 0xFF);
	}

	public static char swap(final char x) {
		return (char) (x << '\b' | x >> '\b' & 0xFF);
	}

	public static int swap(final int x) {
		return swap((short) x) << 16 | swap((short) (x >> 16)) & 0xFFFF;
	}

	public static long swap(final long x) {
		return swap((int) x) << 32 | swap((int) (x >> 32)) & 0xFFFFFFFF;
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

		boolean addchar = true;
		try {
			while (offset < s) {
				in = stream.read();
				if (in == -1) break;
				final char ch = (char) in;
				if (addchar) {
					final String achar = new Character(ch).toString();
					if (ch != 0) tempstr = tempstr + achar;
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
				rtn = rtn + (char) stream.read();
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
				ch = (char) stream.read();
				if (ch != 0) rtn = rtn + ch;
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
		try {
			while (offset < s) {
				in = stream.read();
				if (in == -1) break;
				final char ch = (char) in;
				if (ch != 0) tempstr = tempstr + Character.toString(ch);
				else return tempstr;
				offset++;
			}
		}
		catch (final IOException Read_ASCII_exception) {
			Read_ASCII_exception.printStackTrace();
		}
		return tempstr;
	}

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
		idR /= interval;
		idG /= interval;
		idB /= interval;
		int a = 0;
		for (a = (int) (interval * 0.0F); a < (int) (interval * 0.0F + interval); iB +=
			idB)
		{
			fi.reds[a] = ((byte) (int) iR);
			fi.greens[a] = ((byte) (int) iG);
			fi.blues[a] = ((byte) (int) iB);

			a++;
			iR += idR;
			iG += idG;
		}

		final int b = (int) (interval * 0.0F + interval) - 1;
		fi.reds[b] = ((byte) gc[1].getRed());
		fi.greens[b] = ((byte) gc[1].getGreen());
		fi.blues[b] = ((byte) gc[1].getBlue());
		nColorsfl = size;
		if (nColorsfl > 0.0F) {
			if (nColorsfl < size) interpolate(size, fi.reds, fi.greens, fi.blues,
				(int) nColorsfl);
			showLut(imp, channel, fi, true);
			return;
		}
	}

	private static void interpolate(final int size, final byte[] reds,
		final byte[] greens, final byte[] blues, final int nColors)
	{
		final byte[] r = new byte[nColors];
		final byte[] g = new byte[nColors];
		final byte[] b = new byte[nColors];
		System.arraycopy(reds, 0, r, 0, nColors);
		System.arraycopy(greens, 0, g, 0, nColors);
		System.arraycopy(blues, 0, b, 0, nColors);
		final double scale = nColors / size;

		for (int i = 0; i < size; i++) {
			final int i1 = (int) (i * scale);
			int i2 = i1 + 1;
			if (i2 == nColors) i2 = nColors - 1;
			final double fraction = i * scale - i1;
			reds[i] =
				((byte) (int) ((1.0D - fraction) * (r[i1] & 0xFF) + fraction *
					(r[i2] & 0xFF)));
			greens[i] =
				((byte) (int) ((1.0D - fraction) * (g[i1] & 0xFF) + fraction *
					(g[i2] & 0xFF)));
			blues[i] =
				((byte) (int) ((1.0D - fraction) * (b[i1] & 0xFF) + fraction *
					(b[i2] & 0xFF)));
		}
	}

	public static void showLut(final ImagePlus imp, final int channel,
		final FileInfo fi, final boolean showImage)
	{
		if (imp != null) if (imp.getType() == 4) {
			IJ.error("Color tables cannot be assiged to RGB Images.");
		}
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
