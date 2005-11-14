/*
 * $Id:BaseListenerImpl.java 1205 2005-11-14 07:57:10Z zbinl $
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

import org.apache.log4j.Logger;

import com.apple.dnssd.BaseListener;
import com.apple.dnssd.DNSSDService;

/**
 *
 */
abstract class BaseListenerImpl implements BaseListener {

	private static Logger LOG = Logger.getLogger(BaseListenerImpl.class);
	
	protected DiscoveryCallbackAdapter adapter;
	
	public BaseListenerImpl(DiscoveryCallbackAdapter adapter) {
		this.adapter = adapter;
	}
	
	/**
	 * @see com.apple.dnssd.BaseListener#operationFailed(com.apple.dnssd.DNSSDService, int)
	 */
	public void operationFailed(DNSSDService arg0, int arg1) {
		// TODO: error handling
		LOG.error("operationFailed("+arg0+", "+arg1+")");
	}

}
