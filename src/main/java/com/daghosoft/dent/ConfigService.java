package com.daghosoft.dent;

import java.io.File;
import java.util.List;
import java.util.Properties;

public interface ConfigService {
	
	Properties getPropertySet();
	
	String getBlackList();
	
	String getWordSeparator();
	
	String getBasePath();
	
	String getExclusionPath();
	
	String getYearLimit();
	
	File getReportFile();
	
	List<String> getConcatWords();
	
}
