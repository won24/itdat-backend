package com.itdat.back.service.card;

import com.itdat.back.entity.card.BusinessCard;
import com.itdat.back.repository.card.BusinessCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicCardService {

    @Autowired
    private BusinessCardRepository businessCardRepository;

    // 공개된 명함 가져오기
    public List<BusinessCard> getAllPublicCards() {
        return businessCardRepository.findByIsPublicTrue();
    }

    // 공개 여부 업데이트
    public void updateCardVisibility(Integer cardId, boolean isPublic) {
        BusinessCard card = businessCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        card.setIsPublic(isPublic);
        businessCardRepository.save(card);
    }
}

