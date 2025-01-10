package com.itdat.back.controller.board;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.itdat.back.entity.board.Portfolio;
import com.itdat.back.entity.card.BusinessCard;
import com.itdat.back.service.board.PortfolioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/board")
@CrossOrigin
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // 가져오기
    @GetMapping("/{userEmail}")
    public ResponseEntity<List<Portfolio>> getPortfoliosByUserEmail(@PathVariable String userEmail) {
        try{
            List<Portfolio> portfolios = portfolioService.getPortfoliosByUserEmail(userEmail);

            portfolios.forEach(portfolio -> {
                if (portfolio.getFileUrl() != null) {
                    portfolio.setFileUrl("/uploads/board" + Paths.get(portfolio.getFileUrl()).getFileName());
                }
            });

            return ResponseEntity.ok(portfolios);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    // 저장
//    @PostMapping("/save")
//    public ResponseEntity<Portfolio> savePortfolio(@RequestBody Portfolio newPortfolio) {
//        Portfolio savedPortfolio = portfolioService.savePortfolio(newPortfolio);
//        return ResponseEntity.ok(savedPortfolio);
//    }
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Portfolio> savePortfolio(
            @RequestParam("data") String jsonData,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            Portfolio newPortfolio = objectMapper.readValue(jsonData, Portfolio.class);

            // 파일 처리
            if (file != null && !file.isEmpty()) {
                // 파일 저장 경로 설정
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get("/uploads/board", fileName);
                Files.createDirectories(filePath.getParent()); // 디렉토리 생성
                Files.write(filePath, file.getBytes());

                // 저장된 파일 경로를 Portfolio 객체에 설정
                newPortfolio.setFileUrl(filePath.toString());
            }

            // Portfolio 저장
            Portfolio savedPortfolio = portfolioService.savePortfolio(newPortfolio);

            return ResponseEntity.ok(savedPortfolio);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 수정
    @PutMapping("/edit/{id}")
    public ResponseEntity<Portfolio> updatePortfolio(@PathVariable Integer id, @RequestBody Portfolio updatedPortfolio) {
        Portfolio updated = portfolioService.updatePortfolio(id, updatedPortfolio);
        return ResponseEntity.ok(updated);
    }

    // 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Integer id) {
        portfolioService.deletePortfolio(id);
        return ResponseEntity.noContent().build();
    }

}
