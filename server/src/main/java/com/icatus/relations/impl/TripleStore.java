package com.icatus.relations.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.icatus.library.IResourceVisitor;
import com.icatus.library.Library;
import com.icatus.library.Resource;
import com.icatus.relations.Rel;

import static com.icatus.library.Resource.*;

public class TripleStore{

	private Map<Resource, PredicateIndex> index = new HashMap<Resource, PredicateIndex>();
	private Map<Resource, PredicateIndex> reverseIndex = new HashMap<Resource, PredicateIndex>();
	private Library library;
	
	public TripleStore(Library library){
		this.library = library;
	}
	
	
	
	public Rel query(TriplePattern pattern){
		
		if(pattern.getPredicate()!=null && pattern.getObject()!=null && pattern.isTransitive()){
			Rel baseRel = new Rel(pattern.getObject());
			recurseWalk(reverseIndex.get(res(pattern.getPredicate())),baseRel, "to");
			return baseRel;
		}
		if(pattern.getPredicate()!=null && pattern.getSubject()!=null && pattern.isTransitive()){
			Rel baseRel = new Rel(pattern.getObject());
			recurseWalk(index.get(res(pattern.getPredicate())),baseRel, "to");
			return baseRel;
		}
		
		return null;
	}
	
	private void recurseWalk(PredicateIndex x, Rel rel, String l){
		Resource ras = res(rel.getId());
		List<Resource> linked = x.get(ras);
		if(linked!=null)
		for(Resource r : linked){
			Rel newRel = new Rel(r.uri());
			rel.add(newRel, l);
			recurseWalk(x, newRel, l);
		}
	}
	
	private boolean createIfNeeded(Map<Resource, PredicateIndex> dex, Resource p){
		if(dex.get(p)==null){
			dex.put(p, new PredicateIndex(p));
			return true;
		}
		return false;
	}
	private String predicateToVar(Resource p){
		String v = p.uri();
		if(v.startsWith("/")) v = v.substring(1);
		return v;
	}
	//very very poor way to do it
	public void indexPredicate(final Resource p){
		boolean goOn = false;
		if(createIfNeeded(index, p)) goOn = true;
		if(createIfNeeded(reverseIndex, p)) goOn = true;
		if(!goOn) return;
		final String l = predicateToVar(p);
		final PredicateIndex forward = index.get(p);
		final PredicateIndex backwards = reverseIndex.get(p);
		
		try {
			library.visitKnown(new IResourceVisitor() {
				public void visit(final Resource res) throws Exception {
					if(res.hasExplicitRepresentation()){
						JsonObject obj = library.jsonObj(res);
						if(obj!=null && obj.has(l)){
							String rel = obj.get(l).getAsString();
							if(rel.startsWith("/")){
								forward.link(res, res(rel));
								backwards.link(res(rel), res);
							}
							
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	class Creator 
	{

		
		
	}
	
	class PredicateIndex{
		private Resource p;
		public PredicateIndex(Resource p){
			this.p = p;
		}
		private Map<Resource, List<Resource>> oneRel = new HashMap<Resource, List<Resource>>();
		public void link(Resource subj, Resource obj){
			if(oneRel.get(subj)==null) oneRel.put(subj, new LinkedList<Resource>());
			//should be like if contains obj, return false
			oneRel.get(subj).add(obj);
		}
		public List<Resource> get(Resource r){
			return oneRel.get(r);
		}
		
		
	}
	
}
