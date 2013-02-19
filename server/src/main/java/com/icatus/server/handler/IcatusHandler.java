package com.icatus.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.icatus.server.IcatusAPI;
import com.icatus.server.IcatusRequest;
import com.icatus.server.IcatusResponse;

public class IcatusHandler extends AbstractHandler {

	//how are these things attached from a network level?
	//can I put this provideType here or just leave it alone?
	//set it in the session or request somewhere probably this is a horrid idea
	//so just hardcoding it for now
	protected String typesProvided[] = new String[]{"application/json"};
	protected IcatusAPI api;
	public IcatusHandler(IcatusAPI api){
		this.api = api;
	}
	
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if(request.getPathInfo().equals("/favicon.ico")){
			baseRequest.setHandled(true);
			
			return;
		}
		IcatusRequest internal = httpToIcatus(request);
		if(internal.getResponseType()==null){
			if(isFileURI(internal.getUri())) return;
			internal.setResponseType("application/json");
		}
		String tmpl = request.getParameter("tmpl");
		internal.setTemplate(tmpl);
		IcatusResponse icatresponse = api.handle(internal);
		response.setContentType(icatresponse.contentType());
		PrintWriter out = response.getWriter();
		out.println(icatresponse.response());
		out.close();
		baseRequest.setHandled(true);
        response.setStatus(HttpServletResponse.SC_OK);
		
//    	Enumeration<String> headerNames = request.getHeaderNames();
//    	while(headerNames.hasMoreElements()){
//    		String header = headerNames.nextElement();
//    		System.out.println(header + " = " + request.getHeader(header));
//    		
//    	}		
//    	Enumeration<String> headerNames = request.getHeaders("Accept");
//    	System.out.println("Handling " + request.getPathInfo());
//    	System.out.println("just one " + request.getHeader("Accept"));
    	
	}
	
	private static String endings[] = new String[]{
		".html",".css",".js",".htm",".tmpl"
	};
	
	protected boolean isFileURI(String uri){
		if(uri.startsWith("/_/") || uri.startsWith("/~/")) return false;
		for(String end : endings)
			if(uri.endsWith(end)) return true;
		return false;
	}

	//this has gotten all jankty and seemingly pointless
	protected IcatusRequest httpToIcatus(HttpServletRequest request){
		//if(typesProvided==null) return null;
    	//handling */*? q= nonsense?
		String accepts = request.getHeader("Accept");
		IcatusRequest internal = null;
		internal = new IcatusRequest();
		internal.setUri(request.getPathInfo());
		internal.setMethod(request.getMethod());
		if(accepts!=null)
		for(String t : typesProvided){
			if(accepts.contains(t)){
				internal.setResponseType(t);
				break;
			}
		}
		
		if("PUT".equals(internal.getMethod())){
			String body = "";
			try {
				BufferedReader reader = request.getReader();
				String l = null;
				while((l=reader.readLine())!=null) body+=l;
				System.out.println("PUT BODY \n" + body);
				internal.setBody(body);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return internal;
	}
}
