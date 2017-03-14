package com.daghosoft.dent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
	
	
	//Costruttore di test
	protected ConfigServiceImpl(String pConfigName) {
		super();
		if(!pConfigName.startsWith("/")){
			pConfigName = "/"+pConfigName;
		}
		
		URL config = this.getClass().getResource(pConfigName);
		
		Validate.notNull(config,"Errore recupero file di configurazione config.properties");
		LOGGER.debug("Load config from : [{}]",config.getPath());
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

	public ConfigServiceImpl() {
		this("/config.properties");
	}

	@Override
	public Properties getPropertySet() {
		return properties;
	}
	
	@Override
	public String getBlackList() {
		Validate.notNull(properties,"l'oggetto properties risulta nullo possibile causa errore nel recupero del file di properties");
		return ((String) properties.get(BLACKLIST)).trim();
	}
	
	@Override
	public String getWordSeparator() {
		Validate.notNull(properties,"l'oggetto properties risulta nullo possibile causa errore nel recupero del file di properties");
		return ((String) properties.get(WORDSEPARATOR)).trim();
	}
	
	@Override
	public String getBasePath() {
		Validate.notNull(properties,"l'oggetto properties risulta nullo possibile causa errore nel recupero del file di properties");
		return ((String) properties.get(BASEPATH)).trim();
	}
	
	@Override
	public String getYearLimit() {
		Validate.notNull(properties,"l'oggetto properties risulta nullo possibile causa errore nel recupero del file di properties");
		
		if(properties.containsKey(YEARLIMIT)){
			return ((String) properties.get(YEARLIMIT)).trim();
		}
		
		return StringUtils.EMPTY;
	}
	
	@Override
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
