package com.icatus.server;


public interface IcatusAPI {
	public boolean initialize(ServerSpec spec);
	
	public IcatusResponse handle(IcatusRequest request);
}
