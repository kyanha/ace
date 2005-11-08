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

package ch.iserver.ace.net.beep.profile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.BEEPError;
import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.CloseChannelException;
import org.beepcore.beep.core.InputDataStream;
import org.beepcore.beep.core.MessageMSG;
import org.beepcore.beep.core.OutputDataStream;
import org.beepcore.beep.core.RequestHandler;
import org.beepcore.beep.core.Session;
import org.beepcore.beep.core.StartChannelException;
import org.beepcore.beep.core.StartChannelListener;
import org.beepcore.beep.profile.Profile;
import org.beepcore.beep.profile.ProfileConfiguration;
import org.beepcore.beep.util.BufferSegment;

/**
 *
 */
public class ACEProfile implements Profile, StartChannelListener, RequestHandler {

	private static Logger LOG = Logger.getLogger(ACEProfile.class);
	
    public static final String ACE_URI =
        "http://ace.iserver.ch/profiles/ACE";
    
    private QueryProcessor processor = new TLVQueryProcessor();
	
	/**
	 * @see org.beepcore.beep.profile.Profile#init(java.lang.String, org.beepcore.beep.profile.ProfileConfiguration)
	 */
	public StartChannelListener init(String uri, ProfileConfiguration config)
			throws BEEPException {
		return this;
	}

	public boolean advertiseProfile(Session arg0) throws BEEPException {
		//always advertise the profile
		return true;
	}

	public void startChannel(Channel channel, String encoding, String data) throws StartChannelException {
		channel.setRequestHandler(this);
	}

	public void closeChannel(Channel channel) throws CloseChannelException {
		channel.setRequestHandler(null);
	}

	public void receiveMSG(MessageMSG message) {
		System.out.println("S: receiveMSG");
		
		InputDataStream xmlInput = message.getDataStream();
		//parse xml
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		do {
            try {
            		System.out.println("S: waitForNextSegment");
            		BufferSegment b = xmlInput.waitForNextSegment();
        			System.out.println("S: ok "+b.getLength());
                if (b == null) {
                		out.flush();
                    break;
                }
                out.write(b.getData());
            } catch (Exception e) {
                message.getChannel().getSession().terminate(e.getMessage());
                return;
            }
        } while (!xmlInput.isComplete());
		
		System.out.println("S: read "+out.size()+" bytes from input.");
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		out = process(in);
		OutputDataStream xmlOutput = new OutputDataStream();
		byte[] data = out.toByteArray();
		BufferSegment segment = new BufferSegment(data);
		xmlOutput.add(segment);
		xmlOutput.setComplete();
		try {
			message.sendRPY(xmlOutput);
		} catch (BEEPException be) {
			try {
                message.sendERR(BEEPError.CODE_REQUESTED_ACTION_ABORTED,
                                "Error sending RPY");
            } catch (BEEPException x) {
                message.getChannel().getSession().terminate(x.getMessage());
            }
            return;
		}
	}
	
	private ByteArrayOutputStream process(InputStream input) {
		try {
			byte[] result = processor.process(input);
			ByteArrayOutputStream output = new ByteArrayOutputStream(result.length);
			output.write(result);
			return output;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
