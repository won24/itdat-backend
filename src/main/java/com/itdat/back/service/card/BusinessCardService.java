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


    // 명함 생성 후 저장
    public BusinessCard saveBusinessCard(Map<String, String> userInfo, int templateId, String logoUrl, String userId) {

        // 템플릿 ID로 템플릿 조회
        Template template = templateRepository.findById(templateId);


        // 명함 엔티티 생성
        BusinessCard businessCard = new BusinessCard();
        businessCard.setUserId(userId);
        businessCard.setTemplate(template);
        businessCard.setUserName(userInfo.get("name"));
        businessCard.setPhone(userInfo.get("phone"));
        businessCard.setEmail(userInfo.get("email"));
        businessCard.setCompanyName(userInfo.get("companyName"));
        businessCard.setCompanyNumber(userInfo.get("companyNumber"));
        businessCard.setCompanyAddress(userInfo.get("companyAddress"));
        businessCard.setCompanyFax(userInfo.get("companyFax"));
        businessCard.setDepartment(userInfo.get("department"));
        businessCard.setPosition(userInfo.get("position"));
        businessCard.setLogoUrl(logoUrl);

        // 데이터베이스에 저장
        return businessCardRepository.save(businessCard);
    }


    public List<BusinessCard> getBusinessCardsByUserId(String userId) {
        return businessCardRepository.findByUserId(userId);
    }

}
