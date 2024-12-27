package com.itdat.back.controller.admin;

import com.itdat.back.entity.admin.ReportUser;
import com.itdat.back.entity.admin.UnderManagement;
import com.itdat.back.entity.auth.User;
import com.itdat.back.model.dto.ReportUserDTO;
import com.itdat.back.repository.admin.UnderManagementRepository;
import com.itdat.back.service.admin.UnderManagementService;
import com.itdat.back.service.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class UnderManagementController {

    @Autowired
    private UnderManagementService underManagementService;

    @Autowired
    private UserService userService;

    @Autowired
    private UnderManagementRepository underManagementRepository;

    @GetMapping("/bring-reported-user-list-detail")
    public ResponseEntity<Object> getReportedUserListDetail() {
        System.out.println("-------------------------------- 신고된 유저의 상세 정보 리스트를 가져오는 컨트롤러 --------------------------------");

            List<User> reportedUserList = underManagementService.reportedUserListDetail();

            if(reportedUserList.isEmpty()) {
                return ResponseEntity.status(500).body("신고된 유저가 없습니다.");
            }
            return ResponseEntity.ok(reportedUserList);
    }

    @GetMapping("/bring-reported-user-list-brief")
    public ResponseEntity<Object> getReportedUserListBrief() {
        System.out.println("-------------------------------- 신고된 유저의 간략한 정보 리스트를 가져오는 컨트롤러 --------------------------------");

        List<Object> reportedUserList = underManagementService.getReportedUsers();

        if(reportedUserList.isEmpty()) {
            return ResponseEntity.status(500).body("신고된 유저가 없습니다.");
        }
        return ResponseEntity.ok(reportedUserList);
    }

    @PostMapping("/report-user")
    public ResponseEntity<Object> reportUser(@RequestBody ReportUserDTO reportUserDTO) {
        System.out.println("-------------------------------- 사용자가 특정 유저를 신고하는 컨트롤러 --------------------------------");
        // 사용자로부터 특정 유저의 아이디와 설명(신고 이유) 그리고 신고 당사자의 아이디를 받아낸다.
        // 상기 정보들에 현재 시간을 추가해 ReportUser 엔티티에 추가한다.

        System.out.println("reportUserDTO = " + reportUserDTO);

        boolean result = underManagementService.reportUser(reportUserDTO);
        if (result){
            return ResponseEntity.ok("신고되었습니다.");
        }
        return ResponseEntity.status(500).body("신고에 실패하였습니다.");
    }

    @GetMapping("/report-user-list")
    public ResponseEntity<Object> getReportUserList() {
        System.out.println("-------------------------------- 사용자들의 신고 기록을 가져오는 컨트롤러 --------------------------------");

        List<ReportUser> reportUserList = underManagementService.bringReportUserList();
        if(reportUserList.isEmpty()) {
            return ResponseEntity.status(500).body("아직 신고된 기록이 없습니.");
        }
        return ResponseEntity.ok(reportUserList);
    }

//    @GetMapping("/admin/users")
//    public ResponseEntity<List<User>> getAdminUsers() {
//        List<User> adminUsers = underManagementService.getAdminUsers();
//        return ResponseEntity.ok(adminUsers);
//    }

}
