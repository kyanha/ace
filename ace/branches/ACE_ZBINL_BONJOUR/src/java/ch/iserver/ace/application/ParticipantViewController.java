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

import ca.odell.glazedlists.CompositeList;
import ca.odell.glazedlists.EventList;



public class ParticipantViewController extends ViewControllerImpl {

	private CompositeList participantSourceList;

	public ParticipantViewController() {
		participantSourceList = new CompositeList();
	}
	
	public void setParticipantList(EventList participantList) {
		participantSourceList.clear();
		participantSourceList.addMemberList(participantList);
	}

	private ParticipantView getParticipantView() {
		if(view == null) throw new IllegalStateException("View have to be set before using getView()!");
		return (ParticipantView)view;
	}
	
	public CompositeList getParticipantSourceList() {
		return participantSourceList;
	}
	
	public ParticipantItem getSelectedParticipantItem() {
		return (ParticipantItem)getParticipantView().getSelectedItem();
	}

}

