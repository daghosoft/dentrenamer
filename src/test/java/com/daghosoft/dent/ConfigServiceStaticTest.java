package com.daghosoft.dent;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

public class ConfigServiceStaticTest {

    ConfigServiceStatic sut = ConfigServiceStatic.getConfig("configTest.Properties");

    @Test
    public void getPropertySetTest() {
        Properties out = sut.getPropertySet();
        assertThat(out).isNotNull();
        assertThat(out.get("test")).isEqualTo("fakeString");
        assertThat(out.get("fake")).isNull();
    }

    @Test
    public void getWordSeparatorTest() {
        String out = sut.getWordSeparator();
        assertThat(out).isNotNull();
        assertThat(out).isEqualTo("-;+;_;.;[;]");
    }

    @Test
    public void getBasePathTest() {
        String out = sut.getBasePath();
        assertThat(out).isNotNull();
        assertThat(out).isEqualTo(".//target//fakePath//FileCreator");
    }

    @Test
    public void getExclusionPathTest() {
        String out = sut.getExclusionPath();
        assertThat(out).isNotNull();
        assertThat(out).isEqualTo("@eardir;#recycle");
    }

    @Test
    public void getYearLimitTest() {
        String out = sut.getYearLimit();
        assertThat(out).isNotNull();
        assertThat(out).isEqualTo("1900");

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
    }

}
