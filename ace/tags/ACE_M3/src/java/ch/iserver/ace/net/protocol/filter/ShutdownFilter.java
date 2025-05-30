/*
 * $Id:ShutdownFilter.java 2413 2005-12-09 13:20:12Z zbinl $
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
import org.beepcore.beep.core.MessageMSG;
import org.beepcore.beep.core.OutputDataStream;

import ch.iserver.ace.net.protocol.ProtocolConstants;
import ch.iserver.ace.net.protocol.Request;

/**
 *
 */
public class ShutdownFilter extends AbstractRequestFilter {

	private Logger LOG = Logger.getLogger(ShutdownFilter.class);
	
	public ShutdownFilter(RequestFilter successor) {
		super(successor);
	}

	public void process(Request request) {
		if (request.getType() == ProtocolConstants.SHUTDOWN) {
			LOG.warn("Network layer terminated, stop forwarding request [" + request + "]");
			MessageMSG message = request.getMessage();
			if (message != null) {
				try {				
					OutputDataStream os = new OutputDataStream();
					os.setComplete();
					request.getMessage().sendRPY(os);
//					request.getMessage().sendNUL();
				} catch (Exception e) {
					LOG.error("could not send confirmation ["+e+", "+e.getMessage()+"]");
				}
			}
		} else {
			super.process(request);
		}
	}
	
}
