package com.daghosoft.dent;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
	private static RenamerServiceImpl renamerService;
	
	 
	 public static void main(String[] args) {
		 LOGGER.info("Start Dent Renamer Execution");
		 
		 ConfigService config = new ConfigServiceImpl();
		 renamerService = new RenamerServiceImpl(config);
		 FileService fileService = new FileServiceImpl(config.getBasePath(),renamerService);
		 
		 Collection<File> fileList = fileService.getFilesInBasePath();
		 renameFileProcess(fileList);
		 
		 Collection<File> folderList = fileService.getFolderInBasePath();
		 renameFolderProcess(folderList);
		 
		 LOGGER.info("End Dent Renamer Execution");
	 }
	 
	 private static void renameFileProcess(Collection<File> itemList){
		 for(File f : itemList){
			 String containingPath= FilenameUtils.getFullPath(f.getAbsolutePath());
			 String name = f.getName();
			 String targetName = renamerService.renameFile(name);
			 char separator = File.separatorChar;
			
			 try {
				 File targetFile = new File(containingPath+separator+targetName);
				 if(!targetFile.exists()){
					 LOGGER.info(" File Original Name : [{}] Target Name : [{}]",name,targetName);
					 if(!f.isDirectory()){
						 FileUtils.moveFile(f, targetFile);
					 }
				 }
			} catch (IOException e) {
				LOGGER.error("",e);
			}
		 }
	 }
	 
	 private static void renameFolderProcess(Collection<File> itemList){
		 for(File f : itemList){
			 String containingPath= FilenameUtils.getFullPath(f.getAbsolutePath());
			 String name = f.getName();
			 String targetName = renamerService.renameFolder(name);
			 char separator = File.separatorChar;
			
			 try {
				 File targetFolder = new File(containingPath+separator+targetName);
				 if(!targetFolder.exists()){
					 LOGGER.info(" Folder Original Name : [{}] Target Name : [{}]",name,targetName);
					 if(f.isDirectory()){
						 FileUtils.moveDirectory(f, targetFolder); 
					 }
				 }
			} catch (IOException e) {
				LOGGER.error("",e);
			}
		 }
	 }
}
