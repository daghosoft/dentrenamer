package com.daghosoft.dent;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Ignore;
import org.junit.Test;

public class RenamerServiceTest {
	
	private static final String BLACKLISTPARAM="iTALiAN;BDRip;XviD;TRL;MT;dvdRip;sub;ita;MIRCrew";
	private static final String WORDSEPARATORPARAM = "-;+;_;.;[;]";
	private static final String YEARLIMIT = "1900";
	
	private RenamerServiceImpl sut = new RenamerServiceImpl(BLACKLISTPARAM,WORDSEPARATORPARAM,YEARLIMIT);
	
	@Test
	public void renameFile(){
		String filename = "Guardiani.Della.Galassia.(2014)..        ...iTALiAN.BDRip.XviD-TRL[MT]_DVDrip+sub.ita.avi";
		String out = sut.rename(filename,true);
		
		assertThat(out).isEqualTo("Guardiani Della Galassia (2014).avi");
	}
	
	@Test
	public void renameYearFileTest(){
		String filename = "Guardiani.Della.Galassia.2014.        ...fakenotfilter.iTALiAN.BDRip.XviD-TRL[MT]_DVDrip+sub.ita.avi";
		
		sut = new RenamerServiceImpl(BLACKLISTPARAM,WORDSEPARATORPARAM,"2020");
		String out = sut.rename(filename,true);
		assertThat(out).isEqualTo("Guardiani Della Galassia 2014 Fakenotfilter.avi");
		
		sut = new RenamerServiceImpl(BLACKLISTPARAM,WORDSEPARATORPARAM,"1980");
		out = sut.rename(filename,true);
		assertThat(out).isEqualTo("Guardiani Della Galassia (2014) - Fakenotfilter.avi");
		
		sut = new RenamerServiceImpl(BLACKLISTPARAM,WORDSEPARATORPARAM,"");
		out = sut.rename(filename,true);
		assertThat(out).isEqualTo("Guardiani Della Galassia (2014) - Fakenotfilter.avi");
		
		sut = new RenamerServiceImpl(BLACKLISTPARAM,WORDSEPARATORPARAM,null);
		out = sut.rename(filename,true);
		assertThat(out).isEqualTo("Guardiani Della Galassia (2014) - Fakenotfilter.avi");
	
	}
	
	@Test
	public void renameYearFileCheckAfetYearTest(){
		String filename = "The.italian.job.2010.dvdRip-sub-ita.dvx";

		String out = sut.rename(filename,true);
		assertThat(out).isEqualTo("The Italian Job (2010).dvx");
		
		filename = "The.italian.job.2010.dvdRip-sub-ita";
		out = sut.rename(filename,false);
		assertThat(out).isEqualTo("The Italian Job (2010)");
		
		filename = "The.italian.job.2010.dvdRip-sub-ita-fakestring";
		out = sut.rename(filename,false);
		assertThat(out).isEqualTo("The Italian Job (2010) - Fakestring");
		
	}
	
	
	
	@Test
	public void renameFolder(){
		String folderName = "FOlderGenericInvoker-1.0.3-SNAPSHOT-executable - Copy - 2010.Fakenotfilter";
		String out = sut.rename(folderName,false);
		assertThat(out).isEqualTo("Foldergenericinvoker 1 0 3 Snapshot Executable Copy (2010) - Fakenotfilter");
		
		folderName = "FOlderGenericInvoker - Copy - 2010";
		out = sut.rename(folderName,false);
		assertThat(out).isEqualTo("Foldergenericinvoker Copy (2010)");
	}
	
	@Test
	public void containYearTest(){
		String filename = sut.removeWordSeparator( "The.italian.job.2010.dvdRip-sub-ita.dvx");
		boolean out = sut.containYear(filename.split(" "));
		assertThat(out).isTrue();
		
		out = sut.containYear("The italian job dvdRip sub ita.dvx".split(" "));
		assertThat(out).isFalse();
		
		out = sut.containYear(null);
		assertThat(out).isFalse();
		
		out = sut.containYear(new String[]{""});
		assertThat(out).isFalse();
	}
	
	@Test
	public void normalizeYearSeparatorTest(){
		String out = sut.normalizeYearSeparator("fake -.dvx");
		assertThat(out).isEqualTo("fake.dvx");
		
		out = sut.normalizeYearSeparator("fake -");
		assertThat(out).isEqualTo("fake");
		
		out = sut.normalizeYearSeparator("fake - fake.dvx");
		assertThat(out).isEqualTo("fake - fake.dvx");
		
		out = sut.normalizeYearSeparator("fake - fake");
		assertThat(out).isEqualTo("fake - fake");
		
		out = sut.normalizeYearSeparator("fake - fake -");
		assertThat(out).isEqualTo("fake - fake");
		
		out = sut.normalizeYearSeparator("fake - fake - ");
		assertThat(out).isEqualTo("fake - fake");
	}
	
	@Test
	public void blackListFilterTest(){
		String out = sut.blackListFilter("iTALiAN");
		assertThat(out).isEmpty();
		
		out = sut.blackListFilter("batMan");
		assertThat(out).isEqualTo("batMan");
	}
	
	@Test
	public void yearBuilderTest(){
		String out = sut.yearBuilder("batMan");
		assertThat(out).isEqualTo("batMan");
		
		out = sut.yearBuilder("1989");
		assertThat(out).isEqualTo("(1989) - ");
		
		out = sut.yearBuilder("[2017]");
		assertThat(out).isEqualTo("[2017]");
	}
	
	@Test
	public void removeWordSeparatorTest(){
		
		String out = sut.removeWordSeparator("Guardiani.Della+Galassia_2014-campione[test]fine");
		assertThat(out).isEqualTo("Guardiani Della Galassia 2014 campione test fine");
	}
	
	@Test
	public void fileNameNeedRenameTest(){
		
		Boolean out = sut.fileNameNeedRename("Guardiani.Della+Galassia_2014-campione[test]fine");
		assertThat(out).isTrue();
		
		out = sut.fileNameNeedRename("Guardiani  Della Galassia 2014 campione test fine");
		assertThat(out).isFalse();
	}
	
	@Test
	public void concatWordsFilterTest(){
		RenamerServiceImpl lsut = new RenamerServiceImpl(new ConfigServiceImpl());
		String filename = "Guardiani.Della.Galassia.2014.fakenotfilter.iTALiAN.BDRip.XviD-TRL[MT]_DVDrip+sub.ita.avi";
		String out = lsut.concatWordsFilter(filename);
		
		assertThat(out).isNotEqualTo(filename);
		assertThat(out).isEqualTo("guardiani.della.galassia.2014.fakenotfilter..avi");
	}
	
	@Test
	public void renameConcatWordsFilterTest(){
		RenamerServiceImpl lsut = new RenamerServiceImpl(new ConfigServiceImpl());
		String filename = "Guardiani.Della.Galassia.2014.fakenotfilter.iTALiAN.BDRip.XviD-TRL[MT]_DVDrip+sub.ita.dvx.blueRay.avi";
		String out = lsut.rename(filename,true);
		
		assertThat(out).isNotEqualTo(filename);
		assertThat(out).isEqualTo("Guardiani Della Galassia (2014) - Fakenotfilter.avi");
	}
	
	@Test
	public void removeMultipleHyphenTest(){
		RenamerServiceImpl lsut = new RenamerServiceImpl(new ConfigServiceImpl());
		String filename = "Guardiani.Della.Galassia.2014 - - fakenotfilter.avi";
		String out = lsut.removeMultipleHyphen(filename);
		
		assertThat(out).isNotEqualTo(filename);
		assertThat(out).isEqualTo("Guardiani.Della.Galassia.2014 - fakenotfilter.avi");
		
		filename = "Guardiani.Della.Galassia.2014 - - - - - - - - - - - -  - fakenotfilter.avi";
		out = lsut.removeMultipleHyphen(filename);
		
		assertThat(out).isNotEqualTo(filename);
		assertThat(out).isEqualTo("Guardiani.Della.Galassia.2014 - fakenotfilter.avi");
		
		filename = "Guardiani.Della.Galassia.2014 - - - - - - - - - - - -  - fakenotfilter - - ";
		out = lsut.removeMultipleHyphen(filename);
		
		assertThat(out).isNotEqualTo(filename);
		assertThat(out).isEqualTo("Guardiani.Della.Galassia.2014 - fakenotfilter -");
	}
	
	@Test
	public void renameWithConfigTest(){
		RenamerServiceImpl lsut = new RenamerServiceImpl(new ConfigServiceImpl());
		String filename = "Guardiani.Della.Galassia.2014.        ...fakenotfilter.iTALiAN.BDRip.XviD-TRL[MT]_DVDrip+sub.ita - - -.avi";
		String out = lsut.rename(filename, true);
		
		assertThat(out).isNotEqualTo(filename);
		assertThat(out).isEqualTo("Guardiani Della Galassia (2014) - Fakenotfilter -.avi");
		
		filename = "Guardiani- - -.Della.Galassia.2014.        ...fakenotfilter.iTALiAN.BDRip.XviD-TRL[MT]_DVDrip+sub.ita.";
		out = lsut.rename(filename, false);
		
		assertThat(out).isNotEqualTo(filename);
		assertThat(out).isEqualTo("Guardiani- Della Galassia (2014) - Fakenotfilter");
		
	}
	
}
