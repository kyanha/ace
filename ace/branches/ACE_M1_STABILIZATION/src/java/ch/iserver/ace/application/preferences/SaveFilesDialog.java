/*
 * $Id$
 *
 * ace - a collaborative editor
 * Copyright (C) 2005 Mark Bigler, Simon Raess, Lukas Zbinden
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package ch.iserver.ace.application.preferences;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import ch.iserver.ace.application.DocumentItem;
import ch.iserver.ace.application.LocaleMessageSource;
import ch.iserver.ace.application.LocaleMessageSourceStub;
import ch.iserver.ace.application.dialog.TitledDialog;

/**
 *
 */
public class SaveFilesDialog extends TitledDialog {
	
	private JTable table;
	private SaveFilesTableModel tableModel;
	
	public SaveFilesDialog(Frame owner, LocaleMessageSource messages, List files) {
		super(owner, messages,
		           messages.getMessage("dSaveFilesTitle"),
		           messages.getIcon("iSaveFilesTitle"));
		setModal(true);
		setMessage(messages.getMessage("dSaveFilesMessage"));
		tableModel = new SaveFilesTableModel(files);
	}
	
	public Set getCheckedFiles() {
		return tableModel.getCheckedFiles();
	}
	
	/**
	 * @see ch.iserver.ace.application.dialog.TitledDialog#createContent()
	 */
	protected JComponent createContent() {
		table = new JTable();
		table.setTableHeader(null);
		table.setBackground(Color.WHITE);
		JScrollPane pane = new JScrollPane(table);
		pane.getViewport().setBackground(Color.WHITE);
		pane.setSize(400, 200);
		return pane;
	}
	
	protected void init() {
		table.setModel(tableModel);
		table.getColumnModel().getColumn(1).setCellRenderer(new DocumentItemCellRenderer());
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setMaxWidth(20);
	}

	/**
	 * @see ch.iserver.ace.application.dialog.TitledDialog#createButtonPane()
	 */
	protected JPanel createButtonPane() {
		JPanel pane = new JPanel();
		JButton okButton = new JButton(getMessages().getMessage("dOk"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				finish();
			}
		});
		JButton cancelButton = new JButton(getMessages().getMessage("dCancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hideDialog();
			}
		});
		
		pane.add(cancelButton);
		pane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		
		return pane;
	}
	
	private static class DocumentItemCellRenderer extends DefaultTableCellRenderer {
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			label.setText(((DocumentItem) value).getTitle());
			return label;
		}
		
	}
	
	private static class SaveFilesTableModel extends AbstractTableModel {
		
		private static final int CHECKBOX_COLUMN = 0;
		
		private final List files;
		
		private final Map checked;
		
		public SaveFilesTableModel(List files) {
			this.files = files;
			this.checked = new HashMap(files.size() + 1);
			Iterator it = files.iterator();
			while (it.hasNext()) {
				DocumentItem file = (DocumentItem) it.next();
				checked.put(file, Boolean.TRUE);
			}
		}
		
		public Set getCheckedFiles() {
			Set result = new HashSet();
			Iterator keys = checked.keySet().iterator();
			while (keys.hasNext()) {
				DocumentItem item = (DocumentItem) keys.next();
				if (((Boolean) checked.get(item)).booleanValue()) {
					result.add(item);
				}
			}
			return result;
		}
		
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == CHECKBOX_COLUMN;
		}
		
		public String getColumnName(int column) {
			if (column == CHECKBOX_COLUMN) {
				return "";
			} else {
				return "NAME";
			}
		}
		
		public int getColumnCount() {
			return 2;
		}
		
		public int getRowCount() {
			return files.size();
		}
		
		public Class getColumnClass(int columnIndex) {
			if (columnIndex == CHECKBOX_COLUMN) {
				return Boolean.class;
			} else {
				return DocumentItem.class;
			}
		}
		
		public Object getValueAt(int rowIndex, int columnIndex) {
			DocumentItem item = (DocumentItem) files.get(rowIndex);
			if (columnIndex == CHECKBOX_COLUMN) {
				return checked.get(item);
			} else {
				return files.get(rowIndex);
			}
		}
		
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			DocumentItem item = (DocumentItem) files.get(rowIndex);
			if (columnIndex == CHECKBOX_COLUMN) {
				checked.put(item, (Boolean) aValue);
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		List files = new LinkedList();
		files.add(new DocumentItem("test 1"));
		files.add(new DocumentItem("test 2"));
		files.add(new DocumentItem("test 3"));
		files.add(new DocumentItem("test 4"));
		
		LocaleMessageSource messages = new LocaleMessageSourceStub();
		SaveFilesDialog dialog = new SaveFilesDialog(null, messages, files);
		dialog.setModal(true);
		dialog.showDialog();
		dialog.dispose();
		
		Set checked = dialog.getCheckedFiles();
		Iterator it = checked.iterator();
		while (it.hasNext()) {
			DocumentItem item = (DocumentItem) it.next();
			System.out.println(item.getTitle());
		}
	}

}
