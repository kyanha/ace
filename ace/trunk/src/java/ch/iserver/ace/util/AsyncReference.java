package ch.iserver.ace.util;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public class AsyncReference extends PhantomReference {
	
	private final int id;
	
	public AsyncReference(Object referent, ReferenceQueue queue, int id) {
		super(referent, queue);
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
}
