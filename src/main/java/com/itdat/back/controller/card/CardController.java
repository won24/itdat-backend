package com.itdat.back.controller.card;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.card.BusinessCard;
import com.itdat.back.entity.card.Template;
import com.itdat.back.service.card.BusinessCardService;
import com.itdat.back.service.card.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    @Autowired
    private TemplateService templateService;

    @Value("${file.upload-dir}")
    private String uploadDir;


    // 유저 정보 가져오기
    @GetMapping("/userinfo/{userEmail}")
    public ResponseEntity<?> userInfo(@PathVariable("userEmail") String userEmail) {
        System.out.println("유저정보가져오기");
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

    // 사용자 명함 가져오기
    @GetMapping("/{userEmail}")
    public ResponseEntity<List<BusinessCard>> getBusinessCardsByUserEmail(@PathVariable String userEmail) {
        try {
            List<BusinessCard> cards = businessCardService.getBusinessCardsByUserEmail(userEmail);
            cards.forEach(card -> {
                if (card.getLogoPath() != null) {
                    card.setLogoPath("/uploads/" + Paths.get(card.getLogoPath()).getFileName());
                }
            });
            System.out.println("클라데이터"+cards);
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    // 앱 - 명함 생성
    @PostMapping("/save")
    public ResponseEntity<?> saveBusinessCard(@RequestBody BusinessCard card) {
        try {
            // 유저 이메일 확인
            User user = businessCardService.findByUserEmail(card.getUserEmail());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유효하지 않은 사용자 이메일입니다.");
            }

            // 명함 저장
            BusinessCard savedCard = businessCardService.saveBusinessCard(card);
            return ResponseEntity.ok(savedCard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("명함 저장 중 오류가 발생했습니다.");
        }
    }

    // 앱 - 명함 뒷면 저장
    @PostMapping("/save/logo")
    public ResponseEntity<String> saveBusinessCardWithLogo(
            @RequestPart("cardInfo") String cardInfoJson,
            @RequestPart(value = "logo", required = false) MultipartFile logo
    ) {
        try {
            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            BusinessCard businessCard;
            try {
                businessCard = objectMapper.readValue(cardInfoJson, BusinessCard.class);
            } catch (JsonProcessingException e) {
                return ResponseEntity.badRequest().body("Invalid cardInfo JSON");
            }

            try {
                // 파일 형식 및 크기 검증
                validateFile(logo);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }

            // 로고 파일 저장
            if (logo != null && !logo.isEmpty()) {
                String logoPath = saveFile(logo);
                businessCard.setLogoPath(logoPath);
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
            String upload_dir = "/uploads/logos";
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(upload_dir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

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

        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 크기가 5MB를 초과했습니다.");
        }
    }



    // 템플릿 가져오기
    @GetMapping("/templates")
    public ResponseEntity<List<Template>> getTemplates() {
        try {
            List<Template> templates = templateService.getAllTemplates();
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/public")
    public ResponseEntity<String> updateCardPublicStatus(@RequestBody List<Map<String, Object>> cardData) {
        try {
            System.out.println("컨트롤");
            System.out.println(cardData);
            businessCardService.updateCardPublicStatus(cardData);
            return ResponseEntity.ok("명함 공개 상태가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("명함 공개 상태 변경에 실패했습니다: " + e.getMessage());
        }
    }
    @PutMapping("/front/update")
    public ResponseEntity<BusinessCard> updateBusinessCard(@RequestBody BusinessCard businessCard) {
        System.out.println("카드업뎃정보"+businessCard);
        try {
            BusinessCard updatedCard = businessCardService.updateBusinessCard(businessCard);
            return ResponseEntity.ok(updatedCard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteCard(@RequestBody Map<String, Object> request) {
        Integer cardNo = (Integer) request.get("cardNo");
        String userEmail = (String) request.get("userEmail");
        System.out.println(cardNo+userEmail);
        if (cardNo == null || userEmail == null) {
            return ResponseEntity.badRequest().body("카드 번호와 사용자 이메일은 필수입니다.");
        }
        try {
            boolean deleted = businessCardService.deleteOnlyCard(cardNo, userEmail);
            if (deleted) {
                return ResponseEntity.ok().body("명함이 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("명함 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


}