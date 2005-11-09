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

package ch.iserver.ace.net.protocol;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ch.iserver.ace.net.impl.NetworkConstants;

/**
 *
 */
public class DocumentParserHandler extends DefaultHandler implements ParserHandler {
	
	public static final String DEFAULT_ANSWER = "doc1.txt doc2.txt doc3.txt";
	
//	public static final String DEFAULT_ANSWER = "<ace><publishedDocs><doc name=\"testfile.txt\" id=\"WERS24-RE2\" /><doc name=\"meeting2.txt\" " +
//	"id=\"ADSFBW-45S\" /><doc name=\"notes232.txt\" id=\"23SSWD-3ED\" /></publishedDocs></ace>";
	
	private Map result;
	
	
	public DocumentParserHandler() {
	}

	public void startDocument() throws SAXException {
	}
	
	public void endDocument() throws SAXException {
		
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		System.out.println("StartElement: u-"+uri+" ln-"+localName+" q-"+qName+" aln-"+attributes.getLocalName(0)+" aqn-"+
				attributes.getQName(0)+" at-"+attributes.getType(0)+" av-"+attributes.getValue(0));
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		System.out.println("EndElement: u-"+uri+" ln-"+localName+" qn-"+qName);
	}
	
	/**
	 * Returns a Map with document id's and document names.
	 */
	public Object getResult() {
		return result;
	}
}
