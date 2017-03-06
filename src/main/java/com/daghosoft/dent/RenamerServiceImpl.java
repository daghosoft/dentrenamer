package com.daghosoft.dent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

public class RenamerServiceImpl implements RenamerService {

	//private static final String BLACKLISTPARAM="iTALiAN;BDRip;XviD;TRL;MT;dvdRip;sub;ita";
	//private static final String WORDSEPARATORPARAM = "-;+;_;.;[;]";
	
	private static List<String> BLACKLIST= new ArrayList<String>();
	private static List<String> WORDSEPARATORLIST= new ArrayList<String>();
	
	public RenamerServiceImpl(String blackList,String wordSeparators) {
		String [] arr = blackList.toLowerCase().split(";");
		BLACKLIST = Arrays.asList(arr);
		
		String [] ws = wordSeparators.split(";");
		WORDSEPARATORLIST = Arrays.asList(ws);
	}
	
	public String rename(final String fileName) {
		
		String baseName = FilenameUtils.getBaseName(fileName).toLowerCase();
		String ext = FilenameUtils.getExtension(fileName);
		
		baseName = removeWordSeparator(baseName);
		
		StringBuilder baseNameBuilder = new StringBuilder();
		String[] fileNameArr = baseName.split(" ");
		
		for(int x=0;x<fileNameArr.length;x++){
			String tmp = fileNameArr[x];
			
			tmp = blackListFilter(tmp);
			tmp = yearBuilder(tmp);
			
			baseNameBuilder.append(tmp).append(" ");
		}
		
		String out = baseNameBuilder.toString().trim()+"."+ext;
		out = WordUtils.capitalizeFully(out);
		return out;
	}
	
	
	
	protected String removeWordSeparator(String w){
		String out = w;
		for(String s : WORDSEPARATORLIST){
			out = out.replace(s, " ");
		}
		return out;
	}
	
	protected String yearBuilder(String w){
		String out = w;
		try {
			int year = Integer.valueOf(w);
			if(year>2000){
				out = "("+w+")";
			}
		} catch (NumberFormatException e) {
		}
		
		return out;
	}
	
	protected String blackListFilter(String w){
		String out = w;
		
		if(BLACKLIST.contains(w.toLowerCase())){
			out=StringUtils.EMPTY;
		}
		
		return out;
	}

}
