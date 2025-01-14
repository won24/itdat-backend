package com.itdat.back.service.board;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.board.Portfolio;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.repository.board.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    // 유저 정보 가져오기
    public User findByUserEmail(String userEmail) {

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUserEmail(userEmail));

        if (!optionalUser.isPresent()) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        User user = optionalUser.get();
        return user;
    }


    // 모든 포트폴리오 가져오기
    public List<Portfolio> getPortfoliosByUserEmail(String userEmail) {
        return portfolioRepository.findByUserEmail(userEmail);
    }


    // 포트폴리오 저장
    public Portfolio savePortfolio(Portfolio newPortfolio) {
        if (newPortfolio.getUserEmail() == null) {
            throw new IllegalArgumentException("포트폴리오에 연결된 유효한 사용자가 필요합니다.");
        }
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
