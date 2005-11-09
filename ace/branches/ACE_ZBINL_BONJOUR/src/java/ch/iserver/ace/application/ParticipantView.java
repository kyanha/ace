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
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;

import ca.odell.glazedlists.CompositeList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.EventSelectionModel;



public class ParticipantView extends ViewImpl {

	private CompositeList participantSourceList;
	private EventListModel participantEventListModel;
	private EventSelectionModel participantEventSelectionModel;
	protected JList participantList;

	public ParticipantView(ParticipantViewController controller, LocaleMessageSource messageSource) {
		super(controller, messageSource);
		// get view source
		participantSourceList = controller.getParticipantSourceList();
		
		// create view toolbar & actions
		JToolBar participantToolBar = new JToolBar();

		// create data list
		SortedList participantSortedList = new SortedList(new ObservableElementList(participantSourceList, GlazedLists.beanConnector(ParticipantItem.class)));
		participantEventListModel = new EventListModel(participantSortedList);
		participantEventSelectionModel = new EventSelectionModel(participantSortedList);

		participantList = new JList(participantEventListModel);
		participantList.setCellRenderer(new ParticipantItemCellRenderer(messageSource));
		participantList.setSelectionModel(participantEventSelectionModel);
		participantList.setSelectionMode(EventSelectionModel.SINGLE_SELECTION);
		
		// create frame
		JPanel participantViewContent = new JPanel(new BorderLayout());
		participantViewContent.add(new JScrollPane(participantList), BorderLayout.CENTER);
		SimpleInternalFrame participantView = new SimpleInternalFrame(null, messageSource.getMessage("vParticipantTitle"), participantToolBar, participantViewContent);
		setLayout(new BorderLayout());
		add(participantView);		
	}
	
	public Item getSelectedItem() {
		if(participantEventSelectionModel.getMinSelectionIndex() >= 0) {
			return (ParticipantItem)participantEventListModel.getElementAt(participantEventSelectionModel.getMinSelectionIndex());
		}
		return null;
	}

}