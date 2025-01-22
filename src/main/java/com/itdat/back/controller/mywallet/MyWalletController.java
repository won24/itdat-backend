package com.itdat.back.controller.mywallet;

import com.itdat.back.entity.card.BusinessCard;
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

    /**
     * 사용자의 명함 가져오기
     *
     * @param myEmail 쿼리 파라미터(String): 사용자의 이메일
     * @return 사용자의 명함 리스트(List<MyWallet>)
     */
    @GetMapping("/cards")
    public ResponseEntity<List<MyWallet>> getCards(@RequestParam String myEmail) {
        List<MyWallet> cards = myWalletService.getCards(myEmail);
        return ResponseEntity.ok(cards);
    }

    /**
     * 폴더 생성
     *
     * @param folderRequest 요청 바디(FolderRequest): 생성할 폴더의 정보
     * @return 성공 메시지(String): "Folder created successfully"
     */
    @PostMapping("/folders/create")
    public ResponseEntity<String> createFolder(@RequestBody FolderRequest folderRequest) {
        myWalletService.createFolder(folderRequest);
        return ResponseEntity.ok("Folder created successfully");
    }

    /**
     * 사용자의 폴더 목록 가져오기
     *
     * @param userEmail 쿼리 파라미터(String): 사용자의 이메일
     * @return 사용자의 폴더 리스트(List<Folder>)
     */
    @GetMapping("/folders")
    public ResponseEntity<List<Folder>> getFolders(@RequestParam String userEmail) {
        List<Folder> folders = folderRepository.findByUserEmail(userEmail);
        return ResponseEntity.ok(folders);
    }

    /**
     * 폴더에 포함된 명함 가져오기
     *
     * @param folderName 쿼리 파라미터(String): 폴더 이름
     * @return 폴더에 포함된 명함 리스트(List<CardInfo>)
     */
    @GetMapping("/folderCards")
    public ResponseEntity<List<CardInfo>> getFolderCards(@RequestParam String folderName) {
        List<CardInfo> cards = myWalletService.getCardsByFolderName(folderName);
        return ResponseEntity.ok(cards);
    }

    /**
     * 폴더 이름 업데이트
     *
     * @param request 요청 바디(FolderUpdateRequest): 업데이트할 폴더 정보
     * @return 성공 메시지(String): "Folder name updated successfully"
     */
    @PutMapping("/folders/update")
    public ResponseEntity<String> updateFolderName(@RequestBody FolderUpdateRequest request) {
        myWalletService.updateFolderName(request.getUserEmail(), request.getOldFolderName(), request.getNewFolderName());
        return ResponseEntity.ok("Folder name updated successfully");
    }

    /**
     * 폴더 삭제
     *
     * @param folderName 경로 변수(String): 삭제할 폴더 이름
     * @param userEmail 쿼리 파라미터(String): 사용자의 이메일
     * @return 성공 메시지(String): "Folder deleted successfully"
     */
    @DeleteMapping("/folders/{folderName}")
    public ResponseEntity<String> deleteFolder(@PathVariable String folderName, @RequestParam String userEmail) {
        myWalletService.deleteFolder(folderName, userEmail);
        return ResponseEntity.ok("Folder deleted successfully");
    }

    /**
     * 명함 폴더로 이동 또는 제거
     *
     * @param request 요청 바디(CardMoveRequest): 명함 이동 정보
     * @return 성공 메시지(String):
     *         - "Card moved to folder successfully" (폴더로 이동 성공)
     *         - "Card removed from folder successfully" (폴더에서 제거 성공)
     * @throws HttpStatus.FORBIDDEN: 이동 실패 시 오류 메시지 반환
     */
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

    /**
     * 폴더에 포함되지 않은 명함 가져오기
     *
     * @param myEmail 쿼리 파라미터(String): 사용자의 이메일
     * @return 폴더에 포함되지 않은 명함 리스트(List<MyWallet>)
     */
    @GetMapping("/cards/withoutFolder")
    public ResponseEntity<List<MyWallet>> getCardsWithoutFolder(@RequestParam String myEmail) {
        List<MyWallet> cards = myWalletService.getCardsWithoutFolder(myEmail);
        return ResponseEntity.ok(cards);
    }

    /**
     * 특정 폴더의 명함 가져오기
     *
     * @param myEmail 쿼리 파라미터(String): 사용자의 이메일
     * @param folderName 쿼리 파라미터(String): 폴더 이름
     * @return 폴더에 포함된 명함 리스트(List<MyWallet>)
     */
    @GetMapping("/cards/byFolder")
    public ResponseEntity<List<MyWallet>> getCardsByFolder(
            @RequestParam String myEmail, @RequestParam String folderName) {
        List<MyWallet> cards = myWalletService.getCardsByFolder(myEmail, folderName);
        return ResponseEntity.ok(cards);
    }

    /**
     * 사용자의 모든 명함 가져오기
     *
     * @param userEmail 쿼리 파라미터(String): 사용자의 이메일
     * @return 사용자의 모든 명함 리스트(List<CardInfo>)
     */
    @GetMapping("/allCards")
    public ResponseEntity<List<BusinessCard>> getAllCards(@RequestParam String userEmail) {
        System.out.println("컨트롤러");
        List<BusinessCard> cards = myWalletService.getAllCards(userEmail);
        System.out.println("cards: " + cards);
        return ResponseEntity.ok(cards);
    }
}
