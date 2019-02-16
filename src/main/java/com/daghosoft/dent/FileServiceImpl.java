package com.daghosoft.dent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileServiceImpl implements FileService {

	private String basePath;
	private List<String> filter = new ArrayList<>();

	protected FileServiceImpl() {
		ConfigServiceStatic config = ConfigServiceStatic.getConfig();
		basePath = config.getBasePath();
		String exclusionPath = config.getExclusionPath();
		if (StringUtils.isNotBlank(exclusionPath)) {
			filter = Arrays.asList(exclusionPath.split(";"));
		}

	}

	@Override
	public Collection<File> getFilesInBasePath() {
		Validate.notEmpty(basePath, "Il BasePath e nullo o vuoto");

		File folder = new File(basePath);
		Validate.isTrue(folder.exists(), "Il path fornito non esiste : " + basePath);
		Validate.isTrue(folder.isDirectory(), "Il path fornito non e una directory: " + basePath);

		// Lista solo dei file compresi nel basepath
		Collection<File> list = FileUtils.listFiles(folder, TrueFileFilter.TRUE, DirectoryFileFilter.INSTANCE);

		List<File> out = list.stream().filter(f -> !f.isDirectory())
				.filter(f -> isValidByExclusionPath(f.getAbsolutePath())).collect(Collectors.toList());

		return out;
	}

	@Override
	public Collection<File> getFolderInBasePath() {
		Validate.notEmpty(basePath, "Il BasePath e nullo o vuoto");

		File folder = new File(basePath);
		Validate.isTrue(folder.exists(), "Il path fornito non esiste : " + basePath);
		Validate.isTrue(folder.isDirectory(), "Il path fornito non e una directory: " + basePath);

		// Lista solo delle cartelle
		Collection<File> list = FileUtils.listFilesAndDirs(folder, new NotFileFilter(TrueFileFilter.INSTANCE),
				DirectoryFileFilter.DIRECTORY);

		List<File> out = list.stream().filter(f -> f.isDirectory() && f != folder)
				.filter(f -> isValidByExclusionPath(f.getAbsolutePath())).collect(Collectors.toList());

		return out;
	}

	@Override
	public boolean isValidByExclusionPath(String path) {
		for (String s : filter) {
			if (path.contains(s)) {
				LOGGER.debug("Path excluded by rule : [{}]", path);
				return false;
			}
		}
		return true;
	}

}
