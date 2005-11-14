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

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class Enhancer extends DefaultHandler implements LexicalHandler {
	
	private final TransformerHandler target;
	
	public Enhancer(TransformerHandler target) {
		this.target = target;
	}
	
	protected TransformerHandler getTarget() {
		return target;
	}
	
	public void setDocumentLocator(Locator locator) {
		getTarget().setDocumentLocator(locator);
	}

	public void startDocument() throws SAXException {
		getTarget().startDocument();
	}

	public void endDocument() throws SAXException {
		getTarget().endDocument();
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		getTarget().startPrefixMapping(prefix, uri);
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		getTarget().endPrefixMapping(prefix);
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		getTarget().startElement(namespaceURI, localName, qName, atts);
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		getTarget().endElement(namespaceURI, localName, qName);
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		getTarget().characters(ch, start, length);
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		getTarget().ignorableWhitespace(ch, start, length);
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		getTarget().processingInstruction(target, data);
	}

	public void skippedEntity(String name) throws SAXException {
		getTarget().skippedEntity(name);
	}

	public void notationDecl(String name, String publicId, String systemId)
			throws SAXException {
		getTarget().notationDecl(name, publicId, systemId);
	}

	public void unparsedEntityDecl(String name, String publicId,
			String systemId, String notationName) throws SAXException {
		getTarget().unparsedEntityDecl(name, publicId, systemId, notationName);
	}

	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		getTarget().startDTD(name, publicId, systemId);
	}

	public void endDTD() throws SAXException {
		getTarget().endDTD();
	}

	public void startEntity(String name) throws SAXException {
		getTarget().startEntity(name);
	}

	public void endEntity(String name) throws SAXException {
		getTarget().endEntity(name);
	}

	public void startCDATA() throws SAXException {
		getTarget().startCDATA();
	}

	public void endCDATA() throws SAXException {
		getTarget().endCDATA();
	}

	public void comment(char[] ch, int start, int length) throws SAXException {
		getTarget().comment(ch, start, length);
	}

}
