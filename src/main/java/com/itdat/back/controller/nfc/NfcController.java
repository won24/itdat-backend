package com.itdat.back.controller.nfc;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.nfc.MyWallet;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.service.auth.UserService;
import com.itdat.back.service.card.BusinessCardService;
import com.itdat.back.service.nfc.NfcService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/nfc")
@CrossOrigin
public class NfcController {

    @Autowired
    private NfcService nfcService;
    @Autowired
    private BusinessCardService businessCardService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public NfcController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

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
        User user = userService.findUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/updateuser")
    public ResponseEntity<?> updateUserInfo(@RequestBody Map<String, String> userInfo) {
        System.out.println(userInfo);
        try {
            String email = userInfo.get("email");
            User user = userRepository.findByUserEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found with email: " + email);
            }
            user.setUserName(userInfo.get("userName"));
            user.setUserPhone(userInfo.get("userPhone"));
            user.setCompany(userInfo.get("company"));
            user.setCompanyRank(userInfo.get("companyRank"));
            user.setCompanyDept(userInfo.get("companyDept"));
            user.setCompanyFax(userInfo.get("companyFax"));
            user.setCompanyAddr(userInfo.get("companyAddr"));
            user.setCompanyAddrDetail(userInfo.get("companyAddrDetail"));
            userRepository.save(user);
            return ResponseEntity.ok("User information updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating user information: " + e.getMessage());
        }
    }

    @PostMapping("/password")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("Email and password are required");
        }
        boolean isValid = userService.findByUserEmailPassword(email,password);

        Map<String, Boolean> response = Map.of("isValid", isValid);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/passwordchange")
    public ResponseEntity<?> passwordChange(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        System.out.println("새 비밀번호"+password);
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("Email and password are required");
        }
        boolean isValid = userService.findByUserPasswordchange(email,password);

        Map<String, Boolean> response = Map.of("isValid", isValid);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/deleteaccount")
    @Transactional
    public ResponseEntity<?> deleteAccount(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        try {
            boolean cardDeleted = businessCardService.deleteCard(email);
            boolean accountDeleted = userService.deleteAccount(email);

            if (accountDeleted) {
                if (cardDeleted) {
                    return ResponseEntity.ok().body("Account and associated cards deleted successfully");
                } else {
                    return ResponseEntity.ok().body("Account deleted, but no associated cards were found");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error deleting account: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to delete account: " + e.getMessage());
        }
    }

    @PostMapping("/mywallet/cardmemo")
    public ResponseEntity<?> saveCardMemo(@RequestBody Map<String, String> cardMemo) {
        System.out.println(cardMemo);
        try {
            nfcService.saveDescription(cardMemo);
            return ResponseEntity.ok("카드 메모가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("카드 메모 저장 중 오류 발생: " + e.getMessage());
        }
    }
    @PostMapping("/mywallet/loadmemo")
    public ResponseEntity<String> loadCardMemo(@RequestBody Map<String, String> cardMemo) {
        System.out.println(cardMemo);
        try {
            String memo = nfcService.loadDescription(cardMemo);
            return ResponseEntity.ok(memo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("카드 메모 저장 중 오류 발생: " + e.getMessage());
        }
    }
}