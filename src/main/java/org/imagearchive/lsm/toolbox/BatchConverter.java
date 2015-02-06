
package org.imagearchive.lsm.toolbox;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.MedianCut;
import ij.process.ShortProcessor;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.imagearchive.lsm.reader.Reader;
import org.imagearchive.lsm.reader.info.CZLSMInfo;
import org.imagearchive.lsm.reader.info.ImageDirectory;
import org.imagearchive.lsm.reader.info.LSMFileInfo;

public class BatchConverter {

	private final MasterModel masterModel;

	public BatchConverter(final MasterModel masterModel) {
		this.masterModel = masterModel;
	}

	public void convertFile(final String file, final String outputDir,
		final String format, final boolean verbose, final boolean sepDir)
	{
		String finalDir = "";
		final File f = new File(file);

		final Reader r = new Reader();
		final ImagePlus imp = r.open(f.getParent(), f.getName(), false, false);
		if ((imp != null) && (imp.getStackSize() > 0)) {
			final LSMFileInfo lsm = (LSMFileInfo) imp.getOriginalFileInfo();
			final CZLSMInfo cz =
				(CZLSMInfo) ((ImageDirectory) lsm.imageDirectories.get(0)).TIF_CZ_LSMINFO;
			if (sepDir) {
				finalDir =
					outputDir + System.getProperty("file.separator") + f.getName();
				final File fdir = new File(finalDir);
				if (!fdir.exists()) fdir.mkdirs();
			}
			else {
				finalDir = outputDir;
			}
			int position = 1;
			for (int i = 1; i <= cz.DimensionTime; i++)
				for (int j = 1; j <= cz.DimensionZ; j++)
					for (int k = 1; k <= cz.DimensionChannels; k++) {
						final String title =
							lsm.fileName + " - " +
								cz.channelNamesAndColors.ChannelNames[(k - 1)] + " - C" +
								new Integer(k).toString() + " Z" + new Integer(j).toString() +
								" T" + new Integer(i).toString();
						save(new ImagePlus(title, imp.getImageStack().getProcessor(
							position++)), finalDir, format, title);
					}
		}
	}

	public void convertBatchFile(final String fileName) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String row = null;

			while ((row = br.readLine()) != null) {
				final String[] arr = row.split("\t");
				final String inputFile = arr[0];
				final String outputDir = arr[1];
				String format = arr[2];
				if (arr[2] == null) format = "tiff";
				boolean verbose = false;
				boolean createSepDir = false;
				if (!arr[3].equals("0")) verbose = true;
				if (!arr[4].equals("0")) createSepDir = true;
				IJ.showStatus("Conversion started");
				IJ.showStatus("Converting " + new File(inputFile).getName());
				convertFile(inputFile, outputDir, format, verbose, createSepDir);
				IJ.showStatus("Conversion done");
			}
		}
		catch (final IOException e) {
			IJ.error("Incompatible batch file format");
			IJ.log("IOException error: " + e.getMessage());

			if (br != null) try {
				br.close();
			}
			catch (final IOException e1) {
				IJ.log("IOException error trying to close the file: " + e1.getMessage());
			}
		}
		finally {
			if (br != null) try {
				br.close();
			}
			catch (final IOException e) {
				IJ.log("IOException error trying to close the file: " + e.getMessage());
			}
		}
	}

	public ImagePlus process(final ImagePlus imp) {
		return imp;
	}

	public void save(final ImagePlus img, final String dir, final String format,
		final String fileName)
	{
		final String path = dir + System.getProperty("file.separator") + fileName;
		if (format.equals("Tiff")) new FileSaver(img).saveAsTiff(path + ".tif");
		else if (format.equals("8-bit Tiff")) saveAs8bitTiff(img, path + ".tif");
		else if (format.equals("Zip")) new FileSaver(img).saveAsZip(path + ".zip");
		else if (format.equals("Raw")) new FileSaver(img).saveAsRaw(path + ".raw");
		else if (format.equals("Jpeg")) new FileSaver(img)
			.saveAsJpeg(path + ".jpg");
	}

	void saveAs8bitTiff(final ImagePlus img, final String path) {
		ImageProcessor ip = img.getProcessor();
		if ((ip instanceof ColorProcessor)) {
			ip = reduceColors(ip);
			img.setProcessor(null, ip);
		}
		else if (((ip instanceof ShortProcessor)) ||
			((ip instanceof FloatProcessor)))
		{
			ip = ip.convertToByte(true);
			img.setProcessor(null, ip);
		}
		new FileSaver(img).saveAsTiff(path);
	}

	ImageProcessor reduceColors(final ImageProcessor ip) {
		final MedianCut mc =
			new MedianCut((int[]) ip.getPixels(), ip.getWidth(), ip.getHeight());
		final Image img = mc.convert(256);
		return new ByteProcessor(img);
	}
}
