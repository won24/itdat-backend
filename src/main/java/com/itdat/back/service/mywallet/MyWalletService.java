package com.itdat.back.service.mywallet;

import com.itdat.back.entity.mywallet.CardMoveRequest;
import com.itdat.back.entity.mywallet.FolderCard;
import com.itdat.back.entity.mywallet.FolderRequest;
import com.itdat.back.entity.nfc.MyWallet;
import com.itdat.back.repository.mywallet.FolderCardRepository;
import com.itdat.back.repository.mywallet.FolderRepository;
import com.itdat.back.repository.mywallet.MyWalletRepository;
import com.itdat.back.entity.mywallet.Folder;
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
        return myWalletRepository.findByUserEmail(myEmail);
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
    public void moveCardToFolder(CardMoveRequest request) {
        Folder folder = folderRepository.findByUserEmailAndFolderName(request.getUserEmail(), request.getFolderName())
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        FolderCard folderCard = new FolderCard(folder.getId(), request.getCardId());
        folderCardRepository.save(folderCard);
    }

    public void updateFolderName(String userEmail, String oldFolderName, String newFolderName) {
        Folder folder = folderRepository.findByUserEmailAndFolderName(userEmail, oldFolderName)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        folder.setFolderName(newFolderName);
        folderRepository.save(folder);
    }

    public List<MyWallet> getCardsByFolderName(String folderName) {
        Folder folder = (Folder) folderRepository.findByFolderName(folderName)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        List<FolderCard> folderCards = folderCardRepository.findByFolderId(folder.getId());

        List<MyWallet> cards = new ArrayList<>();
        for (FolderCard folderCard : folderCards) {
            myWalletRepository.findById(folderCard.getCardId())
                    .ifPresent(cards::add);
        }
        return cards;
    }

}
