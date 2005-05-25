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
package ch.iserver.ace.net.beep;

import java.io.IOException;
import java.io.InputStream;

import org.beepcore.beep.core.BEEPError;
import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.Session;
import org.beepcore.beep.core.StringOutputDataStream;
import org.beepcore.beep.lib.Reply;
import org.beepcore.beep.profile.echo.EchoProfile;
import org.beepcore.beep.transport.tcp.TCPSessionCreator;

/**
 * The echo client calls a echo server implemented with BEEP. The command line
 * syntax is:
 * 
 * <pre>
 *  java ch.iserver.ace.net.beep.EchoClient host count size
 * </pre>
 * 
 * <var>host</var> is the host to connect to, <var>count</var> is the number
 * of messages to send and <var>size</var> is the size of messages to
 * send.
 */
public class EchoClient {

	public static void main(String[] argv) throws BEEPException, IOException {
		final String host = argv[0];
		final int count = Integer.parseInt(argv[1]);
		final int size = Integer.parseInt(argv[2]);

		// Initiate a session with the server
		Session session = TCPSessionCreator.initiate(host, EchoServer.PORT);

		// Start a channel for the echo profile
		Channel channel;
		try {
			channel = session.startChannel(EchoProfile.ECHO_URI);
		} catch (BEEPError e) {
			if (e.getCode() == 550) {
				System.err.println("bing: Error host does not support "
						+ "echo profile");
			} else {
				System.err.println("bing: Error starting channel ("
						+ e.getCode() + ": " + e.getMessage() + ")");
			}
			return;
		} catch (BEEPException e) {
			System.err.println("bing: Error starting channel ("
					+ e.getMessage() + ")");
			return;
		}

		// create a bogus request
		String request = createRequest(size);

		for (int i = 0; i < count; ++i) {
			long time;
			int replyLength = 0;
			Reply reply = new Reply();

			time = System.currentTimeMillis();

			// Send the request
			channel.sendMSG(new StringOutputDataStream(request), reply);

			// Get the reply to the request
			InputStream is = reply.getNextReply().getDataStream()
					.getInputStream();

			// Read the data in the reply
			while (is.read() != -1) {
				++replyLength;
			}

			System.out.println("Reply from " + host + ": bytes="
					+ replyLength + " time="
					+ (System.currentTimeMillis() - time) + "ms");
		}
		
		// close channel and session
		channel.close();
		session.close();
	}

	private static String createRequest(int size) {
		char[] c = new char[size];

		c[0] = 'a';

		for (int i = 1; i < c.length; ++i) {
			c[i] = (char) (c[i - 1] + 1);

			if (c[i] > 'z') {
				c[i] = 'a';
			}
		}

		return new String(c);
	}

}
