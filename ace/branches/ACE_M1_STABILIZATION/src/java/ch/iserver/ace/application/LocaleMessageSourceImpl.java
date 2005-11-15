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

package ch.iserver.ace.application;

import ch.iserver.ace.ApplicationError;
import java.util.Locale;

import java.net.URL;
import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;



public class LocaleMessageSourceImpl implements LocaleMessageSource {

	private MessageSource source;
	private Locale locale;
	
	public LocaleMessageSourceImpl(MessageSource source) {
		this(source, Locale.getDefault());
	}
	
	public LocaleMessageSourceImpl(MessageSource source, Locale locale) {
		this.source = source;
	}

	public String getMessage(String code) {
		return source.getMessage(code, null, "message not defined", locale);
	}
	
	public ImageIcon getIcon(String code) {
		URL iconURL = getClass().getResource(getMessage("uImagePath") + getMessage(code));
		if (iconURL == null) {
			throw new ApplicationError("icon with code \"" + code + "\" not found.");
		}
		return new ImageIcon(iconURL);
	}
	
	public URL getResource(String code) {
		URL result = getClass().getResource(getMessage("uResourcePath") + getMessage(code));
		if (result == null) {
			throw new ApplicationError("resource with code \"" + code + "\" not found.");
		}
		return result;
	}

}