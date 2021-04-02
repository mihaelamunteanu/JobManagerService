package com.interview.jobsmanager.jobs;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.interview.jobsmanager.Job;

/**
 * A job for sending emails. 
 * 
 * @author Mihaela Munteanu
 * 
 */
public class SendMailJob extends Job {
	private String mail;
	private String pass;
	private String title;
	private String contentText;
	private String toMail;
	
	public SendMailJob() {
		super();
	}
	
	public SendMailJob(JobPriority jobPriority, String mail, String pass, String title, String contentText, String toMail) {
		super(jobPriority);
		this.mail = mail;
		this.pass = pass;
		this.title = title; 
		this.contentText = contentText;
		this.toMail = toMail;
	}
	
	public SendMailJob(String jobName, JobPriority jobPriority, String mail, String pass, String title, String contentText, String toMail) {
		super(jobName, jobPriority);
		this.mail = mail;
		this.pass = pass;
		this.title = title; 
		this.contentText = contentText;
		this.toMail = toMail;
	}

	@Override
	public void jobAction() throws Exception {
		Properties properties = System.getProperties();
		
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(mail, pass); 
            }
        });	

//        // Create a default MimeMessage object.
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mail));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toMail));
        message.setSubject(title);
        message.setText(contentText); //"text/html";
        System.out.println("Sending message " + this.title); 
        Transport.send(message); 
        
        System.out.println("The job executed is for SEDING MAIL: " + this.title);
//        for(int i = 1; i<=100;i++) { 
//        	System.out.println(" Send Mail " + i);
//        	Thread.sleep((int)(Math.random()*10));
//        }        
	}
	
	@Override
	public void revertModifications() {
		
	}
}
