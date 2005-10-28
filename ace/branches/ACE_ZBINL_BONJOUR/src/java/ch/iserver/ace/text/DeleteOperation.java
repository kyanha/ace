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

package ch.iserver.ace.text;

import org.apache.log4j.Logger;

import ch.iserver.ace.Operation;

/**
 * The DeleteOperation is used to hold a text together with its position that is
 * to be deleted in the document model.
 */
public class DeleteOperation implements Operation {

	private static final Logger LOG = Logger.getLogger(DeleteOperation.class);

	/**
	 * the text to be deleted.
	 */
	private String text;

	/**
	 * the position in the document where the text is to be deleted.
	 */
	private int position;

	/**
	 * this operation's original operation, i.e if an operation is transformed,
	 * a new operation is created and the old one passed to it as the original
	 * operation.
	 */
	private Operation original;

	/**
	 * Class constructor.
	 */
	public DeleteOperation() {}

	/**
	 * Class constructor.
	 * 
	 * @param position
	 *            the position into the document
	 * @param text
	 *            the text to be deleted
	 */
	public DeleteOperation(int position, String text) {
		setPosition(position);
		setText(text);
	}

	/**
	 * Class constructor.
	 * 
	 * @param position
	 *            the position into the document
	 * @param text
	 *            the text to be deleted
	 * @param isUndo
	 *            flag to indicate whether this operation is an undo
	 */
	public DeleteOperation(int position, String text, boolean isUndo) {
		setPosition(position);
		setText(text);
	}

	/**
	 * Returns the position.
	 * 
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Sets the position of this operation.
	 * 
	 * @param position
	 *            the position to set
	 */
	public void setPosition(int position) {
		if (position < 0) {
			throw new IllegalArgumentException("position index must be >= 0");
		}
		this.position = position;
	}

	/**
	 * Returns the text length.
	 * 
	 * @return the length of the text
	 */
	public int getTextLength() {
		return text.length();
	}

	/**
	 * Returns the text to be deleted.
	 * 
	 * @return the text to be deleted
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text to be deleted.
	 * 
	 * @param text
	 *            the text to be deleted
	 */
	public void setText(String text) {
		if (text == null) {
			throw new IllegalArgumentException("text may not be null");
		}
		this.text = text;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setOriginalOperation(Operation op) {
		original = op;
		LOG.info("setOriginalOperation(" + op + ")");
	}

	/**
	 * {@inheritDoc}
	 */
	public Operation getOriginalOperation() {
		return original;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "Delete(" + position + ",'" + text + "'," + original + ")";
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj.getClass().equals(getClass())) {
			DeleteOperation op = (DeleteOperation) obj;
			return op.position == position && op.text.equals(text)
					&& op.original.equals(original);
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		int hashcode = position;
		hashcode += 13 * text.hashCode();
		hashcode += 13 * original.hashCode();
		return hashcode;
	}
}
