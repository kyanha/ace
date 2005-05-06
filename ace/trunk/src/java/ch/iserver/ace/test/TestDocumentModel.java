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
import ch.iserver.ace.text.Delete;
import ch.iserver.ace.text.Insert;

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
			if (operation instanceof Insert) {
				Insert op = (Insert) operation;
				insertString(op.getPosition(), op.getText(), null);
			} else if (operation instanceof Delete) {
				Delete op = (Delete) operation;
				remove(op.getPosition(), op.getLength());
			}
		} catch (BadLocationException e) {
			// TODO: rethrow as some unchecked exception?
			e.printStackTrace();
		}
	}
	
}
