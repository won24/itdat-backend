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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class BusinessCardService {


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

        return businessCardRepository.save(card);
    }


    // 앱 - 명함 뒷면 저장
    public BusinessCard saveBusinessCardWithLogo(BusinessCard card){
        return businessCardRepository.save(card);
    }


    // 명함 가져오기
    public List<BusinessCard> getBusinessCardsByUserEmail(String userEmail) {
        return businessCardRepository.findByUserEmail(userEmail);
    }

}