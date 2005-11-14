/*
 * $Id$
 *
 * ace - a collaborative editor
 * Copyright (C) 2005 Mark Bigler, Simon Raess, Lukas Zbinden
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package ch.iserver.ace.ant.dependency;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.selectors.FilenameSelector;

public class EnhancerTask extends Task {
	
	private PatternSet patterns = new PatternSet();
	
	private FileSet fileset;
	
	private File source;
	
	private File target;
	
	public EnhancerTask() {
		
	}
	
	public FileSet getFileset() {
		return fileset;
	}
	
	public void addFileset(FileSet fileset) {
		this.fileset = fileset;
	}
	
	public File getSource() {
		return source;
	}
	
	public void setSource(File source) {
		this.source = source;
	}
	
	public File getTarget() {
		return target;
	}
	
	public void setTarget(File target) {
		this.target = target;
	}
	
	public PatternSet.NameEntry createExclude() {
		return patterns.createExclude();
	}
	
	protected String[] getExcludes() {
		return patterns.getExcludePatterns(getProject());
	}
	
	public void execute() throws BuildException {
		Set entries = new TreeSet();
		DirectoryScanner scanner = getFileset().getDirectoryScanner(getProject());
		scanner.scan();

		String[] files = scanner.getIncludedFiles();
		for (int i = 0; i < files.length; i++) {
			File source = new File(files[i]);
			if (isIncluded(source)) {
				String name = source.getName();
				entries.add(name);
			}
		}
		
		processDependencies(entries);
	}
	
	private boolean isIncluded(File source) {
		FilenameSelector selector = new FilenameSelector();
		String[] excludes = getExcludes();
		for (int i = 0; i < excludes.length; i++) {
			String exclude = excludes[i];
			selector.setName(exclude);
			if (selector.isSelected(source.getParentFile(), source.getName(), source)) {
				return false;
			}
		}
		return true;
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
			parser.parse(getSource(), enhancer);
		} catch (Exception e) {
			throw new BuildException("enhancing source failed", e);
		}
	}
		
}
