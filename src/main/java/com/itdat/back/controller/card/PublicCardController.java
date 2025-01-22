package com.itdat.back.controller.card;

import com.itdat.back.entity.card.BusinessCard;
import com.itdat.back.service.card.PublicCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 작성자 : 김동규
 *
 * 작성일 : 2024-01-03
 *
 * 명함 공개 페이지 게시 기능 로직 구현
 * */

@RestController
@RequestMapping("/card/public")
@CrossOrigin
public class PublicCardController {

    @Autowired
    private PublicCardService publicCardService;

    /**
     * 모든 공개된 명함 가져오기
     *
     * @return 공개된 명함 리스트(List<BusinessCard>):
     *         - 성공 시: 공개된 명함 목록
     *         - 실패 시: HTTP 500 상태 코드와 null 반환
     * @throws HttpStatus.INTERNAL_SERVER_ERROR: 명함 가져오기 실패
     */
    @GetMapping("/all")
    public ResponseEntity<List<BusinessCard>> getAllPublicCards() {
        try {
            List<BusinessCard> publicCards = publicCardService.getAllPublicCards();
            return ResponseEntity.ok(publicCards);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 명함 공개 여부 업데이트
     *
     * @param request 요청 바디(Map 형식):
     *                - cardId: 업데이트할 명함의 ID
     *                - isPublic: 공개 여부(true 또는 false)
     * @return 성공 메시지(String): "Update successful" 또는 오류 메시지
     * @throws HttpStatus.INTERNAL_SERVER_ERROR: 공개 여부 업데이트 실패
     */
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
