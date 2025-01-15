package com.itdat.back.repository.qna;

import com.itdat.back.entity.qna.QnaAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaAnswerRepository extends JpaRepository<QnaAnswer, Integer> {
    QnaAnswer findByQna_Id(int selectedId);
}
