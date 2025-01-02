package com.itdat.back.service.qna;

import com.itdat.back.entity.qna.Qna;
import com.itdat.back.repository.qna.QnaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QnaService {

    @Autowired
    private QnaRepository qnaRepository;

    public List<Qna> getAllQnaList() {
        return qnaRepository.findAll();
    }
}
