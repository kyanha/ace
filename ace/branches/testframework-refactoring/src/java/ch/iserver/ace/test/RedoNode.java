package ch.iserver.ace.test;


public class RedoNode extends GenerationNode {

	public RedoNode(String siteId) {
		super(siteId);
	}
	
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
