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

package org.eclipse.jface.text;

import java.util.HashMap;
import java.util.Map;


/**
 * Specification of changes applied to documents. All changes are represented as
 * replace commands, i.e. specifying a document range whose text gets replaced
 * with different text. In addition to this information, the event also contains
 * the changed document.
 */
public class DocumentEvent {

	/** The document offset */
	private final int fOffset;
	/** Length of the replaced document text */
	private final int fLength;
	/** Text inserted into the document */
	private final String fText;
	/** Text attributes */
	private final Map fAttributes;
	
	/**
	 * Creates a new document event.
	 *
	 * @param offset the offset of the replaced text
	 * @param length the length of the replaced text
	 * @param text the substitution text
	 */
	public DocumentEvent(int offset, int length, String text) {
		this(offset, length, text, new HashMap());
	}

	public DocumentEvent(int offset, int length, String text, Map attributes) {
		Assert.isTrue(offset >= 0);
		Assert.isTrue(length >= 0);
		Assert.isNotNull(attributes, "attributes");

		fOffset= offset;
		fLength= length;
		fText= text;
		fAttributes = attributes;
	}

	public Map getAttributes() {
		return fAttributes;
	}

	/**
	 * Returns the offset of the change.
	 *
	 * @return the offset of the change
	 */
	public int getOffset() {
		return fOffset;
	}

	/**
	 * Returns the length of the replaced text.
	 *
	 * @return the length of the replaced text
	 */
	public int getLength() {
		return fLength;
	}

	/**
	 * Returns the text that has been inserted.
	 *
	 * @return the text that has been inserted
	 */
	public String getText() {
		return fText;
	}
}
