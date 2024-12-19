package com.itdat.back.service.auth;

import com.itdat.back.entity.auth.User;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NaverWorksEmailService emailService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private final Map<String, VerificationCode> verificationCodes = new HashMap<>(); // 인증 코드와 만료 시간 관리

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, NaverWorksEmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public String login(String email, String password) {
        // 이메일로 사용자 찾기
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUserEmail(email));

        if (!optionalUser.isPresent()) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        User user = optionalUser.get();

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 생성 및 반환
        String token = jwtTokenUtil.generateToken(user.getUserEmail());
        return token;
    }



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
        // 이메일 중복 여부 확인
        if (!isUserEmailAvailable(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 인증 코드 생성
        String code = generateVerificationCode();
        verificationCodes.put(email, new VerificationCode(code, LocalDateTime.now().plusMinutes(5))); // 5분 만료

        // 이메일 내용 생성
        String subject = "ITDAT 인증코드입니다.";
        String text = generateEmailContent(code);

        // 이메일 발송
        emailService.sendEmail(email, subject, text);

        return true;
    }

    public boolean verifyCode(String email, String code) {
        if (!verificationCodes.containsKey(email)) {
            return false;
        }

        VerificationCode storedCode = verificationCodes.get(email);

        // 만료 시간 확인
        if (storedCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            verificationCodes.remove(email); // 만료된 코드 삭제
            return false;
        }

        boolean isValid = storedCode.getCode().equals(code);
        if (isValid) {
            verificationCodes.remove(email); // 인증 성공 시 삭제
        }
        return isValid;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자
        return String.valueOf(code);
    }

    private String generateEmailContent(String code) {
        return "안녕하세요,\n\n아래 인증 코드를 이용해 회원가입을 진행해주시기 바랍니다.\n\n" +
                code + "\n\n감사합니다,\nITDAT Team";
    }

    // 내부 클래스: 인증 코드와 만료 시간 관리
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
