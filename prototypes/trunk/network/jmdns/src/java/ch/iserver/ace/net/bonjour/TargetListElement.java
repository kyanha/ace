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

/**
 *
 */
public class TargetListElement {
	private String serviceName;
	private String domain;
	private String type;
	private int ifidx;
	
	public TargetListElement(String serviceName, String domain, String type, int ifidx) {
		this.serviceName = serviceName;
		this.domain = domain;
		this.type = type;
		this.ifidx = ifidx;
	}

	public String getDomain() {
		return domain;
	}

	public int getIfidx() {
		return ifidx;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getType() {
		return type;
	}
	
	public String toString() {
		return serviceName;
	}
	
}
