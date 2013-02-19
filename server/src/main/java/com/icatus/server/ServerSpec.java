package com.icatus.server;

import java.util.LinkedList;
import java.util.List;

import com.icatus.relations.IRelation;
import com.icatus.relations.IRelationSpec;



public class ServerSpec {
	
	public String getDatafiles() {
		return datafiles;
	}


	public void setDatafiles(String datafiles) {
		this.datafiles = datafiles;
	}


	public String getStaticFiles() {
		return staticFiles;
	}


	public void setStaticFiles(String staticFiles) {
		this.staticFiles = staticFiles;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}

	private transient String defaultTemplate;
	private String pluginDir;
	
	//TODO remove any remaining references hardcoded to /plugins
	private String pluginPath = "/plugins";
	private String datafiles;
	private String staticFiles;
	private String relationPath="/~/";
	private int port;
	public String getPluginPath() {
		return pluginPath;
	}


	public void setPluginPath(String pluginPath) {
		this.pluginPath = pluginPath;
	}

//	private PathRelation domainPaths = new PathRelation(":HOST/:URI mapped, longest match first, to .json files");
//	private PathRelation staticPaths = new PathRelation("if :HOST/:URI can't be mapped in domainPaths or has an obvious extension, send the raw file.");
	private List<IRelationSpec> relations = new LinkedList<IRelationSpec>();
	private transient List<IRelation> relationObjects = new LinkedList<IRelation>();
	//spec.getStaticPaths().getMapping().put("/", spec.getStaticFiles());
//	public PathRelation getDomainPaths() {
//		return domainPaths;
//	}
//
//
//	public void setDomainPaths(PathRelation domainPaths) {
//		this.domainPaths = domainPaths;
//	}
//
//
//	public PathRelation getStaticPaths() {
//		return staticPaths;
//	}
//
//
//	public void setStaticPaths(PathRelation staticPaths) {
//		this.staticPaths = staticPaths;
//	}
//


	public String getRelationPath() {
		return relationPath;
	}


	public void setRelationPath(String relationPath) {
		this.relationPath = relationPath;
	}


	public List<IRelation> getRelationObjects() {
		return relationObjects;
	}


	public void setRelationObjects(List<IRelation> relationObjects) {
		this.relationObjects = relationObjects;
	}


	public List<IRelationSpec> getRelations() {
		return relations;
	}


	public void setRelations(List<IRelationSpec> relations) {
		this.relations = relations;
	}


	public String getDefaultTemplate() {
		return defaultTemplate;
	}
	

	public String getPluginDir() {
		return pluginDir;
	}

	public void setPluginDir(String pluginDir) {
		this.pluginDir = pluginDir;
	}

	public void setDefaultTemplate(String defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}
	
}
