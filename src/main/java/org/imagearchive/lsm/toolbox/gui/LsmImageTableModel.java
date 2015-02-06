
package org.imagearchive.lsm.toolbox.gui;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

public class LsmImageTableModel extends AbstractTableModel {

	public ArrayList<File> files;

	public String[] columnTitles = { "Filename", "Size", "Last modifed" };

	public LsmImageTableModel(final ArrayList<File> files) {
		this.files = files;
	}

	public LsmImageTableModel() {
		files = new ArrayList<File>();
	}

	@Override
	public int getRowCount() {
		return files.size();
	}

	@Override
	public int getColumnCount() {
		return columnTitles.length;
	}

	@Override
	public String getColumnName(final int columnIndex) {
		return columnTitles[columnIndex];
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		final File file = files.get(row);
		if (col == 0) return file.getName();
		if (col == 1) return new DecimalFormat("###.##")
			.format(file.length() / 1024f) +
			" kbytes";
		if (col == 2) return new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss")
			.format(new Date(file.lastModified()));
		return "N/A";
	}

	@Override
	public Class<String> getColumnClass(final int col) {
		return String.class;
	}

	public void addFile(final File file) {
		files.add(file);
		fireTableDataChanged();
	}

	public void removeFile(final int index) {
		files.remove(index);
		fireTableDataChanged();
	}

	public void removeFile(final int row, final int col) {
		files.remove(row * columnTitles.length + col);
		fireTableDataChanged();
	}

	public void removeAllFiles() {
		files.removeAll(files);
		fireTableDataChanged();
	}

	@Override
	public void setValueAt(final Object object, final int row, final int col) {
		files.set(row * columnTitles.length + col, (File) object);
		fireTableDataChanged();
		fireTableCellUpdated(row, col);
	}

	public void insertFile(final Object object, final int row, final int col) {
		files.add(row * columnTitles.length + col, (File) object);
		fireTableDataChanged();
	}

	public void setFileAt(final File file, final int row, final int col) {
		setValueAt(file, row, col);
	}

	public File getFileAt(final int row, final int col) {
		return files.get(row * columnTitles.length + col);
	}

	public ArrayList<File> getFiles() {
		return files;
	}
}
