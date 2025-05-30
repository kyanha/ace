package ch.iserver.ace.test;

import ch.iserver.ace.Operation;
import ch.iserver.ace.text.DeleteOperation;
import ch.iserver.ace.text.InsertOperation;
import ch.iserver.ace.text.SplitOperation;

public class DefaultDocument implements Document {

	private StringBuffer buf;
	
	public DefaultDocument(String content) {
		this.buf = new StringBuffer(content);
	}
	
	public void apply(Operation op) {
		if (op instanceof InsertOperation) {
			InsertOperation iop = (InsertOperation) op;
			buf.insert(iop.getPosition(), iop.getText());
		} else if (op instanceof DeleteOperation) {
			DeleteOperation dop = (DeleteOperation) op;
			buf.delete(dop.getPosition(), dop.getPosition() + dop.getTextLength());
		} else if (op instanceof SplitOperation) {
			SplitOperation sop = (SplitOperation) op;
			apply(sop.getSecond());
			apply(sop.getFirst());
		}
	}

	public String getContent() {
		return buf.toString();
	}

}
