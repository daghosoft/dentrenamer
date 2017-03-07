package com.daghosoft.dent;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Ignore;
import org.junit.Test;

public class RenamerServiceTest {
	
	private static final String BLACKLISTPARAM="iTALiAN;BDRip;XviD;TRL;MT;dvdRip;sub;ita";
	private static final String WORDSEPARATORPARAM = "-;+;_;.;[;]";
	private static final String YEARLIMIT = "1900";
	
	private RenamerServiceImpl sut = new RenamerServiceImpl(BLACKLISTPARAM,WORDSEPARATORPARAM,YEARLIMIT);
	
	@Test
	public void renameFile(){
		String filename = "Guardiani.Della.Galassia.(2014)..        ...iTALiAN.BDRip.XviD-TRL[MT]_DVDrip+sub.ita.avi";
		String out = sut.renameFile(filename);
		
		assertThat(out).isEqualTo("Guardiani Della Galassia (2014).avi");
	}
	
	@Test
	public void renameYearFileTest(){
		String filename = "Guardiani.Della.Galassia.2014.        ...iTALiAN.BDRip.XviD-TRL[MT]_DVDrip+sub.ita.avi";
		
		sut = new RenamerServiceImpl(BLACKLISTPARAM,WORDSEPARATORPARAM,"2020");
		String out = sut.renameFile(filename);
		assertThat(out).isEqualTo("Guardiani Della Galassia 2014.avi");
		
		sut = new RenamerServiceImpl(BLACKLISTPARAM,WORDSEPARATORPARAM,"1980");
		out = sut.renameFile(filename);
		assertThat(out).isEqualTo("Guardiani Della Galassia (2014).avi");
		
		sut = new RenamerServiceImpl(BLACKLISTPARAM,WORDSEPARATORPARAM,"");
		out = sut.renameFile(filename);
		assertThat(out).isEqualTo("Guardiani Della Galassia (2014).avi");
		
		sut = new RenamerServiceImpl(BLACKLISTPARAM,WORDSEPARATORPARAM,null);
		out = sut.renameFile(filename);
		assertThat(out).isEqualTo("Guardiani Della Galassia (2014).avi");
	}
	
	@Test
	@Ignore
	public void renameYearFileCheckAfetYearTest(){
		String filename = "The.italian.job.2010.dvdRip-sub-ita.dvx";

		String out = sut.renameFile(filename);
		assertThat(out).isEqualTo("The Italian Job (2010).dvx");
		
		filename = "The.italian.job.2010.dvdRip-sub-ita";
		out = sut.renameFolder(filename);
		assertThat(out).isEqualTo("The Italian Job (2010)");
		
	}
	
	@Test
	public void renameFolder(){
		String folderName = "FOlderGenericInvoker-1.0.3-SNAPSHOT-executable - Copy - 2010";
		String out = sut.renameFolder(folderName);
		assertThat(out).isEqualTo("Foldergenericinvoker 1 0 3 Snapshot Executable Copy (2010)");
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
		assertThat(out).isEqualTo("(1989)");
		
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
	
	
}
