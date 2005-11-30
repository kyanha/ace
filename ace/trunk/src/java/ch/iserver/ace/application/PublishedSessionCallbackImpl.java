/*
 * $Id:PublishedSessionCallbackImpl.java 1091 2005-11-09 13:29:05Z zbinl $
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

import ch.iserver.ace.collaboration.JoinRequest;
import ch.iserver.ace.collaboration.PublishedSessionCallback;
import ch.iserver.ace.application.editor.CollaborativeDocument;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;



public class PublishedSessionCallbackImpl extends SessionCallbackImpl implements PublishedSessionCallback {

	public PublishedSessionCallbackImpl(DocumentItem documentItem, DialogController dialogController) {
		super(documentItem, dialogController);
	}
	
	public void joinRequest(final JoinRequest request) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int result = dialogController.showJoinRequest(documentItem.getPublisher(), documentItem.getTitle());
				if (result == JOptionPane.OK_OPTION) {
					request.accept();
				} else {
					request.reject();
				}
			}
		});
	}
	
}