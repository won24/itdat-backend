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
import java.util.List;


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
    public List<BusinessCard> getBusinessCardsByUserId(@PathVariable String userEmail) {
        System.out.println("사용자명함 가져오기");
        return businessCardService.getBusinessCardsByUserEmail(userEmail);
    }


    // 앱 - 명함 생성
    @PostMapping("/save")
    public ResponseEntity<BusinessCard> saveBusinessCard(@RequestBody BusinessCard card){
        return ResponseEntity.ok(businessCardService.saveBusinessCard(card));
    }


    // 템플릿 가져오기
    @GetMapping("/templates")
    public ResponseEntity<List<Template>> getTemplates() {
        List<Template> templates = templateService.getAllTemplates();
        System.out.println("모든 템플릿 가져오기 api 호출");
        return ResponseEntity.ok(templates);
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