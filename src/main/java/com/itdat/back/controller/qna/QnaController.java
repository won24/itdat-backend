package com.itdat.back.controller.qna;

import com.itdat.back.entity.qna.Qna;
import com.itdat.back.service.qna.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/qna")
public class QnaController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("/all-list")
    public ResponseEntity<Object> getAllQnaList() {
        try {
            List<Qna> allQnaList = qnaService.getAllQnaList();
            if(allQnaList != null && !allQnaList.isEmpty()){
                return ResponseEntity.ok(allQnaList);
            }else {
                return ResponseEntity.ok("아직 QnA 게시판에 게시된 글이 없습니다.");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR/*500, 500번대는 서버문제*/)
                    .body("서버측에서 문제가 발생하였습니다. 에러: "+e.getMessage());
        }
    }

//    @GetMapping("/selected-qna-list")
//    public ResponseEntity<Object> getSelectedQnaList() {
//
//    }
}
