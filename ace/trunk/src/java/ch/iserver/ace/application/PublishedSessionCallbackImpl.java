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

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import ch.iserver.ace.collaboration.JoinRequest;
import ch.iserver.ace.collaboration.PublishedSessionCallback;



public class PublishedSessionCallbackImpl extends SessionCallbackImpl implements PublishedSessionCallback {
	
	private static final Logger LOG = Logger.getLogger(PublishedSessionCallbackImpl.class);
	
	public PublishedSessionCallbackImpl(DocumentItem documentItem, DialogController dialogController) {
		super(documentItem, dialogController);

		// own caret
		PropertyChangeCaretHandlerImpl pCaretHandler = new PropertyChangeCaretHandlerImpl(0, 0);
		cDocument.addDocumentListener(pCaretHandler);
		participantCaretMap.put("0", pCaretHandler);
	}
	
	public void joinRequest(final JoinRequest request) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String user = "\"" + request.getUser().getName() + "\"";
				String title = "\"" + documentItem.getTitle() + "\"";
				int result = dialogController.showJoinRequest(user, title);
				try {
					if (result == JOptionPane.OK_OPTION) {
						request.accept();
					} else {
						request.reject();
					}
				} catch(IllegalStateException e) {
					LOG.info(e);
				}				
			}
		});
	}
	
}