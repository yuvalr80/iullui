package com.iullui.config;

import static com.rosaloves.bitlyj.Bitly.as;

import javax.inject.Inject;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.iullui.common.Util;
import com.rosaloves.bitlyj.Bitly.Provider;


@Configuration
public class ExtConfig {

	@Inject
	private Environment environment;

	@Inject 
	private StringEncryptor stringEncryptor;

	public @Bean Provider bitly() {
		String conf = environment.getProperty(Util.APP_ENV) + ".";
		return as(environment.getProperty(conf + "bitly.user"), 
				stringEncryptor.decrypt(environment.getProperty(conf + "bitly.apiKey")));
	}
	
}
