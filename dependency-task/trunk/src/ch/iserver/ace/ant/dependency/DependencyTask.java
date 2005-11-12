package ch.iserver.ace.ant.dependency;

import java.io.File;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class DependencyTask extends Task {
	
	private File dependencies;
	
	private File plist;
	
	private File target;
	
	private String pathId;

	public File getDependencies() {
		return dependencies;
	}

	public void setDependencies(File dependencyFile) {
		this.dependencies = dependencyFile;
	}

	public File getPlist() {
		return plist;
	}

	public void setPlist(File plist) {
		this.plist = plist;
	}
	
	public void setTarget(File target) {
		this.target = target;
	}
	
	public File getTarget() {
		return target;
	}
	
	public String getPathId() {
		return pathId;
	}
	
	public void setPathId(String pathId) {
		this.pathId = pathId;
	}
	
	public void execute() throws BuildException {
		DependencyHandler dependencyHandler = new DependencyHandler(getPathId());
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();
			parser.parse(getDependencies(), dependencyHandler);
			processDependencies(dependencyHandler.getDependencies());
		} catch (Exception e) {
			throw new BuildException("failed to parse dependency file", e);
		}
	}
	
	protected void processDependencies(Set dependencies) {
		try {
			SAXTransformerFactory sax = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			TransformerHandler handler = sax.newTransformerHandler();
			Transformer serializer = handler.getTransformer();
			serializer.setOutputProperty(OutputKeys.INDENT,"yes");
			handler.setResult(new StreamResult(getTarget()));
			
			PListEnhancer enhancer = new PListEnhancer(handler, dependencies);
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();
			parser.setProperty("http://xml.org/sax/properties/lexical-handler", enhancer);
			parser.parse(getPlist(), enhancer);
		} catch (Exception e) {
			throw new BuildException("failed to parse info.plist", e);
		}
	}
	
	public static void main(String[] args) {
		DependencyTask task = new DependencyTask();
		task.setDependencies(new File("build.xml"));
		task.setPlist(new File("Info.plist"));
		task.setTarget(new File("Info.plist.new"));
		task.execute();
	}
	
}
