package com.itdat.back.controller.card;

import com.itdat.back.entity.card.BusinessCard;
import com.itdat.back.service.card.PublicCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/card/public")
@CrossOrigin
public class PublicCardController {

    @Autowired
    private PublicCardService publicCardService;

    // 공개된 명함 가져오기
    @GetMapping("/all")
    public ResponseEntity<List<BusinessCard>> getAllPublicCards() {
        try {
            List<BusinessCard> publicCards = publicCardService.getAllPublicCards();
            return ResponseEntity.ok(publicCards);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 공개 여부 업데이트
    @PostMapping("/update")
    public ResponseEntity<String> updateCardVisibility(@RequestBody Map<String, Object> request) {
        try {
            Integer cardId = (Integer) request.get("cardId");
            boolean isPublic = (boolean) request.get("isPublic");

            publicCardService.updateCardVisibility(cardId, isPublic);
            return ResponseEntity.ok("Update successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update failed");
        }
    }
}
