package com.itdat.back.controller.nfc;

import com.itdat.back.entity.nfc.MyWallet;
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

}
