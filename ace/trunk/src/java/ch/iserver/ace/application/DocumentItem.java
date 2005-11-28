/*
 * $Id:DocumentItem.java 1091 2005-11-09 13:29:05Z zbinl $
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ch.iserver.ace.text.InsertOperation;
import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.collaboration.*;
import ch.iserver.ace.collaboration.RemoteDocument;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.collaboration.Session;

import ch.iserver.ace.util.UUID;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;
import java.io.File;
import java.awt.*;
import ch.iserver.ace.application.editor.*;

import ca.odell.glazedlists.EventList;
import spin.Spin;



public class DocumentItem extends ItemImpl implements Comparable, PropertyChangeListener {

	public final static int LOCAL		= 1;
	public final static int PUBLISHED	= 2;
	public final static int REMOTE		= 3;
	public final static int AWAITING	= 4;
	public final static int JOINED		= 5;
	public final static String TYPE_PROPERTY = "type";
	public final static String TITLE_PROPERTY = "title";
	public final static String DIRTY_PROPERTY = "dirty";

	private File file;
	private int type;
	private boolean isDirty = false;
	private String id, title, extendedTitle, toolTip;

	private CollaborativeDocument editorDocument;
	private RemoteDocument remoteDocument;
	private Session session;
	private PublishedSessionCallbackImpl publishedSessionCallback;
	private SessionCallback sessionCallback;



	public DocumentItem(String title) {
		// create local document
		initDocumentItem(UUID.nextUUID(), title, title, title);
		type = LOCAL;
		createEditorDocument();
		
		
		//CollaborativeDocument doc = (CollaborativeDocument)editorDocument;
		
		/*Style pStyle = editorDocument.addStyle("" + 1, null);
		StyleConstants.setBackground(pStyle, Color.RED.brighter());

		Style pStyle2 = editorDocument.addStyle("" + 2, null);
		StyleConstants.setBackground(pStyle2, Color.BLUE.brighter());
		
		try {
			editorDocument.insertString(0, "tescht!", editorDocument.getStyle("" + 1));
			editorDocument.insertString(0, "x ", editorDocument.getStyle("" + 2));
			editorDocument.insertString(0, "x ", editorDocument.getStyle("" + 2));
			editorDocument.insertString(0, "chline ", editorDocument.getStyle("" + 2));
			editorDocument.insertString(0, "e ", editorDocument.getStyle("" + 1));
			editorDocument.insertString(0, "x ", editorDocument.getStyle("" + 2));
			editorDocument.insertString(0, "isch ", editorDocument.getStyle("" + 1));
			editorDocument.insertString(0, "x ", editorDocument.getStyle("" + 2));
			editorDocument.insertString(0, "das ", editorDocument.getStyle("" + 1));
		} catch(Exception e) {
			e.printStackTrace();
		}

		
		pStyle = editorDocument.getStyle("" + 1);
		StyleConstants.setBackground(pStyle, Color.GREEN.brighter());*/
		





	}
	
	public DocumentItem(File file) {
		// create local document
		initDocumentItem(UUID.nextUUID(), file.getName(), file.getAbsolutePath(), file.getAbsolutePath());
		this.file = file;
		type = LOCAL;
		createEditorDocument();
	}
	
	public DocumentItem(RemoteDocument document) {
		// create remote document (used in the joining process)
		initDocumentItem(document.getId(), document.getTitle(),
			document.getPublisher().getName() + " - " + document.getTitle(),
			document.getPublisher().getName() + " - " + document.getTitle());
		remoteDocument = document;
		type = REMOTE;
		document.addPropertyChangeListener(this);
		document.getPublisher().addPropertyChangeListener(this);
	}
	
	private void initDocumentItem(String newId, String newTitle, String newExtendedTitle, String newToolTip) {
		String oldTitle = title;
		id = newId;
		title = newTitle;
		extendedTitle = newExtendedTitle;
		toolTip = newToolTip;
		if( !title.equals(oldTitle) ) {
			firePropertyChange(TITLE_PROPERTY, oldTitle, title);
		}
	}

	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getExtendedTitle() {
		return extendedTitle;
	}

	public String getToolTip() {
		return toolTip;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		int oldType = this.type;
		if (oldType != type) {
			this.type = type;
			firePropertyChange(TYPE_PROPERTY, new Integer(oldType), new Integer(type));
		}		
	}
	
	public boolean isDirty() {
		return isDirty;
	}
	
	public void setDirty(boolean value) {
		if (isDirty != value) {
			isDirty = value;
			firePropertyChange(DIRTY_PROPERTY, new Boolean(!isDirty), new Boolean(isDirty));
		}
	}
	
	public boolean hasBeenSaved() {
		return (file != null);
	}
	
	public RemoteDocument getRemoteDocument() {
		return remoteDocument;
	}
	
	public CollaborativeDocument getEditorDocument() {
		return editorDocument;
	}

	public void setEditorDocument(CollaborativeDocument editorDocument) {
		this.editorDocument = editorDocument;
	}
	
	public EventList getParticipantSourceList() {
		return ((SessionCallbackImpl)sessionCallback).getParticipantSourceList();
	}
	
	public Session getSession() {
		return session;
	}
	
	public void setSession(Session session) {
		this.session = session;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		initDocumentItem(id, file.getName(), file.getAbsolutePath(), file.getAbsolutePath());
		this.file = file;
	}
	
	

	public void createEditorDocument() {
		editorDocument = new CollaborativeDocument();
		// editorDocument = new SyntaxDocument();
		editorDocument.addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
			}
			public void insertUpdate(DocumentEvent e) {
				setDirty(true);
			}
			public void removeUpdate(DocumentEvent e) {
				setDirty(true);
			}
		});
		
	}
	
	
	
	
	
	
	
	
	

/*
BASCHTLE
*/
	public void publish(CollaborationService collaborationService) {
		sessionCallback = new PublishedSessionCallbackImpl(this);
		String documentContent = "";
		try {
			documentContent = editorDocument.getText(0, editorDocument.getLength());
		} catch(Exception e) {
			e.printStackTrace();
		}
		DocumentModel docModel = new DocumentModel(documentContent, 0, 0, new DocumentDetails(title));

		session = (Session)
			//Spin.off(
					collaborationService.publish((PublishedSessionCallback)sessionCallback,
					docModel);
			//);
			
		// editorDocument.setLocal(false);
		// editorDocument.setSession(session);
		setType(PUBLISHED);
		
		
/*		final Session ses = session;
		new Thread() {
			public void run() {
				for(int i = 0; i < 25; i++) {
					ses.lock();
					String text = "" + Math.round(100 * Math.random()) + "-";
					ses.sendOperation(new InsertOperation(0, text));
					ses.unlock();
					System.out.println("sending text: " + text);
					try {
						Thread.sleep(1000);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();*/
		
		
	}
	
	public void conceal() {
		// conceal a document
		session.leave();
		setType(LOCAL);
	}
	
	public void leave() {
		// leave a document
		cleanUp();
		session.leave();
		setType(REMOTE);
	}

	public void join() {
		// join document
		System.out.println(editorDocument);
		editorDocument = new CollaborativeDocument();
		remoteDocument.join(new JoinCallbackImpl(this));
		setType(AWAITING);
	}

	public void setSessionCallback(SessionCallback sessionCallback) {
		this.sessionCallback = sessionCallback;
	}


	public String getPublisher() {
		if(type==REMOTE || type==AWAITING) {
			return remoteDocument.getPublisher().getName();
		} else {
			return "";
		}
	}





/*
BASCHTLE
*/
	
	
	public void cleanUp() {
		System.out.println("DocumentItem::cleanUp()");
		if(remoteDocument != null) {
			remoteDocument.removePropertyChangeListener(this);
			remoteDocument.getPublisher().removePropertyChangeListener(this);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(RemoteDocument.TITLE_PROPERTY)) {
			initDocumentItem(id, (String)evt.getNewValue(),
				getRemoteDocument().getPublisher().getName() + " - " + title,
				getRemoteDocument().getPublisher().getName() + " - " + title);
			//title = (String)evt.getNewValue();
			//extendedTitle = getRemoteDocument().getPublisher().getName() + " - " + title;
			//toolTip = getRemoteDocument().getPublisher().getName() + " - " + title;
			//firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		} else if (RemoteUser.NAME_PROPERTY.equals(evt.getPropertyName())) {
			firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
	}

	public int compareTo(Object o) {
		return -((DocumentItem)o).getTitle().compareTo(title);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof DocumentItem) {
			DocumentItem documentItem = (DocumentItem)obj;
			return getId().equals(documentItem.getId());
		}
		return super.equals(obj);
	}
	
	public int hashCode() {
		return getId().hashCode();
	}

}

