package com.example.behere.utils;

import java.util.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {



    private static final String USERNAME = "behere1664@gmail.com";
    private static final String PASSWORD = "BecauseILoveBeer1664";


    private Properties prepareProperties()
    {
        Properties props = new Properties();
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.port","587");
        return  props;
    }

    public void send(String recipientEmail, String lastName){

        Properties props = prepareProperties();

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });
        try
        {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));
            message.setSubject("Confirmation d'inscription");
            message.setText("Cher " + lastName + ", \n Merci de votre inscription a Be'here," +
                    "\n Buvez sans modération" );

            Transport.send(message);

        }catch(MessagingException e)
        {
            throw new RuntimeException(e);
        }


    }
}
