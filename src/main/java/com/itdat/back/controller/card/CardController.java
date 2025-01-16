package com.itdat.back.controller.card;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.card.BusinessCard;
import com.itdat.back.service.card.BusinessCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;


@RestController
@RequestMapping("/card")
@CrossOrigin
public class CardController {

    @Autowired
    private BusinessCardService businessCardService;
    


    @GetMapping("/userinfo/{userEmail}")
    public ResponseEntity<?> userInfo(@PathVariable("userEmail") String userEmail) {
        try {
            User user = businessCardService.findByUserEmail(userEmail);
            if (user != null) {
                return ResponseEntity.ok(user);
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 유저");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에서 오류가 발생");
        }
    }


    @GetMapping("/{userEmail}")
    public ResponseEntity<List<BusinessCard>> getBusinessCardsByUserEmail(@PathVariable String userEmail) {
        try {
            List<BusinessCard> cards = businessCardService.getBusinessCardsByUserEmail(userEmail);
            cards.forEach(card -> {
                if (card.getLogoUrl() != null) {
                    card.setLogoUrl("/uploads/logos/" + Paths.get(card.getLogoUrl()).getFileName());
                }
            });
            System.out.println("클라데이터"+cards);
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/save")
    public ResponseEntity<?> saveBusinessCard(@RequestBody BusinessCard card) {
        try {
            User user = businessCardService.findByUserEmail(card.getUserEmail());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유효하지 않은 사용자 이메일입니다.");
            }

            BusinessCard savedCard = businessCardService.saveBusinessCard(card);

            return ResponseEntity.ok(savedCard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("명함 저장 중 오류가 발생했습니다.");
        }
    }


    @PostMapping("/save/logo")
    public ResponseEntity<String> saveBusinessCardWithLogo(
            @RequestPart("cardInfo") String cardInfoJson,
            @RequestPart(value = "logo", required = false) MultipartFile logo
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            BusinessCard businessCard;
            try {
                businessCard = objectMapper.readValue(cardInfoJson, BusinessCard.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body("Invalid cardInfo JSON");
            }

            try {
                validateFile(logo);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }


            if (!logo.isEmpty()) {
                String logoPath = saveFile(logo);
                businessCard.setLogoUrl(logoPath);
            }

            businessCardService.saveBusinessCardWithLogo(businessCard);

            return ResponseEntity.ok("명함 저장 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("명함 저장 실패: " + e.getMessage());
        }
    }

    private String saveFile(MultipartFile file) {
        try {
            String upload_dir = "C:/uploads/logos";
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(upload_dir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("이미지 경로:" + filePath);
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        String contentType = file.getContentType();
        List<String> allowedMimeTypes = Arrays.asList("image/png", "image/jpeg", "image/gif");

        if (!allowedMimeTypes.contains(contentType)) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다: " + contentType);
        }

        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 크기가 5MB를 초과했습니다.");
        }
    }


    @PostMapping("/publicstatus")
    public ResponseEntity<?> updateCardPublicStatus(@RequestBody List<Map<String, Object>> cardData) {
        try {
            businessCardService.updateCardPublicStatus(cardData);
            return ResponseEntity.ok("명함 공개 상태가 성공적으로 업데이트되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/front/update")
    public ResponseEntity<?> updateBusinessCard(@RequestBody BusinessCard card) {
        try {
            BusinessCard updatedCard = businessCardService.updateBusinessCard(card);
            if (updatedCard != null) {
                return ResponseEntity.ok(updatedCard);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("명함 업데이트에 실패했습니다. 카드 면이 FRONT인지 확인해주세요.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("명함 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteCard(@RequestBody Map<String, Object> request) {
        try {
            Integer cardNo = (Integer) request.get("cardNo");
            String userEmail = (String) request.get("userEmail");
            System.out.println("컨트롤러 :"+cardNo+userEmail);

            if (cardNo == null || userEmail == null) {
                return ResponseEntity.badRequest().body("cardNo와 userEmail은 필수 입력 항목입니다.");
            }

            boolean isDeleted = businessCardService.deleteOnlyCard(cardNo, userEmail);
            if (isDeleted) {
                return ResponseEntity.ok("명함이 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("삭제할 명함을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("명함 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}