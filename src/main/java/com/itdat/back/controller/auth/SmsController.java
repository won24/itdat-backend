package com.itdat.back.controller.auth;

import com.itdat.back.entity.auth.SmsRequest;
import okhttp3.OkHttpClient;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    private static final Map<String, String> verificationCodes = new HashMap<>();
    private static final Random random = new Random();

    private static final String ALIGO_API_URL = "https://apis.aligo.in/send/";
    private static final String ALIGO_API_KEY = "3ljrbdvl034qijbcaads1bv6zq995ndv";
    private static final String ALIGO_USER_ID = "zzsehdrb";
    private static final String ALIGO_SENDER = "01068164788";

    /**
     * 핸드폰 본인인증
     *
     * @param SmsRequest 요청 바디:
     *
     * @return JWT 토큰(Map 형식):
     *                     - key: 사용자의 이메일
     *                     - user_id: 사용자의 비밀번호
     *                     - sender: 사용자의 비밀번호
     *                     - receiver: 사용자의 비밀번호
     *                     - receiver: 사용자의 비밀번호
     * @throws HttpStatus.UNAUTHORIZED: 인증 실패 시 오류 메시지 반환
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendSms(@RequestBody SmsRequest request) {
        try {
            String verificationCode = generateVerificationCode();
            verificationCodes.put(request.getPhoneNumber(), verificationCode);

            if (request.getMessage() == null || request.getMessage().isEmpty()) {
                request.setMessage("ITDAT 인증번호는 " + verificationCode + " 입니다.");
            }

            OkHttpClient client = new OkHttpClient();

            System.out.println("key: " + ALIGO_API_KEY);
            System.out.println("user_id: " + ALIGO_USER_ID);
            System.out.println("sender: " + ALIGO_SENDER);
            System.out.println("receiver: " + request.getPhoneNumber());
            System.out.println("msg: " + request.getMessage());

            FormBody formBody = new FormBody.Builder()
                    .add("key", ALIGO_API_KEY)
                    .add("user_id", ALIGO_USER_ID)
                    .add("sender", ALIGO_SENDER)
                    .add("receiver", request.getPhoneNumber()) // 디버깅
                    .add("msg", request.getMessage()) // 디버깅
                    .build();


            Request httpRequest = new Request.Builder()
                    .url(ALIGO_API_URL)
                    .post(formBody)
                    .build();

            Response response = client.newCall(httpRequest).execute();
            String responseBody = response.body().string();

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("문자 발송 실패: " + e.getMessage());
        }
    }

    /**
     * 인증 코드 검증
     *
     * @param request 요청 바디:
     *                - phoneNumber: 인증받을 사용자의 핸드폰 번호
     *                - code: 사용자가 입력한 인증 코드
     *
     * @return 응답 메시지(String 형식):
     *                     - 성공 시: "인증 성공"
     *                     - 실패 시: "인증 실패"
     * @throws HttpStatus.BAD_REQUEST: 인증 실패 시 오류 메시지 반환
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String inputCode = request.get("code");

        String storedCode = verificationCodes.get(phoneNumber);

        if (storedCode != null && storedCode.equals(inputCode)) {
            verificationCodes.remove(phoneNumber);
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 실패");
        }
    }

    /**
     * 인증 코드 생성
     *
     * @return 6자리 인증번호(String 형식):
     *                     - 랜덤으로 생성된 숫자 인증 코드
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 인증번호 생성
        return String.valueOf(code);
    }
}
