package com.itdat.back.service.qna;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.qna.Qna;
import com.itdat.back.entity.qna.QnaAnswer;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.repository.qna.QnaAnswerRepository;
import com.itdat.back.repository.qna.QnaRepository;
import jakarta.transaction.Transactional;
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

    public List<QnaAnswer> getListById(int selectedId) {
        List<QnaAnswer> selectedQnaAnswerList = qnaAnswerRepository.findByQnaId(selectedId);
//        List<QnaAnswer> selectedQnaAnswerList = (List<QnaAnswer>) qnaAnswerRepository.findByQna_Id(selectedId).orElse(null);
        return selectedQnaAnswerList;
    }

    public boolean insertQnaAnswer(Map<String, Object> qnaAnswerData) {
        QnaAnswer newQnaAnswer = new QnaAnswer();
        Qna selectedQna = qnaRepository.findById(Integer.valueOf(qnaAnswerData.get("postId").toString())).orElse(null);
        User selectedUser = userRepository.findByUserId(qnaAnswerData.get("loginedUserId").toString());
        newQnaAnswer.setQna(selectedQna); // 새답변이 어느 게시글에 속했는지 대입한다.
        newQnaAnswer.getQna().setAnswered(true);
        newQnaAnswer.setCreateDateAt(LocalDateTime.now());
        newQnaAnswer.setUser(selectedUser); // 새답변을 쓴 관리자의 정보를 대입한다.
        newQnaAnswer.setContents(qnaAnswerData.get("contents").toString());
        QnaAnswer savedQnaAnswer = qnaAnswerRepository.save(newQnaAnswer);
        System.out.println("savedQnaAnswersavedQnaAnswer = " + savedQnaAnswer);

        // newQnaAnswer가 존재하면.. // selectedQna(선택된 게시글)의 답변이 존재하면 isAnswered를 true로 설정. 삭제시 메소드에서

        if (savedQnaAnswer != null) {
            return true;
        }else {
            return false;
        }
    }

    public boolean findAnswerById(int selectedAnswerID) {
        QnaAnswer selectedQnaAnswer = qnaAnswerRepository.findById(selectedAnswerID).orElse(null);
        System.out.println(selectedQnaAnswer);
        int selectedQnaId = selectedQnaAnswer.getQna().getId();
        Qna selectedQna = qnaRepository.findById(selectedQnaId).orElse(null);
        if (selectedQnaAnswer != null) {
            qnaAnswerRepository.delete(selectedQnaAnswer);
            List<QnaAnswer> answerList = qnaAnswerRepository.findByQnaId(selectedQnaId);
            System.out.println("answerList = " + answerList);
            if (!answerList.isEmpty()) {
                System.out.println("selectedQna = " + selectedQna);
                selectedQna.setAnswered(true);
                System.out.println("selectedQna.isAnswered() = 1" + selectedQna.isAnswered());
                qnaRepository.save(selectedQna);
            }else {
                selectedQna.setAnswered(false);
                qnaRepository.save(selectedQna);
                System.out.println("selectedQna.isAnswered() = 2" + selectedQna.isAnswered());
            }
            return true;
        } else {
            return false;
        }
    }
}
