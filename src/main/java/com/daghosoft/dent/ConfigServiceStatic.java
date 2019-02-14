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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigServiceStatic {

    private static final String CONFIGNAME = "/config.properties";
    private static final String WORDSEPARATOR = "word.separator";
    private static final String BLACKLIST = "word.blacklist";
    private static final String YEARLIMIT = "word.year.limit";
    private static final String BASEPATH = "file.basePath";
    private static final String EXCLUSIONPATH = "file.exclusion.path";
    private static final String EXTENSIONDELETE = "file.extension.delete";
    private static String execPath;

    private static Properties properties;
    private static ConfigServiceStatic config;

    private ConfigServiceStatic(String pConfigName) {

        if (!pConfigName.startsWith("/")) {
            pConfigName = "/" + pConfigName;
        }
        URL config = this.getClass().getResource(pConfigName);

        Validate.notNull(config, "Errore recupero file di configurazione config.properties");
        LOGGER.debug("Load config from : [{}]", config.getPath());
        try {
            File fconfig = new File(config.getPath());
            execPath = FilenameUtils.getFullPathNoEndSeparator(fconfig.getAbsolutePath());
            LOGGER.debug("execPath ######################## [{}]", execPath);
            FileInputStream fis = new FileInputStream(fconfig);
            properties = new Properties();
            properties.load(fis);
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

    public Properties getPropertySet() {
        return properties;
    }

    public String getBlackList() {
        Validate.notNull(properties,
                "l'oggetto properties risulta nullo possibile causa errore nel recupero del file di properties");
        return ((String) properties.get(BLACKLIST)).trim();
    }

    public String getWordSeparator() {
        Validate.notNull(properties,
                "l'oggetto properties risulta nullo possibile causa errore nel recupero del file di properties");
        return ((String) properties.get(WORDSEPARATOR)).trim();
    }

    public String getBasePath() {
        Validate.notNull(properties,
                "l'oggetto properties risulta nullo possibile causa errore nel recupero del file di properties");
        return ((String) properties.get(BASEPATH)).trim();
    }

    public String getExclusionPath() {
        Validate.notNull(properties,
                "l'oggetto properties risulta nullo possibile causa errore nel recupero del file di properties");
        return ((String) properties.get(EXCLUSIONPATH)).trim();
    }

    private Set<String> extensionDelete;

    public Set<String> getExtensionDelete() {
        Validate.notNull(properties,
                "l'oggetto properties risulta nullo possibile causa errore nel recupero del file di properties");

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

    public String getYearLimit() {
        Validate.notNull(properties,
                "l'oggetto properties risulta nullo possibile causa errore nel recupero del file di properties");

        if (properties.containsKey(YEARLIMIT)) {
            return ((String) properties.get(YEARLIMIT)).trim();
        }

        return StringUtils.EMPTY;
    }

    public File getReportFile() {
        Validate.notNull(execPath);
        return new File(execPath + File.separatorChar + "dent-renamer-report.csv");
    }

    public Set<String> getConcatWords() {
        Validate.notNull(execPath);

        File words = new File(execPath + File.separatorChar + "concatWords.properties");

        LOGGER.debug("################### search for : {}", words.getAbsolutePath());
        Set<String> out = new HashSet<>();
        if (words.exists()) {
            try {
                out.addAll(FileUtils.readLines(words, "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return out;
    }

}
