package ch.iserver.ace.spring;

import java.util.Locale;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MessageTest {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
		System.out.println(context.getMessage("title", null, Locale.getDefault()));
		System.out.println(context.getMessage("title", null, Locale.GERMAN));
	}

}
