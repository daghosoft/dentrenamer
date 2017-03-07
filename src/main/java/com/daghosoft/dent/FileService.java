package com.daghosoft.dent;

import java.io.File;
import java.util.Collection;

public interface FileService {

	Collection<File> getFilesInBasePath();
	
	Collection<File> getFolderInBasePath();
}
