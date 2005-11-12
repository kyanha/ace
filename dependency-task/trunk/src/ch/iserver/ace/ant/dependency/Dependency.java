package ch.iserver.ace.ant.dependency;

public class Dependency {
	String groupId;
	String artifactId;
	String version;
	public Dependency(String groupId, String artifactId, String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public String getGroupId() {
		return groupId;
	}
	public String getVersion() {
		return version;
	}
	public String getJarName() {
		return artifactId + "-" + version + ".jar";
	}
	public String toString() {
		return groupId + "/" + artifactId + "/" + version;
	}
}