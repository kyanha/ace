package ch.iserver.ace.ant.dependency;
import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DependencyHandler extends DefaultHandler {
	
	private static final String NAMESPACE_URI = "antlib:org.apache.maven.artifact.ant";
	
	private static final String DEPENDENCIES_ELEM = "dependencies";

	private static final String DEPENDENCY_ELEM = "dependency";
	
	private static final String PATH_ID_ATTR = "pathId";
	
	private static final String SCOPE_ATTR = "scope";
	
	private static final String SCOPE_TEST = "test";
	
	private static final String GROUP_ID_ATTR = "groupId";
	
	private static final String ARTIFACT_ID_ATTR = "artifactId";
	
	private static final String VERSION_ATTR = "version";
	
	private boolean enabled;
	
	private Set dependencies = new HashSet();
	
	private final String pathId;
	
	public DependencyHandler(String pathId) {
		this.pathId = pathId;
	}
	
	protected void addDependency(String gid, String aid, String version) {
		dependencies.add(new Dependency(gid, aid, version));
	}
	
	public Set getDependencies() {
		return dependencies;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (NAMESPACE_URI.equals(uri) && DEPENDENCIES_ELEM.equals(localName)) {
			String id = attributes.getValue("PATH_ID_ATTR");
			this.enabled = id != null ? pathId.equals(id) : true;
		} else if (enabled && DEPENDENCY_ELEM.equals(localName)) {
			if (!SCOPE_TEST.equalsIgnoreCase(attributes.getValue(SCOPE_ATTR))) { 
				String groupId = attributes.getValue(GROUP_ID_ATTR);
				String artifactId = attributes.getValue(ARTIFACT_ID_ATTR);
				String version = attributes.getValue(VERSION_ATTR);
				addDependency(groupId, artifactId, version);
			}
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (NAMESPACE_URI.equals(uri) && DEPENDENCIES_ELEM.equals(localName)) {
			this.enabled = false;
		}
	}
	
}
