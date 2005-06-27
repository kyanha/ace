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

package ch.iserver.ace.test.jupiter;

import java.io.InputStream;
import java.util.Iterator;

import org.jdom.Element;

import ch.iserver.ace.test.XMLScenarioLoader;
import ch.iserver.ace.test.ScenarioBuilder;

/**
 *
 */
public class JupiterXMLScenarioLoader extends XMLScenarioLoader {

	/**
	 * @inheritDoc
	 */
	protected InputStream getSchema() {
		return getClass().getResourceAsStream("/test/jupiter-scenario.xsd");
	}
	
	/**
	 * @inheritDoc
	 */
	protected void processRootChildren(ScenarioBuilder builder, Element root) {
		super.processRootChildren(builder, root);
		processServer(builder, root.getChild("server"));
	}
	
	/**
	 * @param builder the scenario builder
	 * @param serverEl the server element to process
	 */
	protected void processServer(ScenarioBuilder builder, Element serverEl) {
		Iterator it = serverEl.getChildren("relay").iterator();
		while (it.hasNext()) {
			Element el = (Element) it.next();
			String ref = el.getAttributeValue("ref");
			String id  = el.getAttributeValue("id");
			builder.addRelay(ref, id);
		}
	}
	
}
