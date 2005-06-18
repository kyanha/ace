package ch.iserver.ace.test;


public class UndoNode extends GenerationNode {

	public UndoNode(String siteId) {
		super(siteId);
	}
	
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
