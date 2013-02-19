package com.icatus.server;

public class IcatusResponse {
	private String type, val;
	
	public IcatusResponse(String response, String type){
		this.type = type;
		this.val = response;
	}
	
	public String contentType(){
		return type;
	}
	public String response(){
		return val;
	}
}
