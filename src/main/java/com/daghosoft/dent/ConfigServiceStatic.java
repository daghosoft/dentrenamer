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
@Getter
public class ConfigServiceStatic {

    public static final String CONFIGNAME = "/config.properties";

    // path di esecuzione sulla base del config name
    private static String execPath;

    private static Properties properties;
    private static ConfigServiceStatic config;

    private int yearDefault = 1900;

    private Set<String> concatWords;

    private String configPropertiesPath;

    private String wordSeparator = StringUtils.EMPTY;

    private String exclusionPath = StringUtils.EMPTY;

    private Set<String> extensionDelete;

    private File reportFile;
    private boolean fileRename;
    private boolean fileMoveBasepath;
    private boolean deteByExtension;
    private boolean deteEmptyFolder;
    private boolean debug;
    private boolean execFlag;
    private int yearLimit;
    private boolean reportNoMkv;
    private Set<File> allPath;

    public static ConfigServiceStatic getConfig(String pConfigName) {
        if (config != null) {
            return config;
        }

        config = new ConfigServiceStatic(pConfigName);
        return config;
    }

    public static ConfigServiceStatic getConfig() {
        return getConfig(CONFIGNAME);
    }

    private ConfigServiceStatic(String pConfigName) {

        loadProperties(pConfigName);

        allPath = loadAllPath();
        yearLimit = populateYearLimit();
        wordSeparator = properties.getProperty("word.separator").trim();
        exclusionPath = properties.getProperty("file.exclusion.path").trim();
        extensionDelete = populateExtensionDelete();

        String filename = properties.getProperty("report.file.name", "dent-renamer-report.csv");
        reportFile = new File(execPath + File.separatorChar + filename);

        fileRename = asBoolean("file.rename");
        fileMoveBasepath = asBoolean("file.move.basepath");
        deteByExtension = asBoolean("file.delete.by.extension");
        deteEmptyFolder = asBoolean("folder.delete.empty");
        debug = asBoolean("debug");
        execFlag = asBoolean("exec");
        reportNoMkv = asBoolean("file.report.no.mkv");
    }

    private void loadProperties(String pConfigName) {
        if (!pConfigName.startsWith("/")) {
            pConfigName = "/" + pConfigName;
        }
        URL config = this.getClass().getResource(pConfigName);

        Validate.notNull(config, "Errore recupero file di configurazione : " + pConfigName);
        LOGGER.debug("Load config from : [{}]", config.getPath());
        try {
            File fconfig = new File(config.getPath());
            configPropertiesPath = fconfig.getAbsolutePath();
            execPath = FilenameUtils.getFullPathNoEndSeparator(fconfig.getAbsolutePath());

            LOGGER.trace("execPath ######################## [{}]", execPath);

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

    private Set<File> loadAllPath() {
        String BASEPATHKEY = "file.basePath";
        Validate.isTrue(properties.containsKey(BASEPATHKEY),
                "Il file di configurazione deve contenre almeno un basepath : " + BASEPATHKEY);

        Set<File> out = new HashSet<>();
        // Recupero il basepath primario
        verifyPathExistence(BASEPATHKEY, out);

        // Recupero tutti i basepath di tipo file.basePath.[1-10]
        for (int x = 10; x > 0; x--) {
            verifyPathExistence(BASEPATHKEY + "." + x, out);
        }
        return out;
    }

    private void verifyPathExistence(String path, Set<File> out) {
        if (!properties.containsKey(path)) {
            return;
        }
        String pathString = properties.getProperty(path);
        LOGGER.debug("Path String [{}]", pathString);
        File folder = new File(pathString);
        if (folder.exists()) {
            out.add(folder);
        }
    }

    private Set<String> populateExtensionDelete() {
        if (extensionDelete != null) {
            return extensionDelete;
        }

        String EXTENSIONDELETE = "file.extension.delete";
        if (properties.get(EXTENSIONDELETE) == null) {
            return new HashSet<String>();
        }
        String tmp = properties.getProperty(EXTENSIONDELETE).trim();
        if (StringUtils.isBlank(tmp) || !tmp.contains(";")) {
            return new HashSet<String>();
        }

        List<String> list = Arrays.asList(tmp.toLowerCase().split(";"));
        extensionDelete = new HashSet<>();
        extensionDelete.addAll(list);
        return extensionDelete;
    }

    private int populateYearLimit() {
        String YEARLIMIT = "word.year.limit";
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

    private boolean asBoolean(String param) {
        if (properties.getProperty(param) == null) {
            return false;
        }
        return "true".equals(properties.getProperty(param).trim()) ? true : false;
    }

    public String logFlags() {
        return "Flags=" + fileRename + ", moveExec=" + fileMoveBasepath + ", deteByExtension="
                + deteByExtension + ", deteEmptyFolder=" + deteEmptyFolder + ", folderDebug=" + debug + ", execFlag="
                + execFlag + ", yearLimit=" + yearLimit;
    }

}
