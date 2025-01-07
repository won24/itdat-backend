package com.itdat.back.service.card;

import com.itdat.back.entity.card.BusinessCard;
import com.itdat.back.entity.nfc.NfcEntity;
import com.itdat.back.repository.card.CardBookRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardBookService {

    @Autowired
    private CardBookRepository cardBookRepository;

    // 내 이메일(myEmail)을 제외한 명함 가져오기
    public List<NfcEntity> getOtherBusinessCards(String myEmail) {
        return cardBookRepository.findByMyEmailNot(myEmail);
    }
}

