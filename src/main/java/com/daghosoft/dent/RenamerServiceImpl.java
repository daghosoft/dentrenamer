package com.daghosoft.dent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenamerServiceImpl implements RenamerService {

	private static Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
	
	private static List<String> blackList= new ArrayList<String>();
	private static List<String> wordSeparatorlist= new ArrayList<String>();
	private int yearLimit = 1900;
	private boolean yearFound =false;
	
	//Costruttore di test
	protected RenamerServiceImpl(String pBlackList,String pWordSeparator,String pYearLimit) {
		String [] arr = pBlackList.toLowerCase().split(";");
		blackList = Arrays.asList(arr);
		
		String [] ws = pWordSeparator.split(";");
		wordSeparatorlist = Arrays.asList(ws);
		
		try {
			yearLimit = Integer.valueOf(pYearLimit);
		} catch (Exception e) {
		}
		
	}
	
	public RenamerServiceImpl(ConfigService configService) {
		
		String pBlackList = configService.getBlackList();
		String pWordSeparator =  configService.getWordSeparator();
		
		String [] arr = pBlackList.toLowerCase().split(";");
		blackList = Arrays.asList(arr);
		
		String [] ws = pWordSeparator.split(";");
		wordSeparatorlist = Arrays.asList(ws);
		
		try {
			yearLimit = Integer.valueOf(configService.getYearLimit());
		} catch (Exception e) {
		}
		
	}
	
	public String renameFile(final String fileName) {
		yearFound = false;
		String baseName = FilenameUtils.getBaseName(fileName).toLowerCase();
		String ext = FilenameUtils.getExtension(fileName);
		
		baseName = removeWordSeparator(baseName);
		
		StringBuilder baseNameBuilder = new StringBuilder();
		String[] fileNameArr = baseName.split(" ");
		
		for(int x=0;x<fileNameArr.length;x++){
			String tmp = fileNameArr[x];
			
			tmp = yearBuilder(tmp);
			tmp = blackListFilter(tmp);
			
			baseNameBuilder.append(tmp).append(" ");
		}
		
		String out = baseNameBuilder.toString().trim()+"."+ext.toLowerCase();
		out = WordUtils.capitalizeFully(out);
		out = StringUtils.normalizeSpace(out);
		return out;
	}
	
	public String renameFolder(String folderName) {
		yearFound = false;
		String baseName = folderName.toLowerCase();
		
		baseName = removeWordSeparator(baseName);
		
		StringBuilder baseNameBuilder = new StringBuilder();
		String[] fileNameArr = baseName.split(" ");
		
		for(int x=0;x<fileNameArr.length;x++){
			String tmp = fileNameArr[x];
			
			tmp = yearBuilder(tmp);
			tmp = blackListFilter(tmp);
			
			baseNameBuilder.append(tmp).append(" ");
		}
		
		String out = baseNameBuilder.toString().trim();
		out = WordUtils.capitalizeFully(out);
		out = StringUtils.normalizeSpace(out);
		return out;
	}
	
	
	
	protected String removeWordSeparator(String w){
		String out = w;
		for(String s : wordSeparatorlist){
			out = out.replace(s, " ");
		}
		return out;
	}
	
	protected String yearBuilder(String w){
		String out = w;
		try {
			int year = Integer.valueOf(w);
			if(year>=yearLimit){
				out = "("+w+")";
				yearFound = true;
			}
		} catch (NumberFormatException e) {
		}
		
		return out;
	}
	
	protected String blackListFilter(String w){
		String out = w;
		
		if(blackList.contains(w.toLowerCase())){
			out=StringUtils.EMPTY;
		}
		
		return out;
	}
	
	public Boolean fileNameNeedRename(String filename){
		Boolean out = false;
		
		for(String blackWord : wordSeparatorlist){
			if(filename.contains(blackWord)){
				return true;
			}
		}
		
		for(String blackWord : blackList){
			if(filename.contains(blackWord)){
				return true;
			}
		}
		
		return out;
	}

	

}
