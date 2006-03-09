package test;

import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.GapTextStore;
import org.eclipse.jface.text.ITextStore;

public class Test {
	
	public static void main(String[] args) {
		ITextStore store = new GapTextStore(5, 20);
		store.replace(0, 0, "hello world");
		store.replace(5, 1, "-");
		Assert.isTrue("hello-world".equals(store.get(0, store.getLength())));
		System.out.println(store.get(0, store.getLength()));
	}
	
}
