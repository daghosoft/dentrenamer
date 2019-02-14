package com.daghosoft.dent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.Validate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileServiceImpl implements FileService {

    private String basePath;
    private List<String> filter;

    protected ConfigServiceStatic config;

    protected FileServiceImpl() {
        config = ConfigServiceStatic.getConfig();
        basePath = config.getBasePath();
        String exclusionPath = config.getExclusionPath();
        filter = new ArrayList<>();
        if (exclusionPath.contains(";")) {
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
        Collection<File> out = new ArrayList<>(list.size());
        for (File f : list) {
            if (!f.isDirectory()) {
                if (isValidByExclusionPath(f.getAbsolutePath())) {
                    out.add(f);
                    LOGGER.debug("File [{}] is valid adding to list.", f.getName());
                }
            } else {
                LOGGER.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Filtro file sbagliato individuata cartella [{}]",
                        f.getName());
            }
        }

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
        Collection<File> out = new ArrayList<>(list.size());

        for (File f : list) {
            if (f.isDirectory() && f != folder) {
                if (isValidByExclusionPath(f.getAbsolutePath())) {
                    out.add(f);
                    LOGGER.debug("Folder [{}] is valid adding to list.", f.getName());
                }
            } else {
                if (!f.isDirectory()) {
                    LOGGER.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Filtro cartelle sbagliato individuato file [{}]",
                            f.getName());
                }

            }
        }

        return out;
    }

    @Override
    public boolean isValidByExclusionPath(String path) {

        if (filter.isEmpty()) {
            return true;
        }

        for (String s : filter) {
            if (path.contains(s)) {
                LOGGER.debug("Path excluded by rule : [{}]", path);
                return false;
            }
        }
        return true;
    }

}
