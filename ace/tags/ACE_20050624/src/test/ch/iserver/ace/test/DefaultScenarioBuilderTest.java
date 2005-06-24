package ch.iserver.ace.test;

import ch.iserver.ace.text.DeleteOperation;
import ch.iserver.ace.text.InsertOperation;
import junit.framework.TestCase;

/**
 * 
 */
public class DefaultScenarioBuilderTest extends TestCase {

	/**
	 * 
	 */
	public void testSimple() {
		ScenarioBuilder builder = new DefaultScenarioBuilder();
		builder.init("abc", "a1c");
		builder.addOperation("1", new InsertOperation(1, "1"));
		builder.addOperation("2", new DeleteOperation(1, "b"));
		
		builder.startSite("1");
		builder.addGeneration("1");
		builder.addReception("2");
		builder.endSite();
		
		builder.startSite("2");
		builder.addGeneration("2");
		builder.addReception("1");
		builder.endSite();
	}
	
	/**
	 * 
	 */
	public void testComplex() {
		ScenarioBuilder builder = new DefaultScenarioBuilder();
		builder.init("abc", "a12cd");
		builder.addOperation("1", new InsertOperation(1, "1"));
		builder.addOperation("2", new InsertOperation(1, "2"));
		builder.addOperation("3", new DeleteOperation(1, "b"));
		
		builder.startSite("1");
		builder.addReception("3");
		builder.addGeneration("1");
		builder.addReception("2");
		builder.endSite();

		builder.startSite("2");
		builder.addGeneration("2");
		builder.addReception("3");
		builder.addReception("1");
		builder.endSite();

		builder.startSite("3");
		builder.addGeneration("3");
		builder.addReception("1");
		builder.addReception("2");
		builder.endSite();
	}
	
}
