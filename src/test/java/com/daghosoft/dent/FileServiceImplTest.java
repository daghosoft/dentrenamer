package com.daghosoft.dent;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FileServiceImplTest {
	
	private ConfigService config;
	private FileServiceImpl sut;
	private RenamerService renamerService;
	
	
	
	@Before
	public void init() throws IOException{
		config =  new ConfigServiceImpl("configTest.properties");
		renamerService = new RenamerServiceImpl(config);
		preFolders();
	}
	
	
	@Test
	public void getFilesInBasePathTest(){
		sut = new FileServiceImpl(config.getBasePath(),renamerService);
		Collection<File> out = sut.getFilesInBasePath();
		for(File f : out){
			assertThat(f.isDirectory()).isFalse();
			System.out.println("getFilesInBasePathTest "+f.getAbsolutePath());
		}
	}
	
	@Test
	public void getFolderInBasePathTest(){
		File basePath = new File(config.getBasePath());
		sut = new FileServiceImpl(config.getBasePath(),renamerService);
		Collection<File> out = sut.getFolderInBasePath();
		for(File f : out){
			assertThat(f.isDirectory()).isTrue();
			assertThat(f).isNotEqualTo(basePath);
			System.out.println("getFolderInBasePathTest "+f.getAbsolutePath());
		}
	}
	
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void exceptionNotExistTest(){
		sut = new FileServiceImpl("xxx",renamerService);
		sut.getFilesInBasePath();
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void exceptionEmptyTest(){
		sut = new FileServiceImpl("",renamerService);
		sut.getFilesInBasePath();
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void exceptionNullTest(){
		sut = new FileServiceImpl(null,renamerService);
		sut.getFilesInBasePath();
	}
	
	private void preFolders() throws IOException{
		
		File basePath = new File(config.getBasePath());
		
		if(basePath.exists()){
			basePath.delete();
		}
		
		FileUtils.forceMkdir(basePath);
		FileUtils.forceMkdir(new File(config.getBasePath()+File.separatorChar+"fakeFoldersub"));
		FileUtils.forceMkdir(new File(config.getBasePath()+File.separatorChar+"fakeFoldersub1"));
		FileUtils.forceMkdir(new File(config.getBasePath()+File.separatorChar+"fakeFoldersub2"));
		
		new File(config.getBasePath()+File.separatorChar+"test.txt").createNewFile();
		new File(config.getBasePath()+File.separatorChar+"test1.txt").createNewFile();
		new File(config.getBasePath()+File.separatorChar+"test2.txt").createNewFile();
	}
	
	@After
	public void postFolders() throws IOException{
		
		File basePath = new File(config.getBasePath());
		
		if(basePath.exists()){
			FileUtils.deleteDirectory(basePath);
		}
		
	}

}
