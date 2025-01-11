package com.itdat.back.service.board;

import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.board.History;
import com.itdat.back.entity.board.Portfolio;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.repository.board.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private UserRepository userRepository;


    // 유저 정보 가져오기
    public User findByUserEmail(String userEmail) {

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUserEmail(userEmail));

        if (!optionalUser.isPresent()) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        User user = optionalUser.get();
        return user;
    }


    // 가져오기
    public List<History> getHistoriesByUserEmail(String userEmail) {
        return historyRepository.findByUserEmail(userEmail);
    }


    // 저장
    public History saveHistory(History history) {
        if (history.getUserEmail() == null) {
            throw new IllegalArgumentException("히스토리에 연결된 유효한 사용자가 필요합니다.");
        }

        return historyRepository.save(history);
    }

    // 수정
    public History updateHistory(Integer id, History history) {
        Optional<History> findHistory = historyRepository.findById(id);

        if (findHistory.isPresent()) {
            History editHistory = findHistory.get();
            editHistory.setTitle(history.getTitle());
            editHistory.setContent(history.getContent());
            return historyRepository.save(editHistory);
        } else {
            throw new IllegalArgumentException("History " + id + " not found.");
        }
    }

    // 삭제
    public void deleteHistory(Integer id) {
        if (historyRepository.existsById(id)) {
            historyRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("History " + id + " not found.");
        }
    }
}
