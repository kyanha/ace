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

package ch.iserver.ace.collaboration.jupiter.server.serializer;

import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.algorithm.TransformationException;
import ch.iserver.ace.collaboration.jupiter.server.Forwarder;

/**
 *
 */
public class AcknowledgeSerializerCommand extends AbstractSerializerCommand {

	private final int siteId;
	
	private final Timestamp timestamp;
	
	public AcknowledgeSerializerCommand(int participantId, Algorithm algorithm, int siteId, Timestamp timestamp) {
		super(participantId, algorithm);
		this.siteId = siteId;
		this.timestamp = timestamp;
	}
	
	protected int getSiteId() {
		return siteId;
	}
	
	protected Timestamp getTimestamp() {
		return timestamp;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.serializer.SerializerCommand#execute(ch.iserver.ace.collaboration.jupiter.server.Forwarder)
	 */
	public void execute(Forwarder forwarder) throws SerializerException {
		try {
			getAlgorithm().acknowledge(getSiteId(), getTimestamp());
		} catch (TransformationException e) {
			throw new SerializerException(getParticipantId(), e);
		}
	}

}
