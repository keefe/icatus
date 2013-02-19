package com.icatus.relations.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.icatus.library.Librarian;
import com.icatus.relations.AbstractRelation;
import com.icatus.relations.BaseRelationSpec;
import com.icatus.relations.IRelationSpec;
import com.icatus.relations.IURIConfiguration;

public class TagRelation extends AbstractRelation {
	private BaseRelationSpec spec;
	private Map<String,HashSet<String>> tagged;
	private Gson gson = new Gson();
	
	public TagRelation(){
		super.setId("tag");
		spec = new BaseRelationSpec();
		tagged = new HashMap<String, HashSet<String>>();
		
		spec.setId(getId());
		spec.setPrefix(prefix());
		spec.setDescription("This relation gives all the documents with particular tags.");
	}
	//even if what is before the delimiter for the relation is the empty set, there is still a prefix that can be treated as /
	public String prefix() {
		return "/~/t";//suffix approach allows to just add to end of a group
	}
	
	public void register(String tag, String uri){
		HashSet<String> gotit = tagged.get(tag);
		if(gotit==null){
			gotit = new HashSet<String>();
			tagged.put(tag, gotit);
		}
		gotit.add(uri);
	}

	public IURIConfiguration generateConfig(String url) {
		String rem = url.replace(prefix()+"/", "");
		TagPattern pat = new TagPattern();
		pat.setURI(url);
		
		String[] tokens = rem.split("/");
		int goodCount=0, badCount=0;
		for(String t : tokens){
			if(t.startsWith("-")){
				badCount++;
			}else{
				goodCount++;
			}
		}

		int gOn =0, bOn =0;
		String g[] = new String[goodCount];
		String b[] = new String[badCount];
		
		for(String t : tokens){
			if(t.startsWith("-")){
				b[bOn++]=t.replaceFirst("-", "");
			}else{
				g[gOn++]=t;
			}
		}
		pat.setGoodTags(g);
		pat.setBadTags(b);
		
		return pat;
	}

	public String process(IURIConfiguration uriConfig) {
		String res = "";
		//res+=uriConfig.getURI()+"\n";
		TagPattern tp = (TagPattern) (uriConfig);
		//res+=("Tags We Want\n");
		List<String> matches = new LinkedList<String>();
		
		if(tp.getGoodTags()!=null){
			//TODO pagination goes here
			HashSet<String> checking = tagged.get(tp.getGoodTags()[0]);
			if(checking!=null)
			for(String check : checking){
				
				boolean ok = true;
				
				//probably faster to reverse
				for(int i=1; i<tp.getGoodTags().length;i++){
					if(!tagged.get(tp.getGoodTags()[i]).contains(check)){
						ok = false;
						break;
					}
				}
				if(ok){
					matches.add(check);
				}
			}
		}
		
		res+=gson.toJson(matches);
//		res+=("Tags We Avoid\n");
//		
//		if(tp.getGoodTags()!=null)
//		for(String s : tp.getBadTags()){
//			res+=("\t"+s+"\n");
//		}

		return res;
	}
	public boolean initialize(Librarian librarian) {
		return true;
	}
	public IRelationSpec getSpec() {
		return spec;
	}

}
