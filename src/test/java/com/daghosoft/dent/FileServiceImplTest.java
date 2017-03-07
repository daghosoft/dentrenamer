package com.daghosoft.dent;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;


public class FileServiceImplTest {
	
	private ConfigService config;
	private FileServiceImpl sut;
	private RenamerService renamerService;
	
	
	
	@Before
	public void init(){
		config =  new ConfigServiceImpl();
		renamerService = new RenamerServiceImpl(config);
	}
	
	
	@Test
	public void getFilesInBasePathTest(){
		sut = new FileServiceImpl(config.getBasePath(),renamerService);
		Collection<File> out = sut.getFilesInBasePath();
		for(File f : out){
			assertThat(f.isDirectory()).isFalse();
		}
	}
	
	@Test
	public void getFolderInBasePathTest(){
		sut = new FileServiceImpl(config.getBasePath(),renamerService);
		Collection<File> out = sut.getFolderInBasePath();
		for(File f : out){
			assertThat(f.isDirectory()).isTrue();
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
	
	

}
