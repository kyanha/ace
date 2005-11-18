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

package ch.iserver.ace.application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.application.dialog.DialogResult;

/**
 *
 */
public class ApplicationControllerImplTest extends TestCase {
	
	private ApplicationControllerImpl controller;
	
	private MockControl dialogControllerCtrl;
	
	private DialogController dialogController;
	
	private MockControl documentManagerCtrl;
	
	private DocumentManager documentManager;
	
	protected void setUp() throws Exception {
		dialogControllerCtrl = MockControl.createControl(DialogController.class);
		dialogController = (DialogController) dialogControllerCtrl.getMock();
		documentManagerCtrl = MockControl.createControl(DocumentManager.class);
		documentManager = (DocumentManager) documentManagerCtrl.getMock();
		
		controller = new ApplicationControllerImpl();
		controller.setDialogController(dialogController);
		controller.setDocumentManager(documentManager);
	}
	
	
	// --> test close document <--
	
	public void testCloseCleanDocument() throws Exception {
		DocumentItem item = new DocumentItem("test");
		
		// define mock behavior
		documentManager.getSelectedDocument();
		documentManagerCtrl.setReturnValue(item);
		documentManager.closeDocument(item);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();
		
		// test
		controller.closeDocument();
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

	public void testCloseDirtyDocumentNoSave() throws Exception {
		DocumentItem item = new DocumentItem("test");
		item.setDirty();
		
		// define mock behavior
		documentManager.getSelectedDocument();
		documentManagerCtrl.setReturnValue(item);
		documentManager.closeDocument(item);
		
		dialogController.showConfirmCloseDirty("test");
		dialogControllerCtrl.setReturnValue(JOptionPane.NO_OPTION);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();
		
		// test
		controller.closeDocument();
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

	public void testCloseSavedDirtyDocument() throws Exception {
		DocumentItem item = new DocumentItem("test");
		item.setFile(new File("test.txt"));
		item.setDirty();
		
		// define mock behavior
		documentManager.getSelectedDocument();
		documentManagerCtrl.setReturnValue(item);
		documentManager.closeDocument(item);
		
		dialogController.showConfirmCloseDirty("test");
		dialogControllerCtrl.setReturnValue(JOptionPane.YES_OPTION);
				
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();
		
		// test
		controller = new ApplicationControllerImpl() {
			protected boolean saveItem(DocumentItem item) throws IOException {
				return true;
			}
		};
		controller.setDialogController(dialogController);
		controller.setDocumentManager(documentManager);

		controller.closeDocument();
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}
	
	public void testCloseCanceledSaveDirtyDocument() throws Exception {
		DocumentItem item = new DocumentItem("test");
		item.setFile(new File("test.txt"));
		item.setDirty();
		
		// define mock behavior
		documentManager.getSelectedDocument();
		documentManagerCtrl.setReturnValue(item);
		
		dialogController.showConfirmCloseDirty("test");
		dialogControllerCtrl.setReturnValue(JOptionPane.YES_OPTION);
				
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();
		
		// test
		controller = new ApplicationControllerImpl() {
			protected boolean saveItem(DocumentItem item) throws IOException {
				return false;
			}
		};
		controller.setDialogController(dialogController);
		controller.setDocumentManager(documentManager);

		controller.closeDocument();
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

	public void testCloseSaveDirtyDocumentException() throws Exception {
		DocumentItem item = new DocumentItem("test");
		item.setFile(new File("test.txt"));
		item.setDirty();
		final IOException e = new IOException();
		
		// define mock behavior
		documentManager.getSelectedDocument();
		documentManagerCtrl.setReturnValue(item);
		
		dialogController.showConfirmCloseDirty("test");
		dialogControllerCtrl.setReturnValue(JOptionPane.YES_OPTION);
		dialogController.showSaveFailed(item, e);
				
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();
		
		// test
		controller = new ApplicationControllerImpl() {
			protected boolean saveItem(DocumentItem item) throws IOException {
				throw e;
			}
		};
		controller.setDialogController(dialogController);
		controller.setDocumentManager(documentManager);

		controller.closeDocument();
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

	
	// --> test open document <--
	
	public void testOpenDocumentCancel() throws Exception {
		DialogResult result = new DialogResult(JOptionPane.CANCEL_OPTION);
		
		// define mock behavior
		dialogController.showOpenDocuments();
		dialogControllerCtrl.setReturnValue(result);

		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller.openDocument();
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

	public void testOpenDocumentOK() throws Exception {
		File[] files = new File[] {
			new File("a.txt"), new File("b.txt")			
		};
		DialogResult result = new DialogResult(JOptionPane.OK_OPTION, files);
		
		// define mock behavior
		dialogController.showOpenDocuments();
		dialogControllerCtrl.setReturnValue(result);
		
		documentManager.openDocument(files[0]);
		documentManager.openDocument(files[1]);

		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller.openDocument();
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}
	
	public void testOpenDocumentOKFailures() throws Exception {
		IOException e = new IOException();
		File[] files = new File[] {
			new File("a.txt"), new File("b.txt")			
		};
		DialogResult result = new DialogResult(JOptionPane.OK_OPTION, files);
		Map failed = new HashMap();
		failed.put(files[1].getAbsolutePath(), e);
		
		// define mock behavior
		dialogController.showOpenDocuments();
		dialogControllerCtrl.setReturnValue(result);
		dialogController.showOpenFilesFailed(failed);
		
		documentManager.openDocument(files[0]);
		documentManager.openDocument(files[1]);
		documentManagerCtrl.setThrowable(e);

		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller.openDocument();
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

	
	// --> test open file <--

	public void testOpenNonExistingFile() throws Exception {
		String filename = "huderi.txt";
		File file = new File(filename);
		
		// define mock behavior
		dialogController.showFileDoesNotExist(file);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller.openFile(filename);
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}
	
	public void testOpenDirectoryFile() throws Exception {
		String filename = "src";
		File file = new File(filename);
		
		// define mock behavior
		dialogController.showFileIsDirectory(file);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller.openFile(filename);
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}
	
	public void testOpenFile() throws Exception {
		String filename = "src/resources/application-context.xml";
		File file = new File(filename);
		
		// define mock behavior
		documentManager.openDocument(file);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller.openFile(filename);
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

	public void testOpenFileFails() throws Exception {
		String filename = "src/resources/application-context.xml";
		File file = new File(filename);
		
		// define mock behavior
		documentManager.openDocument(file);
		documentManagerCtrl.setThrowable(new IOException());
		dialogController.showOpenFailed(file);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller.openFile(filename);
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

	
	// --> test save all documents <--
	
	public void testSaveAllDocumentsNoneDirty() throws Exception {		
		// define mock behavior
		documentManager.getDirtyDocuments();
		documentManagerCtrl.setReturnValue(new ArrayList());
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller.saveAllDocuments();
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}
	
	public void testSaveAllDocuments() throws Exception {
		final List saved = new ArrayList();
		DocumentItem[] items = new DocumentItem[2];
		items[0] = new DocumentItem("XYZ");
		items[1] = new DocumentItem("ABC");
		List dirty = Arrays.asList(items);
		
		// define mock behavior
		documentManager.getDirtyDocuments();
		documentManagerCtrl.setReturnValue(dirty);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller = new ApplicationControllerImpl() {
			protected boolean saveItem(DocumentItem item) throws IOException {
				saved.add(item);
				return true;
			}
		};
		controller.setDialogController(dialogController);
		controller.setDocumentManager(documentManager);
		
		controller.saveAllDocuments();
		
		// assert
		assertEquals(2, saved.size());
		assertSame(items[0], saved.get(0));
		assertSame(items[1], saved.get(1));
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

	public void testSaveAllDocumentsFailed() throws Exception {
		final IOException e = new IOException();
		final List saved = new ArrayList();
		final DocumentItem[] items = new DocumentItem[2];
		items[0] = new DocumentItem("XYZ");
		items[1] = new DocumentItem("ABC");
		List dirty = Arrays.asList(items);
		Map failed = new HashMap();
		failed.put(items[1], e);
		
		// define mock behavior
		documentManager.getDirtyDocuments();
		documentManagerCtrl.setReturnValue(dirty);
		
		dialogController.showSaveFilesFailed(failed);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller = new ApplicationControllerImpl() {
			protected boolean saveItem(DocumentItem item) throws IOException {
				if (item == items[1]) {
					throw e;
				}
				saved.add(item);
				return true;
			}
		};
		controller.setDialogController(dialogController);
		controller.setDocumentManager(documentManager);
		
		controller.saveAllDocuments();
		
		// assert
		assertEquals(1, saved.size());
		assertSame(items[0], saved.get(0));
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

	
	// --> test save document <--
	
	public void testSaveDocumentClean() throws Exception {
		DocumentItem item = new DocumentItem(new File("XYZ"));
		
		// define mock behavior
		documentManager.getSelectedDocument();
		documentManagerCtrl.setReturnValue(item);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller.saveDocument();
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}
	
	public void testSaveDirtyDocument() throws Exception {
		final List saved = new LinkedList();
		DocumentItem item = new DocumentItem(new File("XYZ"));
		item.setDirty();
		
		// define mock behavior
		documentManager.getSelectedDocument();
		documentManagerCtrl.setReturnValue(item);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller = new ApplicationControllerImpl() {
			protected boolean saveItem(DocumentItem item) throws IOException {
				saved.add(item);
				return true;
			}
		};
		controller.setDialogController(dialogController);
		controller.setDocumentManager(documentManager);
		controller.saveDocument();
		
		// assert
		assertEquals(1, saved.size());
		assertSame(item, saved.get(0));
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

	public void testSaveUnsavedDocument() throws Exception {
		DocumentItem item = new DocumentItem("XYZ");
		final List saved = new LinkedList();
		
		// define mock behavior
		documentManager.getSelectedDocument();
		documentManagerCtrl.setReturnValue(item);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller = new ApplicationControllerImpl() {
			protected boolean saveItem(DocumentItem item) throws IOException {
				saved.add(item);
				return true;
			}
		};
		controller.setDialogController(dialogController);
		controller.setDocumentManager(documentManager);
		controller.saveDocument();
		
		// assert
		assertEquals(1, saved.size());
		assertSame(item, saved.get(0));
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}
	
	public void testSaveDocumentFails() throws Exception {
		final IOException e = new IOException();
		DocumentItem item = new DocumentItem("XYZ");
		
		// define mock behavior
		documentManager.getSelectedDocument();
		documentManagerCtrl.setReturnValue(item);
		dialogController.showSaveFailed(item, e);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller = new ApplicationControllerImpl() {
			protected boolean saveItem(DocumentItem item) throws IOException {
				throw e;
			}
		};
		controller.setDialogController(dialogController);
		controller.setDocumentManager(documentManager);

		controller.saveDocument();
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}
	
	
	// --> test save document as <--
		
	public void testSaveDocumentAs() throws Exception {
		final List saved = new LinkedList();
		DocumentItem item = new DocumentItem("XYZ");
		
		// define mock behavior
		documentManager.getSelectedDocument();
		documentManagerCtrl.setReturnValue(item);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller = new ApplicationControllerImpl() {
			protected boolean saveItemAs(DocumentItem item) throws IOException {
				saved.add(item);
				return true;
			}
		};
		controller.setDialogController(dialogController);
		controller.setDocumentManager(documentManager);

		controller.saveDocumentAs();
		
		// assert
		assertEquals(1, saved.size());
		assertSame(item, saved.get(0));

		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}
	
	public void testSaveDocumentAsFails() throws Exception {
		final IOException e = new IOException();
		DocumentItem item = new DocumentItem("XYZ");
		
		// define mock behavior
		documentManager.getSelectedDocument();
		documentManagerCtrl.setReturnValue(item);
		dialogController.showSaveFailed(item, e);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller = new ApplicationControllerImpl() {
			protected boolean saveItemAs(DocumentItem item) throws IOException {
				throw e;
			}
		};
		controller.setDialogController(dialogController);
		controller.setDocumentManager(documentManager);

		controller.saveDocumentAs();
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}
	
	
	// --> test save item <--
	
	public void testSaveSavedItem() throws Exception {
		DocumentItem item = new DocumentItem(new File("XYZ"));
		
		// define mock behavior
		documentManager.saveDocument(item);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		assertTrue(controller.saveItem(item));
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}
	
	public void testSaveUnsavedItemCompleted() throws Exception {
		final List saved = new ArrayList();
		DocumentItem item = new DocumentItem("XYZ");
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller = new ApplicationControllerImpl() {
			protected boolean saveItemAs(DocumentItem item) throws IOException {
				saved.add(item);
				return true;
			}
		};
		controller.setDialogController(dialogController);
		controller.setDocumentManager(documentManager);

		assertTrue(controller.saveItem(item));
		
		// assert
		assertEquals(1, saved.size());
		assertSame(item, saved.get(0));
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

	public void testSaveUnsavedItemAborted() throws Exception {
		final List saved = new ArrayList();
		DocumentItem item = new DocumentItem("XYZ");
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		controller = new ApplicationControllerImpl() {
			protected boolean saveItemAs(DocumentItem item) throws IOException {
				saved.add(item);
				return false;
			}
		};
		controller.setDialogController(dialogController);
		controller.setDocumentManager(documentManager);

		assertFalse(controller.saveItem(item));
		
		// assert
		assertEquals(1, saved.size());
		assertSame(item, saved.get(0));
		
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}
	
	
	// --> test save item as <--
	
	public void testSaveItemAsExistingFileOverwrite() throws Exception {
		File file = new File("src/resources/application-context.xml");
		DocumentItem item = new DocumentItem("XYZ");
		DialogResult result = new DialogResult(JFileChooser.APPROVE_OPTION, file);
		
		// define mock behavior
		dialogController.showSaveDocument("XYZ");
		dialogControllerCtrl.setReturnValue(result);
		dialogController.showConfirmOverwrite(file);
		dialogControllerCtrl.setReturnValue(JOptionPane.OK_OPTION);
		documentManager.saveAsDocument(file, item);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		assertTrue(controller.saveItemAs(item));
				
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

	public void testSaveItemAsExistingFileNoOverwrite() throws Exception {
		File file = new File("src/resources/application-context.xml");
		DocumentItem item = new DocumentItem("XYZ");
		DialogResult result = new DialogResult(JFileChooser.APPROVE_OPTION, file);
		
		// define mock behavior
		dialogController.showSaveDocument("XYZ");
		dialogControllerCtrl.setReturnValue(result);
		dialogController.showConfirmOverwrite(file);
		dialogControllerCtrl.setReturnValue(JOptionPane.CANCEL_OPTION);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		assertFalse(controller.saveItemAs(item));
				
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

	public void testSaveItemAsNonExistingFile() throws Exception {
		File file = new File("huba.txt");
		DocumentItem item = new DocumentItem("XYZ");
		DialogResult result = new DialogResult(JFileChooser.APPROVE_OPTION, file);
		
		// define mock behavior
		dialogController.showSaveDocument("XYZ");
		dialogControllerCtrl.setReturnValue(result);
		
		documentManager.saveAsDocument(file, item);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		assertTrue(controller.saveItemAs(item));
				
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}
	
	public void testSaveItemAsAborted() throws Exception {
		DocumentItem item = new DocumentItem("XYZ");
		DialogResult result = new DialogResult(JFileChooser.CANCEL_OPTION);
		
		// define mock behavior
		dialogController.showSaveDocument("XYZ");
		dialogControllerCtrl.setReturnValue(result);
		
		// replay
		documentManagerCtrl.replay();
		dialogControllerCtrl.replay();

		// test
		assertFalse(controller.saveItemAs(item));
				
		// verify
		documentManagerCtrl.verify();
		dialogControllerCtrl.verify();
	}

}
