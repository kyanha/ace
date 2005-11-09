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

package ch.iserver.ace.application;

import java.util.List;

import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;



public class UserView extends ViewImpl {
	
	public static final String SELECTED_ITEM_PROPERTY = "selectedItem";
	
	private EventList userSourceList;
	private EventListModel userEventListModel;
	private EventSelectionModel userEventSelectionModel;
	protected JList userList;

	public UserView(UserViewController controller, LocaleMessageSource messageSource) {
		super(controller, messageSource);
		// get view source
		userSourceList = controller.getUserSourceList();
		
		// create view toolbar & actions
		JToolBar userToolBar = new JToolBar();

		// create data list
		JTextField userFilterField = new JTextField();
		TextFilterator userFilterator = new TextFilterator() {
			public void getFilterStrings(List baseList, Object element) {
				UserItem item = (UserItem)element;
				baseList.add(item.getName());
			}
		};
		MatcherEditor userMatcherEditor = new TextComponentMatcherEditor(userFilterField, userFilterator);
		SortedList userSortedList = new SortedList(new ObservableElementList(new FilterList(userSourceList, userMatcherEditor), GlazedLists.beanConnector(UserItem.class)));
		userEventListModel = new EventListModel(userSortedList);
		userEventSelectionModel = new EventSelectionModel(userSortedList);

		userList = new JList(userEventListModel);
		userList.setCellRenderer(new UserItemCellRenderer(messageSource));
		userList.setSelectionModel(userEventSelectionModel);
		userList.setSelectionMode(EventSelectionModel.SINGLE_SELECTION);

		// create frame
		JPanel userViewContent = new JPanel(new BorderLayout());
		userViewContent.add(new JScrollPane(userList), BorderLayout.CENTER);
		userViewContent.add(userFilterField, BorderLayout.SOUTH);
		SimpleInternalFrame userView = new SimpleInternalFrame(null, messageSource.getMessage("vUserTitle"), userToolBar, userViewContent);
		setLayout(new BorderLayout());
		add(userView);		
	}

	public synchronized Item getSelectedItem() {
		if (userEventSelectionModel.getMinSelectionIndex() >= 0) {
			return (UserItem)userEventListModel.getElementAt(userEventSelectionModel.getMinSelectionIndex());
		}
		return null;
	}	

}