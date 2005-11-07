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
import java.util.Collection;

import java.awt.event.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;




public class UserView extends ViewImpl {

	private EventList viewSource;
	private boolean userListItemSelected = false;
	private AbstractAction toolbarActBtnDiscoverUser, toolbarActBtnInviteUser;


	public UserView(UserViewController controller, LocaleMessageSource messageSource) {
		super(controller, messageSource);
		/*
		// create view header
		JToolBar toolbar = new JToolBar();
		toolbarActBtnDiscoverUser = new AbstractAction() { public void actionPerformed(ActionEvent e){}};//new DiscoverUserAction("Discover", null);
		toolbar.add(toolbarActBtnDiscoverUser);
		toolbarActBtnInviteUser = new AbstractAction() { public void actionPerformed(ActionEvent e){}};//new InviteUserAction("Invite", null);
		toolbarActBtnInviteUser.setEnabled(false);
		toolbar.add(toolbarActBtnInviteUser);

		// create filtered event list
		JTextField filterField = new JTextField();
		TextFilterator textFilterator = new TextFilterator() {
			public void getFilterStrings(List baseList, Object element) {
				UserItem item = (UserItem)element;
				baseList.add(item.getUserName());
			}
		};
		MatcherEditor matcherEditor = new TextComponentMatcherEditor(filterField, textFilterator);
		viewSource = new BasicEventList();
		FilterList filterList = new FilterList(viewSource, matcherEditor);
		final JList userFilterList = new JList(new EventListModel(new SortedList(filterList)));
		userFilterList.setCellRenderer(new UserItemCellRenderer(source));
		userFilterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userFilterList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(userFilterList.getSelectedIndex() == -1) {
					// no item selected
					setUserListItemSelected(false);
				} else {
					// item selected
					setUserListItemSelected(true);
				}
			}
		});

		// create view body
		JPanel userViewContent = new JPanel(new BorderLayout());
		userViewContent.add(new JScrollPane(userFilterList), BorderLayout.CENTER);
		userViewContent.add(filterField, BorderLayout.SOUTH);

		// create frame
		SimpleInternalFrame userView = new SimpleInternalFrame(null, "UserView", toolbar, userViewContent);
		setLayout(new BorderLayout());
		add(userView);
*/
	}

	/*private void setUserListItemSelected(boolean userListItemSelected) {
		boolean userListItemSelectedOld = this.userListItemSelected;
		if (userListItemSelected != userListItemSelectedOld) {
			this.userListItemSelected = userListItemSelected;
			toolbarActBtnInviteUser.setEnabled(userListItemSelected);
			propertyChangeSupport.firePropertyChange("userListItemSelected", userListItemSelectedOld, userListItemSelected);
		}
	}

	public boolean isUserListItemSelected() {
		return userListItemSelected;
	}

	public EventList getUserViewSource() {
		return viewSource;
	}

	public void setUserViewSource(EventList viewSource) {
		this.viewSource = viewSource;
	}*/

	public Item getSelectedItem() {
		return null;
	}	

}