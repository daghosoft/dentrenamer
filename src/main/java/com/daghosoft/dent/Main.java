package com.daghosoft.dent;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.daghosoft.dent.ReportService.TYPE;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    private static final String EXEC = "exec";
    private static boolean execute = false;

    private static final String MOVEBASE = "moveBasePath";
    private static boolean moveBasePath = false;

    private static final String DELETEEMPTY = "deleteEmpty";
    private static boolean deleteEmpty = false;

    private static final String DELETEEXT = "deleteByExtension";
    private static boolean deleteByExtension = false;

    private static final String DEBUG = "debugFlag";
    private static boolean debugFlag = false;

    private static RenamerServiceImpl renamerService;
    private static ConfigServiceStatic config;

    private static final char separator = File.separatorChar;

    private static FileService fileService;
    private static ReportService reportService;

    private static int deleteByExtensionCount = 0;
    private static int deleteEmptyCount = 0;
    private static int moveProcessCount = 0;
    private static int renameProcesCount = 0;

    private static StringBuilder reportBuilder = new StringBuilder();

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            String msg = String.format("Impossibile eseguire Dent. Parametri Disponibili: [%s] [%s] [%s] [%s] [%s]",
                    EXEC, MOVEBASE, DELETEEMPTY, DELETEEXT, DEBUG);
            System.out.println(msg);
            return;
        }
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
            if (param.equals(DEBUG)) {
                debugFlag = true;
            }

        }
        LOGGER.info("Start Dent Renamer Execution");

        config = ConfigServiceStatic.getConfig();
        renamerService = new RenamerServiceImpl();
        fileService = new FileServiceImpl();
        reportService = ReportService.get();

        Date start = new Date();
        String msg = String.format("Esecuzione partita @%s \n", start);
        reportService.writeLine(msg);

        deleteByExtension();

        // Operazione di rename sulle cartelle
        renameProces(fileService.getFolderInBasePath(), TYPE.FOLDER);

        // Operazione di rename sui file
        renameProces(fileService.getFilesInBasePath(), TYPE.FILE);

        moveBasePath();

        deleteEmptyFolders();

        printSum(start, Arrays.toString(args));

        LOGGER.info("End Dent Renamer Execution");
        // TODO remove
        LOGGER.info("### Report Generato @ [{}]", reportService.getReport().getAbsolutePath());
    }

    private static void deleteByExtension() {
        if (!deleteByExtension) {
            return;
        }

        Collection<File> list = fileService.getFilesInBasePath();

        for (File file : list) {
            String ext = FilenameUtils.getExtension(file.getName()).toLowerCase();
            if (config.getExtensionDelete().contains(ext)) {
                reportService.writeDelete(file.getAbsolutePath(), TYPE.FILE);
                deleteByExtensionCount++;
                if (execute) {
                    file.delete();
                }
            }
        }
        reportBuilder.append(String.format("# Delete By Extension : %s \n", deleteByExtensionCount));
    }

    private static void deleteEmptyFolders() {
        if (!deleteEmpty) {
            return;
        }

        Collection<File> folders = fileService.getFolderInBasePath();
        for (File folder : folders) {
            earSubFolderRemover(folder, "@eaDir");
            earSubFolderRemover(folder, "@earDir");
            earSubFolderRemover(folder, "@eardir");
            if (folder.isDirectory() && folder.listFiles().length == 0) {
                reportService.writeDelete(folder.getAbsolutePath(), TYPE.FOLDER);
                deleteEmptyCount++;
                if (execute) {
                    folder.delete();
                }
            }

            if (debugFlag) {
                String debugString = String.format("\n\n DEBUG %s ------> %s  \n\n", folder.getName(),
                        Arrays.toString(folder.listFiles()));
                reportService.writeLine(debugString);
            }
        }
        reportBuilder.append(String.format("# Delete Empty Folder : %s \n", deleteEmptyCount));
    }

    private static void earSubFolderRemover(File folder, String subName) {
        if (!execute || !folder.isDirectory()) {
            return;
        }
        File sub = new File(folder.getAbsolutePath() + File.separator + subName);
        if (sub.exists()) {
            reportService.writeDelete(sub.getAbsolutePath(), TYPE.FOLDER);
            try {
                FileUtils.forceDelete(sub);
            } catch (IOException e) {
                LOGGER.error("Errore cancellazionde della direcory : [{}]", sub.getAbsolutePath(), e);
            }
        }
    }

    private static void moveBasePath() {
        if (!moveBasePath) {
            return;
        }

        File basePath = new File(config.getBasePath());
        Collection<File> files = fileService.getFilesInBasePath();
        for (File f : files) {
            if (f.getParent().equals(basePath.getPath())) {
                continue;
            }

            reportService.writeMove(f.getAbsolutePath(), config.getBasePath(), TYPE.FILE);
            moveProcessCount++;
            if (execute) {
                File dest = new File(config.getBasePath() + File.separator + f.getName());
                try {
                    FileUtils.moveFile(f, dest);
                } catch (IOException e) {
                    LOGGER.error("Move file : [{}]", f.getName(), e);
                    reportService.writeMoveError(f.getAbsolutePath(), config.getBasePath(), TYPE.FILE);
                }
            }
        }
        reportBuilder.append(String.format("# Move Base Path : %s \n", moveProcessCount));

    }

    private static void renameProces(Collection<File> itemList, TYPE type) {
        for (File f : itemList) {
            String containingPath = FilenameUtils.getFullPath(f.getAbsolutePath());
            String name = f.getName();
            String targetName = renamerService.rename(name, type == TYPE.FILE);
            reportService.writeRename(name, targetName, type);
            if (name.equals(targetName)) {
                continue;
            }
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
                    renameProcesCount++;
                } else {
                    reportService.writeMoveError(name, targetName, type);
                }
            } catch (IOException e) {
                LOGGER.error(StringUtils.EMPTY, e);
            }
        }

        reportBuilder.append(String.format("# Rename %s : %s \n", type.toString(), renameProcesCount));
    }

    private static void printSum(Date date, String argsString) {
        reportService.writeLine("\n\n##############################\n");
        reportService.writeLine(String.format("# @ %s \n", date.toString()));
        reportService.writeLine(String.format("# Args : %s \n", argsString));
        reportService.writeLine("# \n");
        reportService.writeLine(reportBuilder.toString());
        reportService.writeLine("# \n");
    }
}
