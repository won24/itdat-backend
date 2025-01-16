package com.itdat.back.service.qna;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.qna.Qna;
import com.itdat.back.entity.qna.QnaAnswer;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.repository.qna.QnaAnswerRepository;
import com.itdat.back.repository.qna.QnaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class QnaService {

    @Autowired
    private QnaRepository qnaRepository;

    @Autowired
    private QnaAnswerRepository qnaAnswerRepository;

    @Autowired
    private UserRepository userRepository;


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

    public boolean insertQnaAnswer(Map<String, Object> qnaAnswerData) {
        QnaAnswer newQnaAnswer = new QnaAnswer();
        Qna selectedQna = qnaRepository.findById(Integer.valueOf(qnaAnswerData.get("postId").toString())).orElse(null);
        User selectedUser = userRepository.findById(Integer.valueOf(qnaAnswerData.get("loginedUserId").toString())).orElse(null);
        newQnaAnswer.setQna(selectedQna); // 새답변이 어느 게시글에 속했는지 대입한다.
        newQnaAnswer.setCreateDateAt(LocalDateTime.now());
        newQnaAnswer.setUser(selectedUser); // 새답변을 쓴 관리자의 정보를 대입한다.
        newQnaAnswer.setContents(qnaAnswerData.get("contents").toString());
        QnaAnswer savedQnaAnswer = qnaAnswerRepository.save(newQnaAnswer);
        if (savedQnaAnswer != null) {
            return true;
        }else {
            return false;
        }

    }
}
