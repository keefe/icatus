package com.icatus.library;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.icatus.library.files.FileVisitor2ResourceVisitor;

public class Librarian {
	
	private Library library;
	
	public Librarian(Library library){
		this.library = library;
	}
	//this type of librarian is thorough but stupid
	//just looks through everything
	//maybe relations have different indexing systems based on what kind of Librarian they get
	public void peruseTheStacks(IResourceVisitor resourceVisitor)
	{
		List<String> shelves = library.getPrefixes();
		for(String prefix : shelves){
			String dir = library.getDirectory(prefix);
			System.out.println("Prefix : " + prefix);
			System.out.println("Directory " + dir);
			FileVisitor2ResourceVisitor fileVisitor 
				= new FileVisitor2ResourceVisitor(prefix, dir, resourceVisitor);
	        Path whereGo = FileSystems.getDefault().getPath(dir);
			try {
				Files.walkFileTree(whereGo, fileVisitor);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	


}
