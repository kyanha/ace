package ch.iserver.ace.ant.dependency;
import java.util.Iterator;
import java.util.Set;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class PListEnhancer extends Enhancer {
	
	private static final String DICT_ELEM = "dict";
	
	private static final String ARRAY_ELEM = "array";
	
	private static final String STRING_ELEM = "string";
	
	private static final String KEY_ELEM = "key";
	
	private static final String CLASSPATH_KEY = "ClassPath";
	
	private final Set dependencies;
	
	private boolean enabled;
	
	private String key;
	
	private StringBuffer buffer = new StringBuffer();
	
	public PListEnhancer(TransformerHandler target, Set dependencies) throws Exception {
		super(target);
		this.dependencies = dependencies;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (KEY_ELEM.equals(qName)) {
			buffer = new StringBuffer();
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		buffer.append(ch, start, length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (KEY_ELEM.equals(qName)) {
			key = buffer.toString();
			if (CLASSPATH_KEY.equals(key)) {
				enabled = true;
			} else {
				enabled = false;
			}
		} else if (enabled && ARRAY_ELEM.equals(qName)) {
			Attributes atts = new AttributesImpl();
			Iterator it = dependencies.iterator();
			while (it.hasNext()) {
				Dependency dependency = (Dependency) it.next();
				getTarget().startElement(null, "", STRING_ELEM, atts);
				String text = "$JAVAROOT/" + dependency.getJarName();
				getTarget().characters(text.toCharArray(), 0, text.length());
				getTarget().endElement(null, "", STRING_ELEM);
			}
			enabled = false;
		}
		super.endElement(uri, localName, qName);
	}
	
	public void comment(char[] ch, int start, int length) throws SAXException {
		// ignoring comments
	}
	
}
