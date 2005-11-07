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

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventSelectionModel;



public class ParticipantView extends ViewImpl {

	private EventList participantSourceList;
	private EventSelectionModel eventSelectionModel;

	public ParticipantView(ParticipantViewController controller, LocaleMessageSource messageSource) {
		super(controller, messageSource);
		// get view source
		participantSourceList = controller.getParticipantSourceList();
		
		
		// create view toolbar & actions
		JToolBar participantToolBar = new JToolBar();
		//final AbstractAction vtbaDiscoverUser = new AbstractAction() { public void actionPerformed(ActionEvent e) { System.out.println("Discover User"); }};
		//viewToolBar.add(vtbaDiscoverUser);
		//final AbstractAction vtbaJoinSession = new AbstractAction() { public void actionPerformed(ActionEvent e) { System.out.println("Join Session"); }};
		//viewToolBar.add(vtbaJoinSession);

		// create data list
		JTextField filterField = new JTextField();
		JList participantList = new JList();
		
		// create frame
		JPanel participantViewContent = new JPanel(new BorderLayout());
		participantViewContent.add(new JScrollPane(participantList), BorderLayout.CENTER);
		participantViewContent.add(filterField, BorderLayout.SOUTH);
		SimpleInternalFrame participantView = new SimpleInternalFrame(null, messageSource.getMessage("vParticipantTitle"), participantToolBar, participantViewContent);
		setLayout(new BorderLayout());
		add(participantView);		
		
	}
	
	public Item getSelectedItem() {
		return null;
	}

}