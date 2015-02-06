
package org.imagearchive.lsm.toolbox.gui;

import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.swing.table.AbstractTableModel;

class TreeTableModel extends AbstractTableModel {

	private final String[] columnNames = { "Tag", "Property" };

	private LinkedHashMap<String, Object> dataMap = null;

	private Object[][] data = null;

	private boolean filtered = false;

	public TreeTableModel(final LinkedHashMap<String, Object> dataMap) {
		this.dataMap = dataMap;
		setData(dataMap);
	}

	public TreeTableModel() {}

	@Override
	public String getColumnName(final int col) {
		return columnNames[col].toString();
	}

	@Override
	public int getRowCount() {
		if (data == null) return 0;
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		return data[row][col];
	}

	@Override
	public boolean isCellEditable(final int row, final int col) {
		return false;
	}

	@Override
	public void setValueAt(final Object value, final int row, final int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}

	public void setData(LinkedHashMap<String, Object> dataMap) {
		if (dataMap != null) {
			if (filtered) dataMap = getFilteredMap(dataMap);
			final Iterator<String> iterator = dataMap.keySet().iterator();
			String tag;
			data = new Object[dataMap.size()][2];
			for (int i = 0; iterator.hasNext(); i++) {
				tag = iterator.next();
				data[i][0] = tag;
				data[i][1] = dataMap.get(tag);
			}
		}
		else data = null;
		fireTableDataChanged();
	}

	public LinkedHashMap<String, Object> getFilteredMap(
		final LinkedHashMap<String, Object> dataMap)
	{
		final LinkedHashMap<String, Object> filteredMap =
			new LinkedHashMap<String, Object>();
		final Iterator<String> iterator = dataMap.keySet().iterator();
		String tag;
		data = new Object[dataMap.size()][2];
		for (int i = 0; iterator.hasNext(); i++) {
			tag = iterator.next();
			if (tag.indexOf("<UNKNOWN@") == -1) filteredMap
				.put(tag, dataMap.get(tag));
		}
		return filteredMap;
	}

	public void setFiltered(final boolean filtered) {
		this.filtered = filtered;
	}

	public boolean getFiltered() {
		return filtered;
	}
}
