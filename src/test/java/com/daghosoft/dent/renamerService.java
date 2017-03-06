package com.daghosoft.dent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.junit.Before;
import org.junit.Test;

public class renamerService {
	
	private static final String BLACKLISTPARAM="iTALiAN;BDRip;XviD;TRL;MT;dvdRip;sub;ita";
	private static final String WORDSEPARATOR = "-;+;_;.;[;]";
	
	private static List<String> BLACKLIST= new ArrayList<String>();
	private static List<String> WORDSEPARATORLIST= new ArrayList<String>();
	
	@Before
	public void init(){
		String [] arr = BLACKLISTPARAM.toLowerCase().split(";");
		BLACKLIST = Arrays.asList(arr);
		
		String [] ws = WORDSEPARATOR.split(";");
		WORDSEPARATORLIST = Arrays.asList(ws);
	}
	
	@Test
	public void renamerTest(){
		String filename = "Guardiani.Della.Galassia.(2014)..iTALiAN.BDRip.XviD-TRL[MT]_DVDrip+sub.ita.avi";
		
		String baseName = FilenameUtils.getBaseName(filename).toLowerCase();
		String ext = FilenameUtils.getExtension(filename);
		
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
		
		System.out.println(out);
		
	}

	//baseName = WordUtils.capitalizeFully(baseName);
	
	
	private String removeWordSeparator(String w){
		String out = w;
		for(String s : WORDSEPARATORLIST){
			
			out = out.replace(s, " ");
			System.out.println(out + " --- " + s);
			
		}
		return out;
	}
	
	private String yearBuilder(String w){
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
	
	private String blackListFilter(String w){
		String out = w;
		
		if(BLACKLIST.contains(w)){
			out=StringUtils.EMPTY;
		}
		
		return out;
	}
}
