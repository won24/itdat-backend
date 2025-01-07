package com.itdat.back.controller.mywallet;

import com.itdat.back.entity.mywallet.CardMoveRequest;
import com.itdat.back.entity.mywallet.Folder;
import com.itdat.back.entity.mywallet.FolderUpdateRequest;
import com.itdat.back.entity.nfc.MyWallet;
import com.itdat.back.repository.mywallet.FolderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.itdat.back.service.mywallet.MyWalletService;
import com.itdat.back.entity.mywallet.FolderRequest;
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
    public ResponseEntity<List<MyWallet>> getFolderCards(@RequestParam String folderName) {
        List<MyWallet> cards = myWalletService.getCardsByFolderName(folderName);
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
        myWalletService.moveCardToFolder(request);
        return ResponseEntity.ok("Card moved successfully");
    }
}
