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
package ch.iserver.ace.collaboration.jupiter.server.document;


/**
 * Interface for storing and managing text. Provides access to the stored text 
 * and allows to manipulate it.
 */
public interface ITextStore {

	/**
	 * Returns the text of the specified character range.
	 *
	 * @param offset the offset of the range
	 * @param length the length of the range
	 * @return the text of the range
	 */
	String getText(int offset, int length);

	/**
	 * Returns number of characters stored in this text store.
	 *
	 * @return the number of characters stored in this text store
	 */
	int getLength();

	/**
	 * Replaces the specified character range with the given text.
	 * <code>replace(getLength(), 0, "some text")</code> is a valid
	 * call and appends text to the end of the text store.
	 *
	 * @param offset the offset of the range to be replaced
	 * @param length the number of characters to be replaced
	 * @param text the substitution text
	 */
	void replace(int offset, int length, String text);

}
