package com.coderacer.service;

import com.coderacer.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(Account account, String token) {
        String url = "http://localhost:8000/api/accounts/verify?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getEmail());
        message.setSubject("Email Verification");
        message.setText("Click the link to verify your account: " + url);
        mailSender.send(message);
    }
}