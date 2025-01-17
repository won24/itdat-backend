package com.itdat.back.repository.qna;

import com.itdat.back.entity.qna.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Integer> {

    List<Qna> findByUser_UserId(String currentUserId);
}
