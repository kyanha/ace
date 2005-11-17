/*
 * $Id:PublishedDocumentsRequestFilter.java 1205 2005-11-14 07:57:10Z zbinl $
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

import java.util.Map;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.BEEPError;
import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.MessageMSG;
import org.beepcore.beep.core.OutputDataStream;
import org.beepcore.beep.util.BufferSegment;

import ch.iserver.ace.net.impl.NetworkServiceImpl;

/**
 *
 */
public class PublishedDocumentsRequestFilter extends AbstractRequestFilter {

	private static Logger LOG = Logger.getLogger(PublishedDocumentsRequestFilter.class);
	
	private Serializer serializer;
	
	public PublishedDocumentsRequestFilter(RequestFilter successor) {
		super(successor);
		serializer = SerializerImpl.getInstance();
	}
	
	public void process(Request request) {
		LOG.info("--> process("+request+")");
		if (request.getType() == ProtocolConstants.PUBLISHED_DOCUMENTS) {
			processImpl(request);
		} else { //forward
			super.process(request);
		}
		LOG.info("<-- process()");
	}

	private void processImpl(Request request) {
		Map publishedDocs = NetworkServiceImpl.getInstance().getPublishedDocuments();
		MessageMSG message = request.getMessage();
		try {
			byte[] result = serializer.createResponse(ProtocolConstants.PUBLISHED_DOCUMENTS, publishedDocs);
			OutputDataStream output = prepare(result);
			message.sendRPY(output);
		} catch (Exception e) {
			LOG.error("process problem ["+e.getMessage()+"]");
			//TODO: perhaps create a central ErrorHandler for all beep exceptions
			try {
				message.sendERR(BEEPError.CODE_REQUESTED_ACTION_ABORTED, "");
			} catch (BEEPException be) {
				LOG.error("could not send error message ["+be.getMessage()+"]");
			}
		}
	}
	
	private OutputDataStream prepare(byte[] data) {
		BufferSegment buffer = new BufferSegment(data);
		OutputDataStream output = new OutputDataStream();
		output.add(buffer);
		output.setComplete();
		return output;
	}

}
