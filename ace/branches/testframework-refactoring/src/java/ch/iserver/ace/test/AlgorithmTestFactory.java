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

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.Timestamp;

/**
 * Factory for creating all the test relevant classes. There are
 * methods for creating algorithms, timestamps and documents.
 */
public interface AlgorithmTestFactory {

	/**
	 * Creates a new algorithm. The new fully configured algorithm object
	 * to test should be returned.
	 * 
	 * @return a new algorithm instance
	 */
	public Algorithm createAlgorithm(int siteId, Object parameter);
	
	/**
	 * Creates a new timestamp. This must be an initial timestamp that
	 * is understood by the algorithm.
	 * 
	 * @return a new (initial) timestamp instance
	 */
	public Timestamp createTimestamp();
	
	/**
	 * Creates a new document model for use in the test. The document
	 * state is given as argument.
	 * 
	 * @param state the document state
	 * @return a new document model instance
	 */
	public DocumentModel createDocument(String state);
	
}
