
package org.imagearchive.lsm.toolbox.gui;

import ij.ImagePlus;
import ij.io.RandomAccessStream;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.imagearchive.lsm.reader.Reader;

public class ImagePreview extends JPanel implements PropertyChangeListener {

	ImageIcon thumbnail = null;

	ImagePlus imp = null;

	JSlider slider = new JSlider(1, 1, 1);

	File file = null;
	Reader reader;
	JPanel panel;
	Color backgroundcolor = SystemColor.window;

	public ImagePreview(final JFileChooser fc) {
		setPreferredSize(new Dimension(138, 50));
		fc.addPropertyChangeListener(this);

		this.reader = new Reader();
		setLayout(new BorderLayout());
		add(this.slider, "North");
		this.slider.setPaintLabels(true);
		addSliderListener();
		this.backgroundcolor = getBackground();
	}

	private void addSliderListener() {
		this.slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent e) {
				if (ImagePreview.this.imp != null) {
					ImagePreview.this.imp.setSlice(ImagePreview.this.slider.getValue());
					final ImageIcon tmpIcon =
						new ImageIcon(ImagePreview.this.imp.getProcessor().createImage());
					if (tmpIcon != null) {
						if (tmpIcon.getIconWidth() > 128) ImagePreview.this.thumbnail =
							new ImageIcon(tmpIcon.getImage().getScaledInstance(128, -1, 1));
						else {
							ImagePreview.this.thumbnail = tmpIcon;
						}
					}
					ImagePreview.this.repaint();
				}
			}
		});
	}

	public void loadImage() {
		if (this.file == null) {
			this.thumbnail = null;
			this.imp = null;
			return;
		}
		ImageIcon tmpIcon = null;
		try {
			final RandomAccessStream stream =
				new RandomAccessStream(new RandomAccessFile(this.file, "r"));
			if (this.reader.isLSMfile(stream)) {
				this.imp =
					this.reader.open(this.file.getParent(), this.file.getName(), false,
						true);
				if (this.imp != null) {
					this.slider.setValue(1);
					this.slider.setMaximum(this.imp.getNSlices());
					if (this.imp.getNSlices() == 1) {
						this.slider.setVisible(false);
					}
					else {
						this.slider.setLabelTable(this.slider.createStandardLabels(this.imp
							.getNSlices() - 1));
						this.slider.setVisible(true);
					}
					tmpIcon = new ImageIcon(this.imp.getImage());
				}
				else {
					this.thumbnail = null;
					this.imp = null;
					return;
				}
			}
		}
		catch (final IOException e) {
			this.thumbnail = null;
			this.imp = null;
			return;
		}

		if (tmpIcon != null) if (tmpIcon.getIconWidth() > 128) this.thumbnail =
			new ImageIcon(tmpIcon.getImage().getScaledInstance(128, -1, 1));
		else this.thumbnail = tmpIcon;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent e) {
		boolean update = false;
		final String prop = e.getPropertyName();

		if ("directoryChanged".equals(prop)) {
			this.file = null;
			update = true;
		}
		else if ("SelectedFileChangedProperty".equals(prop)) {
			this.file = ((File) e.getNewValue());
			update = true;
		}

		if (update) {
			this.thumbnail = null;
			if (isShowing()) {
				loadImage();
				repaint();
			}
		}
	}

	@Override
	protected void paintComponent(final Graphics g) {
		if (this.thumbnail == null) {
			loadImage();
		}
		if (this.thumbnail != null) {
			int x = getWidth() / 2 - this.thumbnail.getIconWidth() / 2;
			int y =
				getHeight() / 2 - this.thumbnail.getIconHeight() / 2 +
					this.slider.getHeight();

			if (y < 0) {
				y = 0;
			}

			if (x < 5) {
				x = 5;
			}

			g.setColor(this.backgroundcolor);
			g.fillRect(this.slider.getX(), this.slider.getY(), getWidth(),
				getHeight());
			this.thumbnail.paintIcon(this, g, x, y);
		}
	}
}
