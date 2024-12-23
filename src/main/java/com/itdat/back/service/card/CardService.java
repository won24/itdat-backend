package com.itdat.back.service.card;

import com.itdat.back.entity.auth.User;
import com.itdat.back.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CardService {

    @Autowired
    private UserRepository userRepository;

    // 유저 정보 가져오기
    public User selectById(int id) {
        User user = userRepository.findById(id);
        if(Objects.isNull(user)){
            return null;
        }
        return user;
    }
}
