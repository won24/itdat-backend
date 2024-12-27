package com.itdat.back.service.admin;

import com.itdat.back.entity.admin.UnderManagement;
import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.auth.UserStatus;
import com.itdat.back.repository.admin.UnderManagementRepository;
import com.itdat.back.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.itdat.back.entity.auth.UserStatus.*;


@Service
public class UnderManagementService {

    @Autowired
    private UnderManagementRepository underManagementRepository;

    @Autowired
    private UserRepository userRepository;

    public List<User> reportedUserListDetail() {
        System.out.println("-------------------------------- 신고된 유저의 상세 정보 리스트를 가져오는 서비스 --------------------------------");
        List<User> reportedUserList = new ArrayList<>();

        // reportedUserList = userRepository.findByStatusIn(List.of(REPORTED, BANNED)); 두 개 이상을 찾을 땐 findBy~In
        reportedUserList = userRepository.findByStatusNot(ACTIVE); // 해당하는 매개 변수를 제외하고 찾을 땐 findBy~not
        if (reportedUserList.isEmpty()) {
            System.out.println("신고된 유저 List가 비어있습니다.");
            return reportedUserList;
        }
        return reportedUserList;


    }

//    public List<Object> reportedUserListBrief() {
//    }


//    public UnderManagement reportedUserListBrief(String userId) {
//        System.out.println("-------------------------------- 신고된 유저의 상세 정보 리스트를 가져오는 서비스 --------------------------------");
//
//        UnderManagement reportedUser= underManagementRepository.findByuserId(userId);
//        if (reportedUser == null) {
//            System.out.println("신고된 유저가 없습니다.");
//            return reportedUser;
//        }
//        return reportedUser;
//    }
}
