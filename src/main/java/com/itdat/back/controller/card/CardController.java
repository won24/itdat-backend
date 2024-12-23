package com.itdat.back.controller.card;

import com.itdat.back.entity.auth.User;
import com.itdat.back.service.card.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping("/userinfo/{id}")
    public ResponseEntity<?> userInfo(@PathVariable("id") int id) {

        try {
            User user = cardService.selectById(id);
            if (user != null) {
                return ResponseEntity.ok(user);
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 유저");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에서 오류가 발생");
        }
    }

}
