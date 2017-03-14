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
	private static List<String> concatWordslist = new ArrayList<String>();
	private int yearLimit = 1900;
	
	//Costruttore di test
	protected RenamerServiceImpl(String pBlackList,String pWordSeparator,String pYearLimit) {
		super();
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
		
		this(configService.getBlackList(),configService.getWordSeparator(),configService.getYearLimit());
		concatWordslist = configService.getConcatWords();
	}
	
	public String rename(final String name,Boolean isFile) {
		String baseName = name.toLowerCase();
		String ext=StringUtils.EMPTY;
		
		if(isFile){
			baseName = FilenameUtils.getBaseName(name).toLowerCase();
			ext = "."+FilenameUtils.getExtension(name).toLowerCase();
		}
		LOGGER.debug("!!!!!!!!!!!!!!!!!!!!!!! 0 : [{}]",baseName);
		baseName = concatWordsFilter(baseName);
		LOGGER.debug("1 : [{}]",baseName);
		baseName = removeWordSeparator(baseName);
		LOGGER.debug("2 : [{}]",baseName);
		
		StringBuilder baseNameBuilder = new StringBuilder();
		String[] fileNameArr = baseName.split(" ");
		boolean containYear = containYear(fileNameArr);
		boolean yearFound = false;
		
		for(int x=0;x<fileNameArr.length;x++){
			String tmp = fileNameArr[x];
			
			tmp = yearBuilder(tmp);
			
			if(tmp.contains("-")){
				yearFound=true;
			}
			
			if(containYear && yearFound){
				tmp = blackListFilter(tmp);
			}else if(!containYear){
				tmp = blackListFilter(tmp);
			}
			
			
			baseNameBuilder.append(tmp).append(" ");
		}
		
		String out = baseNameBuilder.toString().trim()+ext;
		LOGGER.debug("3 : [{}]",out);
		out = WordUtils.capitalizeFully(out);
		LOGGER.debug("4 : [{}]",out);
		out = StringUtils.normalizeSpace(out);
		LOGGER.debug("5 : [{}]",out);
		//Replace dei doppi -
		out = removeMultipleHyphen(out);
		LOGGER.debug("6 : [{}]",out);
		//Nel caso l'anno sia subito prima dell'estenzione cancello il separatore -
		out = normalizeYearSeparator(out);
		LOGGER.debug("7 : [{}]",out);
		
		return out;
	}
	
	protected String normalizeYearSeparator(String w){
		String out = w.replace(" -.", ".").trim();
		if(out.endsWith("-")){
			out = out.substring(0, out.lastIndexOf("-")).trim();
		}
		
		return out;
	}
	
	protected String removeMultipleHyphen(String w){
		String out = w.trim();
		out = StringUtils.normalizeSpace(out);
		if(out.contains("- -")){
			out = out.replace("- -", "-");
			if(out.contains("- -")){
				out = removeMultipleHyphen(out);
			}
		}
		
		return out;
	}
	
	
	protected Boolean containYear(String[] fileNameArr){
		
		if(fileNameArr==null || fileNameArr.length==0 ){
			return false;
		}
		
		for(int x=0;x<fileNameArr.length;x++){
			try {
				Integer year = Integer.valueOf(fileNameArr[x]);
				if(year>=yearLimit){
					return true;
				}
				
			} catch (NumberFormatException e) {
			}
		}
		return false;
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
				out = "("+w+") - ";
			}
		} catch (NumberFormatException e) {
		}
		
		return out;
	}
	
	protected String concatWordsFilter(String fileName){
		String out = fileName.toLowerCase();
		for(String s : concatWordslist){
			s = s.toLowerCase();
			if(out.contains(s)){
				out = out.replace(s, "");	
				LOGGER.debug("###### String found : {}",s);
			}
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
	//FIXME da sistemare
	public Boolean fileNameNeedRename(String filename){
		Boolean out = true;
		
//		for(String blackWord : wordSeparatorlist){
//			if(filename.contains(blackWord)){
//				return true;
//			}
//		}
//		
//		for(String blackWord : blackList){
//			if(filename.contains(blackWord)){
//				return true;
//			}
//		}
		
		return out;
	}
	

}
