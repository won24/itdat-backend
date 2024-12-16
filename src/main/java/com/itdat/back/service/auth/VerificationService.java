package com.itdat.back.service.auth;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class VerificationService {

    private final Map<String, String> verificationCodes = new HashMap<>();
    private final Random random = new Random();

    public String generateCode(String email) {
        String code = String.format("%06d", random.nextInt(999999)); // 6자리 숫자 코드 생성
        verificationCodes.put(email, code); // 이메일과 함께 코드 저장
        return code;
    }

    public boolean verifyCode(String email, String code) {
        return verificationCodes.containsKey(email) && verificationCodes.get(email).equals(code);
    }

    public void invalidateCode(String email) {
        verificationCodes.remove(email); // 인증 코드 제거
    }
}
