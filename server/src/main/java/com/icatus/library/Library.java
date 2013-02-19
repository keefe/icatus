package com.icatus.library;

import static com.icatus.library.Resource.res;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.icatus.library.files.SimpleResourceVisitor;
import com.icatus.relations.IRelation;
import com.icatus.relations.RelationManager;
import com.icatus.server.ServerSpec;

//should extract an interface
public class Library {
	
	private RelationManager relationManager = new RelationManager();
	
	//absolute URI to JSON representation
	private Map<Resource, JsonObject> representations = new HashMap<Resource,JsonObject>();
	
	private Map<String, String> url2FS = new HashMap<String, String>();
	private List<String> sortedPrefixes = new ArrayList<String>();
	private HashSet<String> pluginPrefixes = new HashSet<String>(); 
	private Gson gson = new Gson();
	private ServerSpec spec;
	//TODO change URL to URI is annoying
	public static void main(String args[]){
		Library library = new Library();
		library.register("/", "/home/keefe/projects/icatus-seed/v1");
		library.register("/_/plugin", "/home/keefe/projects/icatus-static/templates");
		library.initialize(new ServerSpec());
		System.out.println("the forum JSON is \n" + library.json("/_/plugin/forum/forum"));

		System.out.println("Where is forum " + library.getFname("/_/plugin/forum/forum"));
		System.out.println("Where is " + library.getFname("/forum1/doc0"));
		System.out.println("doc0 JSON is \n" + library.json("/forum1/doc0"));
		JsonObject doc0 = library.jsonObj("/forum1/doc0");
		System.out.println(doc0.get("tags").toString());
		System.out.println("Let's visit around");
		Librarian giles = new Librarian(library);
		SimpleResourceVisitor srv = new SimpleResourceVisitor(library);
		giles.peruseTheStacks(srv);
		System.out.println("Now what we saw was...");
		System.out.println(srv);
	}
	public void visitKnown(IResourceVisitor visitor) throws Exception{
		
		List<Resource> toVisit = new LinkedList<Resource>();
		toVisit.addAll(representations.keySet());
		//bad solution to concurrent modification
		for(Resource r : toVisit) visitor.visit(r);
	}
	public RelationManager getRelationManager() {
		return relationManager;
	}
	public void setRelationManager(RelationManager relationManager) {
		this.relationManager = relationManager;
	}
	public void index(IRelation relation){
		
	}
	

	
	//sometimes it might be good not to have any newlines in there or windoze
	private String lineEnding="\n";
	
	public String getLineEnding() {
		return lineEnding;
	}
	public void setLineEnding(String lineEnding) {
		this.lineEnding = lineEnding;
	}
	
//	//this is stupid
//	public void prepare(String pluginURL){
//		JsonObject pluginSpec = getJSONObject(pluginURL);
//		JsonArray eleArray = pluginSpec.getAsJsonArray("relations");
//		JsonElement e = eleArray.get(0);
//		Gson gson = new Gson();
//		RelSpec forum = gson.fromJson(e, RelSpec.class);
//		students.add(new Scholar(this, forum));
//	}
	
	
	public void initialize(ServerSpec spec){
		sortedPrefixes = new ArrayList<String>(url2FS.keySet());
		Collections.sort(sortedPrefixes, new Comparator<String>() {
			public int compare(String o1, String o2){
				if(o1.length()>o2.length()) return -1;
				if(o1.length()<o2.length()) return 1;
				return 0;
			}
		});
		for(String s : sortedPrefixes) System.out.println(s);
//		spec.getDomainPaths().setMapping(url2FS);
		for(IRelation relation : spec.getRelationObjects()){
			if(relation instanceof ILibraryRelation){
				((ILibraryRelation) relation).setLibrary(this);
			}
			relationManager.register(relation);
			spec.getRelations().add(relation.getSpec());
		}
		this.spec = spec;
		
	}
	private String fix(String prefix){
		prefix = prefix.trim();
		if(!prefix.startsWith("/")) prefix = "/"+prefix;
		if(!prefix.endsWith("/")) prefix = prefix+"/";
		return prefix;
	}
	
	//extract to filesystem only sub
	public void register(String prefix, String fsbase){
		register(prefix, fsbase, false); 
	}
	public void register(String prefix, String fsbase, boolean plugin){
		//if one already in there could be real bad
		if(plugin){
			pluginPrefixes.add(prefix); 
		}
		url2FS.put(fix(prefix), fix(fsbase));
	}
	
	//why is this public?
	public String getDirectory(String prefix){
		return url2FS.get(fix(prefix));
	}
	
	public List<String> getPrefixes(){
		return sortedPrefixes;
	}
	//why aren't you using sorted list?
	public String getPrefix(String url){
		String bestYet = null;
		for(String pre : url2FS.keySet()){
			if(url.startsWith(pre) && (bestYet==null || pre.length()>bestYet.length())){
				bestYet = pre;
			}
		}
		return bestYet;
	}
	private String getFname(String prefix, String path){
		prefix = fix(prefix);
		if(path.startsWith(prefix)){
			path = path.replaceFirst(prefix, "");
			if(path.startsWith("/")) path = path.substring(1);
		}
		return url2FS.get(prefix)+path.replaceAll("/", File.separator)+".json";
	}
	public String getFname(String url){
		return getFname(getPrefix(url), url);
	}
	
	//just add a cache here? how should cache invalidation work?
	//could do the collections there and return that first, then check for a .json
	//if it ends with a slash look for a collection? file then collection? always do that?
	private boolean isMeta(Resource r){
		return r.uri().startsWith("/_");
	}
	private Resource meta(Resource r){
		if("/_".equals(r.uri()) || "/_/".equals(r.uri()))
			return res("/");
		
		String metaUri = r.uri().replaceFirst("/_", "");
		
		return res(metaUri);
	}
	
	public String json(String uri){
		JsonObject obj = jsonObj(uri);
		if(obj==null) return "{\"error\":true}";
		return obj.toString();
	}
	public JsonObject jsonObj(String uri){
		return jsonObj(res(uri));
	}
	public JsonObject jsonObj(Resource r){
		if(representations.get(r)==null){
		//	System.out.println("Add new one " + r.uri());
			String json = readJson(r);
			JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
			representations.put(r, obj);
		}
		return representations.get(r);
	}
	public String delete(String uri){
		Resource dead = res(uri);
		if(isMeta(dead)){
			return "{\"error\":\"leave the metadata alone\"}";
		}
		
		if(dead.isStaticRepresentation()){
			return "{\"error\":\"leave the static data alone\"}";
		}
		
		if(dead instanceof ResourceCollection){
			return "{\"error\":\"bulk deletes not yet supported\"}";
		}
		
		String prefix = getPrefix(dead.uri());
		prefix = fix(prefix);
    	String fname = getFname(prefix, dead.uri());
    	System.out.println("DELETE " + fname);
    	File f = new File(fname);
    	representations.remove(dead);
    	if(f.exists()){
    		return "{\"success\":\""+f.delete()+"\"}";
    	}
		return "{\"success\":\""+false+"\"}";
	}
	
	
	private String readJson(Resource r){
		if(isMeta(r)){
			Resource ref = meta(r);
//			System.out.println("Returning meta resource for : " + ref.uri());
			if(ref.uri().equals("/")){
				return gson.toJson(spec, ServerSpec.class);
			}else{
				IRelation relation = relationManager.matchRelation(ref.uri());
				if(relation!=null){
					return gson.toJson(relation.getSpec());
				}else if(ref.uri().startsWith(spec.getPluginPath()+"/")){
					String pluginId = ref.uri().replaceFirst(spec.getPluginPath()+"/", "");
					
					//TODO this is kind of a nasty approach, does URI semantics say URI=URI/ ? leave it for now
					//eventually merge the contains into the json for a hybrid document
//					boolean getDir = false;
					if(pluginId.endsWith("/")){
						pluginId = pluginId.substring(0, pluginId.length()-1);
//						getDir = true;
					}
//					System.out.println("Finding plugin metadata for " + pluginId + " dir " + getDir);
					if(pluginId.contains("/")){//not just a plugin ID
						return gson.toJson(ref, Resource.class);
					}else{
						String redirect = spec.getPluginPath()+"/"+pluginId+"/_/"+pluginId;
//						System.out.println("Redirecting metadata for " + pluginId + " to " + redirect);
						return readJson(res(redirect));
					}
				}else{
					return gson.toJson(ref, Resource.class);
				}
			}
		}

		if(!r.hasExplicitRepresentation()){
			if(r instanceof ResourceCollection){
				ResourceCollection rc = (ResourceCollection) r;
				return gson.toJson(rc, ResourceCollection.class);
			}
			//System.out.println("### ERROR at " + r);
			return "{\"error\":\"virtual resource\"}";
		}
		String prefix = getPrefix(r.uri());
		String stringRepresentation = null;
		try {
			prefix = fix(prefix);
	    	String fname = getFname(prefix, r.uri());
	    	System.out.println(fname);

	        BufferedReader reader;

			String ff = "";
			FileInputStream fis = new FileInputStream(fname);
			InputStreamReader isr = new InputStreamReader(fis);
			reader = new BufferedReader(isr);
	        String line = null;
	        while((line = reader.readLine())!=null){
	        	ff+=line+lineEnding;
	        }
	        reader.close();
	        stringRepresentation = ff;

		} catch (FileNotFoundException e) {
			stringRepresentation = "{\"error\":\"no doc found\"}";
		} catch (IOException e) {
			e.printStackTrace();
			stringRepresentation =  "{\"error\":\"IO Error"+e.getMessage()+"\"}";
		}
		
		return stringRepresentation;
	}
	
	public String writeJson(String uri, String json){
		return writeJson(res(uri), json);
	}
	private String findDir(String fname){
		int last = fname.lastIndexOf("/");
		return fname.substring(0,last);
	}
	//returns messages
	public String writeJson(Resource r, String json){
		String prefix = getPrefix(r.uri());
		prefix = fix(prefix);
    	
    	try {
    		//	        Path whereGo = FileSystems.getDefault().getPath(dir);
    		String fname = getFname(prefix, r.uri());
        	Path file = FileSystems.getDefault().getPath(fname);
        	Path dir = FileSystems.getDefault().getPath(findDir(fname));
        	System.out.println("@.. " + r.uri());
        	System.out.println("living.. " + file);
        	System.out.println("in directory.. " + dir);
        	System.out.println("write " + json);
        	Files.createDirectories(dir);
			InputStream stream = new ByteArrayInputStream(json.getBytes("UTF-8"));
			Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);
			r.setExplicitRepresentation(true);
			JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
			representations.put(r, obj);	
			return "{\"sucess\":\"true\"}";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return "{\"sucess\":\"false\"}";
		
	}	
	
}
