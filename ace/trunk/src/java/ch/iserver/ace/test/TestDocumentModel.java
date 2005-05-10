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

package ch.iserver.ace.test;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;
import ch.iserver.ace.text.DeleteOperation;
import ch.iserver.ace.text.InsertOperation;

/**
 *
 */
public class TestDocumentModel extends PlainDocument implements DocumentModel {

	public TestDocumentModel(String content) {
		try {
			insertString(0, content, null);
		} catch (BadLocationException e) {
			// this can be safely ignored as insert at index 0 must succeed
			throw new RuntimeException("unexpected code path");
		}
	}
	
	public void apply(Operation operation) {
		try {
			if (operation instanceof InsertOperation) {
				InsertOperation op = (InsertOperation) operation;
				insertString(op.getPosition(), op.getText(), null);
			} else if (operation instanceof DeleteOperation) {
				DeleteOperation op = (DeleteOperation) operation;
				remove(op.getPosition(), op.getTextLength());
			}
		} catch (BadLocationException e) {
			// TODO: rethrow as some unchecked exception?
			e.printStackTrace();
		}
	}
	
	public String getText() {
		try {
			return getText(0, getLength());
		} catch (BadLocationException e) {
			// TODO: what to do?
			throw new RuntimeException(e);
		}
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj.getClass().equals(getClass())) {
			TestDocumentModel doc = (TestDocumentModel) obj;
			return getText().equals(doc.getText());
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return getText().hashCode();
	}
	
	public String toString() {
		return getText();
	}
	
}
