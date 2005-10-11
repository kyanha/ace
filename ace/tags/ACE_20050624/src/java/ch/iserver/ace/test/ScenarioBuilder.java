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

package ch.iserver.ace.test;

import ch.iserver.ace.Operation;

/**
 * This interface contains the elementary operations needed to construct a
 * scenario. It is based on the builder pattern (see GOF). 
 */
public interface ScenarioBuilder {

	/**
	 * Initializes the builder and passes in the initial state and the
	 * expected final state.
	 * 
	 * @param initialState the initial state at all sites
	 * @param finalState the expected final state
	 */
	public void init(String initialState, String finalState);
	
	/**
	 * Adds an operation with the given id to the builder.
	 * 
	 * @param id the identifier of the operation
	 * @param op the operation itself
	 * @throws ScenarioException in case of errors
	 */
	public void addOperation(String id, Operation op);
	
	/**
	 * Notifies the builder of the start of a new site to process.
	 * Calls to startSite and endSite must be executed in proper
	 * sequence. Most notably, after calling startSite this method
	 * cannot be executed again until endSite is called.
	 * 
	 * @param siteId the identifier of the site
	 * @throws ScenarioException in case of errors
	 */
	public void startSite(String siteId);
	
	/**
	 * Adds the reception of an operation to the current site. The
	 * current site is the last site for which startSite was called.
	 * This method must be called within startSite/endSite.
	 * 
	 * @param opRef the operation to be received
	 * @throws ScenarioException in case of errors
	 */
	public void addReception(String opRef);
	
	/**
	 * Adds the generation of an operation to the current site. The
	 * current site is the last site for which startSite was called.
	 * 
	 * @param opRef the operation to be generated
	 * @throws ScenarioException in case of errors
	 */
	public void addGeneration(String opRef);
	
	/**
	 * Notifies the builder of the end of a site. A call to startSite
	 * must preceed this call.
	 * 
	 * @throws ScenarioException in case of errors
	 */
	public void endSite();
	
}