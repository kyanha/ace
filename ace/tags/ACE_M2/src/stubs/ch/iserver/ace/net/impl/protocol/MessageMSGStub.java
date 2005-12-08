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

import org.beepcore.beep.core.BEEPError;
import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.InputDataStream;
import org.beepcore.beep.core.MessageMSG;
import org.beepcore.beep.core.MessageStatus;
import org.beepcore.beep.core.OutputDataStream;

/**
 *
 */
public class MessageMSGStub implements MessageMSG {

	private OutputDataStream output = null;
	
	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.MessageMSG#sendANS(org.beepcore.beep.core.OutputDataStream)
	 */
	public MessageStatus sendANS(OutputDataStream arg0) throws BEEPException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.MessageMSG#sendERR(org.beepcore.beep.core.BEEPError)
	 */
	public MessageStatus sendERR(BEEPError arg0) throws BEEPException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.MessageMSG#sendERR(int, java.lang.String)
	 */
	public MessageStatus sendERR(int arg0, String arg1) throws BEEPException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.MessageMSG#sendERR(int, java.lang.String, java.lang.String)
	 */
	public MessageStatus sendERR(int arg0, String arg1, String arg2)
			throws BEEPException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.MessageMSG#sendNUL()
	 */
	public MessageStatus sendNUL() throws BEEPException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.MessageMSG#sendRPY(org.beepcore.beep.core.OutputDataStream)
	 */
	public MessageStatus sendRPY(OutputDataStream output) throws BEEPException {
		this.output = output;
		return null;
	}
	
	public OutputDataStream getRPY() {
		return output;
	}

	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.Message#getDataStream()
	 */
	public InputDataStream getDataStream() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.Message#getChannel()
	 */
	public Channel getChannel() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.Message#getMsgno()
	 */
	public int getMsgno() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.Message#getAnsno()
	 */
	public int getAnsno() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.Message#getMessageType()
	 */
	public int getMessageType() {
		throw new UnsupportedOperationException();
	}

}
