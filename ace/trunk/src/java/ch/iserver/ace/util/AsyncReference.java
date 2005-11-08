package ch.iserver.ace.util;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 *
 */
public class AsyncReference extends WeakReference {
	
	/**
	 * 
	 */
	private Worker worker;
		
	/**
	 * @param referent
	 * @param queue
	 * @param worker
	 */
	public AsyncReference(Object referent, ReferenceQueue queue, Worker worker) {
		super(referent, queue);
		this.worker = worker;
	}
		
	/**
	 * @return
	 */
	public Worker getWorker() {
		return worker;
	}
	
}
