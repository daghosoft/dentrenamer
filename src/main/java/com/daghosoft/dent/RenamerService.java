package com.daghosoft.dent;

public interface RenamerService {

	String renameFile(String fileName);
	
	String renameFolder(String folderName);
	
	Boolean fileNameNeedRename(String filename);

}
