package com.daghosoft.dent;

public interface RenamerService {

	String rename(String name,Boolean isFile);
	
	Boolean fileNameNeedRename(String filename);

}
