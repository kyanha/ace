/*
 * $Id:Register.java 1205 2005-11-14 07:57:10Z zbinl $
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

package ch.iserver.ace.net.impl.discovery.dnssd;

import org.apache.log4j.Logger;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.RegisterListener;
import com.apple.dnssd.TXTRecord;

/**
 *
 */
public class Register extends DNSSDCall {

	private Logger LOG = Logger.getLogger(Register.class);
	
	private String serviceName, registrationType;
	private int port;
	private TXTRecord txt;
	private RegisterListener listener;
	
	/**
	 * Constructor.
	 * 
	 * @param serviceName
	 * @param registrationType
	 * @param port
	 * @param txt
	 * @param listener
	 */
	public Register(String serviceName, String registrationType, int port, TXTRecord txt, RegisterListener listener) {
		this.serviceName = serviceName;
		this.registrationType = registrationType;
		this.port = port;
		this.txt = txt;
		this.listener = listener;
	}
	
	/**
	 * @see ch.iserver.ace.net.impl.discovery.dnssd.DNSSDCall#makeCall()
	 */
	protected Object makeCall() throws DNSSDCallException {
		try {
			//TODO: set host as on some hosts there may be multiple interfaces and IP addresses respectively 
			return DNSSD.register(0, 0, serviceName, registrationType, "", "", port, txt, listener);
		} catch (DNSSDException de) {
			throw new DNSSDCallException(de);
		}
	}
	
	protected Logger getLogger() {
		return LOG;
	}

}
