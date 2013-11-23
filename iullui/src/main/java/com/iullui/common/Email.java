package com.iullui.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

/**
 * Email messages through AWS SES 
 * @author Yuval
 * @version 1.0
 */
@Component
public class Email {

	public static final String SYSTEM = "system";
	public static final String SUPPORT = "support@iullui.com";
	
	Environment environment;
	String conf;
	
	private AmazonSimpleEmailServiceClient ses;
	
	@Inject
	public Email(Environment environment) {
		this.environment = environment;
		this.conf = environment.getProperty(Util.APP_ENV) + ".";
        EnvironmentStringPBEConfig pbeConfig = new EnvironmentStringPBEConfig();
        pbeConfig.setPassword(this.environment.getProperty(Util.APP_TOKEN));
        
        StandardPBEStringEncryptor se = new StandardPBEStringEncryptor();
        se.setConfig(pbeConfig);
        
		AWSCredentials credentials = new BasicAWSCredentials(
				se.decrypt(this.environment.getProperty("common.mail.ses.access.key")), 
				se.decrypt(this.environment.getProperty("common.mail.ses.access.value")));
		
		this.ses = new AmazonSimpleEmailServiceClient(credentials);
	}
	
	public void sendExceptionLog(Throwable t) {
		String env = environment.getProperty(Util.APP_ENV);
		if (env.equals(Util.ENV_DEV)) return;
		
		String recipient = this.environment.getProperty("common.mail.recipient").replace("{env}", env);
		String sender = SYSTEM + " " + env + " <" + this.environment.getProperty("common.mail.sender") + ">";
		final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result, true);
	    t.printStackTrace(printWriter);
		this.doSend(sender, recipient, "Exception: " + t.getClass() + 
			", " + t.getMessage(), "<b>An exception occurred on " + new Date() + "</b><br/><br/>" + 
			result.toString().replace("\r", "<br/>").replace("\n", "<br/>")
			.replace("\t", "<br/>").replace("<br/><br/>", "<br/>"));
	}
	
	public void sendContact(String name, String email, String subject, String message) { 
		String env = environment.getProperty(Util.APP_ENV);
		String sender = SYSTEM + " " + env + " <" + this.environment.getProperty("common.mail.sender") + ">";
		String content = 
				"<br/>" + 
				"Name: " + name + "<br/>" + 
				"Email address: " + email + "<br/>" +
				"Subject: " + subject + "<br/>" +
				"Message: " + "<br/><br/>" + message.replace("\n", "<br/>").replace("\r", "<br/>") + 
				"<br/><br/>";
		this.doSend(sender, SUPPORT, subject, content);
	}

	/**
	 * Sends an email through Amazon SES
	 * @param from
	 * @param to
	 * @param subject
	 * @param content
	 */
	protected void doSend(String sender, String recipient, String subject, String content) {
		List<String> recipients = new LinkedList<String>();
		recipients.add(recipient);

		Destination destination = new Destination(recipients);
		
		Content subjectContent = new Content(subject).withCharset("utf-8");
		Content bodyContent = new Content(content).withCharset("utf-8");

		Body msgBody = new Body().withHtml(bodyContent);
		Message msg = new Message(subjectContent, msgBody);

		SendEmailRequest request = new SendEmailRequest(sender, destination, msg);
		
		try {
			this.ses.sendEmail(request);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}


}
