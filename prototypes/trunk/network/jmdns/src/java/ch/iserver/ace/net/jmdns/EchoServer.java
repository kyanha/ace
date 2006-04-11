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
package ch.iserver.ace.net.jmdns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

/**
 * 
 */
public class EchoServer {

	private ServerSocket serverSocket;
	
	private int localPort;

	public EchoServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		localPort = serverSocket.getLocalPort();
		System.out.println("... server started at " + localPort);
		System.out.println("... registering service");
		JmDNS jmdns = new JmDNS();
		ServiceInfo info = new ServiceInfo(EchoConstants.REGISTRY_TYPE, "echo-server", 
				localPort, 0, 0, "version=1.0");
		jmdns.registerService(info);
		System.out.println("... registered service");
	}

	public void start() {
		try {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));
				PrintWriter writer = new PrintWriter(clientSocket
						.getOutputStream(), true);
				String line = reader.readLine();
				writer.println(line);
				reader.close();
				writer.close();
				clientSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		EchoServer server = new EchoServer(0);
	}

}
