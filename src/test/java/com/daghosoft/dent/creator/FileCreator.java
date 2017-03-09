package com.daghosoft.dent.creator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

public class FileCreator {
	private static final String BASEPATH = "C:\\temp\\zzzdentRenamer";
	
	char separator = File.separatorChar;
	
	@Test 
	@Ignore
	public void BasePathCreator() throws IOException{
		File basePath = new File(BASEPATH);
		if(basePath.exists()){
			return;
		}
		
		FileUtils.forceMkdir(basePath);
		
		
	}
	
	@Test
	@Ignore
	public void generatefakeListFiles() throws IOException{
		String lines[] = readFileNamePackage("/fakeFileList.txt").split("\\r?\\n");
		
		for(int x=0;x<lines.length;x++){
			if(StringUtils.isNotBlank(lines[x])){
				String filename = BASEPATH+separator+lines[x]+".dvx";
				System.out.println("Generating file : "+filename);
				FileUtils.writeStringToFile(new File(filename), UUID.randomUUID().toString(), "UTF-8");
			}
		}
	}
	
	@Test
	@Ignore
	public void generateMyMovieListFiles() throws IOException{
	
		String lines[] = readFileNamePackage("/MyMovieList.txt").split("\\r?\\n");
		
		for(int x=0;x<lines.length;x++){
			if(StringUtils.isNotBlank(lines[x])){
				String filename = BASEPATH+separator+lines[x];
				System.out.println("Generating file : "+filename);
				
				Collection<String> ext = new ArrayList<String>();
				ext.add(".dvx");
				ext.add(".mp4");
				ext.add(".sub");
				ext.add(".avi");
				ext.add(".srt");
				ext.add(".mkv");
				ext.add(".txt");
				ext.add(".idx");
				ext.add(".rar");
				ext.add(".jpg");
				ext.add(".jpeg");
				ext.add(".png");
				ext.add(".m4v ");
				
				
				boolean isFile = false;
				for(String s : ext){
					if(filename.toLowerCase().contains(s)){
						isFile=true;
					}
				}
				
				if(isFile){
					FileUtils.writeStringToFile(new File(filename), UUID.randomUUID().toString(), "UTF-8");
				}else{
					FileUtils.forceMkdir(new File(filename));
				}
				
			}
		}
	}
	
	
	protected String readFileNamePackage(String fileName) {
		InputStream stream = this.getClass().getResourceAsStream(fileName);
		
		String out = StringUtils.EMPTY;
		if (stream != null) {
			try {
				out = IOUtils.toString(stream, "UTF-8");
			} catch (IOException e) {
			} finally {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
		return out;
	}

}
