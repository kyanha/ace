package ch.iserver.ace.text;

import java.util.Iterator;
import java.util.Map;

public interface IAttributedRegion extends IRegion {
	
	Map<String,Object> getAttributes();
	
	Object getAttribute(String name);
	
	Iterator getAttributeNames();
	
}
