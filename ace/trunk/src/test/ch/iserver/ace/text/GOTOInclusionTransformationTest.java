/*
 * $Id: GOTOInclusionTransformationTest.java 389 2005-05-27 08:53:40Z zbinl $
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
package ch.iserver.ace.text;

import junit.framework.TestCase;

import ch.iserver.ace.Operation;
import ch.iserver.ace.text.DeleteOperation;
import ch.iserver.ace.text.GOTOInclusionTransformation;
import ch.iserver.ace.text.InsertOperation;

/**
 * This class tests the Jupiter algorithm implementation of ACE against
 * several counter-examples where other OT algorithms failed. The examples
 * are taken from several papers, especially the two papers from Imine et al.
 * 
 * Yet all tests are done with only character wise transformations. 
 */
public class GOTOInclusionTransformationTest extends TestCase {


	public void testInsertInsert01() throws Exception {
		// init
		Operation insA, insB;
		InsertOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();
				
		// insert before
		insA = new InsertOperation(0, "12");
		insB = new InsertOperation(1, "ABCD");
		result = (InsertOperation)transform.transform(insA, insB);
		assertEquals(0, result.getPosition());
		assertEquals("12", result.getText());
	}

	public void testInsertInsert02() throws Exception {
		// init
		Operation insA, insB;
		InsertOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();
				
		// insert at the same position (ascii order)
		insA = new InsertOperation(0, "12");
		insB = new InsertOperation(0, "ABCD");
		result = (InsertOperation)transform.transform(insA, insB);
		assertEquals(0, result.getPosition());
		assertEquals("12", result.getText());

		// insert at the same poition (origin order)
		insA = new InsertOperation(1, "12", 0);
		insB = new InsertOperation(1, "ABCD", 1);
		result = (InsertOperation)transform.transform(insA, insB);
		assertEquals(1, result.getPosition());
		assertEquals("12", result.getText());

		// insert at the same poition (origin order2)
		insA = new InsertOperation(1, "12", 1);
		insB = new InsertOperation(1, "ABCD", 0);
		result = (InsertOperation)transform.transform(insA, insB);
		assertEquals(5, result.getPosition());
		assertEquals("12", result.getText());

	}

	public void testInsertInsert03() throws Exception {
		// init
		Operation insA, insB;
		InsertOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();
				
		// insert in the middle
		insA = new InsertOperation(2, "12");
		insB = new InsertOperation(0, "ABCD");
		result = (InsertOperation)transform.transform(insA, insB);
		assertEquals(6, result.getPosition());
		assertEquals("12", result.getText());

		// insert at the end
		insA = new InsertOperation(6, "12");
		insB = new InsertOperation(0, "ABCD");
		result = (InsertOperation)transform.transform(insA, insB);
		assertEquals(10, result.getPosition());
		assertEquals("12", result.getText());
	}





	public void testInsertDelete01() throws Exception {
		// init
		Operation insA, delB;
		InsertOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();

		// insert before delete
		insA = new InsertOperation(0, "12");
		delB = new DeleteOperation(1, "ABCD");
		result = (InsertOperation)transform.transform(insA, delB);
		assertEquals(0, result.getPosition());
		assertEquals("12", result.getText());

		// insert at the same position than delete
		insA = new InsertOperation(0, "12");
		delB = new InsertOperation(0, "ABCD");
		result = (InsertOperation)transform.transform(insA, delB);
		assertEquals(0, result.getPosition());
		assertEquals("12", result.getText());
	}

	public void testInsertDelete02() throws Exception {
		// init
		Operation insA, delB;
		InsertOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();

		// insert after delete
		insA = new InsertOperation(6, "12");
		delB = new DeleteOperation(1, "ABCD");
		result = (InsertOperation)transform.transform(insA, delB);
		assertEquals(2, result.getPosition());
		assertEquals("12", result.getText());

		// insert after delete
		insA = new InsertOperation(4, "12");
		delB = new DeleteOperation(0, "ABCD");
		result = (InsertOperation)transform.transform(insA, delB);
		assertEquals(0, result.getPosition());
		assertEquals("12", result.getText());
	}

	public void testInsertDelete03() throws Exception {
		// init
		Operation insA, delB;
		InsertOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();

		// insert after delete
		insA = new InsertOperation(6, "12");
		delB = new DeleteOperation(4, "ABCD");
		result = (InsertOperation)transform.transform(insA, delB);
		assertEquals(4, result.getPosition());
		assertEquals("12", result.getText());
	}





	public void testDeleteInsert01() throws Exception {
		/*
		* Operation A is completly before operation B.
		* (B):          "ABCD"
		* (A):      "12"
		* (A'):     "12"
		*/
	}

	public void testDeleteInsert02() throws Exception {
		/*
		* Operation A starts before or at the same position like operation B.
		* (B):      "ABCD"       |     "ABCD"
		* (A):      "12"         |       "12"
		* (A'):         "12"     |           "12"
		*/
	}

	public void testDeleteInsert03() throws Exception {
		/*
		* !NOT YET IMPLEMENTED!
		* Operation B (insert) is in the range of operation A (delete). Operation A'
		* must be splitted up into two delete operations.
		* (B):       "ABCD"
		* (A):      "123456"
		* (A'):     "1"  "23456"
		*/
	}





	public void testDeleteDelete01() throws Exception {
		/*
		* Operation A is completly before operation B.
		* (B):          "ABCD"
		* (A):      "12"
		* (A'):     "12"
		*/
	}

	public void testDeleteDelete02() throws Exception {
		/*
		* Operation A starts at the end or after operation B. Index of operation A'
		* must be reduced by the length of the text of operation B.
		* (B):      "ABCD"
		* (A):             "12"
		* (A'):        "12"
		*/
	}

	public void testDeleteDelete03() throws Exception {
		/*
		* Operation B starts before or at the same position like operation A
		* and ends after or at the same position like operation A.
		* (B):      "ABCD"     |     "ABCD
		* (A):       "12"      |     "12"
		* (A'):     "12"       |     "12"
		*/
	}

	public void testDeleteDelete04() throws Exception {
		/*
		* Operation B starts before or at the same position like operation A
		* and ends before operation A.
		* (B):      "ABCD"
		* (A):        "12345"
		* (A'):     "345"
		*/
	}

	public void testDeleteDelete05() throws Exception {
		/*
		* Operation B starts after operation A and ends after or at the
		* same position like operation A.
		* (B):        "ABCD"
		* (A):      "12345"
		* (A'):     "12"
		*/
	}

	public void testDeleteDelete06() throws Exception {
		/*
		* Operation B is fully in operation A.
		* (B):       "ABCD"
		* (A):      "123456"
		* (A'):     "12"
		*/
	}

}
