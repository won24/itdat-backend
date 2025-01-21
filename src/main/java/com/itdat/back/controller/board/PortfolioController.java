package com.itdat.back.controller.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.board.Portfolio;
import com.itdat.back.service.board.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/board/portfolio")
@CrossOrigin
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;


    @GetMapping("/{userEmail}")
    public ResponseEntity<List<Portfolio>> getPortfoliosByUserEmail(@PathVariable String userEmail) {
        try{
            List<Portfolio> portfolios = portfolioService.getPortfoliosByUserEmail(userEmail);

            portfolios.forEach(portfolio -> {
                if (portfolio.getFileUrl() != null) {
                    portfolio.setFileUrl("/uploads/board/" + Paths.get(portfolio.getFileUrl()).getFileName());
                }
            });

            return ResponseEntity.ok(portfolios);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Portfolio> savePortfolio(
            @RequestParam("postData") String jsonData,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "document", required = false) MultipartFile document
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Portfolio newPortfolio = objectMapper.readValue(jsonData, Portfolio.class);

            if (file != null && !file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get("/uploads/board/", fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, file.getBytes());

                newPortfolio.setFileUrl(filePath.toString());
            }

            if (document != null && !document.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + document.getOriginalFilename();
                Path filePath = Paths.get("/uploads/board/", fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, document.getBytes());

                newPortfolio.setDocumentUrl(filePath.toString());
            }

            User user = portfolioService.findByUserEmail(newPortfolio.getUserEmail());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Portfolio savedPortfolio = portfolioService.savePortfolio(newPortfolio);
            return ResponseEntity.ok(savedPortfolio);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PutMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Portfolio> updatePortfolio(
            @PathVariable Integer id,
            @RequestParam("postData") String jsonData,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "document", required = false) MultipartFile document
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Portfolio updatedPortfolio = objectMapper.readValue(jsonData, Portfolio.class);

            if (file != null && !file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get("/uploads/board/", fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, file.getBytes());

                updatedPortfolio.setFileUrl(filePath.toString());
            }

            if (document != null && !document.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + document.getOriginalFilename();
                Path filePath = Paths.get("/uploads/board/", fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, document.getBytes());

                updatedPortfolio.setDocumentUrl(filePath.toString());
            }

            Portfolio updated = portfolioService.updatePortfolio(id, updatedPortfolio);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Integer id) {
        portfolioService.deletePortfolio(id);
        return ResponseEntity.noContent().build();
    }

}
