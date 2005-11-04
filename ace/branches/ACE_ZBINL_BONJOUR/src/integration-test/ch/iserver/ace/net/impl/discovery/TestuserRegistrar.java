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

package ch.iserver.ace.net.impl.discovery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.RegisterListener;

/**
 *
 */
public class TestuserRegistrar implements RegisterListener {

	private List usernames;
	private List servicenames;
	private Properties props;
	private List registrations;
	private int[] ports;
	
	public TestuserRegistrar(List usernames, List servicenames, int[] ports, Properties properties) {
		this.usernames = usernames;
		this.servicenames = servicenames;
		this.props = properties;
		this.ports = ports;
		this.registrations = new ArrayList();
	}
	
	public void execute() {
		Iterator iter = servicenames.iterator();
		Iterator iter2 = usernames.iterator();
		int ctr = 0;
		while (iter.hasNext()) {
			String service = (String)iter.next();
			String user = (String)iter2.next();
			props.put(Bonjour.KEY_USER, user);
			props.put(Bonjour.KEY_USERID, user+ports[ctr]);
			
			try {
				DNSSD.register(0, 0, 
					service, 
					(String)props.get(Bonjour.KEY_REGISTRATION_TYPE), 
					"",
					"", 
					ports[ctr++],
					TXTRecordProxy.create(props), 
					this);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		
		}
	}
	
	public void stop() {
		Iterator iter = registrations.iterator();
		while (iter.hasNext()) {
			DNSSDRegistration reg = (DNSSDRegistration)iter.next();
			reg.stop();
			try {
				Thread.sleep(1000);
			} catch (Exception e) {}
		}
	}
	
	
	/**
	 * @see com.apple.dnssd.RegisterListener#serviceRegistered(com.apple.dnssd.DNSSDRegistration, int, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void serviceRegistered(DNSSDRegistration registration, int flags,
			String serviceName, String regType, String domain) {
		registrations.add(registration);

	}

	/**
	 * @see com.apple.dnssd.BaseListener#operationFailed(com.apple.dnssd.DNSSDService, int)
	 */
	public void operationFailed(DNSSDService arg0, int arg1) {
		throw new RuntimeException("TestuserRegistrar.operationFailed: "+arg1);
	}

}
