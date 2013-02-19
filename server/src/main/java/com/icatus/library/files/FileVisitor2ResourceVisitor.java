package com.icatus.library.files;

import static com.icatus.library.Resource.res;
import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import com.icatus.library.IResourceVisitor;
import com.icatus.library.Resource;
import com.icatus.library.ResourceCollection;

public class FileVisitor2ResourceVisitor extends SimpleFileVisitor<Path>{

	private String uriPrefix; 
	private String directory;
	private IResourceVisitor resourceVisitor;
	private String staticExtension[] = new String[]{
		".js", ".css",".html",".htm",".tmpl"	
	};
	public FileVisitor2ResourceVisitor(String uriPrefix, 
									   String directory,
									   IResourceVisitor resourceVisitor){
		this.uriPrefix = uriPrefix;
		this.directory = directory;
		this.resourceVisitor = resourceVisitor;
		
	}
	
	private String path2URI(Path dir){
		return dir.toString().replaceFirst(directory, uriPrefix);
	}
	
	public FileVisitResult preVisitDirectory(Path dir,
            BasicFileAttributes attrs)
              throws IOException
	  {
//		System.out.println("\t\tDIRECTORY ENTER " + dir.toString());
		String uri = path2URI(dir);
		ResourceCollection.res(uri);

		return CONTINUE;
	  }
    // Print information about
    // each type of file.

	private ResourceCollection parent(Resource uri){
		int last = uri.uri().lastIndexOf("/");
		//this line could cause a conflict if happens before ResourceCollection.res
		//maybe that method should overwrite when regular resource found
		Resource parent = res(uri.uri().substring(0, last));
		if(parent instanceof ResourceCollection){
			return (ResourceCollection) parent;
		}
		return null;
	}
	
	@Override
    public FileVisitResult visitFile(Path file,
                                   BasicFileAttributes attr) {
        if (attr.isSymbolicLink() || attr.isRegularFile()) {
        	String fn = file.getFileName().toString();
        	
            if(fn.endsWith(".json")){
            	//System.out.println("ALL GOOD Checking " + file.getFileName());
            	//System.out.println("Lives at " + file.getParent());
            	Resource mr = res(path2URI(file.getParent()) + "/"+fn.replace(".json", ""));
            	mr.setBytes(attr.size());
            	mr.setExplicitRepresentation(true);
//            	System.out.println(" Visiting Domain @ " + mr.uri());
//            	System.out.println("++++++++++++++++++Directory : " + file.getParent().toString());
//            	System.out.println("++++++++++++++++++Filename : " + fn);
//            	System.out.println("++++++++++++++++++URI : " + mr.getUri());
            	
            	try {
					resourceVisitor.visit(mr);
				} catch (Exception e) {
					e.printStackTrace();
				}
            	ResourceCollection oneUp = parent(mr);
//            	System.out.println("Parent Collection at " + oneUp);
            	if(oneUp!=null){
            		oneUp.getContains().add(mr.uri());	
            	}
            	
            	//System.out.println("Is this it ? " + file.getParent());
            }else if(validStatic(fn)){
            	Resource smr = res(path2URI(file.getParent()) + "/"+fn);
//            	System.out.println(" Visiting Static @ " + smr.uri());
            	smr.setStaticRepresentation(true);
            	smr.setBytes(attr.size());
            	ResourceCollection oneUp = parent(smr);
//            	System.out.println("Parent of Static Collection at " + oneUp);
            	if(oneUp!=null){
            		oneUp.getContains().add(smr.uri());	
            	}
            }else{
            	//System.out.println("NO GOOD Checking " + file.getFileName());
            }
        } else{
            System.out.format("Regular file: %s ", file);
        } 
        return CONTINUE;
    }

    private boolean validStatic(String fn){
    	for(String ext : staticExtension){
    		if(fn.endsWith(ext)) return true;
    	}
    	return false;
    }
    
    
    public FileVisitResult postVisitDirectory(Path dir,
                                          IOException exc) {
      //  System.out.format("Directory: %s%n", dir);
    	try {
//    		System.out.println("\t\tDIRECTORY EXIT*!*!*!*!*!*!*!*!** Visiting " + dir.toString());
//    		Resource oneUp = res(path2URI(dir));
//    		if(oneUp!=null && oneUp instanceof ResourceCollection){
//    			System.out.println("Found this collection : " + oneUp);
//    			//resourceVisitor.visit(lastCollection);
//    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        return CONTINUE;
    }

    // If there is some error accessing
    // the file, let the user know.
    // If you don't override this method
    // and an error occurs, an IOException 
    // is thrown.
    @Override
    public FileVisitResult visitFileFailed(Path file,
                                       IOException exc) {
        System.err.println(exc);
        return CONTINUE;
    }	
	
}
