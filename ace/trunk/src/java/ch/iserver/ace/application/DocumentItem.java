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

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.collaboration.*;
import ch.iserver.ace.collaboration.RemoteDocument;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.collaboration.Session;

import ch.iserver.ace.util.UUID;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import java.io.File;



public class DocumentItem extends ItemImpl implements Comparable, PropertyChangeListener {

	public final static int LOCAL		= 1;
	public final static int REMOTE		= 2;
	public final static int PUBLISHED	= 3;
	public final static String TYPE_PROPERTY = "type";
	public final static String TITLE_PROPERTY = "title";
	public final static String DIRTY_PROPERTY = "dirty";

	private String id, title, extendedTitle, toolTip;
	private int type;
	private boolean isDirty = false;
	private File file;

	private StyledDocument editorDocument;
//	private SyntaxDocument editorDocument;
	private RemoteDocument remoteDocument;
	private Session session;
	private SessionCallback sessionCallback;



	public DocumentItem(String title) {
		// create local document
		id = UUID.nextUUID();
		this.title = title;
		extendedTitle = title;
		toolTip = title;
		createEditorDocument();
	}
	
	public DocumentItem(File file) {
		// create local document
		id = UUID.nextUUID();
		this.file = file;
		title = file.getName();
		extendedTitle = file.getAbsolutePath();// + " - " + file.getName();
		toolTip = file.getAbsolutePath();
		createEditorDocument();
	}
	
	public DocumentItem(RemoteDocument document) {
		// create remote document
		id = document.getId();
		title = document.getTitle();
		extendedTitle = document.getPublisher().getName() + " - " + document.getTitle();
		toolTip = document.getPublisher().getName() + " - " + document.getTitle();
		type = REMOTE;
		remoteDocument = document;
		document.addPropertyChangeListener(this);
		document.getPublisher().addPropertyChangeListener(this);
	}

	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
/*	public void setTitle(String title) {
		this.title = title;
	}*/
	
	public String getExtendedTitle() {
		return extendedTitle;
	}

/*	public void setExtendedTitle(String extendedTitle) {
		this.extendedTitle = extendedTitle;
	}*/
		
	public String getToolTip() {
		return toolTip;
	}
	
	public int getType() {
		return type;
	}
	
	public boolean isDirty() {
		return isDirty;
	}
	
	public void setClean() {
		if (isDirty) {
			isDirty = false;
			firePropertyChange(DIRTY_PROPERTY, Boolean.TRUE, Boolean.FALSE);
		}
	}
	
	public void setDirty() {
		if (!isDirty) {
			isDirty = true;
			firePropertyChange(DIRTY_PROPERTY, Boolean.FALSE, Boolean.TRUE);
		}
	}
	
	public boolean hasBeenSaved() {
		return (file != null);
	}
	
	public RemoteDocument getRemoteDocument() {
		return remoteDocument;
	}
	
	public StyledDocument getEditorDocument() {
		return editorDocument;
	}
	
	public Session getSession() {
		return session;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
		String oldTitle = title;
		title = file.getName();
		extendedTitle = file.getAbsolutePath();// + " - " + file.getName();
		toolTip = file.getAbsolutePath();
		firePropertyChange(TITLE_PROPERTY, oldTitle, title);
	}
	
	
	
	
	
	private void createEditorDocument() {
		type = LOCAL;
		editorDocument = new DefaultStyledDocument();
//		editorDocument = new SyntaxDocument();
		editorDocument.addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
			}

			public void insertUpdate(DocumentEvent e) {
				if(!isDirty) {
					isDirty = true;
					firePropertyChange(DIRTY_PROPERTY, "CLEAN", "DIRTY");
				}
			}

			public void removeUpdate(DocumentEvent e) {
				if(!isDirty) {
					isDirty = true;
					firePropertyChange(DIRTY_PROPERTY, "CLEAN", "DIRTY");
				}
			}
		});
	}
	
	
	
	
	
	
	
	public void publish(CollaborationService collaborationService) {
		sessionCallback = new SessionCallbackImpl();
		session = collaborationService.publish(sessionCallback, new DocumentModel("", 0, 0, new DocumentDetails(title)));
		type = PUBLISHED;
		firePropertyChange(TYPE_PROPERTY, "LOCAL", "PUBLISHED");
	}
	
	public void conceal() {
		session.leave();
		type = LOCAL;
		firePropertyChange(TYPE_PROPERTY, "PUBLISHED", "LOCAL");
	}
	
	
	
	




	
	
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(RemoteDocument.TITLE_PROPERTY)) {
			title = (String)evt.getNewValue();
			extendedTitle = getRemoteDocument().getPublisher().getName() + " - " + title;
			toolTip = getRemoteDocument().getPublisher().getName() + " - " + title;
			firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
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

