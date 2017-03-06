package com.daghosoft.dent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class ConfigServiceImpl implements ConfigService {
	
	private Properties pro;
	
	public ConfigServiceImpl() {
		URL config = this.getClass().getResource("/config.properties");
		if(config!=null){
			try {
				System.out.println(config.getPath());
				
				File fconfig = new File(config.getPath());
				System.out.println(fconfig.getAbsolutePath());
				
				FileInputStream fis = new FileInputStream(fconfig);
				pro = new Properties();
				pro.load(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("Errore recupero file di configurazione config.properties");
		}
	}

	public Properties getPropertySet() {
		return pro;
	}

}
