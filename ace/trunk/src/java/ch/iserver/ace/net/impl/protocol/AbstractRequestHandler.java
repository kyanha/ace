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

package ch.iserver.ace.net.impl.protocol;

import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.InputDataStream;
import org.beepcore.beep.core.MessageMSG;
import org.beepcore.beep.core.OutputDataStream;
import org.beepcore.beep.core.RequestHandler;
import org.beepcore.beep.util.BufferSegment;

/**
 *
 */
public abstract class AbstractRequestHandler implements RequestHandler {

	protected static final int PIGGYBACKED_MESSAGE_LENGTH = 2;
	private static final String REPLY = "ack";
	
	protected void handlePiggybackedMessage(MessageMSG message) {
		getLogger().info("piggybacked message received.");
		try {
			OutputDataStream out = new OutputDataStream();
			byte[] data = REPLY.getBytes();
			BufferSegment segment = new BufferSegment(data);
			out.add(segment);
			out.setComplete();
			message.sendRPY(out);
		} catch (Exception e) {
			getLogger().error("could not send piggybacked message reply ["+e.getMessage()+"]");
		}
	}
	
	protected byte[] readData(InputDataStream stream) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		do {
            	BufferSegment b = stream.waitForNextSegment();
             if (b == null) {
                 break;
             }
             out.write(b.getData());
        } while (!stream.isComplete());
		out.flush();
		return out.toByteArray();
	}
	
	public abstract void cleanup();
	
	protected abstract Logger getLogger();
}
