package ch.iserver.ace.application.editor;
// StylishDocument.java
//
import javax.swing.text.*;
import javax.swing.event.*;
import java.util.*;

// An extension of DefaultStyledDocument to keep track of which Elements use each
// Style, allowing changes in Styles to be reflected in Elements that use them.
public class StylishDocument extends DefaultStyledDocument
  implements DocumentListener {

  // Create a new document
  public StylishDocument(Content c, StyleContext styles) {
    super(c, styles);
    init();
  }

  // Create a new document
  public StylishDocument(StyleContext styles) {
    super(styles);
    init();
  }

  // Create a new document
  public StylishDocument() {
    super();
    init();
  }

  // We listen to ourself. Also, we add the first paragraph to our style
  // hashtable since we won't get notified that it was added.
  protected void init() {
    addDocumentListener(this);
    addToStyleHash(getParagraphElement(0));
  }

  // This method indicates that the definition of the given style has changed. It
  // goes through each of the Elements that use the style and fires an event
  // indicating that the attributes for the Element have changed. This causes the
  // View to re-check the attributes and redraw.
  public void styleUpdated(Style style) {
    // Find the set of Elements that use this style . . .

    Hashtable ht = (Hashtable)styleHash.get(style);

    if (ht != null) {
      // somebody's using it if we get here.

      // Create a Vector of Elements that shouldn't be in this table because they
      // no longer use this Style (we don't remove them when they change Styles,
      // so they will still be here)
      Vector cleanUp = new Vector();

      // Update each Element . . .
      Enumeration e = ht.keys();
      while (e.hasMoreElements()) {
        Element el = (Element)e.nextElement();
        int start = el.getStartOffset();
        int end = el.getEndOffset();
        Style check = getLogicalStyle(start);

        // Fire an event only if this Element is still using this Style.
        if (check == style) {
          DefaultDocumentEvent ev = new DefaultDocumentEvent
            (start, end-start, DocumentEvent.EventType.CHANGE);
          fireChangedUpdate(ev);
        }
        else {
          // If not, remove this Element, since it no longer uses this Style
          cleanUp.addElement(el);
        }
      }

      // Clean up . . .
      e = cleanUp.elements();
      while (e.hasMoreElements()) {
        Element bad = (Element)e.nextElement();
        ht.remove(bad);
      }
    }
  }

  // Document Listener Methods

  // Call updateStyleHash() whenever text is inserted
  public void insertUpdate(DocumentEvent ev) { updateStyleHash(ev); }

  // Call updateStyleHash() whenever text is removed
  public void removeUpdate(DocumentEvent ev) { updateStyleHash(ev); }

  // Whenever attributes change, add the paragraph that was changed to our hash.
  public void changedUpdate(DocumentEvent ev) {
    int offset = ev.getOffset();
    Element para = getParagraphElement(offset);
    addToStyleHash(para);
  }

  // Internal Methods

  // Called to see if there are any added or removed Elements. If there are any,
  // we need to update our hash.
  protected void updateStyleHash(DocumentEvent ev) {
    DocumentEvent.ElementChange chg =
      ev.getChange(getDefaultRootElement());
        
    if (chg != null) {

      // Something was added or removed (or both) . . .
      Element[] removed = chg.getChildrenRemoved();
      for (int i=0; i<removed.length; i++) {
        removeFromStyleHash(removed[i]);
      }

      Element[] added = chg.getChildrenAdded();
      for (int i=0;i<added.length;i++) {
        addToStyleHash(added[i]);
      }
    }
  }

  // Called to add an Element to our hash.
  protected void addToStyleHash(Element para) {
    AttributeSet attrs = para.getAttributes();
    if (attrs != null) {
      Style style = (Style)attrs.getResolveParent();
      if (style != null) {

        // We've got the Style, now see if we've got a set of Elements that
        // use this Style
        Hashtable ht = (Hashtable)styleHash.get(style);
        if (ht == null) {
          // First user of this Style . . .add a new set
          ht = new Hashtable();
          styleHash.put(style, ht);
        }
        // If this paragraph isn't already in the set, we add it. We really want
        // a Set, not a Hashtable, but to be JDK 1.1 friendly here, we'll use a
        // Hashtable with a throw-away value. We only care about the keys.
        if (ht.containsKey(para) == false) {
          ht.put(para, new Object());
        }
      }
    }
  }

  // Called to remove an Element from our hash
  protected void removeFromStyleHash(Element para) {
    AttributeSet attrs = para.getAttributes();
    if (attrs != null) {
      Style style = (Style)attrs.getResolveParent();
      if (style != null) {
        Hashtable ht = (Hashtable)styleHash.get(style);
        if (ht != null) {
          ht.remove(para);
        }
      }
    }
  }

  // This Hashtable maps from Style -> Hashtable<Element, null>. That is, each
  // key is a Style. The values are Hashtables, the keys of which are the
  // Elements that use the Style. The values of the inner Hashtables are useless
  // (we should use a "Set" data structure, but in JDK 1.1 there is none).
  private Hashtable styleHash = new Hashtable();
}
