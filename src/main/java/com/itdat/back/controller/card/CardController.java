package com.itdat.back.controller.card;

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
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/card")
public class CardController {

    @Autowired
    private BusinessCardService businessCardService;

    @Autowired
    private TemplateService templateService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 유저 정보 가져오기
    @GetMapping("/userinfo/{userId}")
    public ResponseEntity<?> userInfo(@PathVariable("userId") String userId) {

        try {
            User user = businessCardService.findByUserId(userId);
            if (user != null) {
                return ResponseEntity.ok(user);
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 유저");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에서 오류가 발생");
        }
    }


    // 템플릿 가져오기
    @GetMapping("/templates")
    public ResponseEntity<?> getAllTemplates() {

        List<Template> templates = templateService.getAllTemplates();

        if (!templates.isEmpty()) {
            System.out.println("템플릿" + templates);
            return ResponseEntity.ok(templates);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
    }


    // 명함 저장
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveBusinessCard(
            @RequestPart("info") Map<String, String> userInfo,
            @RequestPart("templateId") int templateId,
            @RequestPart("logo") MultipartFile logo,
            @RequestPart("userId") String userId) {

        try {
            // 로고 파일 저장
            String logoUrl = businessCardService.saveLogoFile(logo);

            // 명함 데이터 저장
            BusinessCard businessCard = businessCardService.saveBusinessCard(userInfo, templateId, logoUrl, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("cardId", businessCard.getCardId());
            response.put("svgUrl", businessCard.getTemplate().getSvgUrl());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("명함 저장 error", e.getMessage()));
        }
    }



    // 사용자 명함 가져오기
    @GetMapping("/{userId}")
    public List<BusinessCard> getBusinessCardsByUserId(@PathVariable String userId) {
        return businessCardService.getBusinessCardsByUserId(userId);
    }


    // 새 템플릿 저장
    @PostMapping("/upload")
    public String uploadTemplate(@RequestParam("svgFile") MultipartFile file) throws IOException {
        // 파일 이름 생성
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        // 파일 저장 경로
        File filePath = Paths.get(uploadDir, fileName).toFile();
        file.transferTo(filePath);
        // 저장된 파일의 URL 반환
        return "http://localhost:8082/template/" + fileName;
    }


}
