package ch.iserver.ace.test;

import ch.iserver.ace.Operation;

public interface Document {
	
	void apply(Operation op);
	
	String getContent();
	
}
