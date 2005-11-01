package ch.iserver.ace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.GapContent;
import javax.swing.text.SimpleAttributeSet;

public class DefaultDocumentModel extends AbstractDocument implements
				DocumentModel, PortableDocument {

	public static final String PARTICIPANT_ATTR = "participant";

	private final Map carets = new HashMap();

	private BranchElement defaultRoot;

	public DefaultDocumentModel() {
		super(new GapContent());
		defaultRoot = (BranchElement) createDefaultRoot();
	}
	
	// --> utility methods <--
	
	private CaretHandler getCaretHandler(int participantId) {
		return (CaretHandler) carets.get(new Integer(participantId));
	}
	
	private void setCaretHandler(int participantId, CaretHandler handler) {
		carets.put(new Integer(participantId), handler);
		addDocumentListener(handler);
	}
	
	protected int getParticipantId(AttributeSet attr) {
		int participantId = 0;
		Integer pid = (Integer) attr.getAttribute(DefaultDocumentModel.PARTICIPANT_ATTR);
		if (pid != null) {
			participantId = pid.intValue();
		}
		return participantId;
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
	
	protected Element createLeafElement(Element parent, AttributeSet a, int p0, int p1) {
		return new FragmentElement(parent, a, p0, p1);
	}

	public Element getDefaultRootElement() {
		return defaultRoot;
	}

	public Element getParagraphElement(int pos) {
		return defaultRoot;
	}
	
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

		if (participantId == nparticipantId) {
			removed.add(candidate);
			added.add(createLeafElement(defaultRoot, attr, start, end));
		} else {
			removed.add(candidate);
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
		
	// --> DocumentModel methods <--
	
	public void participantJoined(int participantId) {
		setCaretHandler(participantId, new CaretHandler(-1, -1));
	}
	
	public void participantLeft(int participantId) {
		CaretHandler handler = getCaretHandler(participantId);
		removeDocumentListener(handler);
		setCaretHandler(participantId, null);
	}
	
	public String getText() {
		try {
			return getText(0, getLength());
		} catch (BadLocationException e) {
			throw new RuntimeException("internal error");
		}
	}
	
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

	public void insertString(int offset, String text, int participantId) {
		SimpleAttributeSet attr = new SimpleAttributeSet();
		attr.addAttribute(PARTICIPANT_ATTR, new Integer(participantId));
		try {
			super.insertString(offset, text, attr);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeString(int offset, int length) {
		try {
			super.remove(offset, length);
		} catch (BadLocationException e) {
			// TODO: fix RuntimeException
			throw new RuntimeException(e);
		}
	}
	
	public PortableDocument toPortableDocument() {
		return this;
	}
			
	// --> PortableDocument methods <--
	
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
	
	public Set getParticipants() {
		return null;
	}
	
	public int getDot(int participantId) {
		CaretHandler handler = getCaretHandler(participantId);
		if (handler != null) {
			return handler.getDot();
		} else {
			return -1;
		}
	}
	
	public int getMark(int participantId) {
		CaretHandler handler = getCaretHandler(participantId);
		if (handler != null) {
			return handler.getMark();
		} else {
			return -1;
		}
	}
	
	// --> Fragment Element <--
	
	protected class FragmentElement extends LeafElement implements Fragment {
		
		protected FragmentElement(Element parent, AttributeSet a, int p0, int p1) {
			super(parent, a, p0, p1);
		}
		
		public int getParticipantId() {
			return DefaultDocumentModel.this.getParticipantId(getAttributes());
		}
		
		public String getText() {
			int length = getEndOffset() - getStartOffset();
			try {
				return getDocument().getText(getStartOffset(), length);
			} catch (BadLocationException e) {
				throw new RuntimeException(e);
			}
		}
		
		public String toString() {
			return "Fragment [pid=" + getParticipantId() + "] " + getText();
		}
		
	}
		
}
