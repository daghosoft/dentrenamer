package com.daghosoft.dent;

import java.util.Properties;

public interface ConfigService {
	
	Properties getPropertySet();
	
	String getBlackList();
	
	String getWordSeparator();
	
	String getBasePath();
	
	String getYearLimit();
	
}
