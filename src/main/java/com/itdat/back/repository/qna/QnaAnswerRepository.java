package com.itdat.back.repository.qna;

import com.itdat.back.entity.qna.QnaAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QnaAnswerRepository extends JpaRepository<QnaAnswer, Integer> {

//    Optional<Object> findByQna_Id(int selectedId);

    List<QnaAnswer> findByQnaId(int selectedId);
}
