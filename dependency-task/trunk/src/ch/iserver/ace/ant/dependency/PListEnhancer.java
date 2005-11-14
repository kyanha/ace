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

package ch.iserver.ace.ant.dependency;

import java.util.Iterator;
import java.util.Set;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class PListEnhancer extends Enhancer {
	
	private static final String ARRAY_ELEM = "array";
	
	private static final String STRING_ELEM = "string";
	
	private static final String KEY_ELEM = "key";
	
	private static final String CLASSPATH_KEY = "ClassPath";
	
	private final Set dependencies;
	
	private boolean enabled;
	
	private String key;
	
	private StringBuffer buffer = new StringBuffer();
	
	public PListEnhancer(TransformerHandler target, Set dependencies) throws Exception {
		super(target);
		this.dependencies = dependencies;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (KEY_ELEM.equals(qName)) {
			buffer = new StringBuffer();
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		buffer.append(ch, start, length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (KEY_ELEM.equals(qName)) {
			key = buffer.toString();
			if (CLASSPATH_KEY.equals(key)) {
				enabled = true;
			} else {
				enabled = false;
			}
		} else if (enabled && ARRAY_ELEM.equals(qName)) {
			Attributes atts = new AttributesImpl();
			Iterator it = dependencies.iterator();
			while (it.hasNext()) {
				String dependency = (String) it.next();
				getTarget().startElement(null, "", STRING_ELEM, atts);
				String text = "$JAVAROOT/" + dependency;
				getTarget().characters(text.toCharArray(), 0, text.length());
				getTarget().endElement(null, "", STRING_ELEM);
			}
			enabled = false;
		}
		super.endElement(uri, localName, qName);
	}
	
	public void comment(char[] ch, int start, int length) throws SAXException {
		// ignoring comments
	}
	
}
