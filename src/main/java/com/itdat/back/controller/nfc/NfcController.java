package com.itdat.back.controller.nfc;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.nfc.NfcEntity;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.service.nfc.NfcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/nfc")
@CrossOrigin
public class NfcController {

    @Autowired
    private NfcService nfcService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/save")
    public ResponseEntity<?> processCardInfo(@RequestBody(required = false) Map<String, String> cardInfo) {
        try {
            System.out.println("테스트");
            String userEmail = cardInfo.get("userEmail");
            int cardNo = Integer.parseInt(cardInfo.get("CardNo"));
            String myEmail = cardInfo.get("myEmail");
            System.out.println(userEmail+" "+cardNo);

            MyWallet nfcEntity = nfcService.saveNfcInfo(userEmail, cardNo,myEmail);

            return ResponseEntity.ok("카드 정보가 성공적으로 처리되었습니다. ID: " + nfcEntity.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("카드 정보 처리 중 오류 발생: " + e.getMessage());
        }
    }


        @PostMapping("/userinfo")
        public ResponseEntity<?> getUserInfo(@RequestBody Map<String, String> payload) {
            String email = payload.get("email");
            User user = userRepository.findByUserEmail(email);
            return ResponseEntity.ok(user);
        }
        @PostMapping("/updateuser")
        public ResponseEntity<?> updateUserInfo(@RequestBody Map<String, String> userInfo) {
        System.out.println("왜 오질 않는거여");
        try {
            String email = userInfo.get("email");
            String userName = userInfo.get("userName");
            String userPhone = userInfo.get("userPhone");
            User user = userRepository.findByUserEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found with email: " + email);
            }
            user.setUserName(userName);
            user.setUserPhone(userPhone);
            userRepository.save(user);
            System.out.println(user);
            return ResponseEntity.ok("User information updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating user information: " + e.getMessage());
        }
    }
}