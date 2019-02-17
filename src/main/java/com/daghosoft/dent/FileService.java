package com.daghosoft.dent;

import java.io.File;
import java.util.Set;

public interface FileService {

	Set<File> getFilesInBasePath();

	Set<File> getFoldersInBasePath();

	boolean isValidByExclusionPath(String path);

	Set<File> getFiles(File basePathFolder);

	Set<File> getFolders(File basePathFolder);
}
