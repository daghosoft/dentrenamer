package com.daghosoft.dent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.DirectoryFileComparator;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileServiceImpl implements FileService{

	private static Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
	
	private String basePath;
	private RenamerService renamerService;
	private String exclusionPath;

	
	protected FileServiceImpl(ConfigService config,RenamerService pRenamerService) {
		basePath = config.getBasePath();
		LOGGER.debug("BasePath is : [{}]",basePath);
		
		exclusionPath = config.getExclusionPath();
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
					if(isValidByExclusionPath(f.getAbsolutePath())){
						out.add(f);	
						LOGGER.debug("File [{}] is valid adding to list.",f.getName());
					}
				}
			}else{
				LOGGER.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Filtro file sbagliato individuata cartella [{}]",f.getName());
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
					if(isValidByExclusionPath(f.getAbsolutePath())){
						out.add(f);	
						LOGGER.debug("Folder [{}] is valid adding to list.",f.getName());
					}
				}
			}else{
				if(!f.isDirectory()){
					LOGGER.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Filtro cartelle sbagliato individuato file [{}]",f.getName());
				}
				
			}
		}
		
		return out;
	}
	
	protected Boolean isValidByExclusionPath(String path){
		
		if(StringUtils.isBlank(exclusionPath)){
			return true;
		}
		
		List<String> filter = Arrays.asList(exclusionPath.split(";"));
		for(String s : filter){
			if(path.contains(s)){
				LOGGER.debug("Path excluded by rule : [{}]",path);
				return false;
			}
		}
		return true;
	}



	/**
	 * A scopo di test
	 */
	protected void setBasePath(String basePath) {
		this.basePath = basePath;
	}



	/**
	 * A scopo di test
	 */
	protected void setExclusionPath(String exclusionPath) {
		this.exclusionPath = exclusionPath;
	}

	
	
}
