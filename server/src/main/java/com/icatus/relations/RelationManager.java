package com.icatus.relations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RelationManager {

	private Map<String, IRelation> name2Relation
		= new HashMap<String, IRelation>();
	
	//might want to have multiple of these
	private String relSymbol = "/~/";
	
	public RelationManager(){
	}
	
	public IRelation matchRelation(String uri){
		for(IRelation rel : relations()){
			if(uri.startsWith(rel.prefix())){
				return rel;
			}
		}
		return null;
	}
	
	public Collection<IRelation> relations(){
		return name2Relation.values();
	}
	public String relate(String uri, String id)
	{
		IRelation relation = getRelation(id);
		IURIConfiguration config = relation.generateConfig(uri);
		return relation.process(config);
//    	System.out.println("URI IS " + uri); 
//    	if(uri.contains(relSymbol)){
//    		String parts[] = uri.split(relSymbol);
//    		System.out.println("Part 0 " + parts[0]);
//    		System.out.println("Part 1 " + parts[1]);
//    		int whereSlash = parts[1].indexOf("/");
//    		String mainResource = parts[0];
//    		String relType = parts[1].substring(0, whereSlash);
//    		String params = parts[1].substring(whereSlash, parts[1].length());
//    		System.out.println("Relate : " + relType); 
//    		System.out.println("Main Resource : " + mainResource);
//    		System.out.println("Params : " + params);
//    	}
//    	return null;
	}

	
	public void register(IRelation relation){
		name2Relation.put(relation.getId(), relation);
	}
	public IRelation getRelation(String id){
		return name2Relation.get(id);
	}

}
