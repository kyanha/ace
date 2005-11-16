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

package ch.iserver.ace.application.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import ch.iserver.ace.application.ApplicationController;
import ch.iserver.ace.application.LocaleMessageSource;



public class ApplicationExitAction extends AbstractAction {

	private ApplicationController appController;

	public ApplicationExitAction(LocaleMessageSource messageSource, ApplicationController appController) {
		super(messageSource.getMessage("mAppExit"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		this.appController = appController;
	}
	
	public void actionPerformed(ActionEvent e) {
		appController.quit();
	}

}

