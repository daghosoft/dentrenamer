package com.daghosoft.dent;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.daghosoft.dent.ReportService.TYPE;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

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

		LOGGER.info("### Start Dent Renamer Execution");

		if (args == null || args.length == 0 || StringUtils.isBlank(args[0])) {
			config = ConfigServiceStatic.getConfig();
		} else {
			LOGGER.info("# Override config file with [{}]", args[0]);
			config = ConfigServiceStatic.getConfig(args[0]);
		}

		renamerService = new RenamerServiceImpl();
		fileService = new FileServiceImpl();
		reportService = ReportService.get();

		Date start = new Date();
		String msg = String.format("Esecuzione partita @%s \n", start);
		reportService.writeLine(msg);

		deleteByExtension();

		// Operazione di rename sulle cartelle
		renameProces(fileService.getFoldersInBasePath(), TYPE.FOLDER);

		// Operazione di rename sui file
		renameProces(fileService.getFilesInBasePath(), TYPE.FILE);

		reportBuilder.append("# Move \n");
		for (File basePAth : config.getAllPath()) {
			moveBasePath(basePAth);
		}
		reportBuilder.append("# \n");

		deleteEmptyFolders();

		reportNoMkv();

		printSum(start);

		LOGGER.info("### Report Generato @ [{}]", ReportService.getReport().getAbsolutePath());
	}

	private static void reportNoMkv() {
		if (!config.isReportNoMkv()) {
			return;
		}
		Set<File> allFiles = fileService.getFilesInBasePath();
		deleteByExtensionCount++;
		Set<File> filtered = allFiles.stream()
				.filter(file -> !"mkv".equals(FilenameUtils.getExtension(file.getName().toLowerCase())))
				.collect(Collectors.toSet());

		filtered.forEach(file -> {
			reportService.writeLine("NO-MKV [%s] Size : [%s] \n", file.getAbsolutePath(),
					FileUtils.byteCountToDisplaySize(file.length()));
		});
		reportBuilder.append(String.format("# No Mkv : %s \n", filtered.size()));
	}

	private static void deleteByExtension() {
		if (!config.isDeteByExtension()) {
			return;
		}

		Collection<File> list = fileService.getFilesInBasePath();

		for (File file : list) {
			String ext = FilenameUtils.getExtension(file.getName()).toLowerCase();
			if (config.getExtensionDelete().contains(ext)) {
				reportService.writeDelete(file.getAbsolutePath(), TYPE.FILE);
				deleteByExtensionCount++;
				if (config.isExecFlag()) {
					boolean result = file.delete();
					LOGGER.trace("Delete [{}] result [{}]", file.getAbsolutePath(), result);
				}
			}
		}
		reportBuilder.append(String.format("# Delete By Extension : %s \n", deleteByExtensionCount));
	}

	private static void deleteEmptyFolders() {
		if (!config.isDeteEmptyFolder()) {
			return;
		}

		Collection<File> folders = fileService.getFoldersInBasePath();
		for (File folder : folders) {
			earSubFolderRemover(folder, "@eaDir");
			earSubFolderRemover(folder, "@earDir");
			earSubFolderRemover(folder, "@eardir");
			if (folder.isDirectory() && folder.listFiles().length == 0) {
				reportService.writeDelete(folder.getAbsolutePath(), TYPE.FOLDER);
				deleteEmptyCount++;
				if (config.isExecFlag()) {
					boolean result = folder.delete();
					LOGGER.trace("Delete [{}] result [{}]", folder.getAbsolutePath(), result);
				}
			}

			if (config.isDebug()) {
				String debugString = String.format("\n\n DEBUG %s ------> %s  \n\n", folder.getName(),
						Arrays.toString(folder.listFiles()));
				reportService.writeLine(debugString);
			}
		}
		reportBuilder.append(String.format("# Delete Empty Folder : %s \n", deleteEmptyCount));
	}

	private static void earSubFolderRemover(File folder, String subName) {
		if (!config.isExecFlag() || !folder.isDirectory()) {
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

	private static void moveBasePath(File basePath) {
		if (!config.isFileMoveBasepath()) {
			return;
		}

		reportService.writeLine("\n####################### Move Base Path : %s \n", basePath.getAbsolutePath());
		Set<File> files = fileService.getFiles(basePath);
		for (File f : files) {
			if (f.getParent().equals(basePath.getPath())) {
				continue;
			}

			reportService.writeMove(f.getAbsolutePath(), basePath.getAbsolutePath(), TYPE.FILE);
			moveProcessCount++;
			if (config.isExecFlag()) {
				File dest = new File(basePath.getAbsolutePath() + File.separator + f.getName());
				try {
					FileUtils.moveFile(f, dest);
				} catch (IOException e) {
					LOGGER.error("Move file : [{}]", f.getName(), e);
					reportService.writeMoveError(f.getAbsolutePath(), basePath.getAbsolutePath(), TYPE.FILE);
				}
			}
		}
		reportBuilder.append(String.format("#    %s : %s \n", basePath.getPath(), moveProcessCount));

	}

	private static void renameProces(Collection<File> itemList, TYPE type) {
		if (!config.isFileRename()) {
			return;
		}
		for (File f : itemList) {
			LOGGER.debug("{} #Evaluate # [{}]", type, f.getAbsolutePath());
			String containingPath = FilenameUtils.getFullPath(f.getAbsolutePath());
			String name = f.getName();
			String targetName = renamerService.rename(name, type == TYPE.FILE);
			if (name.equals(targetName) || StringUtils.isBlank(targetName)) {
				continue;
			}
			reportService.writeRename(name, targetName, type);
			try {
				File targetFile = new File(containingPath + separator + targetName);
				if (!targetFile.exists()) {
					LOGGER.debug(" {} Original Name : [{}] Target Name : [{}]", type, name, targetName);
					if (type == TYPE.FILE && !f.isDirectory() && config.isExecFlag()) {
						FileUtils.moveFile(f, targetFile);
					}
					if (type == TYPE.FOLDER && f.isDirectory() && config.isExecFlag()) {
						FileUtils.moveDirectory(f, targetFile);
					}
					renameProcesCount++;
				} else {
					reportService.writeMoveExist(name, targetName, type);
				}
			} catch (IOException e) {
				LOGGER.error(StringUtils.EMPTY, e);
			}
		}

		reportBuilder.append(String.format("# Rename %s : %s \n", type.toString(), renameProcesCount));
	}

	private static void printSum(Date date) {
		reportService.writeLine("\n\n##############################\n");
		reportService.writeLine("# @ %s \n", date.toString());
		reportService.writeLine("# Properties Path : %s \n", config.getConfigPropertiesPath());
		reportService.writeLine("# All path : %s \n", config.getAllPath());
		reportService.writeLine("# \n");
		reportService.writeLine("# Config : %s \n", config.logFlags().replaceAll(",", "\n#"));
		reportService.writeLine("# \n");
		reportService.writeLine(reportBuilder.toString());
		reportService.writeLine("# \n");
	}
}
