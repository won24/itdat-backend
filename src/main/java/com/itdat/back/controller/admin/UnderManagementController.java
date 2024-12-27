//package com.itdat.back.controller.admin;
//
//import com.itdat.back.entity.admin.UnderManagement;
//import com.itdat.back.entity.auth.User;
//import com.itdat.back.repository.admin.UnderManagementRepository;
//import com.itdat.back.service.admin.UnderManagementService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@RestController
//@RequestMapping("/admin")
//public class UnderManagementController {
//
//    @Autowired
//    private UnderManagementService underManagementService;
//
//    @Autowired
//    private UnderManagementRepository underManagementRepository;
//
//    @GetMapping("/bring-reported-user-list-detail")
//    public ResponseEntity<Object> getReportedUserListDetail() {
//        System.out.println("-------------------------------- 신고된 유저의 상세 정보 리스트를 가져오는 컨트롤러 --------------------------------");
//
//            List<User> reportedUserList = underManagementService.reportedUserListDetail();
//
//            if(reportedUserList.isEmpty()) {
//                return ResponseEntity.status(500).body("신고된 유저가 없습니다.");
//            }
//            return ResponseEntity.ok(reportedUserList);
//    }
//
////    @GetMapping("/bring-reported-user-list-brief")
////    public ResponseEntity<Object> getReportedUserListBrief() {
////        System.out.println("-------------------------------- 신고된 유저의 간략한 정보 리스트를 가져오는 컨트롤러 --------------------------------");
//
////        List<Object> reportedUserList = underManagementService.reportedUserListBrief();
//
////        if(reportedUserList.isEmpty()) {
////            return ResponseEntity.status(500).body("신고된 유저가 없습니다.");
////        }
////        return ResponseEntity.ok(reportedUserList);
//
//
////        List<User> selectedUserList = underManagementService.reportedUserListDetail();
////        List<UnderManagement> reportedUserList = new ArrayList<>();
////        for(User user : selectedUserList) {
////            String userId = user.getUserId();
////            UnderManagement reportedUserBriefInfo = underManagementService.reportedUserListBrief(userId);
////
////            reportedUserList.add(reportedUserBriefInfo);
////        }
////        if(reportedUserList.isEmpty()) {
////            return ResponseEntity.status(500).body("신고된 유저가 없습니다.");
////        }
////        return ResponseEntity.ok(reportedUserList);
//
////    }
//}
