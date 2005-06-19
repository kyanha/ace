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

import java.io.FileNotFoundException;
import java.io.InputStream;

import junit.framework.TestCase;
import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.algorithm.jupiter.JupiterVectorTime;
import ch.iserver.ace.test.jupiter.JupiterExecuteVisitor;
import ch.iserver.ace.test.jupiter.JupiterXMLScenarioLoader;
import ch.iserver.ace.text.GOTOInclusionTransformation;

/**
 *
 */
public abstract class JupiterTestCase extends TestCase implements AlgorithmTestFactory {
	
	/**
	 * Executes the given scenario contained in <var>stream</var>.
	 * The scenario is built using a default scenario builder and
	 * loaded using a default scenario loader. Then the scenario
	 * is executed with an execute visitor.
	 * 
	 * @param stream the source of the scenario
	 * @throws Exception if anything goes wrong
	 */
	protected void execute(InputStream stream) throws Exception {
		assertNotNull(stream);
		ScenarioLoader loader = new JupiterXMLScenarioLoader();
		DefaultScenarioBuilder builder = new DefaultScenarioBuilder();
		loader.loadScenario(builder, stream);
		Scenario scenario = builder.getScenario();
		NodeVisitor visitor = new JupiterExecuteVisitor(this);
		scenario.accept(visitor);
	}
	
	protected void execute(String resource) throws Exception {
		InputStream stream = getClass().getResourceAsStream(resource);
		if (stream == null) {
			throw new FileNotFoundException(resource);
		}
		execute(stream);
	}
	
	/**
	 * @see ch.iserver.ace.test.AlgorithmTestFactory#createAlgorithm(int,Object)
	 */
	public Algorithm createAlgorithm(int siteId, Object parameter) {
		boolean isClient = ((Boolean) parameter).booleanValue();
		Jupiter jupiter = new Jupiter(siteId, isClient);
		jupiter.setInclusionTransformation(new GOTOInclusionTransformation());
		return jupiter;
	}
	
	/**
	 * @see ch.iserver.ace.test.AlgorithmTestFactory#createTimestamp()
	 */
	public Timestamp createTimestamp() {
		return new JupiterVectorTime(0, 0);
	}
	
	/**
	 * @see ch.iserver.ace.test.AlgorithmTestFactory#createDocument(java.lang.String)
	 */
	public DocumentModel createDocument(String state) {
		return new TestDocumentModel(state);
	}
		
}
