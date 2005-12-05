/*
 * $Id:DocumentItem.java 1091 2005-11-09 13:29:05Z zbinl $
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

package ch.iserver.ace.application.editor;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TextAction;

import ch.iserver.ace.Operation;
import ch.iserver.ace.collaboration.Session;
import ch.iserver.ace.collaboration.util.SessionTemplate;
import ch.iserver.ace.collaboration.util.SessionTemplateCallback;
import ch.iserver.ace.text.DeleteOperation;



public class CollaborativeEditorKit extends StyledEditorKit { //implements ViewFactory {

	private ActionMap actionMap;

	public CollaborativeEditorKit() {
		// create action map
		actionMap = new ActionMap();
		Action[] defaultActions = super.getActions();
		for(int i = 0; i < defaultActions.length; i++) {
			String actionName = (String)defaultActions[i].getValue(Action.NAME);
			actionMap.put(actionName, defaultActions[i]);
		}
		
		// replace actions: DeleteNextCharAction, DeletePrevCharAction & CutAction
		actionMap.put(DefaultEditorKit.deletePrevCharAction, new CollaborativeDeletePrevCharAction());
		actionMap.put(DefaultEditorKit.deleteNextCharAction, new CollaborativeDeleteNextCharAction());
		actionMap.put(DefaultEditorKit.cutAction, new CollaborativeCutAction());

	}

/*	public ViewFactory getViewFactory() {
		return this;
	}

	public View create(Element elem) {
//		System.out.println("createView: " + elem.getName());
//		return new CollaborativeEditorView(elem);
String kind = elem.getName();
if (kind != null) {
	if (kind.equals(AbstractDocument.ContentElementName)) {
		return new CollaborativeEditorView(elem);
	} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
		return new ParagraphView(elem);
	} else if (kind.equals(AbstractDocument.SectionElementName)) {
		return new BoxView(elem, View.Y_AXIS);
	} else if (kind.equals(StyleConstants.ComponentElementName)) {
		return new ComponentView(elem);
	} else if (kind.equals(StyleConstants.IconElementName)) {
		return new IconView(elem);
	}
}
return new CollaborativeEditorView(elem);

	}*/
	
	public Action[] getActions() {
		// create action array
		Object[] keys = actionMap.keys();
		Action[] actions = new Action[keys.length];
		for(int i = 0; i < keys.length; i ++) {
			actions[i] = actionMap.get(keys[i]);
		}
		return actions;
	}

	public Action getCutAction() {
		return actionMap.get(DefaultEditorKit.cutAction);
	}
	
	public Action getCopyAction() {
		return actionMap.get(DefaultEditorKit.copyAction);
	}
	
	public Action getPasteAction() {
		return actionMap.get(DefaultEditorKit.pasteAction);
	}
	
	public Action getSelectAllAction() {
		return actionMap.get(DefaultEditorKit.selectAllAction);
	}







	/**
	 * Extend the default actions
	 */
	public static class CollaborativeDeletePrevCharAction extends DeletePrevCharAction {
		public void actionPerformed(ActionEvent e) {
			if(getTextComponent(e) instanceof CollaborativeTextPane) {

				CollaborativeTextPane cTextPane = (CollaborativeTextPane)getTextComponent(e);
				if(cTextPane.isLocalEditing()) {
					// local editing
					super.actionPerformed(e);
				} else {
					// collab editing -> session
					final JTextComponent target = getTextComponent(e);
	
					Session session = cTextPane.getSession();
					SessionTemplate template = new SessionTemplate(session);
					template.execute(new SessionTemplateCallback() {
						public void execute(Session session) {

							// COPY & PASTE FROM ORIGINAL
							//JTextComponent target = getTextComponent(e);
							boolean beep = true;
							if ((target != null) && (target.isEditable())) {
								try {
									Document doc = target.getDocument();
									Caret caret = target.getCaret();
									int dot = caret.getDot();
									int mark = caret.getMark();
									if (dot != mark) {
										Operation op = new DeleteOperation(Math.min(dot, mark), doc.getText(Math.min(dot, mark), Math.abs(dot - mark)));
										doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
//										System.out.println(op);
										session.sendOperation(op);
										beep = false;
									} else if (dot > 0) {
										int delChars = 1;
									   
										if (dot > 1) {
											String dotChars = doc.getText(dot - 2, 2);
											char c0 = dotChars.charAt(0);
											char c1 = dotChars.charAt(1);
										   
											if (c0 >= '\uD800' && c0 <= '\uDBFF' &&
												c1 >= '\uDC00' && c1 <= '\uDFFF') {
												delChars = 2;
											}
										}
										Operation op = new DeleteOperation(dot - delChars, doc.getText(dot - delChars, delChars));
										doc.remove(dot - delChars, delChars);
//										System.out.println(op);
										session.sendOperation(op);
										beep = false;
									}
								} catch (BadLocationException bl) {
								}
							}
							// END COPY & PASTE

						}
					});				

   				}
			} else {
				// other component
				super.actionPerformed(e);
			}
		}
	}

	public static class CollaborativeDeleteNextCharAction extends DeleteNextCharAction {
		public void actionPerformed(ActionEvent e) {
			if(getTextComponent(e) instanceof CollaborativeTextPane) {

				CollaborativeTextPane cTextPane = (CollaborativeTextPane)getTextComponent(e);
				if(cTextPane.isLocalEditing()) {
					// local editing
					super.actionPerformed(e);
				} else {
					// collab editing -> session
					final JTextComponent target = getTextComponent(e);
	
					Session session = cTextPane.getSession();
					SessionTemplate template = new SessionTemplate(session);
					template.execute(new SessionTemplateCallback() {
						public void execute(Session session) {

							// COPY & PASTE FROM ORIGINAL
							if ((target != null) && (target.isEditable())) {
								try {
									Document doc = target.getDocument();
									Caret caret = target.getCaret();
									int dot = caret.getDot();
									int mark = caret.getMark();
									if (dot != mark) {
										Operation op = new DeleteOperation(Math.min(dot, mark), doc.getText(Math.min(dot, mark), Math.abs(dot - mark)));
										doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
										session.sendOperation(op);
									} else if (dot < doc.getLength()) {
										int delChars = 1;
									   
										if (dot < doc.getLength() - 1) {
											String dotChars = doc.getText(dot, 2);
											char c0 = dotChars.charAt(0);
											char c1 = dotChars.charAt(1);
										   
											if (c0 >= '\uD800' && c0 <= '\uDBFF' &&
												c1 >= '\uDC00' && c1 <= '\uDFFF') {
												delChars = 2;
											}
										}
										Operation op = new DeleteOperation(dot, doc.getText(dot, delChars));
										doc.remove(dot, delChars);
										session.sendOperation(op);
									}
								} catch (BadLocationException bl) {
								}
							}
							// END COPY & PASTE

						}
					});
					
   				}
			} else {
				// other component
				super.actionPerformed(e);
			}
		}
	}

	public static class CollaborativeCutAction extends CutAction {
		public void actionPerformed(ActionEvent e) {
			if(getTextComponent(e) instanceof CollaborativeTextPane) {

				CollaborativeTextPane cTextPane = (CollaborativeTextPane)getTextComponent(e);
				if(cTextPane.isLocalEditing()) {
					// local editing
					super.actionPerformed(e);
				} else {
					// collab editing -> session
					final JTextComponent target = getTextComponent(e);
	
					Session session = cTextPane.getSession();
					SessionTemplate template = new SessionTemplate(session);
					template.execute(new SessionTemplateCallback() {
						public void execute(Session session) {
							// OWN IMPLEMENTATION
							//JTextComponent target = getTextComponent(e);
							boolean beep = true;
							if ((target != null) && (target.isEditable())) {
								try {
									Document doc = target.getDocument();
									Caret caret = target.getCaret();
									int dot = caret.getDot();
									int mark = caret.getMark();
									if (dot != mark) {
										Operation op = new DeleteOperation(Math.min(dot, mark), doc.getText(Math.min(dot, mark), Math.abs(dot - mark)));
//										System.out.println(op);
										session.sendOperation(op);
										beep = false;
									}
								} catch (BadLocationException bl) {
								}
							}							
							// COPY & PASTE FROM ORIGINAL
							if (target != null) {
								target.cut();
							}
							// END COPY & PASTE
						}
					});				

   				}
			} else {
				// other component
				super.actionPerformed(e);
			}
		}
	}

	/*
	 * Deletes the character of content that precedes the
	 * current caret position.
	 * @see DefaultEditorKit#deletePrevCharAction
	 * @see DefaultEditorKit#getActions
	 */
	static class DeletePrevCharAction extends TextAction {
	
		/**
		 * Creates this object with the appropriate identifier.
		 */
		DeletePrevCharAction() {
			super(deletePrevCharAction);
		}
	
		/**
		 * The operation to perform when this action is triggered.
		 *
		 * @param e the action event
		 */
		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			boolean beep = true;
			if ((target != null) && (target.isEditable())) {
				try {
					Document doc = target.getDocument();
					Caret caret = target.getCaret();
					int dot = caret.getDot();
					int mark = caret.getMark();
					if (dot != mark) {
						doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
						beep = false;
					} else if (dot > 0) {
						int delChars = 1;
					   
						if (dot > 1) {
							String dotChars = doc.getText(dot - 2, 2);
							char c0 = dotChars.charAt(0);
							char c1 = dotChars.charAt(1);
						   
							if (c0 >= '\uD800' && c0 <= '\uDBFF' &&
								c1 >= '\uDC00' && c1 <= '\uDFFF') {
								delChars = 2;
							}
						}
					   
						doc.remove(dot - delChars, delChars);
						beep = false;
					}
				} catch (BadLocationException bl) {
				}
			}
			if (beep) {
				UIManager.getLookAndFeel().provideErrorFeedback(target);
			}
		}
	}
	
	/*
	 * Deletes the character of content that follows the
	 * current caret position.
	 * @see DefaultEditorKit#deleteNextCharAction
	 * @see DefaultEditorKit#getActions
	 */
	static class DeleteNextCharAction extends TextAction {
	
		/* Create this object with the appropriate identifier. */
		DeleteNextCharAction() {
			super(deleteNextCharAction);
		}
	
		/** The operation to perform when this action is triggered. */
		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			boolean beep = true;
			if ((target != null) && (target.isEditable())) {
				try {
					Document doc = target.getDocument();
					Caret caret = target.getCaret();
					int dot = caret.getDot();
					int mark = caret.getMark();
					if (dot != mark) {
						doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
						beep = false;
					} else if (dot < doc.getLength()) {
						int delChars = 1;
					   
						if (dot < doc.getLength() - 1) {
							String dotChars = doc.getText(dot, 2);
							char c0 = dotChars.charAt(0);
							char c1 = dotChars.charAt(1);
						   
							if (c0 >= '\uD800' && c0 <= '\uDBFF' &&
								c1 >= '\uDC00' && c1 <= '\uDFFF') {
								delChars = 2;
							}
						}
					   
						doc.remove(dot, delChars);
						beep = false;
					}
				} catch (BadLocationException bl) {
				}
			}
			if (beep) {
				UIManager.getLookAndFeel().provideErrorFeedback(target);
			}
		}
	}

	/**
	 * Cuts the selected region and place its contents
	 * into the system clipboard.
	 * <p>
	 * <strong>Warning:</strong>
	 * Serialized objects of this class will not be compatible with
	 * future Swing releases. The current serialization support is
	 * appropriate for short term storage or RMI between applications running
	 * the same version of Swing.  As of 1.4, support for long term storage
	 * of all JavaBeans<sup><font size="-2">TM</font></sup>
	 * has been added to the <code>java.beans</code> package.
	 * Please see {@link java.beans.XMLEncoder}.
	 *
	 * @see DefaultEditorKit#cutAction
	 * @see DefaultEditorKit#getActions
	 */
	public static class CutAction extends TextAction {
	
		/** Create this object with the appropriate identifier. */
		public CutAction() {
			super(cutAction);
		}
	
		/**
		 * The operation to perform when this action is triggered.
		 *
		 * @param e the action event
		 */
		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			if (target != null) {
				target.cut();
			}
		}
	}

}