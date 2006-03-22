package ch.iserver.ace.collaboration.jupiter.server.document;

import java.util.HashMap;
import java.util.Map;

import ch.iserver.ace.util.ParameterValidator;


public class DocumentEvent {

	private final int offset;

	private final int length;

	private final String text;

	private final Map attributes;
	
	public DocumentEvent(int offset, int length, String text) {
		this(offset, length, text, new HashMap());
	}

	public DocumentEvent(int offset, int length, String text, Map attributes) {
		ParameterValidator.notNegative("offset", offset);
		ParameterValidator.notNegative("length", length);
		ParameterValidator.notNull("attributes", attributes);

		this.offset = offset;
		this.length = length;
		this.text = text;
		this.attributes = attributes;
	}

	public Map getAttributes() {
		return attributes;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	public String getText() {
		return text;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass().equals(obj.getClass())) {
			DocumentEvent evt = (DocumentEvent) obj;
			return offset == evt.offset && length == evt.length
					&& text.equals(evt.text)
					&& attributes.equals(evt.attributes);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		int hash = 7 + offset;
		hash += 11 * length;
		hash += 13 * text.hashCode();
		hash += 17 * attributes.hashCode();
		return hash;
	}
	
}
