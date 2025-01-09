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
    /**
     * 이메일로 인증 코드 발송
     *
     * @param request 요청 바디(EmailVerificationRequest):
     *                - email: 인증 코드를 받을 이메일 주소
     * @return 성공 메시지(String): "인증 코드가 이메일로 발송되었습니다." 또는 오류 메시지
     * @throws HttpStatus.INTERNAL_SERVER_ERROR: 이메일 발송 실패
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailVerificationRequest request) {
        boolean isSent = emailService.sendVerificationCode(request.getEmail());
        if (isSent) {
            return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
        } else {
            return ResponseEntity.status(500).body("이메일 발송 실패.");
        }
    }

    /**
     * 이메일 인증 코드 검증
     *
     * @param request 요청 바디(EmailVerificationRequest):
     *                - email: 인증 코드가 발송된 이메일 주소
     *                - code: 사용자가 입력한 인증 코드
     * @return 인증 결과 메시지(String): "인증 성공" 또는 "인증 실패: 잘못된 코드입니다."
     * @throws HttpStatus.BAD_REQUEST: 잘못된 인증 코드
     */
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