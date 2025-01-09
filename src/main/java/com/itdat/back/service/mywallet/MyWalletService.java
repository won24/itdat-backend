package com.itdat.back.service.mywallet;

import com.itdat.back.entity.card.BusinessCard;
import com.itdat.back.entity.mywallet.*;
import com.itdat.back.entity.nfc.MyWallet;
import com.itdat.back.repository.mywallet.FolderCardRepository;
import com.itdat.back.repository.mywallet.FolderRepository;
import com.itdat.back.repository.mywallet.MyWalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyWalletService {
    private final MyWalletRepository myWalletRepository;
    private final FolderRepository folderRepository;
    private final FolderCardRepository folderCardRepository;

    public MyWalletService(MyWalletRepository myWalletRepository, FolderRepository folderRepository, FolderCardRepository folderCardRepository) {
        this.myWalletRepository = myWalletRepository;
        this.folderRepository = folderRepository;
        this.folderCardRepository = folderCardRepository;
    }

    // 명함 가져오기
    public List<MyWallet> getCards(String myEmail) {
        return myWalletRepository.findByMyEmail(myEmail);
    }

    // 폴더 생성
    public void createFolder(FolderRequest folderRequest) {
        Folder folder = new Folder(folderRequest.getUserEmail(), folderRequest.getFolderName());
        folderRepository.save(folder);
    }

    // 폴더 삭제
    public void deleteFolder(String folderName, String userEmail) {
        Folder folder = folderRepository.findByUserEmailAndFolderName(userEmail, folderName)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        folderRepository.delete(folder);
    }

    // 명함 폴더로 이동
    @Transactional
    public void moveCardToFolder(CardMoveRequest request) {
        String myEmail = request.getMyEmail();
        String userEmail = request.getUserEmail();
        String folderName = request.getFolderName();

        // 폴더 조회
        Folder folder = folderRepository.findByUserEmailAndFolderName(myEmail, folderName)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        // MyWallet 데이터 조회
        MyWallet wallet = (MyWallet) myWalletRepository.findByUserEmailAndMyEmail(userEmail, myEmail)
                .orElseThrow(() -> new RuntimeException("MyWallet entry not found"));

        // 중복 관계 확인
        boolean alreadyExists = folderCardRepository.existsByFolderIdAndCardId(folder.getId(), wallet.getId());
        if (alreadyExists) {
            throw new RuntimeException("This card is already in the folder.");
        }

        // FolderCard 관계 추가
        FolderCard folderCard = new FolderCard(folder.getId(), wallet.getId());
        folderCardRepository.save(folderCard);
    }

    // 폴더에서 명함 제거
    @Transactional
    public void removeCardFromFolder(CardMoveRequest request) {
        String myEmail = request.getMyEmail();
        String userEmail = request.getUserEmail();

        // 해당 명함의 FolderCard 관계를 모두 삭제
        List<FolderCard> folderCards = folderCardRepository.findByCardIdAndUserEmail(myEmail, userEmail);
        if (folderCards.isEmpty()) {
            throw new RuntimeException("The card is not associated with any folder.");
        }
        folderCardRepository.deleteAll(folderCards);
    }


    public void updateFolderName(String userEmail, String oldFolderName, String newFolderName) {
        Folder folder = folderRepository.findByUserEmailAndFolderName(userEmail, oldFolderName)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        folder.setFolderName(newFolderName);
        folderRepository.save(folder);
    }

    public List<CardInfo> getCardsByFolderName(String folderName) {
        Folder folder = (Folder) folderRepository.findByFolderName(folderName)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        // FolderCard에서 folderId를 기반으로 MyWallet 조회
        List<FolderCard> folderCards = folderCardRepository.findByFolderId(folder.getId());
        List<CardInfo> cardInfoList = new ArrayList<>();

        for (FolderCard folderCard : folderCards) {
            myWalletRepository.findById(folderCard.getCardId()).ifPresent(myWallet -> {
                BusinessCard businessCard = myWallet.getBusinessCard();
                cardInfoList.add(new CardInfo(
                        businessCard != null ? businessCard.getUserName() : "이름 없음",
                        businessCard != null ? businessCard.getCompanyName() : "정보 없음",
                        businessCard != null ? businessCard.getUserEmail() : "이메일 없음",
                        myWallet.getCardNo()
                ));
            });
        }

        return cardInfoList;
    }



    public List<CardInfo> getAllCards(String myEmail) {
        List<MyWallet> myWallets = myWalletRepository.findByMyEmail(myEmail);
        List<CardInfo> cardInfoList = new ArrayList<>();

        for (MyWallet myWallet : myWallets) {
            // FolderCard에 포함된 명함은 제외
            boolean isInFolder = folderCardRepository.existsByCardId(myWallet.getId());
            if (isInFolder) {
                continue;
            }

            BusinessCard businessCard = myWallet.getBusinessCard();
            if (businessCard != null) {
                cardInfoList.add(new CardInfo(
                        businessCard.getUserName(),
                        businessCard.getCompanyName(),
                        businessCard.getUserEmail(),
                        myWallet.getCardNo()
                ));
            }
        }

        return cardInfoList;
    }

}
