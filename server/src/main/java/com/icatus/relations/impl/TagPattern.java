package com.icatus.relations.impl;

import com.icatus.relations.IURIConfiguration;

public class TagPattern implements IURIConfiguration {
	String URI;
	String goodTags[];
	String badTags[];
	public String getURI() {
		return URI;
	}
	public void setURI(String uRI) {
		URI = uRI;
	}
	public String[] getGoodTags() {
		return goodTags;
	}
	public void setGoodTags(String[] goodTags) {
		this.goodTags = goodTags;
	}
	public String[] getBadTags() {
		return badTags;
	}
	public void setBadTags(String[] badTags) {
		this.badTags = badTags;
	}
	
}
