package com.icatus.relations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

//used for output to jquery of trees
public class Rel {
	protected static final String DEFAULT_PREDICATE="_";
	
	protected String id;
	protected Map<String, List<Rel>> link;
	
	
	public static void main( String[] args )
    {
    	Gson g = new Gson();
    	Rel r = new Rel("abc");
    	r.add(new Rel("def")).add(new Rel("keefe").add(new Rel("kate"), "wife"));
    	Rel r2 = new Rel("123");
    	Rel r3 = new Rel("r3");
    	Rel r9 = new Rel("86");
    	Rel r10 = new Rel("11");
    	r9.add(r10);
    	r.add(r9);
    	r.add(r3);
    	r.add(r2);    	
    	
    	System.out.println(g.toJson(r, Rel.class));
    }
    	
	
	public Rel(String id){
		this.id = id;
	}
	

	public Rel add(Rel r){
		return add(r,null);
	}
	public Rel add(Rel r, String predicate){
		if(predicate==null) predicate = DEFAULT_PREDICATE;

		if(link==null){
			link = new HashMap<String, List<Rel>>();
		}
		if(link.get(predicate)==null){
			link.put(predicate, new ArrayList<Rel>());
		}
			
		link.get(predicate).add(r);
		return this;
	}

	public String getId() {
		return id;
	}
}
