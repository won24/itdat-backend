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

    // 사용자 명함 가져오기
    @GetMapping("/{userEmail}")
    public ResponseEntity<List<BusinessCard>> getBusinessCardsByUserEmail(@PathVariable String userEmail) {
        try {
            List<BusinessCard> cards = businessCardService.getBusinessCardsByUserEmail(userEmail);
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
            User user = businessCardService.findByUserEmail(card.getUser().getUserEmail());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유효하지 않은 사용자 이메일입니다.");
            }

            // 명함 저장
            card.setUser(user);
            BusinessCard savedCard = businessCardService.saveBusinessCard(card);
            return ResponseEntity.ok(savedCard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("명함 저장 중 오류가 발생했습니다.");
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

    // 새 템플릿 저장
    @PostMapping("/upload")
    public ResponseEntity<?> uploadTemplate(@RequestParam("svgFile") MultipartFile file) {
        try {
            // 업로드 디렉토리 확인 및 생성
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            // 파일 이름 생성 및 저장
            String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            File filePath = Paths.get(uploadDir, fileName).toFile();
            file.transferTo(filePath);

            // URL 반환
            String fileUrl = "http://localhost:8082/template/" + fileName;
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 중 오류가 발생했습니다.");
        }
    }

}