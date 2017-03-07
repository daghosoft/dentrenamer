package com.daghosoft.dent;

import java.util.Properties;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfigServiceImplTest {
	
	ConfigService sut = new ConfigServiceImpl();
	
	@Test
	public void getPropertySetTest(){
		
		Properties out = sut.getPropertySet();
		assertThat(out).isNotNull();
		assertThat(out.get("test")).isEqualTo("fakeString");
		assertThat(out.get("fake")).isNull();
		
	}
	
	@Test
	public void getWordSeparatorTest(){
		
		String out = sut.getWordSeparator();
		assertThat(out).isNotNull();
		assertThat(out).isEqualTo("-;+;_;.;[;]");
		
	}
	
	@Test
	public void getBlackListTest(){
		
		String out = sut.getBlackList();
		assertThat(out).isNotNull();
		assertThat(out).isEqualTo("iTALiAN;BDRip;XviD;TRL;MT;dvdRip;sub;ita;SNAPSHOT;executable");
		
	}
	
	@Test
	public void getBasePathTest(){
		
		String out = sut.getBasePath();
		assertThat(out).isNotNull();
		assertThat(out).isEqualTo("C:\\temp\\zzzdentRenamer");
		
	}
	
	@Test
	public void getYearLimitTest(){
		String out = sut.getYearLimit();
		assertThat(out).isNotNull();
		assertThat(out).isEqualTo("1970");
		
	}

}
