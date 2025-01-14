package com.itdat.back.service.nfc;

import com.itdat.back.entity.nfc.MyWallet;
import com.itdat.back.repository.nfc.NfcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NfcService {

    @Autowired
    private NfcRepository nfcRepository;

    public MyWallet saveNfcInfo(String userEmail, int cardNo, String myEmail) {
        MyWallet nfcEntity = new MyWallet();
        nfcEntity.setUserEmail(userEmail);
        nfcEntity.setCardNo(cardNo);
        nfcEntity.setMyEmail(myEmail);
        return nfcRepository.save(nfcEntity);
    }

    public MyWallet saveDescription(Map<String, String> cardmemo) {
        String description = cardmemo.get("description");
        String myEmail = cardmemo.get("myEmail");
        String userEmail = cardmemo.get("userEmail");
        int cardNo = Integer.parseInt(cardmemo.get("cardNo")); // "CardNo"를 "cardNo"로 변경
        if (description == null || myEmail == null || userEmail == null) {
            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다.");
        }

        MyWallet card = nfcRepository.findByUserEmailAndCardNoAndMyEmail(userEmail, cardNo, myEmail);
        if (card == null) {
            throw new IllegalArgumentException("해당 카드를 찾을 수 없습니다.");
        }

        card.setDescription(description);
        return nfcRepository.save(card);
    }

    public String loadDescription(Map<String, String> cardmemo) {
        String myEmail = cardmemo.get("myEmail");
        String userEmail = cardmemo.get("userEmail");
        int cardNo = Integer.parseInt(cardmemo.get("cardNo")); //
        MyWallet card = nfcRepository.findByUserEmailAndCardNoAndMyEmail(userEmail, cardNo, myEmail);
        if (card == null) {
            throw new IllegalArgumentException("해당 카드를 찾을 수 없습니다.");
        }
        return card.getDescription();

    }
}