package com.itdat.back.service.auth;

import com.itdat.back.entity.auth.User;
import com.itdat.back.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 작성자 : 김동규 / 작성일 2024-12-12
 * 설명 : 사용자 회원가입 및 로그인 처리
 * */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private Map<String, String> verificationCodes = new HashMap<>();

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean isUserIdAvailable(String userId) {
        return userRepository.findByUserId(userId) == null;
    }

    public boolean isUserEmailAvailable(String userEmail) {
        return userRepository.findByUserEmail(userEmail) == null;
    }

    public boolean sendVerificationCode(String email) {
        if (!isUserEmailAvailable(email)) {
            return false;
        }

        // 인증 코드 생성
        String code = generateVerificationCode();
        verificationCodes.put(email, code);

        // 이메일 제목과 본문
        String subject = "ITDAT 인증코드 입니다.";
        String text = "안녕하세요,\n\n아래 인증 코드를 이용해 회원가입을 진행해주시기 바랍니다.\n\n " + code +
                "\n\n감사합니다,\nITDAT Team";

        emailService.sendMail(email, subject, text);

        return true;
    }


    public boolean verifyCode(String email, String code) {
        if (!verificationCodes.containsKey(email)) {
            return false;
        }

        String expectedCode = verificationCodes.get(email);
        boolean isValid = expectedCode.equals(code);

        if (isValid) {
            verificationCodes.remove(email);
        }

        return isValid;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
