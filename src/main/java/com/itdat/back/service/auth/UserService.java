package com.itdat.back.service.auth;

import com.itdat.back.entity.auth.Role;
import com.itdat.back.entity.auth.SocialLogin;
import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.card.BusinessCard;
import com.itdat.back.repository.auth.SocialLoginRepository;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.repository.card.BusinessCardRepository;
import com.itdat.back.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SocialLoginRepository socialLoginRepository;
    private final PasswordEncoder passwordEncoder;
    private final NaverWorksEmailService emailService;
    private final JwtTokenUtil jwtTokenUtil;

    private final Map<String, VerificationCode> verificationCodes = new HashMap<>(); // 인증 코드와 만료 시간 관리

    @Autowired
    public UserService(UserRepository userRepository,
                       SocialLoginRepository socialLoginRepository,
                       PasswordEncoder passwordEncoder,
                       NaverWorksEmailService emailService,
                       JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.socialLoginRepository = socialLoginRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public String login(String identifier, String password) {
        Optional<User> optionalUser;

        // 이메일인지 확인
        if (identifier.contains("@")) {
            optionalUser = Optional.ofNullable(userRepository.findByUserEmail(identifier));
        } else {
            // 이메일이 아니라면 아이디로 간주
            optionalUser = Optional.ofNullable(userRepository.findByUserId(identifier));
        }

        if (!optionalUser.isPresent()) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        User user = optionalUser.get();

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 생성 및 반환
        return jwtTokenUtil.generateToken(user);
    }


    public User getUserByEmail(String email) {
        return userRepository.findByUserEmail(email);
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

    public boolean findByUserEmailPassword(String email, String password) {
        User user = userRepository.findByUserEmail(email);
        return passwordEncoder.matches(password, user.getPassword());

    }

    public boolean findByUserPasswordchange(String email, String password) {
        User user = userRepository.findByUserEmail(email);
        System.out.println(password);
        user.setPassword(passwordEncoder.encode(password));
        System.out.println(passwordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }

    public boolean deleteAccount(String email) {
        User user = userRepository.findByUserEmail(email);
        userRepository.delete(user);
        return true;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByUserEmail(email);
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
