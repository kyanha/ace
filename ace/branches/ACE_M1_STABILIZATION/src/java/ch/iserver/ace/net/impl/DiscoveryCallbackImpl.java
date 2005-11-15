/*
 * $Id:DiscoveryCallbackImpl.java 1205 2005-11-14 07:57:10Z zbinl $
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
package ch.iserver.ace.net.impl;

import org.apache.log4j.Logger;

import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.util.ParameterValidator;

/**
 * 
 *
 */
public class DiscoveryCallbackImpl implements DiscoveryCallback {

	private static Logger LOG = Logger.getLogger(DiscoveryCallbackImpl.class);
	
	private NetworkServiceCallback callback;
	private NetworkServiceExt service;
	
	/**
	 * 
	 * @param callback
	 */
	public DiscoveryCallbackImpl(NetworkServiceCallback callback, NetworkServiceExt service) {
		ParameterValidator.notNull("callback", callback);
		this.callback = callback;
		this.service = service;
	}
	
	/**
	 * 
	 */
	public void userDiscovered(RemoteUserProxyExt proxy) {
		LOG.debug("userDiscovered("+proxy+")");
		System.out.println("userDiscovered("+proxy+")");
		callback.userDiscovered(proxy);
	}

	/**
	 * 
	 */
	public void userDiscarded(RemoteUserProxyExt proxy) {
		LOG.debug("userDiscarded("+proxy+")");
		callback.userDiscarded(proxy);
		//TODO: clean up session manager etc.
	}

	/**
	 * 
	 */
	public void userDetailsChanged(RemoteUserProxyExt proxy) {
		LOG.debug("userDetailsChanged("+proxy+")");
		callback.userDetailsChanged(proxy);
	}

	/**
	 * 
	 */
	public void userDiscoveryCompleted(RemoteUserProxyExt proxy) {
		LOG.debug("userDiscoveryCompleted("+proxy+")");
		service.discoverDocuments(proxy);
	}


}
