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
		
	}

}
