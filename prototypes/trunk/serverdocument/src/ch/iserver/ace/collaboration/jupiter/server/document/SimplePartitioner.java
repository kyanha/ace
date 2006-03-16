package ch.iserver.ace.collaboration.jupiter.server.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimplePartitioner implements DocumentPartitioner {
	
	private List partitions = new ArrayList();

	public void documentUpdated(DocumentEvent e) {
		final int length = e.getLength();
		final int offset = e.getOffset();
		final String text = e.getText();
				
		if (text.length() == 0) {
			removeUpdate(offset, length);
		} else if (length == 0) {
			insertUpdate(offset, text, e.getAttributes());
		} else {
			replaceUpdate(offset, length, text, e.getAttributes());
		}
	}
	
	protected void removeUpdate(int offset, int length) {
		int[] range = findRange(offset, length, partitions);
		int p0 = range[0];
		int p1 = range[1];
		
		assert p0 <= p1 : "p0 > p1";
		
		if (p0 == p1) {
			AttributedRegion region = (AttributedRegion) partitions.get(p0);
			region.length -= length;
			updateOffsets(p0, -length);
		} else {
			AttributedRegion first = (AttributedRegion) partitions.get(p0);
			AttributedRegion last  = (AttributedRegion) partitions.get(p1);
			
			for (int i = p0 + 1; i < p1; i++) {
				partitions.remove(p0 + 1);
			}
			
			if (first.getAttributes().equals(last.getAttributes())) {
				partitions.remove(p0 + 1);
				first.length -= first.getEnd() - offset;
				first.length -= (offset + length) - last.getEnd();
				updateOffsets(p0, -length);
			} else {
				first.length -= first.getEnd() - offset;
				last.length -= last.getEnd() - (offset + length);
				last.offset = first.getEnd();
				updateOffsets(p0 + 1, -length);
			}			
		}
	}
	
	protected void insertUpdate(int offset, String text, Map attributes) {		
		int[] range = findRange(offset, 0, partitions);
		int p0 = range[0];
		int p1 = range[1];
		
		assert (p0 == p1 || p0 + 1 == p1) : "insert can affect at most two positions";
		
		if (p0 == -1) {
			AttributedRegion inserted = new AttributedRegion(offset, text.length(), attributes);
			partitions.add(inserted);
		} else if (p0 == p1) {
			AttributedRegion region = (AttributedRegion) partitions.get(p0);
			if (region.attributes.equals(attributes)) {
				region.length += text.length();
				updateOffsets(p0, text.length());
			} else if (offset == region.offset) {
				AttributedRegion inserted = new AttributedRegion(offset, text.length(), attributes);
				partitions.add(p0, inserted);
				updateOffsets(p0, text.length());
			} else if (offset == region.getEnd()) {
				AttributedRegion inserted = new AttributedRegion(offset, text.length(), attributes);
				partitions.add(p0 + 1, inserted);
				updateOffsets(p0 + 1, text.length());
			} else {
				AttributedRegion inserted = new AttributedRegion(offset, text.length(), attributes);
				int oldLength = region.length;
				region.length = offset - region.offset;
				partitions.add(p0 + 1, inserted);
				AttributedRegion fragment = new AttributedRegion(
						offset + text.length(), 
						oldLength - region.length, 
						region.getAttributes());
				partitions.add(p0 + 2, fragment);
				updateOffsets(p0 + 2, text.length());
			}
		} else if (p0 < p1) {
			AttributedRegion first = (AttributedRegion) partitions.get(p0);
			AttributedRegion second = (AttributedRegion) partitions.get(p1);
			if (first.attributes.equals(attributes)) {
				first.length += text.length();
				updateOffsets(p0, text.length());
			} else if (second.attributes.equals(attributes)) {
				second.length += text.length();
				updateOffsets(p1, text.length());
			} else {
				AttributedRegion inserted = new AttributedRegion(offset, text.length(), attributes);
				partitions.add(p1, inserted);
				updateOffsets(p1, text.length());
			}
		} else {
			throw new RuntimeException("p0 > p1");
		}
	}
	
	protected void replaceUpdate(int offset, int length, String text, Map attributes) {
		removeUpdate(offset, length);
		insertUpdate(offset, text, attributes);
	}
	
	private void updateOffsets(int idx, int delta) {
		for (int i = idx + 1; i < partitions.size(); i++) {
			AttributedRegion region = (AttributedRegion) partitions.get(i);
			region.offset += delta;
		}
	}
	
	protected static int[] findRange(int offset, int length, List partitions) {
		int start = 0;
		int end   = 0;

		int left  = 0;
		int right = partitions.size();
		
		if (right == 0) {
			return new int[] { -1, -1 };
		} else if (offset == getLength(partitions)) {
			return new int[] { right - 1, right - 1 };
		}

		while (left <= right) {
			int mid = (right + left) / 2;
			IRegion region = (IRegion) partitions.get(mid);
			if (isAtStart(offset, region)) {
				start = mid == 0 ? 0 : mid - 1;
				break;
			} else if (isWithin(offset, region)) {
				start = mid;
				break;
			} else if (isAtEnd(offset, region)) {
				start = mid;
				break;
			} else if (isAfter(offset, region)) {
				left = mid + 1;
			} else if (isBefore(offset, region)) {
				right = mid - 1;
			}
		}
		
		left = start;
		right = partitions.size();
		while (left <= right) {
			int mid = (right + left) / 2;
			IRegion region = (IRegion) partitions.get(mid);
			if (isAtStart(offset + length, region)) {
				end = mid;
				break;
			} else if (isWithin(offset + length, region)) {
				end = mid;
				break;
			} else if (isAtEnd(offset + length, region)) {
				end = mid == partitions.size() - 1 ? mid : mid + 1;
				break;
			} else if (isAfter(offset + length, region)) {
				left = mid + 1;
			} else if (isBefore(offset, region)) {
				right = mid - 1;
			}
		}
		
		return new int[] { start, end };
	}
	
	private static int getLength(List regions) {
		int length = 0;
		Iterator it = regions.iterator();
		while (it.hasNext()) {
			IRegion region = (IRegion) it.next();
			length += region.getLength();
		}
		return length; 
	}
		
	protected static boolean isWithin(int offset, IRegion region) {
		return offset > region.getStart() && offset < region.getEnd();
	}
	
	protected static boolean isAfter(int offset, IRegion region) {
		return offset > region.getEnd();
	}
	
	protected static boolean isBefore(int offset, IRegion region) {
		return offset < region.getStart();
	}
	
	protected static boolean isAtStart(int offset, IRegion region) {
		return offset == region.getStart();
	}
	
	protected static boolean isAtEnd(int offset, IRegion region) {
		return offset == region.getEnd();
	}
	
	protected IAttributedRegion[] getRegions(int offset, int length) {
		List regions = new ArrayList(partitions);
				
		return new IAttributedRegion[0];
	}
	
	public IAttributedRegion[] getRegions() {
		return (IAttributedRegion[]) partitions.toArray(new IAttributedRegion[partitions.size()]);
	}
	
	private static class AttributedRegion implements IAttributedRegion, Comparable {
		private int offset;
		private int length;
		private Map attributes;
		
		private AttributedRegion(int offset, int length, Map attributes) {
			this.offset = offset;
			this.length = length;
			this.attributes = new HashMap(attributes);
		}
		
		public int getStart() {
			return offset;
		}
		
		public int getLength() {
			return length;
		}
		
		public int getEnd() {
			return getStart() + getLength();
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
		
		public int compareTo(Object o) {
			IRegion region = (IRegion) o;
			if (getStart() < region.getStart() 
					|| (getStart() == region.getStart() && getLength() < region.getLength())) {
				return -1;
			} else if (getStart() == region.getStart() && getLength() == region.getLength()) {
				return 0;
			} else {
				return 1;
			}
		}
		
		public String toString() {
			return "[start=" + offset + ",length=" + length + ",attributes=" + attributes + "]";
		}
		
	}

}
