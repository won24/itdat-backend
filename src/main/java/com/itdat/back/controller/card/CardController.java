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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public ResponseEntity<List<Template>> getTemplates() {
        List<Template> templates = templateService.getAllTemplates();
        System.out.println("모든 템플릿 가져오기 api 호출");
        return ResponseEntity.ok(templates);
    }


//    // 명함 저장
//    @PostMapping("/save")
//    public ResponseEntity<Map<String, Object>> saveBusinessCard(
//            @RequestPart("info") Map<String, String> userInfo,
//            @RequestPart("templateId") int templateId,
//            @RequestPart("logo") MultipartFile logo,
//            @RequestPart("userId") String userId) {
//
//        try {
//            // 로고 파일 저장
//            String logoUrl = businessCardService.saveLogoFile(logo);
//
//            // 명함 데이터 저장
//            BusinessCard businessCard = businessCardService.saveBusinessCard(userInfo, templateId, logoUrl, userId);
//
//            String svgUrl = businessCard.getTemplate() != null ? businessCard.getTemplate().getSvgUrl() : "템플릿 없음";
//            Map<String, Object> response = new HashMap<>();
//            response.put("svgUrl", svgUrl);
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("명함 저장 error", e.getMessage()));
//        }
//    }

    @GetMapping("/save")
    public ResponseEntity<Map<String, String>> saveBusinessCard(
                @RequestPart("info") Map<String, String> userInfo,
                @RequestPart("templateId") int templateId,
                @RequestPart("logo") MultipartFile logo,
                @RequestPart("userId") String userId
    ) throws IOException {

        // 1. 템플릿 파일 읽기
        String svgTemplatePath = "src/main/resources/templates/"+templateId;
        String svgTemplate = new String(Files.readAllBytes(Paths.get(svgTemplatePath)));

        // 2. 플레이스홀더 대체
        String svgContent = svgTemplate
                .replace("{name}", userInfo.get("name"))
                .replace("{phone}", userInfo.get("phone"))
                .replace("{email}", userInfo.get("email"));

        // 3. SVG 문자열 JSON으로 반환
        Map<String, String> response = new HashMap<>();
        response.put("svg", svgContent);
        return ResponseEntity.ok(response);
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