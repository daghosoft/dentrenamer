package com.daghosoft.dent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

public class ConfigServiceImpl implements ConfigService {
	
	private static final String WORDSEPARATOR = "word.separator";
	private static final String BLACKLIST = "word.blacklist";
	private static final String YEARLIMIT = "word.year.limit";
	private static final String BASEPATH = "file.basePath";
	private static String execPath;
	
	private Properties properties;
	
	public ConfigServiceImpl() {
		URL config = this.getClass().getResource("/config.properties");
		
		Validate.notNull(config,"Errore recupero file di configurazione config.properties");
		
			try {
				File fconfig = new File(config.getPath());
				execPath = FilenameUtils.getFullPathNoEndSeparator(config.getPath());
				FileInputStream fis = new FileInputStream(fconfig);
				properties = new Properties();
				properties.load(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public Properties getPropertySet() {
		return properties;
	}

	public String getBlackList() {
		Validate.notNull(properties,"l'oggetto properties risulta nullo possibile causa errore nel recupero del file di properties");
		return ((String) properties.get(BLACKLIST)).trim();
	}

	public String getWordSeparator() {
		Validate.notNull(properties,"l'oggetto properties risulta nullo possibile causa errore nel recupero del file di properties");
		return ((String) properties.get(WORDSEPARATOR)).trim();
	}

	public String getBasePath() {
		Validate.notNull(properties,"l'oggetto properties risulta nullo possibile causa errore nel recupero del file di properties");
		return ((String) properties.get(BASEPATH)).trim();
	}
	
	public String getYearLimit() {
		Validate.notNull(properties,"l'oggetto properties risulta nullo possibile causa errore nel recupero del file di properties");
		
		if(properties.containsKey(YEARLIMIT)){
			return ((String) properties.get(YEARLIMIT)).trim();
		}
		
		return StringUtils.EMPTY;
	}

	public File getReportFile() {
		Validate.notNull(execPath);
		return new File(execPath+File.separatorChar+"dent-renamer-report.log");
	}
	
	
}
