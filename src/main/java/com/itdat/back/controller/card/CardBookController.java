//package com.itdat.back.controller.card;
//
//import com.itdat.back.entity.card.BusinessCard;
//import com.itdat.back.entity.nfc.NfcEntity;
//import com.itdat.back.service.card.CardBookService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.nio.file.Paths;
//import java.util.List;
//
//
//@RestController
//@RequestMapping("/card/book")
//@CrossOrigin
//public class CardBookController {
//
//    @Autowired
//    private CardBookService cardBookService;
//
//    @Value("${file.upload-dir}")
//    private String uploadDir;
//
//    // 내 이메일(myEmail)을 제외한 명함 가져오기
//    @GetMapping("/others/{myEmail}")
//    public ResponseEntity<List<BusinessCard>> getOtherBusinessCards(@PathVariable String myEmail) {
//        try {
//            List<NfcEntity> nfcEntities = cardBookService.getOtherBusinessCards(myEmail);
//
//            // NfcEntity -> BusinessCard 변환
//            List<BusinessCard> otherCards = nfcEntities.stream().map(nfc -> {
//                BusinessCard card = new BusinessCard();
//                card.setUserEmail(nfc.getUserEmail());
//                card.setCardNo(nfc.getCardNo());
//                card.setDescription(nfc.getDescription());
//                card.setLogoPath(nfc.getLogoPath() != null
//                        ? "/uploads/" + Paths.get(nfc.getLogoPath()).getFileName()
//                        : null);
//                return card;
//            }).toList();
//
//            return ResponseEntity.ok(otherCards);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//}
