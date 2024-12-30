package com.itdat.back.service.card;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.card.BusinessCard;
import com.itdat.back.entity.card.Template;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.repository.card.BusinessCardRepository;
import com.itdat.back.repository.card.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class BusinessCardService {

    private static final String LOGO_DIRECTORY = "logo/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessCardRepository businessCardRepository;
    @Autowired
    private TemplateRepository templateRepository;

    // 유저 정보 가져오기
    public User findByUserId(String userId) {
        User user = userRepository.findByUserId(userId);
        if(Objects.isNull(user)){
            return null;
        }
        return user;
    }

    // 앱 - 명함 생성 후 저장
    public BusinessCard saveBusinessCard(String userId, BusinessCard card) {
        card.setUserId(userId);
        return businessCardRepository.save(card);
    }


    // 로고 저장
    public String saveLogoFile(MultipartFile logo) throws IOException {
        // 파일 저장 경로 생성
        String fileName = UUID.randomUUID() + "_" + logo.getOriginalFilename();
        Path filePath = Paths.get(LOGO_DIRECTORY, fileName);

        // 디렉토리가 없으면 생성
        Files.createDirectories(filePath.getParent());

        // 파일 저장
        Files.write(filePath, logo.getBytes());

        return "/uploads/" + fileName;
    }




    public List<BusinessCard> getBusinessCardsByUserId(String userId) {
        return businessCardRepository.findByUserId(userId);
    }

}