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

import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.ProfileRegistry;
import org.beepcore.beep.profile.echo.EchoProfile;
import org.beepcore.beep.transport.tcp.TCPSessionCreator;

/**
 * An echo server implementation based on BEEP. The existing
 * <code>EchoProfile</code> implementation from the beepcore project
 * is used to implement the echo protocol. The server itself sets
 * up the profile registry and listens for client connections only.
 */
public class EchoServer {
	/** port used by this echo server */
	public static final int PORT = 5555;

	/**
	 * Creates a new echo server listening on the given <var>port</var>.
	 * 
	 * @param port the port to listen for connections
	 * @throws BEEPException if any BEEP related error occurs
	 */
	public EchoServer(int port) throws BEEPException {
		// create new profile registry
		ProfileRegistry registry = new ProfileRegistry();
		// create echo protocol
		EchoProfile profile = new EchoProfile();
		// add echo profile's start channel listener to profile registry
		// known as EchoProfile.ECHO_URI
		registry.addStartChannelListener(EchoProfile.ECHO_URI, profile.init(
				EchoProfile.ECHO_URI, null), null);

		System.out.println("... listening for connection");
		while (true) {
			// listen for client connections
			TCPSessionCreator.listen(port, registry);
			System.out.println("... client connected");
		}
	}

	public static void main(String[] args) throws BEEPException {
		new EchoServer(PORT);
	}

}
