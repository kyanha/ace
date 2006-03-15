package ch.iserver.ace.text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MockRegion extends Region implements IAttributedRegion {
	
	private final Map attributes;
	
	public MockRegion(int start, int length, Map attributes) {
		super(start, length);
		this.attributes = new HashMap(attributes);
	}
	
	public Map getAttributes() {
		return new HashMap(attributes);
	}
	
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public Iterator getAttributeNames() {
		return attributes.keySet().iterator();
	}

}
