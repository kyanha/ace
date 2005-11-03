package ch.iserver.ace.spring;

import javax.swing.JFrame;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringTest extends JFrame {
	
	public SpringTest() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setSize(200, 200);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new SpringTest();
		ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
		Person person = (Person) context.getBean("person");
		person.assignWork("'LOTS OF WORK'");
		System.out.println("... going on");
		System.out.println(person.getName());
	}
	
}
