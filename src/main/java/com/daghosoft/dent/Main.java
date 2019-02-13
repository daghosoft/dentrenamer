package com.daghosoft.dent;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final String EXEC = "exec";
    private static boolean execute = false;

    private static final String MOVEBASE = "moveBasePath";
    private static boolean moveBasePath = false;

    private static final String DELETEEMPTY = "deleteEmpty";
    private static boolean deleteEmpty = false;

    private static final String DELETEEXT = "deleteByExtension";
    private static boolean deleteByExtension = false;

    private static Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
    private static RenamerServiceImpl renamerService;
    private static ConfigServiceStatic config;

    private static File report;
    private static final char separator = File.separatorChar;

    private static FileService fileService;

    private enum TYPE {
        FILE, FOLDER
    }

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            for (String param : args) {
                if (param.equals(EXEC)) {
                    execute = true;
                }
                if (param.equals(MOVEBASE)) {
                    moveBasePath = true;
                }
                if (param.equals(DELETEEMPTY)) {
                    deleteEmpty = true;
                }
                if (param.equals(DELETEEXT)) {
                    deleteByExtension = true;
                }
            }
        }
        LOGGER.info("Start Dent Renamer Execution");

        config = ConfigServiceStatic.getConfig();
        renamerService = new RenamerServiceImpl();
        fileService = new FileServiceImpl();

        initReport();

        deleteByExtension();

        // Operazione di rename sulle cartelle
        renameProces(fileService.getFolderInBasePath(), TYPE.FOLDER);

        // Operazione di rename sui file
        renameProces(fileService.getFilesInBasePath(), TYPE.FILE);

        moveProcess();

        deleteEmpty();

        LOGGER.info("End Dent Renamer Execution");
    }

    private static void deleteByExtension() {
        if (!deleteByExtension) {
            return;
        }

        File baseFolder = getBaseFolder();
        Collection<File> list = FileUtils.listFiles(baseFolder, TrueFileFilter.TRUE, DirectoryFileFilter.INSTANCE);

        for (File file : list) {
            String ext = FilenameUtils.getExtension(file.getName()).toLowerCase();
            if (config.getExtensionDelete().contains(ext)) {
                writeLineInReport("!!!Da Cancellare ", file.getAbsolutePath(), baseFolder.getAbsolutePath(), TYPE.FILE);
                if (execute) {
                    file.delete();
                }
            }
        }
    }

    private static void deleteEmpty() {
        if (!deleteEmpty) {
            return;
        }
        File baseFolder = getBaseFolder();
        Collection<File> folders = FileUtils.listFilesAndDirs(baseFolder, new NotFileFilter(TrueFileFilter.INSTANCE),
                DirectoryFileFilter.DIRECTORY);
        for (File folder : folders) {
            if (folder.isDirectory() && folder.listFiles().length == 0) {
                writeLineInReport("!!!Da Cancellare ", folder.getAbsolutePath(), baseFolder.getAbsolutePath(), TYPE.FOLDER);
                if (execute) {
                    folder.delete();
                }
            }
        }

    }

    private static void moveProcess() {
        if (!moveBasePath) {
            return;
        }

        File baseFolder = getBaseFolder();
        Collection<File> files = FileUtils.listFiles(baseFolder, TrueFileFilter.TRUE, DirectoryFileFilter.INSTANCE);
        for (File f : files) {
            if (fileService.isValidByExclusionPath(f.getAbsolutePath())
                    && !f.getParent().equals(baseFolder.getAbsolutePath())) {
                writeLineInReport("!!!Da spostare ", f.getAbsolutePath(), baseFolder.getAbsolutePath(), TYPE.FILE);
                if (execute) {
                    File dest = new File(baseFolder.getAbsolutePath() + File.separator + f.getName());
                    try {
                        FileUtils.moveFile(f, dest);
                    } catch (IOException e) {
                        LOGGER.error("Move file : [{}]", f.getName(), e);
                        writeLineInReport("!!!File presente ", f.getAbsolutePath(), baseFolder.getAbsolutePath(), TYPE.FILE);
                    }
                }
            }
        }

    }

    private static void renameProces(Collection<File> itemList, TYPE type) {
        for (File f : itemList) {
            String containingPath = FilenameUtils.getFullPath(f.getAbsolutePath());
            String name = f.getName();
            String targetName = renamerService.rename(name, type == TYPE.FILE);
            writeLineInReport(name, targetName, type);
            try {
                File targetFile = new File(containingPath + separator + targetName);
                if (!targetFile.exists()) {
                    LOGGER.debug(" {} Original Name : [{}] Target Name : [{}]", type, name, targetName);
                    if (type == TYPE.FILE && !f.isDirectory() && execute) {
                        FileUtils.moveFile(f, targetFile);
                    }
                    if (type == TYPE.FOLDER && f.isDirectory() && execute) {
                        FileUtils.moveDirectory(f, targetFile);
                    }
                }
            } catch (IOException e) {
                LOGGER.error(StringUtils.EMPTY, e);
            }
        }
    }

    protected static void initReport() {
        report = config.getReportFile();

        Validate.notNull(report, "Il File di report risulta nullo impossibile procedere");
        LOGGER.info("Init report file @ [{}]", report.getAbsoluteFile());

        if (report.exists()) {
            report.delete();
        }
        try {
            StringBuilder headerBuilder = new StringBuilder("Dent renamer eseguito in modalitaa report @ ")
                    .append(new Date().toString())
                    .append(" - Per eseguire il rename dei file utilizzare parametro [exec]")
                    .append("\n");
            FileUtils.write(report, headerBuilder.toString(), "UTF-8", true);
            FileUtils.write(report, " \n", "UTF-8", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeLineInReport(String fileName, String targetName, TYPE type) {
        writeLineInReport(StringUtils.EMPTY, fileName, targetName, type);
    }

    private static void writeLineInReport(String prefix, String fileName, String targetName, TYPE type) {
        try {
            String out = String.format("%s#%s# %s -------> %s \n", prefix, type.toString(), fileName, targetName);
            FileUtils.write(report, out, "UTF-8", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getBaseFolder() {
        String basePath = config.getBasePath();
        LOGGER.debug("BasePath is : [{}]", basePath);
        return new File(basePath);

    }
}
