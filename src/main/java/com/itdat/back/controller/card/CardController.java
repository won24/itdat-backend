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
    @PostMapping("/save/back")
    public ResponseEntity<BusinessCard> saveBackSide(
            @RequestParam("logo") MultipartFile logo,
            @RequestParam("businessCard") BusinessCard card) {
        try {
            BusinessCard savedCard = businessCardService.saveBusinessCardWithLogo(logo, card);
            return ResponseEntity.ok(savedCard);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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


}