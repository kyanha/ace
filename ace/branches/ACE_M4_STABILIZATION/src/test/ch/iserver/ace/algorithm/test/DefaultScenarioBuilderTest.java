package ch.iserver.ace.algorithm.test;

import junit.framework.TestCase;
import ch.iserver.ace.algorithm.test.DefaultScenarioBuilder;
import ch.iserver.ace.algorithm.text.DeleteOperation;
import ch.iserver.ace.algorithm.text.InsertOperation;

/**
 * 
 */
public class DefaultScenarioBuilderTest extends TestCase {

	/**
	 * 
	 */
	public void testSimple() {
		DefaultScenarioBuilder builder = new DefaultScenarioBuilder();
		builder.init("abc", "a1c");
		
		builder.startSite("1");
		builder.addDoGeneration("1", new InsertOperation(1, "1"));
		builder.addReception("2");
		builder.endSite();
		
		builder.startSite("2");
		builder.addDoGeneration("2", new DeleteOperation(1, "b"));
		builder.addReception("1");
		builder.endSite();
		
		// get the scenario
		builder.getScenario();
	}
	
	/**
	 * 
	 */
	public void testComplex() {
		DefaultScenarioBuilder builder = new DefaultScenarioBuilder();
		builder.init("abc", "a12cd");
		
		builder.startSite("1");
		builder.addReception("3");
		builder.addDoGeneration("1", new InsertOperation(1, "1"));
		builder.addReception("2");
		builder.endSite();

		builder.startSite("2");
		builder.addDoGeneration("2", new InsertOperation(1, "2"));
		builder.addReception("3");
		builder.addReception("1");
		builder.endSite();

		builder.startSite("3");
		builder.addDoGeneration("3", new DeleteOperation(1, "b"));
		builder.addReception("1");
		builder.addReception("2");
		builder.endSite();
		
		// get the scenario
		builder.getScenario();
	}
	
	public void testWithServer() {
		DefaultScenarioBuilder builder = new DefaultScenarioBuilder();
		builder.init("abc", "a1b2cd");
		
		builder.startSite("1");
		builder.addDoGeneration("1", new InsertOperation(1, "1"));
		builder.addReception("20");
		builder.endSite();
		
		builder.startSite("2");
		builder.addDoGeneration("2", new InsertOperation(2, "2"));
		builder.addReception("10");
		builder.endSite();
		
		builder.addRelay("1", "10");
		builder.addRelay("2", "20");
		
		// get the scenario
		builder.getScenario();
	}
	
	public void testWithUndoRedo() {
		DefaultScenarioBuilder builder = new DefaultScenarioBuilder();
		builder.init("", "a");
		
		builder.startSite("1");
		builder.addDoGeneration("1", new InsertOperation(0, "a"));
		builder.addUndoGeneration("2");
		builder.addRedoGeneration("3");
		builder.endSite();
		
		builder.startSite("2");
		builder.addReception("1");
		builder.addReception("2");
		builder.addReception("3");
		builder.endSite();
		
		// get the scenario
		builder.getScenario();
	}
	
	public void testWithVerification() {
		DefaultScenarioBuilder builder = new DefaultScenarioBuilder();
		builder.init("", "");
		
		builder.startSite("1");
		builder.addDoGeneration("1", new InsertOperation(0, "a"));
		builder.addVerification("a");
		builder.addUndoGeneration("2");
		builder.endSite();
		
		builder.startSite("2");
		builder.addReception("1");
		builder.addVerification("a");
		builder.addReception("2");
		builder.endSite();
		
		// get the scenario
		builder.getScenario();
	}
	
}
