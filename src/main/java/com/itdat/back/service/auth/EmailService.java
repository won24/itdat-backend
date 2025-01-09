package com.itdat.back.service.auth;


import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {

    private final Map<String, String> verificationCodes = new HashMap<>();

    public boolean sendVerificationCode(String email) {
        try {
            String code = generateVerificationCode();
            verificationCodes.put(email, code);

            // 이메일 발송 로직 추가 (예: JavaMailSender)
//            System.out.println("인증 코드 [" + code + "] 이메일 [" + email + "] 로 발송 완료.");
            return true;
        } catch (Exception e) {
            System.err.println("이메일 발송 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        return storedCode != null && storedCode.equals(code);
    }

    private String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}