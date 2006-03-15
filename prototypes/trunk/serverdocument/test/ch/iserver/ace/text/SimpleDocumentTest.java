package ch.iserver.ace.text;

import java.util.Collections;

import org.easymock.MockControl;

import junit.framework.TestCase;

public class SimpleDocumentTest extends TestCase {
	
	private static final String KEY = "test-key";
	
	public void testSimple() throws Exception {
		MockControl textStoreCtrl = MockControl.createStrictControl(ITextStore.class);
		ITextStore textStore = (ITextStore) textStoreCtrl.getMock();
		MockControl partitionerCtrl = MockControl.createStrictControl(IPartitioner.class);
		IPartitioner partitioner = (IPartitioner) partitionerCtrl.getMock();
		
		// expectations
		textStore.replace(0, 0, "abc");
		partitioner.documentUpdated(new DocumentEvent(0, 0, "abc", Collections.singletonMap(KEY, 1)));
		textStore.replace(3, 0, "xyz");
		partitioner.documentUpdated(new DocumentEvent(3, 0, "xyz", Collections.singletonMap(KEY, 2)));
		textStore.replace(1, 4, "");
		partitioner.documentUpdated(new DocumentEvent(1, 4, ""));
		
		// replay
		textStoreCtrl.replay();
		partitionerCtrl.replay();
		
		// test
		SimpleDocument doc = new SimpleDocument(textStore);
		doc.setPartitioner(partitioner);
		doc.insertString(0, "abc", Collections.singletonMap(KEY, 1));
		doc.insertString(3, "xyz", Collections.singletonMap(KEY, 2));
		doc.removeRange(1, 4);
		
		// verify
		textStoreCtrl.verify();
		partitionerCtrl.verify();
	}
	
}
