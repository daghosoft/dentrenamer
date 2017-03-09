package com.daghosoft.dent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigServiceImpl implements ConfigService {
	
	private static Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
	
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
				execPath = FilenameUtils.getFullPathNoEndSeparator(fconfig.getAbsolutePath());
				LOGGER.debug("execPath ######################## [{}]",execPath);
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
		return new File(execPath+File.separatorChar+"dent-renamer-report.csv");
	}

	@Override
	public List<String> getConcatWords() {
		Validate.notNull(execPath);
		
		File words =  new File(execPath+File.separatorChar+"concatWords.properties");
		
		LOGGER.debug("################### search for : {}",words.getAbsolutePath());
		List<String>  out = new ArrayList<String>();
		if(words.exists()){
			try {
				out = FileUtils.readLines(words, "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return out;
	}
	
	
}
