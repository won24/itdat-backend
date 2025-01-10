package com.itdat.back.repository.board;

import com.itdat.back.entity.board.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {
    List<Portfolio> findByUserEmail(String userEmail);
}
