package com.itdat.back.service.card;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.card.BusinessCard;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.repository.card.BusinessCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Service
public class BusinessCardService {

    private final String UPLOAD_DIR = "uploads/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessCardRepository businessCardRepository;

    // 유저 정보 가져오기
    public User findByUserEmail(String userEmail) {

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUserEmail(userEmail));

        if (!optionalUser.isPresent()) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        User user = optionalUser.get();
        return user;

    }

    // 앱 - 명함 저장
    public BusinessCard saveBusinessCard(BusinessCard card) {
        if (card.getUserEmail() == null) {
            throw new IllegalArgumentException("명함에 연결된 유효한 사용자가 필요합니다.");
        }

        // 명함 저장
        return businessCardRepository.save(card);
    }

    // 앱 - 명함 뒷면 저장
    public BusinessCard saveBusinessCardWithLogo(MultipartFile logo, BusinessCard card) throws IOException {

        if (logo != null && !logo.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + logo.getOriginalFilename();
            String filePath = UPLOAD_DIR + fileName;

            File dest = new File(filePath);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            logo.transferTo(dest);

            card.setLogoPath(filePath);
        }

        return businessCardRepository.save(card);
    }


    // 명함 가져오기
    public List<BusinessCard> getBusinessCardsByUserEmail(String userEmail) {
        return businessCardRepository.findByUserEmail(userEmail);
    }


}