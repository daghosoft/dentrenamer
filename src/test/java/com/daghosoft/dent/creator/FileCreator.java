package com.daghosoft.dent.creator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

public class FileCreator {
	private static final String BASEPATH = "C:\\temp\\zzzdentRenamer\\FileCreator";
	
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
	public void generateFiles() throws IOException{
		String separator = File.separator;
		String lines[] = readFileNamePackage().split("\\r?\\n");
		
		for(int x=0;x<lines.length;x++){
			if(StringUtils.isNotBlank(lines[x])){
				String filename = BASEPATH+separator+lines[x]+".dvx";
				System.out.println("Generating file : "+filename);
				FileUtils.writeStringToFile(new File(filename), UUID.randomUUID().toString(), "UTF-8");
			}
		}
	}
	
	
	protected String readFileNamePackage() {
		InputStream stream = this.getClass().getResourceAsStream("/fakeFileList.txt");
		
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
