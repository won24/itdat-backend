package com.itdat.back.service.qna;

import com.itdat.back.entity.qna.Qna;
import com.itdat.back.entity.qna.QnaAnswer;
import com.itdat.back.repository.qna.QnaAnswerRepository;
import com.itdat.back.repository.qna.QnaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QnaService {

    @Autowired
    private QnaRepository qnaRepository;

    @Autowired
    private QnaAnswerRepository qnaAnswerRepository;



    public List<Qna> getAllQnaList() {
        return qnaRepository.findAll();
    }

    public Qna findById(int selectedId) {
        return qnaRepository.findById(selectedId).orElse(null);
    }

    public List<Qna> findByUserID(String currentUserId) {
        return qnaRepository.findByUser_UserId(currentUserId); // User 클래스의 userId 필드에 접근하기 위해 _를 사용했다.
    }

    public QnaAnswer getListById(int selectedId) {
        return qnaAnswerRepository.findByQna_Id(selectedId);
    }
}
