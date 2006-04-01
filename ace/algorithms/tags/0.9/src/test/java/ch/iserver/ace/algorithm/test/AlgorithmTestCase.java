package ch.iserver.ace.algorithm.test;

import java.io.FileNotFoundException;
import java.io.InputStream;

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
		assertNotNull(stream);
		ScenarioLoader loader = createScenarioLoader();
		DefaultScenarioBuilder builder = new DefaultScenarioBuilder();
		loader.loadScenario(builder, stream);
		Scenario scenario = builder.getScenario();
		ExecuteVisitor visitor = createExecuteVisitor(this);
		scenario.accept(visitor);
		visitor.getVerificationResult().verify();
	}
	
	protected void execute(String resource) throws Exception {
		InputStream stream = getClass().getResourceAsStream(resource);
		if (stream == null) {
			throw new FileNotFoundException(resource);
		}
		execute(stream);
	}

	protected abstract ScenarioLoader createScenarioLoader();

	protected abstract ExecuteVisitor createExecuteVisitor(AlgorithmTestFactory factory);
		
}
