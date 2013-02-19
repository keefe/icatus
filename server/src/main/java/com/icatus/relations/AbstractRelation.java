package com.icatus.relations;

public abstract class AbstractRelation implements IRelation{

	protected String id; 
	
	protected String template; 
	
	public static void main(String[] args) {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getURITemplate() {
		return template;
	}

	public void setURITemplate(String template) {
		this.template = template;
	}

}
