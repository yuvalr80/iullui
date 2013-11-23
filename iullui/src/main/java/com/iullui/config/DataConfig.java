package com.iullui.config;

import javax.inject.Inject;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.core.MongoFactoryBean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.iullui.common.Util;
import com.mongodb.Mongo;

@Configuration
public class DataConfig {

	@Inject
	private Environment environment;

	@Inject 
	private StringEncryptor stringEncryptor;
	
	public @Bean MongoOperations mongoTemplate(Mongo mongo) {
		String conf = environment.getProperty(Util.APP_ENV) + ".";
		
		MongoTemplate mongoTemplate = new MongoTemplate(
			mongo, environment.getProperty(conf + "db.name"), 
				new UserCredentials(environment.getProperty(conf + "db.user"), 
					stringEncryptor.decrypt(environment.getProperty(conf + "db.pwd"))));
		
		return mongoTemplate;
	}

	/*
	 * Factory bean that creates the Mongo instance
	 */
	public @Bean MongoFactoryBean mongo() {
		MongoFactoryBean mongo = new MongoFactoryBean();
		String conf = environment.getProperty(Util.APP_ENV) + ".";
		mongo.setHost(environment.getProperty(conf + "db.host"));
		mongo.setPort(Integer.valueOf(environment.getProperty(conf + "db.port")));
		
		return mongo;
	}

	/*
	 * Use this post processor to translate any MongoExceptions thrown in @Repository annotated classes
	 */
	public @Bean PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

}
