package com.itdat.back.service.auth;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Map<String, VerificationCode> verificationCodes = new HashMap<>();

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendVerificationCode(String email) {
        try {
            String code = generateVerificationCode();
            verificationCodes.put(email, new VerificationCode(code, LocalDateTime.now().plusMinutes(5))); // 5분 유효기간 설정

            sendEmail(email, "ITDAT 인증 코드", generateEmailContent(code));
//            System.out.println("인증 코드 [" + code + "] 이메일 [" + email + "] 로 발송 완료.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("이메일 발송 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyCode(String email, String code) {
        VerificationCode storedCode = verificationCodes.get(email);

        // 인증 코드 유효성 검증
        if (storedCode == null || storedCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            verificationCodes.remove(email); // 만료된 코드 삭제
            return false;
        }

        if (storedCode.getCode().equals(code)) {
            verificationCodes.remove(email); // 인증 성공 시 삭제
            return true;
        }

        return false;
    }

    private String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000); // 6자리 숫자
    }

    private String generateEmailContent(String code) {
        return "안녕하세요,\n\n아래 인증 코드를 이용해 회원가입을 진행해주시기 바랍니다.\n\n" +
                code + "\n\n인증 코드는 5분 동안 유효합니다.\n감사합니다,\nITDAT Team";
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    // 주기적으로 만료된 인증 코드 삭제
    @Scheduled(fixedRate = 60000) // 1분 간격으로 실행
    public void removeExpiredCodes() {
        Iterator<Map.Entry<String, VerificationCode>> iterator = verificationCodes.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, VerificationCode> entry = iterator.next();
            if (entry.getValue().getExpiryTime().isBefore(LocalDateTime.now())) {
                iterator.remove();
//                System.out.println("만료된 인증 코드 삭제: " + entry.getKey());
            }
        }
    }

    private static class VerificationCode {
        private final String code;
        private final LocalDateTime expiryTime;

        public VerificationCode(String code, LocalDateTime expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }

        public String getCode() {
            return code;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }
    }
}
