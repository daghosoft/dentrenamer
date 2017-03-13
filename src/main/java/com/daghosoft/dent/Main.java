package com.daghosoft.dent;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
	private static RenamerServiceImpl renamerService;
	private static ConfigService config;
	
	private static boolean execute = false;
	private static File report;
	private static final char separator = File.separatorChar;
	
	 
	 public static void main(String[] args) {
		 if(args!=null && args.length>0 && StringUtils.isNotBlank(args[0])){
			 String executeParam = args[0];
			 if(executeParam.equals("exec")){
				 execute = true;
			 }
		 }
		 LOGGER.info("Start Dent Renamer Execution");
		
		 
		 config = new ConfigServiceImpl();
		 renamerService = new RenamerServiceImpl(config);
		 FileService fileService = new FileServiceImpl(config.getBasePath(),renamerService);
		 
		 initReport();
		 
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
			 String targetName = renamerService.rename(name,true);
			 writeLineInReport(name, targetName);
			 try {
				 File targetFile = new File(containingPath+separator+targetName);
				 if(!targetFile.exists()){
					 LOGGER.debug(" File Original Name : [{}] Target Name : [{}]",name,targetName);
					 if(!f.isDirectory() && execute){
						 FileUtils.moveFile(f, targetFile);
					 }
				 }
			} catch (IOException e) {
				LOGGER.error(StringUtils.EMPTY,e);
			}
		 }
	 }
	 
	 private static void renameFolderProcess(Collection<File> itemList){
		 for(File f : itemList){
			 String containingPath= FilenameUtils.getFullPath(f.getAbsolutePath());
			 String name = f.getName();
			 String targetName = renamerService.rename(name,false);
			 try {
				 File targetFolder = new File(containingPath+separator+targetName);
				 writeLineInReport(name, targetName);
				 LOGGER.debug(" Folder Original Name : [{}] Target Name : [{}]",name,targetName);
				 if(!targetFolder.exists()){
					 LOGGER.info(" Folder Original Name : [{}] Target Name : [{}]",name,targetName);
					 if(f.isDirectory() && execute){
						 FileUtils.moveDirectory(f, targetFolder); 
					 }
				 }
			} catch (IOException e) {
				LOGGER.error(StringUtils.EMPTY,e);
			}
		 }
	 }
	 
	 protected static void initReport(){
		 report = config.getReportFile();

		 Validate.notNull(report,"Il File di report risulta nullo impossibile procedere");
		 LOGGER.info("Init report file @ [{}]",report.getAbsoluteFile());
		 
		 if(report.exists()){
			 report.delete();
		 }
		 try {
			StringBuilder headerBuilder = new StringBuilder("Dent renamer eseguito in modalita� report @ ")
			 									.append(new Date().toString())
			 									.append(" - Per eseguire il rename dei file utilizzare parametro [exec]")
			 									.append("\n");
			FileUtils.write(report, headerBuilder.toString(), "UTF-8", true);
			FileUtils.write(report, " \n", "UTF-8", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	 
	 private static void writeLineInReport(String fileName,String targetName){
		 try {
			FileUtils.write(report, fileName + " ; "+targetName+"\n", "UTF-8", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
}
