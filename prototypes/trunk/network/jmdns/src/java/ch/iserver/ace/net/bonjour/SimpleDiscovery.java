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
package ch.iserver.ace.net.bonjour;

import java.net.DatagramSocket;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.RegisterListener;

public class SimpleDiscovery implements RegisterListener, BrowseListener {

	static final String RegType = "_test._tcp";

	static final String kWireCharSet = "ISO-8859-1";
	
	DNSSDRegistration registration;
	DNSSDService browser;
	
	String user;
	
	public SimpleDiscovery() throws Exception {
		DatagramSocket ds = new DatagramSocket();
		registration = DNSSD.register(0, 0, System.getProperty("user.name"), RegType, "",
				"", ds.getLocalPort(), null, this);
	}
	
	public void serviceRegistered(DNSSDRegistration arg0, int arg1,
			String arg2, String arg3, String arg4) {
		user = arg2;
		System.out.println("name: "+user);
		try {
			browser = DNSSD.browse(0, 0, arg3, "", this);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void operationFailed(DNSSDService arg0, int arg1) {
		System.err.println(arg1);
		System.exit(-1);

	}

	public void serviceFound(DNSSDService arg0, int arg1, int arg2,
			String arg3, String arg4, String arg5) {
		System.out.println("SF: "+arg3+" "+arg4+" "+arg5);

		//resolve all other users
		if ( !arg3.equals(user) ) {
			System.out.println("resolve user "+arg3);
		}
	}

	public void serviceLost(DNSSDService arg0, int arg1, int arg2, String arg3,
			String arg4, String arg5) {
		System.out.println("SL: "+arg3+" "+arg4+" "+arg5);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		SimpleDiscovery sd = new SimpleDiscovery();

	}

}
