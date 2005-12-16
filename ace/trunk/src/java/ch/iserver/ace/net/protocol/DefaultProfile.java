/*
 * $Id:AbstractProfile.java 1095 2005-11-09 13:56:51Z zbinl $
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

package ch.iserver.ace.net.protocol;

import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.StartChannelListener;
import org.beepcore.beep.profile.Profile;
import org.beepcore.beep.profile.ProfileConfiguration;

/**
 * This is a BEEP Core specific class. A profile is used
 * to register with the BEEP library.
 * 
 * @see org.beepcore.beep.profile.Profile
 */
public class DefaultProfile implements Profile {

	/**
	 * the start channel listener
	 */
	private StartChannelListener listener;
	
	/**
	 * Creates a new DefaultProfile with the given <code>listener</code>.
	 * 
	 * @param listener 	the StartChannelListener
	 */
	public DefaultProfile(StartChannelListener listener) {
		this.listener = listener;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public StartChannelListener init(String uri, ProfileConfiguration config) throws BEEPException {
		//can uri and config be used in any concern?
		return listener;
	}

}
