package com.itdat.back.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);  // 수신자 이메일
        message.setSubject(subject);  // 제목
        message.setText(text);  // 내용
        message.setFrom("noreply@www.itdat.store");  // 발신자 이메일 (네이버 웍스 계정)

        mailSender.send(message);
    }
}
