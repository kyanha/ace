package ch.iserver.ace.collaboration.jupiter.server.document;

import java.util.Iterator;
import java.util.Map;

public interface AttributedRegion extends Region {
	
	Map getAttributes();
	
	Object getAttribute(String name);
	
	Iterator getAttributeNames();
	
}
