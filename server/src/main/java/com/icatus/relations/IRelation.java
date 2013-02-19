package com.icatus.relations;

import com.icatus.library.Librarian;


public interface IRelation {

	public String getId();
	public String prefix();
	public String getURITemplate();
	public IRelationSpec getSpec();
	
	public boolean initialize(Librarian librarian);
	
	//what about POST/PUT payloads?
	public IURIConfiguration generateConfig(String url);
	
	//this feels like it is coupling HTTP too tight
	//but time is short
	
	//should this be limited to two parameters?
	public String process(IURIConfiguration uriConfig); 
		
}
