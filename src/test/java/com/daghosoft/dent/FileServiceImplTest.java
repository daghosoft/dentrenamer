package com.daghosoft.dent;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConfigServiceStatic.class })
public class FileServiceImplTest {

    private static final String FakeBasePath = "./src/main/";
    private static final String FakeBasePath1 = "./src/test/";

    private static final String filter1 = "properties";
    private static final String filter2 = "daghosoft";
    private static final String FakeFilter = filter1 + ";" + filter2;

    private FileServiceImpl sut = new FileServiceImpl();

    @Mock
    private ConfigServiceStatic config;

    @Before
    public void init() {
        Set<File> out = new HashSet<>();
        out.add(new File(FakeBasePath));
        out.add(new File(FakeBasePath1));
        PowerMockito.mockStatic(ConfigServiceStatic.class);
        BDDMockito.given(ConfigServiceStatic.getConfig()).willReturn(config);

        BDDMockito.given(config.getAllPath()).willReturn(out);
        BDDMockito.given(config.getExclusionPath()).willReturn(FakeFilter);
        sut = new FileServiceImpl();
    }

    @Test
    public void getFilesInBasePathTest() throws Exception {
        File basePath = new File(FakeBasePath);
        File basePath1 = new File(FakeBasePath1);
        Collection<File> files = sut.getFilesInBasePath();
        assertThat(files.size()).isGreaterThan(0);
        for (File f : files) {

            assertThat(f.isDirectory()).isFalse();
            boolean out = false;
            if (f.getAbsolutePath().contains(basePath.getAbsolutePath())
                    || f.getAbsolutePath().contains(basePath1.getAbsolutePath())) {
                out = true;

            }
            assertThat(out).isTrue();
            assertThat(f.getAbsolutePath()).doesNotContain(filter1);
            assertThat(f.getAbsolutePath()).doesNotContain(filter2);
        }
    }

    @Test
    public void getFolderInBasePathTest() throws Exception {
        File basePath = new File(FakeBasePath);
        File basePath1 = new File(FakeBasePath1);
        Collection<File> files = sut.getFoldersInBasePath();
        assertThat(files.size()).isGreaterThan(0);
        for (File f : files) {

            assertThat(f.isDirectory()).isTrue();
            boolean out = false;
            if (f.getAbsolutePath().contains(basePath.getAbsolutePath())
                    || f.getAbsolutePath().contains(basePath1.getAbsolutePath())) {
                out = true;

            }
            assertThat(out).isTrue();
            assertThat(f.getAbsolutePath()).doesNotContain(filter1);
            assertThat(f.getAbsolutePath()).doesNotContain(filter2);
        }
    }

    @Test
    public void getFolderInBasePathTestEmptyFilter() throws Exception {
        BDDMockito.given(config.getExclusionPath()).willReturn(StringUtils.EMPTY);

        Collection<File> folders = sut.getFoldersInBasePath();
        assertThat(folders.size()).isGreaterThan(0);

        for (File f : folders) {
            assertThat(f.isDirectory()).isTrue();
        }
    }
}
