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

package ch.iserver.ace.collaboration.jupiter;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;

/**
 * Wrapper interface for algorithm instances. Adds methods for receiving
 * and generating CaretUpdate and the corresponding CaretUpdateMessage.
 */
public interface AlgorithmWrapper {
	
	/**
	 * Retrieves the wrapped algorithm instance.
	 * 
	 * @return the wrapped Algorithm instance
	 */
	Algorithm getAlgorithm();
	
	/**
	 * Receives a Request and returns the transformed Operation.
	 * 
	 * @param request the Request to receive
	 * @return the transformed Operation
	 */
	Operation receiveRequest(Request request);
	
	/**
	 * Receives a CaretUpdateMessage and returns the transformed CaretUpdate.
	 * 
	 * @param message the CaretUpdateMessage to receive
	 * @return the transformed CaretUpdate
	 */
	CaretUpdate receiveCaretUpdateMessage(CaretUpdateMessage message);
	
	/**
	 * Generates a Request for a locally generated Operation.
	 * 
	 * @param operation the locally generated Operation
	 * @return the Request to be sent
	 */
	Request generateRequest(Operation operation);
	
	/**
	 * Generates a CaretUpdateMessage for a locally generated CaretUpdate.
	 * 
	 * @param update the locally generated CaretUpdate
	 * @return the CaretUpdateMessage to be sent
	 */
	CaretUpdateMessage generateCaretUpdateMessage(CaretUpdate update);
	
}
