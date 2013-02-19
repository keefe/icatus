package com.icatus.server.error;

public class Error {

	public static Error NOT_IMPLEMENTED=new Error("unavailable", "This URI represents an unimplemented feature");
	public static Error WRITE_ERROR=new Error("io", "Could not write resource");
	
	private String error;
	private String message;
	
	public Error(String error, String message){
		this.error = error;
		this.message = message;
	}
	public Error(){
	}

	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
