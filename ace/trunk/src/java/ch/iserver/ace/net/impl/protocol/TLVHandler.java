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

package ch.iserver.ace.net.impl.protocol;

import java.io.CharArrayWriter;
import java.util.Iterator;

import org.apache.log4j.Logger;

import ch.iserver.ace.Fragment;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.impl.FragmentImpl;
import ch.iserver.ace.net.impl.PortableDocumentExt;

/**
 *
 */
public class TLVHandler {

	private static Logger LOG = Logger.getLogger(TLVHandler.class);
	
	private static final char SPACE = ' ';
	
	public static char[] create(PortableDocument doc) {
		Iterator iter = doc.getFragments();
		CharArrayWriter buffer = new CharArrayWriter();
		while (iter.hasNext()) {
			Fragment fragment = (Fragment) iter.next();
			int participantId = fragment.getParticipantId();
			char[] tag = createCharArray(participantId);
			char[] value = fragment.getText().toCharArray();
			char[] length = createCharArray(value.length);
			buffer.write(tag, 0, tag.length);
			buffer.write(SPACE);
			buffer.write(length, 0, length.length);
			buffer.write(SPACE);
			buffer.write(value, 0, value.length);
			if (iter.hasNext())
				buffer.write(SPACE);
		}
		LOG.debug("create TLV of length "+buffer.size());
		//drawback: toCharArray() returns copy of input data: can we avoid this?
		return buffer.toCharArray();
	}
	
	public static void parse(String data, PortableDocumentExt document) {
		int endIndex = 0;
		int startIndex = 0;
		
		 while ((endIndex = data.indexOf(SPACE, startIndex)) != -1) {
			String tag = data.substring(startIndex, endIndex++);
			startIndex = endIndex;
			endIndex = data.indexOf(SPACE, startIndex);
			String length = data.substring(startIndex, endIndex++);
			startIndex = endIndex;
			endIndex += Integer.parseInt(length);
			String value = data.substring(startIndex, endIndex++);
			LOG.debug("tag: '" + tag + "'");
			LOG.debug("length: '" + length + "'");
			LOG.debug("value: '" + value + "'");
			Fragment fragment = new FragmentImpl(Integer.parseInt(tag), value);
			document.addFragment(fragment);
			startIndex = endIndex;
		}	
	}
	
	private static char[] createCharArray(int id) {
		String str = Integer.toString(id);
		char[] charArr = str.toCharArray();
		return charArr;
	}
	
}
