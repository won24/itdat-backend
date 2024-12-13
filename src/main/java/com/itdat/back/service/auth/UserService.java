package com.itdat.back.service.auth;

import com.itdat.back.entity.auth.User;
import com.itdat.back.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NaverWorksEmailService emailService;

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

        String code = generateVerificationCode();
        verificationCodes.put(email, code);

        String subject = "ITDAT 인증코드입니다.";
        String text = "안녕하세요,\n\n아래 인증 코드를 이용해 회원가입을 진행해주시기 바랍니다.\n\n " + code +
                "\n\n감사합니다,\nITDAT Team";

        emailService.sendEmail(email, subject, text);

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
