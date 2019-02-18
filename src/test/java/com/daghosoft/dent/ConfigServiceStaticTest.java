package com.daghosoft.dent;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Set;

import org.junit.Test;

public class ConfigServiceStaticTest {

    private ConfigServiceStatic sut = ConfigServiceStatic.getConfig("configTest.Properties");

    @Test
    public void getWordSeparatorTest() {
        String out = sut.getWordSeparator();
        assertThat(out).isNotNull();
        assertThat(out).isEqualTo("-;+;_;.;[;]");
    }

    @Test
    public void getBasePathTest() {
        Set<File> out = sut.getAllPath();
        assertThat(out.size()).isEqualTo(2);
    }

    @Test
    public void getExclusionPathTest() {
        String out = sut.getExclusionPath();
        assertThat(out).isNotNull();
        assertThat(out).isEqualTo("@eardir;#recycle");
    }

    @Test
    public void getYearLimitTest() {
        int out = sut.getYearLimit();
        assertThat(out).isNotNull();
        assertThat(out).isEqualTo(1900);

    }

    @Test
    public void getReportFileTest() {
        File f = sut.getReportFile();
        assertThat(f).isNotNull();
        assertThat(f.getName()).isEqualTo("dent-renamer-report.csv");
    }

    @Test
    public void getConcatWordsTest() {
        Set<String> out = sut.getConcatWords();
        assertThat(out).isNotNull();
        assertThat(out.size() > 1).isTrue();
        assertThat(out).contains("xvid");
        assertThat(out).contains("****p o w");
    }

    @Test
    public void getRENAMETest() {
        assertThat(sut.isFileRename()).isTrue();
    };

    @Test
    public void getMOVETest() {
        assertThat(sut.isFileMoveBasepath()).isTrue();
    };

    @Test
    public void getDELETEEXTTest() {
        assertThat(sut.isDeteByExtension()).isTrue();
    };

    @Test
    public void getDELTEEMPTYTest() {
        assertThat(sut.isDeteEmptyFolder()).isTrue();
    };

    @Test
    public void getDEbugTest() {
        assertThat(sut.isDebug()).isTrue();
    };

    @Test
    public void getEXECTest() {
        assertThat(sut.isExecFlag()).isFalse();
    };

}
