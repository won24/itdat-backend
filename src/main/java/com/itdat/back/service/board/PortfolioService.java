package com.itdat.back.service.board;

import com.itdat.back.entity.board.Portfolio;
import com.itdat.back.repository.board.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    // 모든 포트폴리오 가져오기 (특정 사용자 이메일 기준)
    public List<Portfolio> getPortfoliosByUserEmail(String userEmail) {
        return portfolioRepository.findByUserEmail(userEmail);
    }

    // 포트폴리오 저장
    public Portfolio savePortfolio(Portfolio newPortfolio) {
        return portfolioRepository.save(newPortfolio);
    }

    // 포트폴리오 수정
    public Portfolio updatePortfolio(Integer id, Portfolio updatedPortfolio) {
        Optional<Portfolio> findPost = portfolioRepository.findById(id);
        if (findPost.isPresent()) {
            Portfolio editPortfolio = findPost.get();
            editPortfolio.setTitle(updatedPortfolio.getTitle());
            editPortfolio.setContent(updatedPortfolio.getContent());
            editPortfolio.setFileUrl(updatedPortfolio.getFileUrl());
            return portfolioRepository.save(editPortfolio);
        } else {
            throw new IllegalArgumentException("Portfolio " + id + " not found.");
        }
    }

    // 포트폴리오 삭제
    public void deletePortfolio(Integer id) {
        if (portfolioRepository.existsById(id)) {
            portfolioRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Portfolio " + id + " not found.");
        }
    }
}
