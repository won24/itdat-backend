package com.itdat.back.service.card;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.card.Card;
import com.itdat.back.entity.card.Template;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.repository.card.CardRepository;
import com.itdat.back.repository.card.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
public class CardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private TemplateRepository templateRepository;

    // 유저 정보 가져오기
    public User findByUserId(int id) {
        User user = userRepository.findById(id);
        if(Objects.isNull(user)){
            return null;
        }
        return user;
    }


//    // 명함 만들기
//    public Card createBusinessCard(Card card, Template template, String logoPath) throws IOException {
//
//        // 템플릿 내용 가져오기
//        String svgContent = template.getSvgContent();
//
//        // 사용자 정보로 템플릿 업데이트
//        svgContent = svgContent
//                .replace("{name}", card.getUserName())
//                .replace("{phone}", card.getPhone())
//                .replace("{email}", card.getEmail())
//                .replace("{companyName}", card.getCompanyName())
//                .replace("{companyNumber}", card.getCompanyNumber())
//                .replace("{position}", card.getPosition())
//                .replace("{department}", card.getDepartment())
//                .replace("{fax}", card.getFax());
//
//        svgContent = svgContent.replace("{logo}", logoPath);
//
//        // 저장
//        String fileName = "business_card_" + card.getUserName() + card.getId() + ".svg";
//        String filePath = "./uploads/" + fileName;
//        Files.write(Paths.get(filePath), svgContent.getBytes());
//
//        card.setSvgUrl("/uploads/" + fileName);
//
//        return cardRepository.save(card);
//    }


//    // 명함 가져오기
//    public Card findById(int id) {
//        Card card = cardRepository.findById(id);
//        if(Objects.isNull(card)){
//            return null;
//        }
//        return card;
//    }

    public Card createBusinessCard(Card card) {
        Template template = templateRepository.findById(Integer.parseInt(card.getTemplateId()))
                .orElseThrow(() -> new RuntimeException("Template not found"));
        Card newCard = new Card(card, template);
        return cardRepository.save(newCard);
    }

    public List<Card> getCardsByUserId(String userId) {

        Card card = cardRepository.findById(userId);

        if(Objects.isNull(card)){
            return null;
        }
    }
}
