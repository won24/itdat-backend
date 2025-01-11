package com.itdat.back.repository.board;

import com.itdat.back.entity.board.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Integer> {
    List<History> findByUserEmail(String userEmail);
}
