package ch.iserver.ace.collaboration.jupiter.server.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class SimplePartitionerTest extends TestCase {
	
	private static final String KEY = "participant-id";
	
	protected void assertRegionEquals(IAttributedRegion r1, IAttributedRegion r2) {
		assertEquals("start offset do not match", r1.getStart(), r2.getStart());
		assertEquals("length do not match", r1.getLength(), r2.getLength());
		assertEquals("attributes do not match", r1.getAttributes(), r2.getAttributes());
	}
	
	public void testInsertAtEnd() throws Exception {
		DocumentPartitioner partitioner = new SimplePartitioner();
		partitioner.documentUpdated(new DocumentEvent(0, 0, "abc", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(3, 0, " hello", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(9, 0, "x", Collections.singletonMap(KEY, "2")));
		
		IAttributedRegion[] regions = partitioner.getRegions();
		assertEquals(2, regions.length);
		
		assertRegionEquals(new MockRegion(0, 3, Collections.singletonMap(KEY, "1")), regions[0]);
		assertRegionEquals(new MockRegion(3, 7, Collections.singletonMap(KEY, "2")), regions[1]);		
	}
	
	public void testInsertAtStart() throws Exception {
		DocumentPartitioner partitioner = new SimplePartitioner();
		partitioner.documentUpdated(new DocumentEvent(0, 0, "cba", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(0, 0, "hgfe", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(0, 0, "ki", Collections.singletonMap(KEY, "2")));
		
		IAttributedRegion[] regions = partitioner.getRegions();
		assertEquals(2, regions.length);
		
		assertRegionEquals(new MockRegion(0, 6, Collections.singletonMap(KEY, "2")), regions[0]);
		assertRegionEquals(new MockRegion(6, 3, Collections.singletonMap(KEY, "1")), regions[1]);
	}
	
	public void testInsertWithin() throws Exception {
		DocumentPartitioner partitioner = new SimplePartitioner();
		partitioner.documentUpdated(new DocumentEvent(0, 0, "abcdefg", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(6, 0, "abc", Collections.singletonMap(KEY, "2")));
		
		IAttributedRegion[] regions = partitioner.getRegions();
		assertEquals(3, regions.length);
		
		assertRegionEquals(new MockRegion(0, 6, Collections.singletonMap(KEY, "1")), regions[0]);
		assertRegionEquals(new MockRegion(6, 3, Collections.singletonMap(KEY, "2")), regions[1]);
		assertRegionEquals(new MockRegion(9, 1, Collections.singletonMap(KEY, "1")), regions[2]);
	}
	
	public void testInsertBetween() throws Exception {
		DocumentPartitioner partitioner = new SimplePartitioner();
		partitioner.documentUpdated(new DocumentEvent(0, 0, "cba", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(0, 0, "hgfe", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(4, 0, "ki", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(6, 0, "xyzhe", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(6, 0, "a", Collections.singletonMap(KEY, "3")));
		
		IAttributedRegion[] regions = partitioner.getRegions();
		assertEquals(3, regions.length);
		
		assertRegionEquals(new MockRegion(0, 6, Collections.singletonMap(KEY, "2")), regions[0]);
		assertRegionEquals(new MockRegion(6, 1, Collections.singletonMap(KEY, "3")), regions[1]);
		assertRegionEquals(new MockRegion(7, 8, Collections.singletonMap(KEY, "1")), regions[2]);
	}
	
	public void testInsertMultiple() throws Exception {
		SimplePartitioner partitioner = new SimplePartitioner();
		partitioner.documentUpdated(new DocumentEvent(0, 0, "xyz", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(0, 0, "a", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(4, 0, "b", Collections.singletonMap(KEY, "3")));
		partitioner.documentUpdated(new DocumentEvent(2, 0, "c", Collections.singletonMap(KEY, "4")));
		
		IAttributedRegion[] regions = partitioner.getRegions();
		assertEquals(5, regions.length);
		
		assertRegionEquals(new MockRegion(0, 1, Collections.singletonMap(KEY, "2")), regions[0]);
		assertRegionEquals(new MockRegion(1, 1, Collections.singletonMap(KEY, "1")), regions[1]);
		assertRegionEquals(new MockRegion(2, 1, Collections.singletonMap(KEY, "4")), regions[2]);
		assertRegionEquals(new MockRegion(3, 2, Collections.singletonMap(KEY, "1")), regions[3]);
		assertRegionEquals(new MockRegion(5, 1, Collections.singletonMap(KEY, "3")), regions[4]);
	}
	
	public void testRemoveInside() throws Exception {
		DocumentPartitioner partitioner = new SimplePartitioner();
		partitioner.documentUpdated(new DocumentEvent(0, 0, "abcdefghijk", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(11, 0, "XYZ", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(1, 9, ""));
		
		IAttributedRegion[] regions = partitioner.getRegions();
		assertEquals(2, regions.length);
		
		assertRegionEquals(new MockRegion(0, 2, Collections.singletonMap(KEY, "2")), regions[0]);
		assertRegionEquals(new MockRegion(2, 3, Collections.singletonMap(KEY, "1")), regions[1]);
	}
	
	public void testRemoveBetween() throws Exception {
		DocumentPartitioner partitioner = new SimplePartitioner();
		partitioner.documentUpdated(new DocumentEvent(0, 0, "abc", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(3, 0, "xyzxyz", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(2, 4, ""));
		
		IAttributedRegion[] regions = partitioner.getRegions();
		assertEquals(2, regions.length);
		
		assertRegionEquals(new MockRegion(0, 2, Collections.singletonMap(KEY, "1")), regions[0]);
		assertRegionEquals(new MockRegion(2, 3, Collections.singletonMap(KEY, "2")), regions[1]);
	}
	
	public void testRemoveMerge() throws Exception {
		DocumentPartitioner partitioner = new SimplePartitioner();
		partitioner.documentUpdated(new DocumentEvent(0, 0, "XYZ", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(3, 0, "XYZ", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(6, 0, "XYZ", Collections.singletonMap(KEY, "3")));
		partitioner.documentUpdated(new DocumentEvent(9, 0, "XYZ", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(12, 0, "XYZ", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(15, 0, "XYZ", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(3, 9, ""));
		
		IAttributedRegion[] regions = partitioner.getRegions();
		assertEquals(2, regions.length);
		
		assertRegionEquals(new MockRegion(0, 6, Collections.singletonMap(KEY, "1")), regions[0]);
		assertRegionEquals(new MockRegion(6, 3, Collections.singletonMap(KEY, "2")), regions[1]);
	}
	
	public void testRemoveMerge2() throws Exception {
		DocumentPartitioner partitioner = new SimplePartitioner();
		partitioner.documentUpdated(new DocumentEvent(0, 0, "XYZ", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(3, 0, "XYZ", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(6, 0, "XYZ", Collections.singletonMap(KEY, "3")));
		partitioner.documentUpdated(new DocumentEvent(9, 0, "XYZ", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(12, 0, "XYZ", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(15, 0, "XYZ", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(2, 11, ""));
		
		IAttributedRegion[] regions = partitioner.getRegions();
		assertEquals(2, regions.length);
		
		assertRegionEquals(new MockRegion(0, 4, Collections.singletonMap(KEY, "1")), regions[0]);
		assertRegionEquals(new MockRegion(4, 3, Collections.singletonMap(KEY, "2")), regions[1]);
	}
	
	public void testReplaceInside() throws Exception {
		DocumentPartitioner partitioner = new SimplePartitioner();
		partitioner.documentUpdated(new DocumentEvent(0, 0, "abcdefg", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(7, 0, "abc", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(1, 4, "xy", Collections.singletonMap(KEY, "2")));
		
		IAttributedRegion[] regions = partitioner.getRegions();
		assertEquals(2, regions.length);
		
		assertRegionEquals(new MockRegion(0, 5, Collections.singletonMap(KEY, "2")), regions[0]);
		assertRegionEquals(new MockRegion(5, 3, Collections.singletonMap(KEY, "1")), regions[1]);
	}
	
	public void testReplaceAcross() throws Exception {
		DocumentPartitioner partitioner = new SimplePartitioner();
		partitioner.documentUpdated(new DocumentEvent(0, 0, "abcdef", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(6, 0, "abcdef", Collections.singletonMap(KEY, "2")));
		partitioner.documentUpdated(new DocumentEvent(12, 0, "abcdef", Collections.singletonMap(KEY, "1")));
		partitioner.documentUpdated(new DocumentEvent(3, 12, "xy", Collections.singletonMap(KEY, "2")));
		
		IAttributedRegion[] regions = partitioner.getRegions();
		assertEquals(3, regions.length);
		
		assertRegionEquals(new MockRegion(0, 3, Collections.singletonMap(KEY, "1")), regions[0]);
		assertRegionEquals(new MockRegion(3, 2, Collections.singletonMap(KEY, "2")), regions[1]);
		assertRegionEquals(new MockRegion(5, 3, Collections.singletonMap(KEY, "1")), regions[2]);
	}
	
	public void testFindRange() throws Exception {
		List regions = new ArrayList();
		regions.add(new Region(0, 2));
		regions.add(new Region(2, 7));
		regions.add(new Region(9, 10));
		regions.add(new Region(19, 3));
		
		int[] range = SimplePartitioner.findRange(0, 2, regions);
		assertEquals(0, range[0]);
		assertEquals(1, range[1]);
		
		range = SimplePartitioner.findRange(0, 1, regions);
		assertEquals(0, range[0]);
		assertEquals(0, range[1]);
		System.out.println();
		
		range = SimplePartitioner.findRange(2, 7, regions);
		assertEquals(0, range[0]);
		assertEquals(2, range[1]);

		range = SimplePartitioner.findRange(3, 5, regions);
		assertEquals(1, range[0]);
		assertEquals(1, range[1]);
		
		range = SimplePartitioner.findRange(19, 3, regions);
		assertEquals(2, range[0]);
		assertEquals(3, range[1]);
		
		range = SimplePartitioner.findRange(0, 22, regions);
		assertEquals(0, range[0]);
		assertEquals(3, range[1]);
		
		range = SimplePartitioner.findRange(20, 1, regions);
		assertEquals(3, range[0]);
		assertEquals(3, range[1]);
		
		range = SimplePartitioner.findRange(2, 0, regions);
		assertEquals(0, range[0]);
		assertEquals(1, range[1]);
		
		range = SimplePartitioner.findRange(1, 0, regions);
		assertEquals(0, range[0]);
		assertEquals(0, range[1]);
	}
	
	public void testRegionHelpers() throws Exception {
		IRegion region = new Region(2, 5);

		// 0123456789
		// --xxxxx---
		
		assertTrue(SimplePartitioner.isBefore(1, region));
		assertFalse(SimplePartitioner.isBefore(2, region));
		assertFalse(SimplePartitioner.isBefore(3, region));

		assertFalse(SimplePartitioner.isAtStart(1, region));
		assertTrue(SimplePartitioner.isAtStart(2, region));
		assertFalse(SimplePartitioner.isAtStart(3, region));		

		assertFalse(SimplePartitioner.isWithin(2, region));
		assertTrue(SimplePartitioner.isWithin(3, region));
		assertTrue(SimplePartitioner.isWithin(6, region));
		assertFalse(SimplePartitioner.isWithin(7, region));

		assertFalse(SimplePartitioner.isAtEnd(6, region));
		assertTrue(SimplePartitioner.isAtEnd(7, region));
		assertFalse(SimplePartitioner.isAtEnd(8, region));		

		assertFalse(SimplePartitioner.isAfter(6, region));
		assertFalse(SimplePartitioner.isAfter(7, region));
		assertTrue(SimplePartitioner.isAfter(8, region));
	}
	
	private static final class MockRegion extends Region implements IAttributedRegion {
		
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
	
}
