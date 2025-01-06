package com.itdat.back.controller.qna;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.qna.Qna;
import com.itdat.back.entity.qna.QnaCategory;
import com.itdat.back.model.dto.QnaDTO;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.repository.qna.QnaRepository;
import com.itdat.back.service.qna.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/qna")
public class QnaController {

    @Autowired
    private QnaService qnaService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QnaRepository qnaRepository;

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

    @GetMapping("/selected-qna")
    public ResponseEntity<Object> getSelectedQnaList(@RequestParam int selectedId) {
        System.out.println("selectedId = " + selectedId);

        try {
            Qna selectedQna = qnaService.findById(selectedId);
            if(selectedQna != null){
                return ResponseEntity.ok(selectedQna);
            }else {
                return ResponseEntity.ok("선택된 게시물의 정보가 비어있습니다.");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버측에서 문제가 발생하였습니다. "+e.getMessage());
        }
    }

    @PostMapping("/write")
    public ResponseEntity<Object> writeQna(@RequestBody Map<String,Object> qnaData) {
        System.out.println("qnaData asd  = " + qnaData);
        User slectedUser = userRepository.findByUserId(qnaData.get("loginedUserId").toString());

        Qna createdQna = new Qna();
        createdQna.setTitle(qnaData.get("title").toString());
        createdQna.setContents(qnaData.get("contents").toString());
        createdQna.setUser(slectedUser);
        createdQna.setCreateDateAt(LocalDateTime.now());
        createdQna.setUpdateAt(LocalDateTime.now());
//        System.out.println("qnaDTO.isSecret() = " + qnaDTO.isSecret());
        createdQna.setSecret((Boolean) qnaData.get("isSecret"));
      if((Boolean) qnaData.get("isSecret")){
          createdQna.setPassword(passwordEncoder.encode(qnaData.get("password").toString()));
      }else {
          createdQna.setPassword(null);
      }
//        createdQna.setPassword(passwordEncoder.encode(qnaData.get("password").toString()));
        createdQna.setCategory(QnaCategory.valueOf(qnaData.get("category").toString()));

        try {
            qnaRepository.save(createdQna);
            return ResponseEntity.ok("저장됐습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버측의 문제로 게시물이 저장되지 못 했습니다. " + e.getMessage());
        }

    }

    @PostMapping("/check-password")
    public ResponseEntity<Object> checkPassword(@RequestBody Map<String,String> data) {
        System.out.println("data = " + data);
        int selectedQnaId = Integer.parseInt(data.get("id"));
        String password = data.get("checkPassword");
        Qna selectedQna = qnaRepository.findById(selectedQnaId).orElse(null);
        // User slectedUser = userRepository.findByUserId(loginedUserId);

        // 비밀번호 검증
        try {
            assert selectedQna != null; // selectedQna가 비어있지 않음을 보장!!
            if(passwordEncoder.matches(password, selectedQna.getPassword())){
                return ResponseEntity.ok(true);
            }else {
                return ResponseEntity.ok(false);
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
