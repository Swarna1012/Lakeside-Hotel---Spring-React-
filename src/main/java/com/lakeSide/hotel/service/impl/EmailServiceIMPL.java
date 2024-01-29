package com.lakeSide.hotel.service.impl;

import com.lakeSide.hotel.model.EmailDetails;
import com.lakeSide.hotel.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceIMPL implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public String sendSimpleEmail(EmailDetails details) {

        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setSubject(details.getSubject());
            mailMessage.setText(details.getMsgBody());

//            javaMailSender.send(mailMessage);
            return "Email Sent Successfully...";
        }catch (Exception e){
            return "Error while sending Email :( " + e.getMessage();
        }
    }
}
