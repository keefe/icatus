package com.icatus.library;

import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icatus.library.files.SimpleResourceVisitor;
import com.icatus.relations.IRelation;
import com.icatus.relations.IURIConfiguration;
import com.icatus.relations.impl.TagRelation;
import com.icatus.server.IcatusAPI;
import com.icatus.server.IcatusRequest;
import com.icatus.server.IcatusResponse;
import com.icatus.server.ServerSpec;

public class IcatusLibrary implements IcatusAPI {

	private Gson gson;
	private Library library;
	private String templateDefault = "";

	//TODO pull out put, get etc into separate methods, this method is heinous
	public IcatusResponse handle(IcatusRequest request) {
		System.out.println("handling internally " + request.getUri());
		String response = gson.toJson(request, IcatusRequest.class);
		String responseType = request.getResponseType();
		IRelation rel = library.getRelationManager().matchRelation(request.getUri());
		System.out.println(request);
		if("GET".equals(request.getMethod())){
			
			if(rel!=null){
				IURIConfiguration config = rel.generateConfig(request.getUri());
				response =  rel.process(config);
				//response=rel.put(config); if put, only above if get
			}else{
				if(request.getTemplate()!=null){
					response = templateDefault;
					responseType = "text/html";
				}else{
					response = library.json(request.getUri());
				}
			}
		}else if("PUT".equals(request.getMethod())){
				response = library.writeJson(request.getUri(), request.getBody());
				
		}else if("DELETE".equals(request.getMethod())){
			System.out.println(request.getUri() + " DELETE");
			response = library.delete(request.getUri());
		}
//		System.out.println(r);
		return new IcatusResponse(response, responseType);
	}

	public boolean initialize(ServerSpec spec) {
		gson = new Gson();
		this.templateDefault = spec.getDefaultTemplate();
		library = new Library();
		library.register("/", spec.getDatafiles());
		library.register(spec.getPluginPath(), spec.getPluginDir());
		library.initialize(spec);
		Librarian giles = new Librarian(library);
		SimpleResourceVisitor srv = new SimpleResourceVisitor(library);
		final TagRelation tagr = (TagRelation) library.getRelationManager().getRelation("tag");
		giles.peruseTheStacks(new IResourceVisitor(){
			private HashSet<String> visits = new HashSet<String>();
			
			public void visit(Resource res) throws Exception {
				if(visits.contains(res.uri())) return;
				visits.add(res.uri());
				JsonObject jsonObj = library.jsonObj(res);
				if(jsonObj.has("tags")){
					JsonElement element = jsonObj.get("tags");
					if(element.isJsonArray()){
						JsonArray arr = element.getAsJsonArray();
						for(int i=0; i<arr.size();i++){
							String tag = arr.get(i).getAsString().trim();
							if(tag.length()>0)
								tagr.register(tag, res.uri());
						}
					}else{
						String tagString = element.getAsString();
						String tags[] = tagString.split(",");
						for(String tag : tags){
							if(tag.length()>0)
								tagr.register(tag, res.uri());

						}
					}
				}
			}
			
		});              
		return true;
	}
	
	
	

}
