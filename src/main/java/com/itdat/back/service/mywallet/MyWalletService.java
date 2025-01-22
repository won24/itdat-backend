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
import java.util.stream.Collectors;

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
//    @Transactional
//    public void moveCardToFolder(CardMoveRequest request) {
//        String myEmail = request.getMyEmail();
//        String userEmail = request.getUserEmail();
//        String folderName = request.getFolderName();
//
//        // 폴더 조회
//        Folder folder = folderRepository.findByUserEmailAndFolderName(myEmail, folderName)
//                .orElseThrow(() -> new RuntimeException("Folder not found"));
//
//        // MyWallet 데이터 조회
//        MyWallet wallet = (MyWallet) myWalletRepository.findByUserEmailAndMyEmail(userEmail, myEmail)
//                .orElseThrow(() -> new RuntimeException("MyWallet entry not found"));
//
//        // 중복 관계 확인
//        boolean alreadyExists = folderCardRepository.existsByFolderIdAndCardId(folder.getId(), wallet.getId());
//        if (alreadyExists) {
//            throw new RuntimeException("This card is already in the folder.");
//        }
//
//        // FolderCard 관계 추가
//        FolderCard folderCard = new FolderCard(folder.getId(), wallet.getId());
//        folderCardRepository.save(folderCard);
//    }
    @Transactional
    public void moveCardToFolder(CardMoveRequest request) {
        String myEmail = request.getMyEmail();
        String userEmail = request.getUserEmail();
        String folderName = request.getFolderName();

        // 폴더 조회
        Folder folder = folderRepository.findByUserEmailAndFolderName(myEmail, folderName)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        // MyWallet 데이터 조회
        List<MyWallet> wallets = myWalletRepository.findByUserEmailAndMyEmail(userEmail, myEmail);
        if (wallets.isEmpty()) {
            throw new RuntimeException("MyWallet entry not found");
        }
        MyWallet wallet = wallets.get(0); // 첫 번째 결과 가져오기

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
//    @Transactional
//    public void removeCardFromFolder(CardMoveRequest request) {
//        String myEmail = request.getMyEmail();
//        String userEmail = request.getUserEmail();
//
//        // MyWallet에서 명함 존재 여부 확인
//        MyWallet wallet = (MyWallet) myWalletRepository.findByUserEmailAndMyEmail(userEmail, myEmail)
//                .orElseThrow(() -> new RuntimeException("The card does not exist in MyWallet."));
//
//        // 해당 명함의 FolderCard 관계 조회 및 삭제
//        List<FolderCard> folderCards = folderCardRepository.findByCardIdAndUserEmail(myEmail, userEmail);
//        if (folderCards.isEmpty()) {
//            throw new RuntimeException("The card is not associated with any folder.");
//        }
//        folderCardRepository.deleteAll(folderCards);
//    }
//
    // 폴더에 속하지 않은 명함 조회
    public List<MyWallet> getCardsWithoutFolder(String myEmail) {
        return myWalletRepository.findCardsWithoutFolder(myEmail);
    }
    @Transactional
    public void removeCardFromFolder(CardMoveRequest request) {
        String myEmail = request.getMyEmail();
        String userEmail = request.getUserEmail();

        // MyWallet에서 명함 존재 여부 확인
        List<MyWallet> wallets = myWalletRepository.findByUserEmailAndMyEmail(userEmail, myEmail);
        if (wallets.isEmpty()) {
            throw new RuntimeException("The card does not exist in MyWallet.");
        }

        for (MyWallet wallet : wallets) {
            // 해당 명함의 FolderCard 관계 조회 및 삭제
            List<FolderCard> folderCards = folderCardRepository.findByCardIdAndUserEmail(wallet.getId(), myEmail);
            if (folderCards.isEmpty()) {
                throw new RuntimeException("The card is not associated with any folder.");
            }
            folderCardRepository.deleteAll(folderCards);
        }
    }


    // 특정 폴더의 명함 조회
    public List<MyWallet> getCardsByFolder(String myEmail, String folderName) {
        Folder folder = folderRepository.findByUserEmailAndFolderName(myEmail, folderName)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        List<FolderCard> folderCards = folderCardRepository.findByFolderId(folder.getId());

        // FolderCards에서 MyWallet 리스트 반환
        return folderCards.stream()
                .map(fc -> myWalletRepository.findById(fc.getCardId())
                        .orElseThrow(() -> new RuntimeException("Card not found")))
                .collect(Collectors.toList());
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

        List<FolderCard> folderCards = folderCardRepository.findByFolderId(folder.getId());
        List<CardInfo> cardInfoList = new ArrayList<>();

        for (FolderCard folderCard : folderCards) {
            myWalletRepository.findById(folderCard.getCardId()).ifPresent(myWallet -> {
                BusinessCard businessCard = myWallet.getBusinessCard();
                if (businessCard != null) {
                    cardInfoList.add(new CardInfo(businessCard, myWallet.getCardNo()));
                }
            });
        }

        return cardInfoList;
    }


    public List<CardInfo> getAllCards(String myEmail) {
        List<MyWallet> myWallets = myWalletRepository.findByMyEmail(myEmail);
        List<CardInfo> cardInfoList = new ArrayList<>();

        for (MyWallet myWallet : myWallets) {
            boolean isInFolder = folderCardRepository.existsByCardId(myWallet.getId());
            if (isInFolder) {
                continue;
            }

            BusinessCard businessCard = myWallet.getBusinessCard();
            if (businessCard != null) {
                cardInfoList.add(new CardInfo(businessCard, myWallet.getCardNo()));
            }
        }

        return cardInfoList;
    }


}
