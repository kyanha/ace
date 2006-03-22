/*
 * $Id:NullRequestFilter.java 2413 2005-12-09 13:20:12Z zbinl $
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
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.MessageMSG;
import org.beepcore.beep.core.OutputDataStream;

import ch.iserver.ace.net.protocol.ProtocolConstants;
import ch.iserver.ace.net.protocol.Request;

/**
 * NullRequestFilter. If the channel of the request is available, it sends
 * a empty reply.
 * 
 * @see ch.iserver.ace.net.protocol.filter.AbstractRequestFilter
 */
public class NullRequestFilter extends AbstractRequestFilter {

	private static Logger LOG = Logger.getLogger(NullRequestFilter.class);
	
	/**
	 * Public constructor.
	 * 
	 * @param successor
	 */
	public NullRequestFilter(RequestFilter successor) {
		super(successor);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void process(Request request) {
		try {
			if (request.getType() == ProtocolConstants.NULL) {
				LOG.info("--> process()");		
				MessageMSG message = request.getMessage();
				if (message.getChannel() != null &&
						message.getChannel().getState() == Channel.STATE_ACTIVE) {
					LOG.debug("send empty reply");
					OutputDataStream os = new OutputDataStream();
					os.setComplete();
					message.sendRPY(os);
				} else {
					LOG.debug("cannot send empty reply [" + ((message.getChannel() != null) ? 
							message.getChannel() + "] [" + message.getChannel().getState() + "]" : "null") + "]");
				}
				LOG.info("<-- process()");
			} else {
				super.process(request);
			}
		} catch (Exception e) {
			LOG.error("exception processing request ["+e+", "+e.getMessage()+"]");
		}
	}

}
