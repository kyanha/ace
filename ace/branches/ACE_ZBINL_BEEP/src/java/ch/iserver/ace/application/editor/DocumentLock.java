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

package ch.iserver.ace.application.editor;

import ch.iserver.ace.util.InterruptedRuntimeException;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;

/**
 * A Lock implementation based on the write lock of a 
 * {@link javax.swing.text.AbstractDocument}.
 */
public class DocumentLock implements Lock {
	
	/**
	 * The document to be locked/unlocked.
	 */
	private final CollaborativeDocument document;
	
	/**
	 * Creates a new DocumentLock using the given document.
	 * 
	 * @param document the document which is locked/unlocked
	 */
	public DocumentLock(CollaborativeDocument document) {
		ParameterValidator.notNull("document", document);
		this.document = document;
	}
	
	/**
	 * A DocumentLock does not support this method. So it returns
	 * always true.
	 * 
	 * @see ch.iserver.ace.util.Lock#isOwner(java.lang.Thread)
	 */
	public boolean isOwner(Thread thread) {
		return true;
	}

	/**
	 * Aquires the write lock of the AbstractDocument.
	 * 
	 * @see ch.iserver.ace.util.Lock#lock()
	 */
	public void lock() throws InterruptedRuntimeException {
		document.lock();
	}

	/**
	 * Releases the write lock of the AbstractDocument. This method should
	 * be balanced with preceeding {#lock()} calls.
	 * 
	 * <pre>
	 *  lock();
	 *  try {
	 *    // do some work
	 *  } finally {
	 *    unlock();
	 *  }
	 * </pre>
	 * 
	 * @see ch.iserver.ace.util.Lock#unlock()
	 */
	public void unlock() {
		document.unlock();
	}

}
