package com.daghosoft.dent;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportService {

	public enum TYPE {
		FILE, FOLDER
	}

	@Getter
	private static File report;

	private static ConfigServiceStatic config;
	private static ReportService reportService;

	private ReportService() {
	}

	public static ReportService get() {
		if (reportService != null) {
			return reportService;
		}
		reportService = new ReportService();
		config = ConfigServiceStatic.getConfig();
		report = config.getReportFile();
		Validate.notNull(report, "Il File di report risulta nullo impossibile procedere");

		if (report.exists()) {
			report.delete();
		}

		return reportService;
	}

	public void writeLine(String val) {
		try {
			FileUtils.write(report, val, "UTF-8", true);
		} catch (IOException e) {
			LOGGER.error("Eccezione nella scrittura sul file : ", e);
		}
	}

	private void writeRename(String prefix, String fileName, String targetName, TYPE type) {
		String out = String.format("%s #%s# [%s] -------> [%s] \n", prefix, type.toString(), fileName, targetName);
		writeLine(out);
	}

	public void writeMove(String fileName, String targetName, TYPE type) {
		writeRename("MOVE", fileName, targetName, type);
	}

	public void writeMoveError(String fileName, String targetName, TYPE type) {
		writeRename("######## ERROR-MOVE", fileName, targetName, type);
	}

	public void writeRename(String fileName, String targetName, TYPE type) {
		writeRename("RENAME", fileName, targetName, type);
	}

	public void writeDelete(String fileName, TYPE type) {
		String out = String.format("DELETE #%s# [%s] \n", type.toString(), fileName);
		writeLine(out);
	}
}
