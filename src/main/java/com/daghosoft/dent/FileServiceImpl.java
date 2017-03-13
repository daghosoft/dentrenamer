package com.daghosoft.dent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileServiceImpl implements FileService{

	private static Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
	
	private String basePath;
	private RenamerService renamerService;

	public FileServiceImpl(String pBasePath,RenamerService pRenamerService) {
		basePath = pBasePath;
		LOGGER.info("BasePath is : [{}]",basePath);
		renamerService = pRenamerService;
	}
	
	public Collection<File> getFilesInBasePath() {
		Validate.notNull(basePath,"Il BasePath e nullo o vuoto");
		Validate.notEmpty(basePath,"Il BasePath e nullo o vuoto");
		
		File folder = new File(basePath);
		Validate.isTrue(folder.exists(),"Il path fornito non esiste : "+basePath);
		Validate.isTrue(folder.isDirectory(),"Il path fornito non e una directory: "+basePath);
		
		Collection<File> out = new ArrayList<File>();
		
		// Lista solo dei file compresi nel basepath
		Collection<File> list = FileUtils.listFiles(folder, TrueFileFilter.TRUE, DirectoryFileFilter.INSTANCE);
		
		
		for(File f : list){
			if(!f.isDirectory()){
				if(renamerService.fileNameNeedRename(f.getName())){
					out.add(f);	
					LOGGER.debug("File Need Rename : [{}]",f.getName());
				}
			}else{
				LOGGER.info("Filtro file sbagliato individuata cartella [{}]",f.getName());
			}
		}
		
		return out;
	}

	public Collection<File> getFolderInBasePath() {
		
		Validate.notNull(basePath,"Il BasePath e nullo o vuoto");
		Validate.notEmpty(basePath,"Il BasePath e nullo o vuoto");
		
		File folder = new File(basePath);
		Validate.isTrue(folder.exists(),"Il path fornito non esiste : "+basePath);
		Validate.isTrue(folder.isDirectory(),"Il path fornito non e una directory: "+basePath);

		Collection<File> out = new ArrayList<File>();
		
		// Lista solo delle cartelle
		Collection<File> list = FileUtils.listFilesAndDirs(folder, new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY);
		
		for(File f : list){
			if(f.isDirectory() && f!=folder){
				if(renamerService.fileNameNeedRename(f.getName())){
					out.add(f);	
					LOGGER.debug("Folder Need Rename : [{}]",f.getName());
				}
			}else{
				if(!f.isDirectory()){
					LOGGER.info("Filtro cartelle sbagliato individuato file [{}]",f.getAbsolutePath());
				}
				
			}
		}
		
		return out;
	}

}
