package com.itdat.back.service.nfc;

import com.itdat.back.entity.nfc.NfcEntity;
import com.itdat.back.repository.nfc.NfcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NfcService {

    @Autowired
    private NfcRepository nfcRepository;

    public NfcEntity saveNfcInfo(String userEmail, int cardNo,String myEmail) {
        NfcEntity nfcEntity = new NfcEntity();
        nfcEntity.setUserEmail(userEmail);
        nfcEntity.setCardNo(cardNo);
        nfcEntity.setMyEmail(myEmail);
        return nfcRepository.save(nfcEntity);
    }
}