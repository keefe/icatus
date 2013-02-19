package com.icatus.relations.impl;

import com.icatus.library.Resource;

public class Triple {
	private Resource subject,predicate,object;

	public Triple(){
		
	}
	
	public Triple(Resource s, Resource p, Resource o){
		subject=s;
		predicate=p;
		object=o;
	}
	
	public Resource s() {
		return subject;
	}

	public void setSubject(Resource subject) {
		this.subject = subject;
	}

	public Resource p() {
		return predicate;
	}

	public void setPredicate(Resource predicate) {
		this.predicate = predicate;
	}

	public Resource o() {
		return object;
	}

	public void setObject(Resource object) {
		this.object = object;
	}
	
	
}
