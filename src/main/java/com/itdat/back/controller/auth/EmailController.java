package com.itdat.back.controller.auth;

import com.itdat.back.entity.auth.EmailVerificationRequest;
import com.itdat.back.service.auth.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailVerificationRequest request) {
        boolean isSent = emailService.sendVerificationCode(request.getEmail());
        if (isSent) {
            return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
        } else {
            return ResponseEntity.status(500).body("이메일 발송 실패.");
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody EmailVerificationRequest request) {
        boolean isValid = emailService.verifyCode(request.getEmail(), request.getCode());
        if (isValid) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.status(400).body("인증 실패: 잘못된 코드입니다.");
        }
    }
}