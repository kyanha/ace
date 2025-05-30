/*
 * $Id:DocumentDiscovery.java 1095 2005-11-09 13:56:51Z zbinl $
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

package ch.iserver.ace.net.impl.protocol;

import ch.iserver.ace.net.impl.RemoteUserProxyExt;

/**
 * Interface for document discovery.
 */
public interface DocumentDiscovery {

	/**
	 * Discovers the published documents for the {@link RemoteUserProxy} 
	 * <var>proxy</var>.
	 * 
	 * @param proxy the RemoteUserProxy
	 */
	public void execute(RemoteUserProxyExt proxy);
	
//	/**
//	 * Sets the DocumentDiscoveryCallback.
//	 * 
//	 * @param callback the callback to set
//	 */
//	public void setCallback(DocumentDiscoveryCallback callback);
}
