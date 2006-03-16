package ch.iserver.ace.collaboration.jupiter.server.document;

public class Region implements IRegion {

	private int start;
	
	private int length;
	
	public Region(int start, int length) {
		this.start = start;
		this.length = length;
	}
	
	public int getLength() {
		return length;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return start + length;
	}

}