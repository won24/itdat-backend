package com.itdat.back.controller.mywallet;

import com.itdat.back.entity.mywallet.*;
import com.itdat.back.entity.nfc.MyWallet;
import com.itdat.back.repository.mywallet.FolderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.itdat.back.service.mywallet.MyWalletService;

import java.util.List;

@RestController
@RequestMapping("/api/mywallet")
public class MyWalletController {
    private final MyWalletService myWalletService;
    private final FolderRepository folderRepository;

    public MyWalletController(MyWalletService myWalletService, FolderRepository folderRepository) {
        this.myWalletService = myWalletService;
        this.folderRepository = folderRepository;
    }

    // 명함 가져오기
    @GetMapping("/cards")
    public ResponseEntity<List<MyWallet>> getCards(@RequestParam String myEmail) {
        List<MyWallet> cards = myWalletService.getCards(myEmail);
        return ResponseEntity.ok(cards);
    }

    // 폴더 생성
    @PostMapping("/folders/create")
    public ResponseEntity<String> createFolder(@RequestBody FolderRequest folderRequest) {
        myWalletService.createFolder(folderRequest);
        return ResponseEntity.ok("Folder created successfully");
    }

    // 폴더 목록 가져오기
    @GetMapping("/folders")
    public ResponseEntity<List<Folder>> getFolders(@RequestParam String userEmail) {
        List<Folder> folders = folderRepository.findByUserEmail(userEmail);
        return ResponseEntity.ok(folders);
    }

    @GetMapping("/folderCards")
    public ResponseEntity<List<CardInfo>> getFolderCards(@RequestParam String folderName) {
        List<CardInfo> cards = myWalletService.getCardsByFolderName(folderName);
        System.out.println("폴더 '{}'에서 가져온 명함 데이터: {}" + folderName + cards);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/folders/update")
    public ResponseEntity<String> updateFolderName(@RequestBody FolderUpdateRequest request) {
        myWalletService.updateFolderName(request.getUserEmail(), request.getOldFolderName(), request.getNewFolderName());
        return ResponseEntity.ok("Folder name updated successfully");
    }



    // 폴더 삭제
    @DeleteMapping("/folders/{folderName}")
    public ResponseEntity<String> deleteFolder(@PathVariable String folderName, @RequestParam String userEmail) {
        myWalletService.deleteFolder(folderName, userEmail);
        return ResponseEntity.ok("Folder deleted successfully");
    }

    // 명함 폴더로 이동
    @PostMapping("/moveCard")
    public ResponseEntity<String> moveCardToFolder(@RequestBody CardMoveRequest request) {
        try {
            if (request.getFolderName() == null || request.getFolderName().isEmpty()) {
                // 폴더에서 명함 제거
                myWalletService.removeCardFromFolder(request);
                return ResponseEntity.ok("Card removed from folder successfully.");
            } else {
                // 폴더로 명함 이동
                myWalletService.moveCardToFolder(request);
                return ResponseEntity.ok("Card moved to folder successfully.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }



    @GetMapping("/allCards")
    public ResponseEntity<List<CardInfo>> getAllCards(@RequestParam String userEmail) {
        List<CardInfo> cards = myWalletService.getAllCards(userEmail);
        return ResponseEntity.ok(cards);
    }
}
