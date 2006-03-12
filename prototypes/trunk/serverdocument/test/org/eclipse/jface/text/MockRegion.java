package org.eclipse.jface.text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MockRegion extends Region implements IAttributedRegion {
	
	private final Map<String,Object> attributes;
	
	public MockRegion(int start, int length, Map<String,?> attributes) {
		super(start, length);
		this.attributes = new HashMap<String,Object>(attributes);
	}
	
	public Map<String, Object> getAttributes() {
		return new HashMap<String,Object>(attributes);
	}
	
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public Iterator getAttributeNames() {
		return attributes.keySet().iterator();
	}

}
