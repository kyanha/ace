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
		// init
		Operation delA, insB;
		DeleteOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();

		// insert after delete
		delA = new DeleteOperation(1, "12");
		insB = new InsertOperation(4, "ABCD");
		result = (DeleteOperation)transform.transform(delA, insB);
		assertEquals(1, result.getPosition());
		assertEquals("12", result.getText());
	}

	public void testDeleteInsert02() throws Exception {
		// init
		Operation delA, insB;
		DeleteOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();

		// insert starts before or at the same position like insert operation
		delA = new DeleteOperation(1, "12");
		insB = new InsertOperation(0, "ABCD");
		result = (DeleteOperation)transform.transform(delA, insB);
		assertEquals(5, result.getPosition());
		assertEquals("12", result.getText());

		delA = new DeleteOperation(1, "12");
		insB = new InsertOperation(1, "ABCD");
		result = (DeleteOperation)transform.transform(delA, insB);
		assertEquals(5, result.getPosition());
		assertEquals("12", result.getText());
	}

	public void testDeleteInsert03() throws Exception {
		// init
		Operation delA, insB;
		DeleteOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();

		// insert starts before or at the same position like insert operation
		delA = new DeleteOperation(1, "12");
		insB = new InsertOperation(2, "ABCD");
		result = (DeleteOperation)transform.transform(delA, insB);
	}





	public void testDeleteDelete01() throws Exception {
		// init
		Operation delA, delB;
		DeleteOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();

		// Operation A is completly before operation B.
		delA = new DeleteOperation(1, "12");
		delB = new DeleteOperation(4, "ABCD");
		result = (DeleteOperation)transform.transform(delA, delB);
		assertEquals(1, result.getPosition());
		assertEquals("12", result.getText());
	}

	public void testDeleteDelete02() throws Exception {
		// init
		Operation delA, delB;
		DeleteOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();

		// Operation A starts at the end or after operation B. Index of operation A'
		// must be reduced by the length of the text of operation B.
		delA = new DeleteOperation(8, "12");
		delB = new DeleteOperation(4, "ABCD");
		result = (DeleteOperation)transform.transform(delA, delB);
		assertEquals(4, result.getPosition());
		assertEquals("12", result.getText());

		delA = new DeleteOperation(10, "12");
		delB = new DeleteOperation(4, "ABCD");
		result = (DeleteOperation)transform.transform(delA, delB);
		assertEquals(6, result.getPosition());
		assertEquals("12", result.getText());
	}

	public void testDeleteDelete03() throws Exception {
		// init
		Operation delA, delB;
		DeleteOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();

		// Operation B starts before or at the same position like operation A
		// and ends after or at the same position like operation A.
		delA = new DeleteOperation(4, "12");
		delB = new DeleteOperation(4, "ABCD");
		result = (DeleteOperation)transform.transform(delA, delB);
		assertEquals(4, result.getPosition());
		assertEquals("", result.getText());

		delA = new DeleteOperation(6, "12");
		delB = new DeleteOperation(4, "ABCD");
		result = (DeleteOperation)transform.transform(delA, delB);
		assertEquals(6, result.getPosition());
		assertEquals("", result.getText());
	}

	public void testDeleteDelete04() throws Exception {
		// init
		Operation delA, delB;
		DeleteOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();

		// Operation B starts before or at the same position like operation A
		// and ends before operation A.
		delA = new DeleteOperation(6, "12345");
		delB = new DeleteOperation(4, "ABCD");
		result = (DeleteOperation)transform.transform(delA, delB);
		assertEquals(4, result.getPosition());
		assertEquals("345", result.getText());
	}

	public void testDeleteDelete05() throws Exception {
		// init
		Operation delA, delB;
		DeleteOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();

		// Operation B starts after operation A and ends after or at the
		// same position like operation A.
		delA = new DeleteOperation(2, "12345");
		delB = new DeleteOperation(4, "ABCD");
		result = (DeleteOperation)transform.transform(delA, delB);
		assertEquals(2, result.getPosition());
		assertEquals("12", result.getText());
	}

	public void testDeleteDelete06() throws Exception {
		// init
		Operation delA, delB;
		DeleteOperation result;
		GOTOInclusionTransformation transform = new GOTOInclusionTransformation();

		// Operation B is fully in operation A.
		delA = new DeleteOperation(2, "12345");
		delB = new DeleteOperation(3, "ABC");
		result = (DeleteOperation)transform.transform(delA, delB);
		assertEquals(2, result.getPosition());
		assertEquals("15", result.getText());
	}

}
