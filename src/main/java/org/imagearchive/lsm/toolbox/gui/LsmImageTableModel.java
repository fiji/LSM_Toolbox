
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
		this.files = new ArrayList();
	}

	@Override
	public int getRowCount() {
		return this.files.size();
	}

	@Override
	public int getColumnCount() {
		return this.columnTitles.length;
	}

	@Override
	public String getColumnName(final int columnIndex) {
		return this.columnTitles[columnIndex];
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		final File file = this.files.get(row);
		if (col == 0) return file.getName();
		if (col == 1) return new DecimalFormat("###.##")
			.format(file.length() / 1024.0F) +
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
		this.files.add(file);
		fireTableDataChanged();
	}

	public void removeFile(final int index) {
		this.files.remove(index);
		fireTableDataChanged();
	}

	public void removeFile(final int row, final int col) {
		this.files.remove(row * this.columnTitles.length + col);
		fireTableDataChanged();
	}

	public void removeAllFiles() {
		this.files.removeAll(this.files);
		fireTableDataChanged();
	}

	@Override
	public void setValueAt(final Object object, final int row, final int col) {
		this.files.set(row * this.columnTitles.length + col, (File) object);
		fireTableDataChanged();
		fireTableCellUpdated(row, col);
	}

	public void insertFile(final Object object, final int row, final int col) {
		this.files.add(row * this.columnTitles.length + col, (File) object);
		fireTableDataChanged();
	}

	public void setFileAt(final File file, final int row, final int col) {
		setValueAt(file, row, col);
	}

	public File getFileAt(final int row, final int col) {
		return this.files.get(row * this.columnTitles.length + col);
	}

	public ArrayList<File> getFiles() {
		return this.files;
	}
}
