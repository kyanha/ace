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
package ch.iserver.ace.algorithm;

import java.util.LinkedList;
import java.util.List;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;
import ch.iserver.ace.text.InsertOperation;

/**
 * This is dummy document model implementation for simple text.
 * 
 * @see ch.iserver.ace.DocumentModel
 */
public class DummyDocumentModel implements DocumentModel {

	private List operations = new LinkedList();
	private String text = "";
	
	/**
	 * {@inheritDoc}
	 */
	public void apply(Operation operation) {
		operations.add(operation);
		if (operation instanceof InsertOperation) {
			text += ((InsertOperation)operation).getText();
		}
	}
	/**
	 * Returns all received operations.
	 * 
	 * @return a list with all received operations
	 */
	public List getOperations() {
		return operations;
	}
	/**
	 * Returns the document content.
	 * 
	 * @return the document content
	 */
	public String getText() {
		return text;
	}
}
