/*
 * $Id:ParticipantItem.java 1091 2005-11-09 13:29:05Z zbinl $
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

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.RemoteUser;



public class ParticipantItem extends ItemImpl implements Comparable, PropertyChangeListener {

	private String name;
	private Color color;
	private RemoteUser user;
	private Participant participant;

	public ParticipantItem(Participant participant, Color color) {
		user = participant.getUser();
		user.addPropertyChangeListener(this);
		name = user.getName();
		this.color = color;
		this.participant = participant;
	}

	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public RemoteUser getUser() {
		return user;
	}
	
	public Participant getParticipant() {
		return participant;
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
//		System.out.println("" + evt);
		if(evt.getPropertyName().equals(RemoteUser.NAME_PROPERTY)) {
			name = (String)evt.getNewValue();
			firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
	}

	public int compareTo(Object o) {
		return -((ParticipantItem)o).getName().compareTo(name);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof ParticipantItem) {
			ParticipantItem participantItem = (ParticipantItem)obj;
			return getUser().equals(participantItem.getUser());
		}
		return super.equals(obj);
	}
	
	public int hashCode() {
		return getUser().hashCode();
	}

}

