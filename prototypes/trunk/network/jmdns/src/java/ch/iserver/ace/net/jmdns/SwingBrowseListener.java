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

import javax.swing.SwingUtilities;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSDService;

/**
 * 
 */
public class SwingBrowseListener implements BrowseListener {

	private BrowseListener listener;

	public SwingBrowseListener(BrowseListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("listener cannot be null");
		}
		this.listener = listener;
	}

	public void serviceFound(final DNSSDService browser, final int flags,
			final int ifidx, final String serviceName, final String type,
			final String domain) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				listener.serviceFound(browser, flags, ifidx, serviceName, type,
						domain);
			}
		});
	}

	public void serviceLost(final DNSSDService browser, final int flags,
			final int ifidx, final String serviceName, final String type,
			final String domain) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				listener.serviceLost(browser, flags, ifidx, serviceName, type,
						domain);
			}
		});
	}

	public void operationFailed(final DNSSDService service, final int errorCode) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				listener.operationFailed(service, errorCode);
			}
		});
	}

}
