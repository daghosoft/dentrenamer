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
		sut = new FileServiceImpl(config,renamerService);
		preFolders();
	}
	
	
	@Test
	public void getFilesInBasePathTest(){
		Collection<File> out = sut.getFilesInBasePath();
		assertThat(out.size()).isGreaterThan(0);
		for(File f : out){
			assertThat(f.isDirectory()).isFalse();
			assertThat(sut.isValidByExclusionPath(f.getAbsolutePath())).isTrue();
		}
	}
	
	@Test
	public void getFolderInBasePathTest(){
		File basePath = new File(config.getBasePath());
		Collection<File> out = sut.getFolderInBasePath();
		assertThat(out.size()).isGreaterThan(0);
		for(File f : out){
			assertThat(f.isDirectory()).isTrue();
			assertThat(f).isNotEqualTo(basePath);
			assertThat(sut.isValidByExclusionPath(f.getAbsolutePath())).isTrue();
		}
	}
	
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void exceptionNotExistTest(){
		sut = new FileServiceImpl(config,renamerService);
		sut.setBasePath("xxx");
		sut.getFilesInBasePath();
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void exceptionEmptyTest(){
		sut = new FileServiceImpl(config,renamerService);
		sut.setBasePath("");
		sut.getFilesInBasePath();
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void exceptionNullTest(){
		sut = new FileServiceImpl(config,renamerService);
		sut.setBasePath(null);
		sut.getFilesInBasePath();
	}
	
	@Test
	public void isValidByExclusionPathTest(){
		
		File folder1 = new File(config.getBasePath()+File.separatorChar+"@eardir");
		File folder2 = new File(config.getBasePath()+File.separatorChar+"fakeFoldersub2");
		
		Boolean out = sut.isValidByExclusionPath(folder1.getAbsolutePath());
		assertThat(folder1.isDirectory()).isTrue();
		assertThat(out).isFalse();
		
		out = sut.isValidByExclusionPath(folder2.getAbsolutePath());
		assertThat(folder2.isDirectory()).isTrue();
		assertThat(out).isTrue();
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
		FileUtils.forceMkdir(new File(config.getBasePath()+File.separatorChar+"@eardir"));
		FileUtils.forceMkdir(new File(config.getBasePath()+File.separatorChar+"#recycle"));
		
		new File(config.getBasePath()+File.separatorChar+"test.txt").createNewFile();
		new File(config.getBasePath()+File.separatorChar+"test1.txt").createNewFile();
		new File(config.getBasePath()+File.separatorChar+"test2.txt").createNewFile();
		new File(config.getBasePath()+File.separatorChar+"#recycle"+File.separatorChar+"test2.txt").createNewFile();
	}
	
	@After
	public void postFolders() throws IOException{
		
		File basePath = new File(config.getBasePath());
		
		if(basePath.exists()){
			FileUtils.deleteDirectory(basePath);
		}
		
	}

}
