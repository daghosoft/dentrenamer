package com.daghosoft.dent;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class RenamerServiceImplTest {

    private ConfigServiceStatic config = ConfigServiceStatic.getConfig("configTest.Properties");
    private RenamerServiceImpl sut = new RenamerServiceImpl();

    @Before
    public void init() {
        sut = new RenamerServiceImpl(config);
    }

    @Test
    public void renameSeparator() {
        String out = sut.rename(" ita Storia di un italiano -Il.Gatto.Con.Gli.Stivali.2011-.iTALiAN.BDRip.XviD-Twice.2011.avi ",
                true);
        assertThat(out).isEqualTo("Storia Di Un Italiano Il Gatto Con Gli Stivali (2011) - (2011).avi");
    }

    // ------------------------------- removeWordSeparator
    @Test
    public void removeWordSeparatorTest() {
        String out = sut.removeWordSeparator(" -;+;_;.;[;]ita.eng.tt-.uu-[-]-textsenzaspazi.avi ");
        assertThat(out).isEqualTo("; ; ; ; ; ita eng tt  uu     textsenzaspazi avi");
    }

    // ------------------------------- blackListFilter
    @Test
    public void blackListFilterNoSpaceTest() {
        String out = sut.blackListFilter(" ita-eng-textsenzaspazi.avi ");
        assertThat(out).isEqualTo("ita-eng-textsenzaspazi.avi");
    }

    @Test
    public void blackListFilterTest() {
        String out = sut.blackListFilter(" ita eng-textsenzaspazi.avi ");
        assertThat(out).isEqualTo("eng-textsenzaspazi.avi");
    }

    // --------------------------------------- formatYear
    @Test
    public void formatYearTest() {
        String out = sut.formatYear(" 3 Il Gatto Con Gli Stivali 2011 ");
        assertThat(out).isEqualTo("3 Il Gatto Con Gli Stivali (2011) -");
    }

    // --------------------------------------- removeMultipleHyphen

    @Test
    public void removeMultipleHyphenTest() {
        String out = sut
                .removeMultipleHyphen(" 3 Il Gatto Con Gli Stivali 2011 - - - -      - -  - - - - - - -   -    -   -   ");
        assertThat(out).isEqualTo("3 Il Gatto Con Gli Stivali 2011 -");
    }

    @Test
    public void removeMultipleHyphenNoHyphenTest() {
        String out = sut.removeMultipleHyphen(" 3 Il Gatto Con Gli Stivali 2011 ");
        assertThat(out).isEqualTo("3 Il Gatto Con Gli Stivali 2011");
    }

    // ------------------------------------------------ normalizeYearSeparator
    @Test
    public void normalizeYearSeparator() {
        String out = sut.normalizeYearSeparator(" 3 Il Gatto Con Gli Stivali 2011 -");
        assertThat(out).isEqualTo("3 Il Gatto Con Gli Stivali 2011");
    }

    @Test
    public void normalizeYearSeparatorBegin() {
        String out = sut.normalizeYearSeparator(" - 3 Il Gatto Con Gli Stivali 2011 -");
        assertThat(out).isEqualTo("3 Il Gatto Con Gli Stivali 2011");
    }

    // ------------------------------------------ killerHyphenContainingWord
    @Test
    public void killerHyphenContainingWord() {
        String out = sut.killerHyphenContainingWord("wall-e blueRay-XviD XviD-xmen ");
        assertThat(out).isEqualTo("wall-e XviD-xmen");
    }

    // ------------------------------------------- superKillerWords
    @Test
    public void superKillerWords() {
        String out = sut.superKillerWords("wall-e P O W");
        assertThat(out).isEqualTo("wall-e");
    }

}
