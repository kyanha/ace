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

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import java.util.List;
import com.jgoodies.uif_lite.panel.SimpleInternalFrame;



public class UserView extends ViewImpl {
	
	private JToolBar userToolBar;

	public UserView(LocaleMessageSource messageSource, UserViewController controller) {
		super(controller, messageSource);
		// create view toolbar & actions
		userToolBar = new JToolBar();
		userToolBar.setFloatable(false);
		userToolBar.setRollover(true);
		
		// create data list & filters
		JTextField userFilterField = new JTextField();
		TextFilterator userFilterator = new TextFilterator() {
			public void getFilterStrings(List baseList, Object element) {
				UserItem item = (UserItem)element;
				baseList.add(item.getName());
			}
		};
		MatcherEditor userMatcherEditor = new TextComponentMatcherEditor(userFilterField, userFilterator);
		
		setSourceList(new ObservableElementList(new FilterList(controller.getSourceList(), userMatcherEditor), GlazedLists.beanConnector(UserItem.class)));
		SortedList userSortedList = new SortedList(getSourceList());

		// create list & model
		setEventListModel(new EventListModel(userSortedList));
		setEventSelectionModel(new EventSelectionModel(userSortedList));

		setList(new JList(getEventListModel()));
		getList().setCellRenderer(new UserViewCellRenderer(messageSource));
		getList().setSelectionModel(getEventSelectionModel());
		getList().setSelectionMode(EventSelectionModel.SINGLE_SELECTION);
		
		// add mouse listener
		getList().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if(event.getValueIsAdjusting()) {
					return;
				}
				UserItem newItem = null;
				try {
					newItem = (UserItem)getEventListModel().getElementAt(getEventSelectionModel().getMinSelectionIndex());
				} catch(ArrayIndexOutOfBoundsException e) { }
				fireItemSelectionChange(newItem);
			}
		});

		// create frame
		JPanel userViewContent = new JPanel(new BorderLayout());
		userViewContent.add(new JScrollPane(getList()), BorderLayout.CENTER);
		userViewContent.add(userFilterField, BorderLayout.SOUTH);
		SimpleInternalFrame userView = new SimpleInternalFrame(null, messageSource.getMessage("vUserTitle"), userToolBar, userViewContent);
		setLayout(new BorderLayout());
		add(userView);		
	}

	public synchronized Item getSelectedItem() {
		UserItem selectedItem = null;
		try {
			selectedItem = (UserItem)getEventListModel().getElementAt(getEventSelectionModel().getMinSelectionIndex());
		} catch(ArrayIndexOutOfBoundsException e) {}
		return selectedItem;
	}	

	public void setToolBarActions(List toolBarActions) {
		for(int i = 0; i < toolBarActions.size(); i++) {
			JButton toolBarButton = userToolBar.add(((AbstractAction)toolBarActions.get(i)));
			toolBarButton.setBorder(BorderFactory.createEmptyBorder());
			userToolBar.addSeparator();
		}
	}

}