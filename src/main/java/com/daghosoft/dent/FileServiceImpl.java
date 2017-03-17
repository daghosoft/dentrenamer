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
import org.apache.commons.io.filefilter.IOFileFilter;
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
		Collection<File> list = FileUtils.listFilesAndDirs(folder, folderFilter(), DirectoryFileFilter.DIRECTORY);
		
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
	
	
	private IOFileFilter folderFilter(){
		
		
		
		IOFileFilter folderFilter = new IOFileFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if(dir.isDirectory() && isValid(dir.getAbsolutePath())){
					System.out.println("-------------/\\//---------"+dir.getAbsolutePath());
					return true;
				}
				return false;
			}
			
			@Override
			public boolean accept(File file) {
				if(file.isDirectory()&& isValid(file.getAbsolutePath())){
					System.out.println("-------------/\\//---------"+file.getAbsolutePath());
					return true;
				}
				return false;
			}
		};
		return folderFilter;
	}
	
	protected Boolean isValid(String path){
		List<String> filter = Arrays.asList("@ear;#recycle".split(";"));
		for(String s : filter){
			if(path.contains(s)){
				return false;
			}
		}
		return true;
	}

}
