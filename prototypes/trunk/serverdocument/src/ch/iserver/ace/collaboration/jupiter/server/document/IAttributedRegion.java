package ch.iserver.ace.collaboration.jupiter.server.document;

import java.util.Iterator;
import java.util.Map;

public interface IAttributedRegion extends IRegion {
	
	Map getAttributes();
	
	Object getAttribute(String name);
	
	Iterator getAttributeNames();
	
}
