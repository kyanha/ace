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

import ch.iserver.ace.application.DocumentManager;
import ch.iserver.ace.application.LocaleMessageSource;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

import java.awt.Toolkit;
import javax.swing.KeyStroke;



public class ApplicationExitAction extends AbstractAction {

	private DocumentManager documentManager;

	public ApplicationExitAction(LocaleMessageSource messageSource, DocumentManager documentManager) {
		super(messageSource.getMessage("mAppExit"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		this.documentManager = documentManager;
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("ApplicationExitAction: ");
		/*System.out.println("ActionCommand: " + e.getActionCommand());
		System.out.println("Modifiers: " + e.getModifiers());
		System.out.println("getWhen: " + e.getWhen());
		System.out.println("paramString: " + e.paramString());
		System.out.println("source: " + e.getSource());
		// check for unsaved documents
		*/
		documentManager.exitApplication();
		System.exit(0);
	}

}

