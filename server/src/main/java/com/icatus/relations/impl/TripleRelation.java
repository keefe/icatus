package com.icatus.relations.impl;

import static com.icatus.library.Resource.res;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icatus.library.ILibraryRelation;
import com.icatus.library.Librarian;
import com.icatus.library.Library;
import com.icatus.relations.AbstractRelation;
import com.icatus.relations.BaseRelationSpec;
import com.icatus.relations.IRelationSpec;
import com.icatus.relations.IURIConfiguration;
import com.icatus.relations.Rel;

public class TripleRelation extends AbstractRelation implements ILibraryRelation {
	//for now just do forward relationships, inverse is too annoying
	//for basically not much profit
	//would be easier to do an arbitrary path forward
	private transient TripleStore mem; // = new TripleStore();
	private transient Library library;
	private BaseRelationSpec spec;
	public TripleRelation(){
		super.id = "trpl";
		
		spec = new BaseRelationSpec();
		spec.setId(getId());
		spec.setPrefix(prefix());
		spec.setDescription("This relation is a triple match, possibly transitively applied.");
		
		//super.template = ":host/:uri1/~~?/trpl/:predicateURI";
	}
	
	public void setLibrary(Library library){
		this.library = library;
		this.mem = new TripleStore(library);
	}
	
	public IRelationSpec getSpec() {
		return spec;
	}	

	public IURIConfiguration generateConfig(String ex){
		
		TriplePattern pattern = new TriplePattern();
		pattern.setURI(ex);
		String rem = ex.replace(prefix()+"/", "");
		if(rem.endsWith("/~")||rem.endsWith("/~/")){
			pattern.setTransitive(true);
			rem = rem.replace("/~", "");
		}
		String[] tokens = rem.split("/");
		String lastVal = "";
		String lastTag = null;
		
		for(String t : tokens){
			if(t.equals("_s") || t.equals("_p") || t.equals("_o")){
				addNode(pattern, lastVal, lastTag);
				lastTag = t;
				lastVal="";
			}else{
				lastVal+="/"+t;
			}
		}
		addNode(pattern, lastVal, lastTag);
		
		return pattern;
	}


	private void addNode(TriplePattern pattern, String lastVal, String lastTag) {
		if(lastTag!=null){
			if(lastTag.equals("_s")) pattern.setSubject(lastVal);
			if(lastTag.equals("_p")) pattern.setPredicate(lastVal);
			if(lastTag.equals("_o")) pattern.setObject(lastVal);
		}
	}
	
	/**
	  
	 
	 * */
	public String prefix() {
		return "/~/3";
	}
	
	
	
	public String process(IURIConfiguration uriConfig) {
		String res = "";
		//res+=uriConfig.getURI()+"\n";
		TriplePattern tp = (TriplePattern) uriConfig;
    	Gson g = new GsonBuilder().setPrettyPrinting().create();
    	
		if(tp.getSubject()!=null && tp.getPredicate()!=null && tp.getObject()==null){
			String p = tp.getPredicate();
			if(p.startsWith("/")) p = p.replaceFirst("/", "");
			
			Rel rel = new Rel(tp.getSubject());
			JsonObject jSubj = library.jsonObj(rel.getId());
			JsonElement obj = jSubj.get(p);
			if(obj!=null && !obj.isJsonNull()){
				rel.add(new Rel(obj.getAsString()), p);
			}
			res+=g.toJson(rel, Rel.class);
		}else{
			if(tp.getPredicate()!=null){
				mem.indexPredicate(res(tp.getPredicate()));
			}
			Rel rel = mem.query(tp);
			if(rel!=null){
				res+=g.toJson(rel, Rel.class);	
			}
		}
		
		
		//res+=tp.toString();
		return res;
	}
	
	
	public static void ma12en(String args[]){
		/*
		 * 
		 * 
		 *
		 *  
		 /~/ global URI for relationships/link objects
		 /~/triple/s/:URI1/p/:URI2/o/:URI3
		 /~/triple/s/:URI1/p/:URI2
		 /~/triple/p/:URI2/o/:URI3
		 /~/triple/s/:URI1/o/:URI3
		 
		 
		 
		 
		 
		   :uri1/~
		 	:uri1/~/:predicate?inverse=true&transitive=true
		 
		  :uri1/~/:predicate
		  :uri1/~/:predicate
		  
		 
		 GENERALLY
		 ~ means relation
		 
		  :uri1/~/ all of :uri1's relations
		  :uri1/~/:predicate all of uri1's relations through predicate
		  :uri1/~/:predicate/~/ all of uri1's relations through predicate, transitively
		  :uri1/~-/:predicate inverse relationship of the predicate
		  :uri1/~-/:predicate/~ transitive inverse relationship
		  :uri1/~_/:uuid - relationships centered on :uri1 as described in json on :uuid
		  
		  register something at like
		  /_/server/relationId to give these patterns and an idea of what this plugin accepts
		   Should query parameters and fragments be used?
		   
		   should the general URL design be self descriptive?
		   
		   
		   maybe for all relations something like
		   :uri1/~/relationDescription/~/:relatedToWhat
		   if another /~/ at the end this means it is transitive
		   
		   :uri1/~/:predicate/~/ does this imply transitive then?
		   
		   :uri1/~/:predicate/~/:uri2 - is this even sensible?
		   :uri1/~/:predicate/~/:uri2/~/
		  
		  :predicate must be a json property value
		  :uri1/~/:uri2 - all the paths between :uri1 and :uri2
		   
		 
		 :uri1/~/:predicate1 
		  
		 
		 * */
	}


	public boolean initialize(Librarian librarian) {
		return true;
	}



}
