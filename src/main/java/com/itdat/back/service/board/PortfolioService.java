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


    public User findByUserEmail(String userEmail) {

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUserEmail(userEmail));

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        return optionalUser.get();
    }


    public List<Portfolio> getPortfoliosByUserEmail(String userEmail) {
        return portfolioRepository.findByUserEmail(userEmail);
    }



    public Portfolio savePortfolio(Portfolio newPortfolio) {
        if (newPortfolio.getUserEmail() == null) {
            throw new IllegalArgumentException("포트폴리오에 연결된 유효한 사용자가 필요합니다.");
        }
        return portfolioRepository.save(newPortfolio);
    }



    public Portfolio updatePortfolio(Integer id, Portfolio updatedPortfolio) {
        Optional<Portfolio> findPost = portfolioRepository.findById(id);
        if (findPost.isPresent()) {
            Portfolio editPortfolio = findPost.get();
            editPortfolio.setTitle(updatedPortfolio.getTitle());
            editPortfolio.setContent(updatedPortfolio.getContent());
            editPortfolio.setFileUrl(updatedPortfolio.getFileUrl());
            editPortfolio.setLinkUrl(updatedPortfolio.getLinkUrl());
            editPortfolio.setDocumentUrl(updatedPortfolio.getDocumentUrl());
            return portfolioRepository.save(editPortfolio);
        } else {
            throw new IllegalArgumentException("Portfolio " + id + " not found.");
        }
    }


    public void deletePortfolio(Integer id) {
        if (portfolioRepository.existsById(id)) {
            portfolioRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Portfolio " + id + " not found.");
        }
    }
}
