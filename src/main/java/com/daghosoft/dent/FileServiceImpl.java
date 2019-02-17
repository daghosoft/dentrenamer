package com.daghosoft.dent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileServiceImpl implements FileService {

	private Set<File> basePathSet;
	private List<String> filter = new ArrayList<>();

	protected FileServiceImpl() {
		ConfigServiceStatic config = ConfigServiceStatic.getConfig();
		basePathSet = config.getBasePath();
		String exclusionPath = config.getExclusionPath();
		if (StringUtils.isNotBlank(exclusionPath)) {
			filter = Arrays.asList(exclusionPath.split(";"));
		}

	}

	@Override
	public Set<File> getFiles(File basePathFolder) {
		Collection<File> list = FileUtils.listFiles(basePathFolder, TrueFileFilter.TRUE, DirectoryFileFilter.INSTANCE);
		return list.stream().filter(f -> !f.isDirectory()).filter(f -> isValidByExclusionPath(f.getAbsolutePath()))
				.collect(Collectors.toSet());
	}

	@Override
	public Set<File> getFilesInBasePath() {
		Set<File> out = new HashSet<>();
		for (File folder : basePathSet) {
			out.addAll(getFiles(folder));
		}

		return out;
	}

	@Override
	public Set<File> getFolders(File basePathFolder) {
		Collection<File> list = FileUtils.listFilesAndDirs(basePathFolder, new NotFileFilter(TrueFileFilter.INSTANCE),
				DirectoryFileFilter.DIRECTORY);
		return list.stream().filter(f -> f.isDirectory() && f != basePathFolder)
				.filter(f -> isValidByExclusionPath(f.getAbsolutePath())).collect(Collectors.toSet());
	}

	@Override
	public Set<File> getFoldersInBasePath() {
		Set<File> out = new HashSet<>();
		for (File folder : basePathSet) {
			out.addAll(getFolders(folder));
		}

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
