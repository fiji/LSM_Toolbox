
package org.imagearchive.lsm.toolbox.gui;

import javax.swing.tree.DefaultMutableTreeNode;

public class InfoNode extends DefaultMutableTreeNode {

	public Object data;
	public String title;

	public InfoNode(final String title, final Object data) {
		super(data);
		this.title = title;
		this.data = data;
	}

	@Override
	public String toString() {
		return this.title;
	}
}
