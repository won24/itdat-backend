package com.itdat.back.service.card;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.card.BusinessCard;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.repository.card.BusinessCardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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

    @Transactional
    public boolean deleteCard(String email) {
        List<BusinessCard> cards = businessCardRepository.findByUserEmail(email);
        if (cards.isEmpty()) {
            return false;
        } else {
            businessCardRepository.deleteByUserEmail(email);
            return true;
        }
    }

    public void updateCardPublicStatus(List<Map<String, Object>> cardData) {
        System.out.println("서비스");
        for (Map<String, Object> card : cardData) {
            String userEmail = (String) card.get("userEmail");
            Integer cardNo = (Integer) card.get("cardNo");
            boolean isPublic = (boolean) card.get("isPublic");

            // userEmail을 기반으로 명함을 찾아 공개 상태 업데이트
            BusinessCard businessCard = businessCardRepository.findByCardNoAndUserEmail(cardNo, userEmail);
            if (businessCard != null) {
                businessCard.setIsPublic(isPublic);
                businessCardRepository.save(businessCard);
            } else {
                // 명함을 찾지 못한 경우 예외 처리
                throw new IllegalArgumentException("명함을 찾을 수 없습니다. 카드 번호: " + cardNo + ", 이메일: " + userEmail);
            }
        }
    }


    public BusinessCard updateBusinessCard(BusinessCard businessCard) {
        if ("FRONT".equals(businessCard.getCardSide())) {
            BusinessCard existingCard = businessCardRepository.findByCardNoAndUserEmail(
                    businessCard.getCardNo(), businessCard.getUserEmail());
            if (existingCard != null) {
                return businessCardRepository.save(existingCard);
            }
        }
        return null; // Return null if update fails or if cardSide is not FRONT
    }

    @Transactional
    public boolean deleteOnlyCard(Integer cardNo, String userEmail) {
        List<BusinessCard> cards = (List<BusinessCard>) businessCardRepository.findByCardNoAndUserEmail(cardNo, userEmail);
        if (!cards.isEmpty()) {
            businessCardRepository.deleteAll(cards);
            return true;
        } else {
            return false;
        }
    }
}