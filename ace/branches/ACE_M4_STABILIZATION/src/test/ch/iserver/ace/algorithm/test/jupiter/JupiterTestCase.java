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
package ch.iserver.ace.algorithm.test.jupiter;

import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.algorithm.test.AlgorithmTestCase;
import ch.iserver.ace.algorithm.test.AlgorithmTestFactory;
import ch.iserver.ace.algorithm.text.GOTOInclusionTransformation;

/**
 * Abstract TestCase for Jupiter tests.
 *
 */
public abstract class JupiterTestCase extends AlgorithmTestCase implements
		AlgorithmTestFactory {
	
	/**
	 * @see ch.iserver.ace.algorithm.test.AlgorithmTestFactory#createAlgorithm(int,Object)
	 */
	public Algorithm createAlgorithm(int siteId, Object parameter) {
		boolean isClient = ((Boolean) parameter).booleanValue();
		Jupiter jupiter = new Jupiter(isClient);
		jupiter.setInclusionTransformation(new GOTOInclusionTransformation());
		return jupiter;
	}
		
}
