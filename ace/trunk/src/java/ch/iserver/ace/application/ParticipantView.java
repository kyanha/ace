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
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.EventSelectionModel;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;



public class ParticipantView extends ViewImpl {

	private JToolBar participantToolBar;
	
	public ParticipantView(LocaleMessageSource messageSource, ParticipantViewController controller) {
		super(controller, messageSource);
		// create view toolbar & actions
		participantToolBar = new JToolBar();
		participantToolBar.setFloatable(false);
		participantToolBar.setRollover(true);

		// create data list & filters
		setSourceList(new ObservableElementList(controller.getCompositeSourceList(), GlazedLists.beanConnector(ParticipantItem.class)));
		SortedList participantSortedList = new SortedList(getSourceList());

		// create list & model
		setEventListModel(new EventListModel(participantSortedList));
		setEventSelectionModel(new EventSelectionModel(participantSortedList));

		setList(new JList(getEventListModel()));
		getList().setCellRenderer(new ParticipantViewCellRenderer(messageSource));
		getList().setSelectionModel(getEventSelectionModel());
		getList().setSelectionMode(EventSelectionModel.SINGLE_SELECTION);
		
		// add mouse listener
		getList().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if(event.getValueIsAdjusting()) {
					return;
				}
				ParticipantItem newItem = null;
				try {
					newItem = (ParticipantItem)getEventListModel().getElementAt(getEventSelectionModel().getMinSelectionIndex());
				} catch(ArrayIndexOutOfBoundsException e) {}
				fireItemSelectionChange(newItem);
			}
		});

		// create frame
		JPanel participantViewContent = new JPanel(new BorderLayout());
		participantViewContent.add(new JScrollPane(getList()), BorderLayout.CENTER);
		SimpleInternalFrame participantView = new SimpleInternalFrame(null, messageSource.getMessage("vParticipantTitle"), participantToolBar, participantViewContent);
		setLayout(new BorderLayout());
		add(participantView);		
	}
	
	public Item getSelectedItem() {
		ParticipantItem selectedItem = null;
		try {
			selectedItem = (ParticipantItem)getEventListModel().getElementAt(getEventSelectionModel().getMinSelectionIndex());
		} catch(ArrayIndexOutOfBoundsException e) {}
		return selectedItem;
	}

	public void setToolBarActions(List toolBarActions) {
		for(int i = 0; i < toolBarActions.size(); i++) {
			JButton toolBarButton = participantToolBar.add(((AbstractAction)toolBarActions.get(i)));
			toolBarButton.setBorder(BorderFactory.createEmptyBorder());
			toolBarButton.setBackground(Color.WHITE);
			participantToolBar.addSeparator(new Dimension(3, 0));
		}
	}

}