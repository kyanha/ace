package org.eclipse.jface.text;

import java.util.Iterator;

public interface IAttributedRegion extends IRegion {
	
	Object getAttribute(String name);
	
	Iterator getAttributeNames();
	
}
