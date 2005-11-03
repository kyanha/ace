package ch.iserver.ace.spring;

public class PersonImpl implements Person {
	
	private String name;
	
	public PersonImpl() { }
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void assignWork(Object work) {
		System.out.println("work assigned: " + work + " (" + getName() + ")");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("work completed: " + work + " (" + getName() + ")");
	}

}
