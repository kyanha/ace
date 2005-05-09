package ch.iserver.ace.test;

import java.io.InputStream;

import ch.iserver.ace.test.AlgorithmTestFactory;
import ch.iserver.ace.test.DefaultScenarioBuilder;
import ch.iserver.ace.test.DefaultScenarioLoader;
import ch.iserver.ace.test.ExecuteVisitor;
import ch.iserver.ace.test.Scenario;
import ch.iserver.ace.test.ScenarioLoader;

import junit.framework.TestCase;

public abstract class AlgorithmTestCase extends TestCase 
		implements AlgorithmTestFactory {

	protected void execute(InputStream stream) throws Exception {
		ScenarioLoader loader = new DefaultScenarioLoader();
		DefaultScenarioBuilder builder = new DefaultScenarioBuilder();
		loader.loadScenario(builder, stream);
		Scenario scenario = builder.getScenario();
		ExecuteVisitor visitor = new ExecuteVisitor(this);
		scenario.accept(visitor);
	}
		
}
