package com.example.pensionat.Security;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class EmailService {

    @Autowired
    JavaMailSender mailSender;

    public void sendResetPasswordEmail(String to, String token) {
        String subject = "Reset Password";
        String text = "To reset your password, click the link below:\n" +
                "http://localhost:8080/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("noreply@example.com");

        mailSender.send(message);
    }



}
