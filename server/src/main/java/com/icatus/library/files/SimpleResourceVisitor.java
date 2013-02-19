package com.icatus.library.files;

import java.util.HashSet;

import com.google.gson.JsonObject;
import com.icatus.library.IResourceVisitor;
import com.icatus.library.Library;
import com.icatus.library.Resource;

public class SimpleResourceVisitor implements IResourceVisitor {
	private HashSet<String> visits = new HashSet<String>();
	private Library library;
	public SimpleResourceVisitor(Library library){
		this.library = library;
	}
	public String toString(){
		String s = "Total Resources Visited " + visits.size() + " \n";
		for(String v:visits) s+=v+"\n";
		return s;
	}
	public void visit(Resource res) throws Exception {
		if(visits.contains(res.uri())) return;
		visits.add(res.uri());
		JsonObject jsonObj = library.jsonObj(res);
//		System.out.println("====Visiting===");
//		System.out.println(res);
//		System.out.println("URI " + res.getUri());
//		System.out.println("\tLives At " + res.getFname());
//		System.out.println("\tHas got an explicit json doc? " + res.hasExplicitRepresentation());
//		System.out.println("\tTotal Size in Bytes " + res.getBytes());
//		if(res.hasExplicitRepresentation())
//		{
//			BufferedReader r = Files.newBufferedReader(res.getFname(), Charset.defaultCharset());	
//			JsonObject jso =  new JsonParser().parse(r).getAsJsonObject();
//			if(jso.has("repliesTo")){
//				JsonElement ele = jso.get("repliesTo");
//				System.out.println("Replies to Is : " + ele.getAsString());
//			}
//		}
	}

}
