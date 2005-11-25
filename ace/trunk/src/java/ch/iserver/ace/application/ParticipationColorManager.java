/*
 * $Id:ParticipationColorManager.java 1091 2005-11-09 13:29:05Z zbinl $
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
import java.util.HashMap;

import ch.iserver.ace.collaboration.Participant;



public class ParticipationColorManager {

	private HashMap participantColorMap;
	private int participantCount = 0;
	private Color[] defaultColors = {
		new Color(0xFF, 0x60, 0x60), new Color(0xFF, 0xDD, 0x60),
		new Color(0xFF, 0xFF, 0x60), new Color(0x60, 0xDD, 0x60),
		new Color(0x60, 0xFF, 0xFF), new Color(0x60, 0x60, 0xFF),
		new Color(0xDD, 0x60, 0xFF), new Color(0xFF, 0x60, 0xDD),
	};

	public ParticipationColorManager() {
		participantColorMap = new HashMap();
	}
	
	public synchronized Color participantJoined(Participant participant) {
		// add the new participant to the map and create new color for him
		// or return the old color from this aprticipant
		Color participantColor;
		if(participantColorMap.containsKey("" + participant.getParticipantId())) {
			// return old participant color
			participantColor = getHighlightColor(participant);
		} else {
			// next participant color
			participantColor = defaultColors[participantCount++%8];
			participantColorMap.put("" + participant.getParticipantId(), participantColor);
		}
		return participantColor;
	}
	
	public void participantLeft(Participant participant) {
		// do nothing to keep the participants who left
		//participantColorMap.remove("" + participant.getParticipantId());
	}
	
	public Color getHighlightColor(Participant participant) {
		if(participantColorMap.containsKey("" + participant.getParticipantId())) {
			return (Color)participantColorMap.get("" + participant.getParticipantId());
		}
		return new Color(0xFF, 0xFF, 0xFF);
	}

}