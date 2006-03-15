/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package ch.iserver.ace.text;

import java.util.HashMap;
import java.util.Map;

import ch.iserver.ace.util.ParameterValidator;


/**
 * Specification of changes applied to documents. All changes are represented as
 * replace commands, i.e. specifying a document range whose text gets replaced
 * with different text. In addition to this information, the event also contains
 * the changed document.
 */
public class DocumentEvent {

	/** The document offset */
	private final int offset;
	/** Length of the replaced document text */
	private final int length;
	/** Text inserted into the document */
	private final String text;
	/** Text attributes */
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

	/**
	 * Returns the offset of the change.
	 *
	 * @return the offset of the change
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Returns the length of the replaced text.
	 *
	 * @return the length of the replaced text
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Returns the text that has been inserted.
	 *
	 * @return the text that has been inserted
	 */
	public String getText() {
		return text;
	}
	
	@Override
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
	
	@Override
	public int hashCode() {
		int hash = 7 + offset;
		hash += 11 * length;
		hash += 13 * text.hashCode();
		hash += 17 * attributes.hashCode();
		return hash;
	}
	
}
