/*
 * $Id:FailureFilter.java 1205 2005-11-14 07:57:10Z zbinl $
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

package ch.iserver.ace.net.protocol.filter;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.BEEPError;
import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.MessageMSG;

import ch.iserver.ace.net.protocol.Request;

/**
 * Catches all requests which could not be filtered.
 * This class is used at the end of the request filter chain.
 */
public class FailureFilter extends AbstractRequestFilter {

	private static Logger LOG = Logger.getLogger(FailureFilter.class);
	
	/**
	 * Constructor.
	 * 
	 * @param successor the successor
	 */
	public FailureFilter(AbstractRequestFilter successor) {
		super(successor);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void process(Request request) {
		LOG.debug(request+" could not be processed, reply with error code.");
		MessageMSG message = request.getMessage();
		if (message != null) {
			try {
				message.sendERR(BEEPError.CODE_SERVICE_NOT_AVAILABLE, "could not process request.");
			} catch (BEEPException be) {			
				LOG.error("could not send BEEPError ["+be.getMessage()+"]");
			}
		} else {
			LOG.error("unknown request could not be serialized ["+request.getType()+"]");
		}
		
	}
}
