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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import ch.iserver.ace.Operation;

/**
 * Default implementation of the ScenarioLoader interface. Reads scenarios from
 * an xml file that is valid with respect to a schema (/test/scenario.xsd).
 */
public class XMLScenarioLoader implements ScenarioLoader {
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

	/** default implementation of scenario loader interface */
	public XMLScenarioLoader() { }
	
	/**
	 * @inheritDoc
	 */
	public void loadScenario(ScenarioBuilder scenarioBuilder, InputStream source) 
			throws IOException {
		InputStream schema = getSchema();
		SAXBuilder builder = createSAXBuilder(schema);

		try {
			Document doc = builder.build(source);
			Element root = doc.getRootElement();
		
			String initialState = root.getAttributeValue("initial");
			String finalState = root.getAttributeValue("final");
		
			scenarioBuilder.init(initialState, finalState);
			
			processRootChildren(scenarioBuilder, root);
		} catch (JDOMException e) {
			throw new ScenarioLoaderException(e);
		}
	}
	
	protected InputStream getSchema() {
		return getClass().getResourceAsStream("/test/scenario.xsd");
	}
	
	protected void processRootChildren(ScenarioBuilder builder, Element root) {
		processSites(builder, root.getChildren("site"));
	}
		
	protected Map getProperties(Element el) {
		Map result = new HashMap();
		Iterator it = el.getChildren("property").iterator();
		while (it.hasNext()) {
			Element property = (Element) it.next();
			String name = property.getAttributeValue("name");
			String value = property.getAttributeValue("value");
			result.put(name, value);
		}
		return result;
	}
	
	protected void processSites(ScenarioBuilder builder, List sites) {
		Iterator it = sites.iterator();
		
		while (it.hasNext()) {
			Element siteEl = (Element) it.next();
			String siteId = siteEl.getAttributeValue("id");
			builder.startSite(siteId);
			processSiteChildren(builder, siteEl.getChildren());
			builder.endSite();
		}
	}
	
	protected void processSiteChildren(ScenarioBuilder builder, List children) {
		Iterator it = children.iterator();
		while (it.hasNext()) {
			Element childEl = (Element) it.next();
			if ("generate".equals(childEl.getName())) {
				String id = childEl.getAttributeValue("id");
				Operation op = processOperation(childEl.getChild("operation"));
				builder.addDoGeneration(id, op);
			} else if ("undo".equals(childEl.getName())) {
				String id = childEl.getAttributeValue("id");
				builder.addUndoGeneration(id);
			} else if ("redo".equals(childEl.getName())) {
				String id = childEl.getAttributeValue("id");
				builder.addRedoGeneration(id);
			} else if ("receive".equals(childEl.getName())) {
				String ref = childEl.getAttributeValue("ref");
				builder.addReception(ref);
			} else if ("verify".equals(childEl.getName())) {
				String expect = childEl.getAttributeValue("expect");
				builder.addVerification(expect);
			}
		}
	}
	
	protected Operation processOperation(Element operationEl) { 
		String type = operationEl.getAttributeValue("type");
		try {
			Operation operation = (Operation) Class.forName(type).newInstance();
			Map params = getProperties(operationEl);
			BeanUtils.populate(operation, params);
			return operation;
		} catch (InstantiationException e) {
			throw new ScenarioLoaderException(e);
		} catch (IllegalAccessException e) {
			throw new ScenarioLoaderException(e);
		} catch (InvocationTargetException e) {
			throw new ScenarioLoaderException(e);
		} catch (ClassNotFoundException e) {
			throw new ScenarioLoaderException(e);
		}
	}
	
	protected SAXBuilder createSAXBuilder(InputStream schema) throws IOException {
		SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
		builder.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		builder.setProperty(JAXP_SCHEMA_SOURCE, schema);
		builder.setErrorHandler(new DefaultHandler() {
			public void error(SAXParseException e) throws SAXException {
				throw e;
			}
		});
		
		return builder;
	}
	
}
