package com.icatus.library;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Resource {
	//need to keep a unique index of these
	//since string pooling does that, does eliminate the need?
	//can we just use string cause of string pooling... is that a good idea?
	//not really meta is it
	//why not use jena model
	protected static Map<String, Resource> resources = new HashMap<String, Resource>();
	public static Resource res(String uri){
		if(resources.get(uri)==null) resources.put(uri, new Resource(uri));
		return resources.get(uri);
	}	
	
	protected Resource(String uri){
		this.uri=uri;
	}
	private String uri;
	private boolean explicitRepresentation;
	private boolean staticRepresentation=false;
	
	public boolean isStaticRepresentation() {
		return staticRepresentation;
	}

	public void setStaticRepresentation(boolean staticRepresentation) {
		this.staticRepresentation = staticRepresentation;
	}
	private boolean plugin = false;
	public boolean isPlugin() {
		return plugin;
	}
	public void setPlugin(boolean plugin) {
		this.plugin = plugin;
	}

	private long bytes=0;
	
	public long getBytes() {
		return bytes;
	}
	public void setBytes(long bytes) {
		this.bytes = bytes;
	}
	public boolean hasExplicitRepresentation() {
		return explicitRepresentation;
	}
	public void setExplicitRepresentation(boolean noExplicitRepresentation) {
		this.explicitRepresentation = noExplicitRepresentation;
	}
	//add JSON representation here for query
	public String uri() {
		return uri;
	}
	
	public String toString(){
		String s = "Lives @ " + uri+"\n";
		s+="Explicit File? " + explicitRepresentation;
		s+="TotalSize " + bytes + "\n";
		return s;
	}
}
