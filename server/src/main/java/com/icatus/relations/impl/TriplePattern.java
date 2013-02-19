package com.icatus.relations.impl;

import com.icatus.relations.IURIConfiguration;

public class TriplePattern implements IURIConfiguration{
	
	
	private String subject, predicate, object;
	private boolean transitive = false;
	private String URI;
	public String toString(){
		String s = "Triple Pattern @ " + URI+"\n\n";
		s+="["+subject+" , "+ predicate + " , " + object+"]"+"\n\n";
		s+="Transitive?"+transitive;
		return s;
	}
	public String getURI() {
		return URI;
	}
	public void setURI(String uRI) {
		URI = uRI;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public boolean isTransitive() {
		return transitive;
	}
	public void setTransitive(boolean transitive) {
		this.transitive = transitive;
	}
}
