package com.daghosoft.dent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigServiceStatic {

	public static final String CONFIGNAME = "/config.properties";
	private static final String WORDSEPARATOR = "word.separator";
	private static final String YEARLIMIT = "word.year.limit";
	private static final String BASEPATH = "file.basePath";
	private static final String EXCLUSIONPATH = "file.exclusion.path";
	private static final String EXTENSIONDELETE = "file.extension.delete";

	protected static final String RENAME = "file.rename";
	protected static final String MOVE = "file.move.basepath";
	protected static final String DELETEEXT = "file.delete.by.extension";
	protected static final String DELTEEMPTY = "folder.delete.empty";
	protected static final String FOLDERDEBUG = "folder.debug";
	protected static final String EXEC = "exec";

	// path di esecuzione sulla base del config name
	private static String execPath;

	private static Properties properties;
	private static ConfigServiceStatic config;

	private int yearDefault = 1900;

	private Set<String> extensionDelete;
	private Set<String> concatWords;

	@Getter
	private String configPropertiesPath;

	private ConfigServiceStatic(String pConfigName) {

		if (!pConfigName.startsWith("/")) {
			pConfigName = "/" + pConfigName;
		}
		URL config = this.getClass().getResource(pConfigName);

		Validate.notNull(config, "Errore recupero file di configurazione config.properties");
		LOGGER.debug("Load config from : [{}]", config.getPath());
		try {
			File fconfig = new File(config.getPath());
			configPropertiesPath = fconfig.getAbsolutePath();
			execPath = FilenameUtils.getFullPathNoEndSeparator(fconfig.getAbsolutePath());
			LOGGER.debug("execPath ######################## [{}]", execPath);
			FileInputStream fis = new FileInputStream(fconfig);
			properties = new Properties();
			properties.load(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static ConfigServiceStatic getConfig(String pPath) {
		if (config != null) {
			return config;
		}

		config = new ConfigServiceStatic(pPath);
		return config;
	}

	public static ConfigServiceStatic getConfig() {
		if (config != null) {
			return config;
		}

		config = new ConfigServiceStatic(CONFIGNAME);
		return config;
	}

	public String getWordSeparator() {
		return ((String) properties.get(WORDSEPARATOR)).trim();
	}

	public Set<File> getBasePath() {
		Validate.isTrue(properties.containsKey(BASEPATH),
				"Il file di configurazione deve contenre almeno un basepath : " + BASEPATH);

		Set<File> out = new HashSet<>();
		verifyFile(BASEPATH, out);
		for (int x = 10; x > 0; x--) {
			verifyFile(BASEPATH + "." + x, out);
		}
		// TODO da rimuovere
		LOGGER.info("### [{}]", out.size());
		return out;
	}

	private void verifyFile(String path, Set<File> out) {
		if (!properties.containsKey(path)) {
			// TODO da rimuovere
			LOGGER.trace("Not present [{}]", path);
			return;
		}
		String pathString = properties.getProperty(path);
		LOGGER.info("Path String [{}]", pathString);
		File folder = new File(pathString);
		if (folder.exists()) {
			out.add(folder);
		}
	}

	public String getExclusionPath() {
		return ((String) properties.get(EXCLUSIONPATH)).trim();
	}

	public Set<String> getExtensionDelete() {
		if (extensionDelete != null) {
			return extensionDelete;
		}

		if (properties.get(EXTENSIONDELETE) == null) {
			return new HashSet<String>();
		}
		String tmp = ((String) properties.get(EXTENSIONDELETE)).trim();
		if (StringUtils.isBlank(tmp) || !tmp.contains(";")) {
			return new HashSet<String>();
		}

		List<String> list = Arrays.asList(tmp.toLowerCase().split(";"));
		extensionDelete = new HashSet<>();
		extensionDelete.addAll(list);
		return extensionDelete;
	}

	public int getYearLimit() {
		if (properties.containsKey(YEARLIMIT)) {
			String year = properties.getProperty((YEARLIMIT)).trim();
			try {
				return Integer.parseInt(year);
			} catch (Exception e) {
				LOGGER.trace(
						"Il parametro [{}] non contiene un valore valido verrà usato il default. Valore : [{}] default: [{}]",
						YEARLIMIT, year, yearDefault);
				return yearDefault;
			}
		} else {
			return yearDefault;
		}

	}

	public File getReportFile() {
		Validate.notNull(execPath);
		return new File(execPath + File.separatorChar + "dent-renamer-report.csv");
	}

	public Set<String> getConcatWords() {
		Validate.notNull(execPath);
		if (concatWords != null) {
			return concatWords;
		}

		File words = new File(execPath + File.separatorChar + "concatWords.properties");
		LOGGER.debug("################### search for : {} {}", words.getAbsolutePath(), words.exists());
		if (words.exists()) {
			try {
				List<String> listWords = FileUtils.readLines(words, "UTF-8");
				concatWords = listWords.stream().filter(s -> StringUtils.isNotBlank(s)).map(s -> s.toLowerCase().trim())
						.collect(Collectors.toSet());
			} catch (Exception e) {
				LOGGER.error("Impossibile leggere il file : [{}]", words.getAbsolutePath(), e);
				concatWords = new HashSet<>();
			}
		}

		return concatWords;
	}

	public boolean getRENAME() {
		return asBoolean(RENAME);
	};

	public boolean getMOVE() {
		return asBoolean(MOVE);
	};

	public boolean getDELETEEXT() {
		return asBoolean(DELETEEXT);
	};

	public boolean getDELTEEMPTY() {
		return asBoolean(DELTEEMPTY);
	};

	public boolean getFOLDERDEBUG() {
		return asBoolean(FOLDERDEBUG);
	};

	public boolean getEXEC() {
		return asBoolean(EXEC);
	};

	private boolean asBoolean(String param) {
		if (properties.getProperty(param) == null) {
			return false;
		}
		return "true".equals(properties.getProperty(param).trim()) ? true : false;
	}

	public String logFlags() {
		StringBuilder out = new StringBuilder();
		out.append("RENAME : ").append(getRENAME()).append("\n").append("MOVE : ").append(getMOVE()).append("\n")
				.append("DELETEEXT : ").append(getDELETEEXT()).append("\n").append("DELTEEMPTY : ")
				.append(getDELTEEMPTY()).append("\n").append("FOLDERDEBUG : ").append(getFOLDERDEBUG()).append("\n")
				.append("EXEC : ").append(getEXEC());

		return out.toString();
	}

}
