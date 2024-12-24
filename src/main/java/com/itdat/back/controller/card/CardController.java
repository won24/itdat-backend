package com.itdat.back.controller.card;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.card.Card;
import com.itdat.back.entity.card.Template;
import com.itdat.back.service.card.CardService;
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
import java.util.List;


@RestController
@RequestMapping("/card")
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private TemplateService templateService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 유저 정보 가져오기
    @GetMapping("/userinfo/{id}")
    public ResponseEntity<?> userInfo(@PathVariable("id") int id) {

        try {
            User user = cardService.findByUserId(id);
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


//    // 명함 저장
//    @PostMapping("/save")
//    public ResponseEntity<Card> createBusinessCard(
//            @RequestPart("info") Card card,
//            @RequestPart("logo") MultipartFile logoFile,
//            @RequestParam("templateId") int templateId) throws IOException {
//
//        // 템플릿 가져오기
//        Template template = templateService.getTemplateById(templateId);
//
//        // 로고 파일 저장
//        String logoPath = "./uploads/" + logoFile.getOriginalFilename();
//        logoFile.transferTo(Paths.get(logoPath));
//
//        // 명함 생성
//        Card savedCard = cardService.createBusinessCard(card, template, logoPath);
//
//        return ResponseEntity.ok(savedCard);
//    }
//
//
//    // 명함 가져오기
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getBusinessCard(@PathVariable int id) {
//        try {
//            Card card = cardService.findById(id);
//            if (card != null) {
//                return ResponseEntity.ok(card);
//            }else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 명함");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에서 오류가 발생");
//        }
//    }

    // 사용자 명함 저장
    @PostMapping("/save")
    public ResponseEntity<Card> createBusinessCard(@RequestBody Card card) {
        Card savedCard = cardService.createBusinessCard(card);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCard);
    }


    // 사용자 명함 불러오기
    @GetMapping("/{userId}")
        public ResponseEntity<List<Card>> getBusinessCardsByUserId(@PathVariable String userId) {
            List<Card> cards = cardService.getCardsByUserId(userId);
            return ResponseEntity.ok(cards);
        }


    // 템플릿 저장
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
