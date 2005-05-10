package ch.iserver.ace.test;

import java.io.InputStream;

import ch.iserver.ace.test.AlgorithmTestFactory;
import ch.iserver.ace.test.DefaultScenarioBuilder;
import ch.iserver.ace.test.DefaultScenarioLoader;
import ch.iserver.ace.test.ExecuteVisitor;
import ch.iserver.ace.test.Scenario;
import ch.iserver.ace.test.ScenarioLoader;

import junit.framework.TestCase;

/**
 * Abstract test case for algorithm implementations that whish
 * to use scenario based testing using the classes in package
 * <code>ch.iserver.ace.test</code>.
 */
public abstract class AlgorithmTestCase extends TestCase 
		implements AlgorithmTestFactory {

	/**
	 * Executes the given scenario contained in <var>stream</var>.
	 * The scenario is built using a default scenario builder and
	 * loaded using a default scenario loader. Then the scenario
	 * is executed with an execute visitor.
	 * 
	 * @param stream the source of the scenario
	 * @throws Exception if anything goes wrong
	 */
	protected void execute(InputStream stream) throws Exception {
		ScenarioLoader loader = new DefaultScenarioLoader();
		DefaultScenarioBuilder builder = new DefaultScenarioBuilder();
		loader.loadScenario(builder, stream);
		Scenario scenario = builder.getScenario();
		ExecuteVisitor visitor = new ExecuteVisitor(this);
		scenario.accept(visitor);
	}
		
}
