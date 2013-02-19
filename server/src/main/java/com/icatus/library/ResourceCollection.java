package com.icatus.library;

import java.util.LinkedList;
import java.util.List;

public class ResourceCollection extends Resource {
	private List<String> contains = new LinkedList<String>();

	public static ResourceCollection res(String uri){
		Resource current = resources.get(uri);
		if(current!=null && !(current instanceof ResourceCollection)){
			System.err.println("ERROR/t/tERROR " + uri + " is both a resource and collection");
			current = null;
		}
		if(current==null)resources.put(uri, new ResourceCollection(uri));
		return (ResourceCollection) resources.get(uri);
	}
	
	protected ResourceCollection(String uri){
		super(uri);
	}
	
	public List<String> getContains() {
		return contains;
	}
	
	public String toString(){
		String sup = super.toString();
		sup+="Collection Resource with " + contains.size() + " items";
		for(int i = 0; i<contains.size();i++){
			sup+="\t"+i+" "+ contains.get(i)+"\n";
		}
		return sup;
	}
}
