package com.iullui.config;

import javax.inject.Inject;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.iullui.common.Util;

@Configuration
public class SecurityConfig {

	public static final String CONFIG_FILE_ENCRYPTOR = "configFileEncryptor";
	
	@Inject
	private Environment environment;
	
    @Bean(name = CONFIG_FILE_ENCRYPTOR)
    @Autowired
    public StringEncryptor configurationEncryptor() {
        StandardPBEStringEncryptor se = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig pbeConfig = new EnvironmentStringPBEConfig();
        pbeConfig.setPassword(this.environment.getProperty(Util.APP_TOKEN));
        se.setConfig(pbeConfig);
        return se;
    }

}
