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

package ch.iserver.ace.collaboration.jupiter.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.GapContent;
import javax.swing.text.SimpleAttributeSet;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Fragment;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.CompareUtil;

/**
 * Default implementation of the ServerDocument. Implements also the
 * PortableDocument interface from the network layer. Uses the 
 * Swing Document classes to keep the structure of the document.
 */
public class ServerDocumentImpl extends AbstractDocument implements
				ServerDocument, ch.iserver.ace.net.PortableDocument {

	public static final String PARTICIPANT_ATTR = "participant";

	private final Map carets = new HashMap();

	private BranchElement defaultRoot;
	
	private final Map participants = new TreeMap();

	public ServerDocumentImpl() {
		super(new GapContent());
		defaultRoot = (BranchElement) createDefaultRoot();
		addParticipant(0, null);
		setCaretHandler(0, new CaretHandler(0, 0));
	}
	
	// --> utility methods <--
	
	private CaretHandler getCaretHandler(int participantId) {
		return (CaretHandler) carets.get(new Integer(participantId));
	}
	
	private void setCaretHandler(int participantId, CaretHandler handler) {
		carets.put(new Integer(participantId), handler);
	}
	
	protected int getParticipantId(AttributeSet attr) {
		int participantId = -1;
		Integer pid = (Integer) attr.getAttribute(ServerDocumentImpl.PARTICIPANT_ATTR);
		if (pid != null) {
			participantId = pid.intValue();
		}
		return participantId;
	}
	
	private void addParticipant(int participantId, RemoteUserProxy proxy) {
		participants.put(new Integer(participantId), proxy);
	}
	
	private void removeParticipant(int participantId) {
		participants.remove(new Integer(participantId));
	}
	
	// --> AbstractDocument methods <--
	
	protected AbstractElement createDefaultRoot() {
		BranchElement map = (BranchElement) createBranchElement(null, null);
		Element line = createLeafElement(map, null, 0, 1);
		Element[] lines = new Element[1];
		lines[0] = line;
		map.replace(0, 0, lines);
		return map;
	}
	
	/**
	 * @see javax.swing.text.AbstractDocument#createLeafElement(javax.swing.text.Element, javax.swing.text.AttributeSet, int, int)
	 */
	protected Element createLeafElement(Element parent, AttributeSet a, int p0, int p1) {
		return new FragmentElement(parent, a, p0, p1);
	}

	/**
	 * @see javax.swing.text.Document#getDefaultRootElement()
	 */
	public Element getDefaultRootElement() {
		return defaultRoot;
	}

	/**
	 * @see javax.swing.text.AbstractDocument#getParagraphElement(int)
	 */
	public Element getParagraphElement(int pos) {
		return defaultRoot;
	}
	
	/**
	 * @see javax.swing.text.AbstractDocument#insertUpdate(javax.swing.text.AbstractDocument.DefaultDocumentEvent, javax.swing.text.AttributeSet)
	 */
	protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
		List added = new ArrayList();
		List removed = new ArrayList();
		int offset = chng.getOffset();
		int length = chng.getLength();

		int index = defaultRoot.getElementIndex(offset);
		Element candidate = defaultRoot.getElement(index);
		int start = candidate.getStartOffset();
		int end = candidate.getEndOffset();
		
		int participantId = getParticipantId(candidate.getAttributes());
		int nparticipantId = getParticipantId(attr);
		
		removed.add(candidate);

		if (participantId == nparticipantId) {
			added.add(createLeafElement(defaultRoot, attr, start, end));
			
		} else {
			if (start < offset) {
				added.add(createLeafElement(defaultRoot, candidate.getAttributes(), start, offset));
			}
			added.add(createLeafElement(defaultRoot, attr, offset, offset + length));
			if (offset + length < end) {
				added.add(createLeafElement(defaultRoot, candidate.getAttributes(), offset + length, end));
			}
		}

		Element[] addedEl = (Element[]) added.toArray(new Element[added.size()]);
		Element[] removedEl = (Element[]) removed.toArray(new Element[removed.size()]);
		defaultRoot.replace(index, removedEl.length, addedEl);
		ElementEdit edit = new ElementEdit(defaultRoot, index, removedEl, addedEl);
		chng.addEdit(edit);
		super.insertUpdate(chng, attr);
	}

	/**
	 * @see javax.swing.text.AbstractDocument#removeUpdate(javax.swing.text.AbstractDocument.DefaultDocumentEvent)
	 */
	protected void removeUpdate(DefaultDocumentEvent chng) {
		List removed = new ArrayList();		
		BranchElement map = (BranchElement) getDefaultRootElement();
		int offset = chng.getOffset();
		int length = chng.getLength();
		int index0 = map.getElementIndex(offset);
		int index1 = map.getElementIndex(offset + length);
		if (index0 != index1) {
			AttributeSet attr0 = map.getElement(index0).getAttributes();
			AttributeSet attr1 = map.getElement(index1).getAttributes();
			for (int i = index0; i <= index1; i++) {
				removed.add(map.getElement(i));
			}
			int p0 = map.getElement(index0).getStartOffset();
			int p1 = map.getElement(index1).getEndOffset();
			Element[] aelems = new Element[1];
			if (p0 == offset) {
				if (index0 > 0) {
					Element el = map.getElement(index0 - 1);
					int p2 = el.getStartOffset();
					if (getParticipantId(el.getAttributes()) == getParticipantId(attr1)) {
						removed.add(0, el);
						aelems[0] = createLeafElement(map, attr1, p2, p1);
						index0--;
					} else {
						aelems[0] = createLeafElement(map, attr1, p0, p1);
					}
				} else {
					aelems[0] = createLeafElement(map, attr1, p0, p1);
				}
			} else {
				aelems[0] = createLeafElement(map, attr0, p0, p1);
			}
			Element[] relems = (Element[]) removed.toArray(new Element[removed.size()]);
			ElementEdit ee = new ElementEdit(map, index0, relems, aelems);
			chng.addEdit(ee);
			map.replace(index0, relems.length, aelems);
		}
		super.removeUpdate(chng);
	}
		
	// --> ServerDocument methods <--
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerDocument#participantJoined(int, ch.iserver.ace.net.RemoteUserProxy)
	 */
	public void participantJoined(int participantId, RemoteUserProxy proxy) {
		CaretHandler handler = new CaretHandler(0, 0);
		setCaretHandler(participantId, handler);
		addDocumentListener(handler);
		addParticipant(participantId, proxy);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerDocument#participantLeft(int)
	 */
	public void participantLeft(int participantId) {
		CaretHandler handler = getCaretHandler(participantId);
		removeDocumentListener(handler);
		setCaretHandler(participantId, null);
		removeParticipant(participantId);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerDocument#getText()
	 */
	public String getText() {
		try {
			return getText(0, getLength());
		} catch (BadLocationException e) {
			throw new RuntimeException("internal error");
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerDocument#updateCaret(int, int, int)
	 */
	public void updateCaret(int participantId, int dot, int mark) {
		CaretHandler handler = getCaretHandler(participantId);
		if (handler == null) {
			handler = new CaretHandler(dot, mark);
			setCaretHandler(participantId, handler);
		} else {
			handler.setDot(dot);
			handler.setMark(mark);
		}
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerDocument#insertString(int, int, java.lang.String)
	 */
	public void insertString(int participantId, int offset, String text) {
		SimpleAttributeSet attr = new SimpleAttributeSet();
		attr.addAttribute(PARTICIPANT_ATTR, new Integer(participantId));
		try {
			super.insertString(offset, text, attr);
		} catch (BadLocationException e) {
			// TODO: fix RuntimeException
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerDocument#removeString(int, int)
	 */
	public void removeString(int offset, int length) {
		try {
			super.remove(offset, length);
		} catch (BadLocationException e) {
			// TODO: fix RuntimeException
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerDocument#toPortableDocument()
	 */
	public PortableDocument toPortableDocument() {
		return this;
	}
			
	// --> PortableDocument methods <--
	
	/**
	 * @see ch.iserver.ace.collaboration.PortableDocument#getFragments()
	 */
	public Iterator getFragments() {
		final BranchElement root = (BranchElement) getDefaultRootElement();
		return new Iterator() {
			private int idx = 0;
			public Object next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return root.getChildAt(idx++);
			}		
			public boolean hasNext() {
				return idx < root.getChildCount() - 1;
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}		
		};
	}
	
	/**
	 * @see ch.iserver.ace.net.PortableDocument#getParticipantIds()
	 */
	public int[] getParticipantIds() {
		Set ids = participants.keySet();
		int[] result = new int[ids.size()];
		int idx = 0;
		Iterator it = ids.iterator();
		while (it.hasNext()) {
			Integer id = (Integer) it.next();
			result[idx++] = id.intValue(); 
		}
		return result;
	}
	
	/**
	 * @see ch.iserver.ace.net.PortableDocument#getUserProxy(int)
	 */
	public RemoteUserProxy getUserProxy(int participantId) {
		return (RemoteUserProxy) participants.get(new Integer(participantId));
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.PortableDocument#getSelection(int)
	 */
	public CaretUpdate getSelection(int participantId) {
		CaretHandler handler = getCaretHandler(participantId);
		if (handler != null) {
			return new CaretUpdate(handler.getDot(), handler.getMark());
		} else {
			return null;
		}
	}
	
	// --> java.lang.Object methods <--
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof PortableDocument) {
			PortableDocument doc = (PortableDocument) obj;
			if (!CompareUtil.arrayEquals(getParticipantIds(), doc.getParticipantIds())) {
				return false;
			}
			int[] ids = getParticipantIds();
			for (int i = 0; i < ids.length; i++) {
				int id = ids[i];
				if (!CompareUtil.nullSafeEquals(getSelection(id), doc.getSelection(id))) {
					return false;
				}
			}
			return CompareUtil.iteratorEquals(getFragments(), doc.getFragments());
		} else {
			return false;
		}
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("ServerDocument\n  ");
		int[] ids = getParticipantIds();
		for (int i = 0; i < ids.length; i++) {
			buf.append(ids[i]);
			buf.append("(");
			buf.append(getSelection(i));
			buf.append(")");
			if (i + 1 < ids.length) {
				buf.append(",");
			}
		}
		buf.append("\n  ");
		buf.append(getText());
		return buf.toString();
	}
		
	// --> Fragment Element <--
	
	/**
	 * Implementation of the Fragment interface which is also a Swing element
	 * used to represent the document structure.
	 */
	protected class FragmentElement extends LeafElement implements Fragment {
		
		protected FragmentElement(Element parent, AttributeSet a, int p0, int p1) {
			super(parent, a, p0, p1);
		}
		
		/**
		 * @see ch.iserver.ace.Fragment#getParticipantId()
		 */
		public int getParticipantId() {
			return ServerDocumentImpl.this.getParticipantId(getAttributes());
		}
		
		/**
		 * @see ch.iserver.ace.Fragment#getText()
		 */
		public String getText() {
			int length = getEndOffset() - getStartOffset();
			try {
				return getDocument().getText(getStartOffset(), length);
			} catch (BadLocationException e) {
				// TODO: fix RuntimeException
				throw new RuntimeException(e);
			}
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "Fragment [pid=" + getParticipantId() + "] " + getText();
		}
		
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj instanceof Fragment) {
				Fragment f = (Fragment) obj;
				return getText().equals(f.getText())
				        && getParticipantId() == f.getParticipantId();
			} else {
				return false;
			}
		}
		
		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return (getText() + "-" + getParticipantId()).hashCode();
		}
		
	}
		
}
