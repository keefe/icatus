package com.icatus.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.security.Constraint;

import com.icatus.library.IcatusLibrary;
import com.icatus.relations.impl.TagRelation;
import com.icatus.relations.impl.TripleRelation;
import com.icatus.server.handler.IcatusHandler;

public class Icatus {

	private ServerSpec spec; 
	
	public Icatus(ServerSpec spec){
		this.spec = spec;
	}
	
	public static void main(String[] args) throws Exception {
        ServerSpec spec = null;
        
        spec = specFromArguments();
        TagRelation tags = new TagRelation();
        TripleRelation trip = new TripleRelation();
        spec.getRelationObjects().add(trip);
        spec.getRelationObjects().add(tags);

	        HandlerList handlers = new HandlerList();
        
			IcatusAPI icatus = new IcatusLibrary();
	        //this should be in the classpath
			icatus.initialize(spec);
        	
        
	      ResourceHandler resource_handler = new ResourceHandler();
	      resource_handler.setDirectoriesListed(true);
	      resource_handler.setResourceBase(spec.getStaticFiles());
	      ContextHandler staticContext = new ContextHandler();
	      //TODO generalize static context to parameter if they want to reclaim
	      staticContext.setContextPath("/");
	      staticContext.setHandler(resource_handler);
	        
	        handlers.setHandlers(new Handler[]{
	        		getContext("/", new IcatusHandler(icatus)),
	        		staticContext
	        });
	        Server server = new Server(spec.getPort());
	        //TODO whatif multiple invocations?
	        //TODO could add another server on another port for just in case commands
	        server.setHandler(handlers);
	        server.start();
	        server.join();
        
	}

	public static ServerSpec specFromArguments() throws IOException {
		ServerSpec spec;
		spec = new ServerSpec();
        String home = System.getProperty("user.home");
        System.out.println("home is " + home); 
        String staticDir = home + File.separator + "projects" + File.separator + "icatus-static";
        staticDir = System.getProperty("icatus.static", staticDir);
        int port = Integer.parseInt(System.getProperty("icatus.port", "8080"));
        System.out.println("Serving Static Files From " + staticDir);
        String pluginPath =System.getProperty("icatus.plugins","/plugins");
        String pluginDir = staticDir + pluginPath;
        System.out.println("Plugins @ " + pluginPath+ " stored under " + pluginDir);
        String rawPath=home+File.separator+"projects"+File.separator+"icatus-seed"+File.separator+"v1";
        rawPath = System.getProperty("icatus.datafiles", rawPath);
        System.out.println("Serving Json Data From " + rawPath);
        //TODO definitely don't want this here, but where?
        String defaultHtm = getDefault(staticDir+"/default");
        spec.setPluginDir(pluginDir);
        spec.setDefaultTemplate(defaultHtm);
        spec.setPort(port);
        spec.setDatafiles(rawPath);
        spec.setStaticFiles(staticDir);
		return spec;
	}
	
	//TODO add basic auth
	private static ConstraintSecurityHandler getConstraintSecurityHandler(String realm){
	    Constraint constraint = new Constraint(Constraint.__BASIC_AUTH, "user");
	    constraint.setRoles(new String[]{"user","admin"});
	    constraint.setAuthenticate(true);

	    ConstraintMapping statsConstraintMapping = new ConstraintMapping();
	    statsConstraintMapping.setConstraint(constraint);
	    statsConstraintMapping.setPathSpec("/resources"); //directory I want to protect

	    ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
	    csh.setAuthenticator(new BasicAuthenticator());
	    csh.setRealmName(realm);
	    csh.setConstraintMappings(new ConstraintMapping[] {statsConstraintMapping});

	    csh.setLoginService(getHashLoginService(realm));

	    return csh;
	}

	private static HashLoginService getHashLoginService(String realm) {
	    HashLoginService loginServ = new HashLoginService();
	    loginServ.setName(realm);
	    loginServ.setConfig("realm.properties"); //location of authentication file
	    loginServ.setRefreshInterval(1);
	    return loginServ;
	}	
	//TODO this probably does not belong here
	private static String getDefault(String fname) throws IOException{
		System.out.println("reading default template from from" + fname);
		BufferedReader reader = new BufferedReader(new FileReader(fname));
		String s = "";
		String l = null;
		while((l=reader.readLine())!=null){
			s+=l+"\n";
		}
		reader.close();
		return s;
	}	
	private static ContextHandler getContext(String loc, Handler handler){
	      ContextHandler context = new ContextHandler();
	      context.setContextPath(loc);
	      context.setHandler(handler);
	      return context;
	}
}
