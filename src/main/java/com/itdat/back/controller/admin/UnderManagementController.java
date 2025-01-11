package com.itdat.back.controller.admin;

import com.itdat.back.entity.admin.ReportUser;
import com.itdat.back.entity.admin.UnderManagement;
import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.auth.UserStatus;
import com.itdat.back.model.dto.ReportUserDTO;
import com.itdat.back.repository.admin.ReportUserRepository;
import com.itdat.back.repository.admin.UnderManagementRepository;
import com.itdat.back.repository.auth.UserRepository;
import com.itdat.back.service.admin.UnderManagementService;
import com.itdat.back.service.auth.UserService;
import com.itdat.back.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.itdat.back.entity.auth.Role.ADMIN;

@RestController
@RequestMapping("/admin")
public class UnderManagementController {

    @Autowired
    private UnderManagementService underManagementService;

    @Autowired
    private UserService userService;

    @Autowired
    private UnderManagementRepository underManagementRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportUserRepository reportUserRepository;

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

    /**
     * 신고된 유저의 정보를 가져오는 컨트롤러
     */
    @GetMapping("/bring-reported-user-list")
    public ResponseEntity<Object> getReportedUserListBrief() {
        List<Object> reportedUserList = underManagementService.getReportedUsers();

        if (reportedUserList.isEmpty()) {
            return ResponseEntity.status(500).body("신고된 유저가 없습니다.");
        }
        return ResponseEntity.ok(reportedUserList);
    }

    /**
     * 사용자가 특정 유저를 신고하는 컨트롤러
     */
    @PostMapping("/report-user")
    public ResponseEntity<Object> reportUser(@RequestBody Map<String, Object> data) {
        System.out.println("datadatadatadatadatadata = " + data);
        String reason = (String) data.get("reason");
        String reportedUserEmail = (String) data.get("reportedUserEmail");
        String loginedUserEmail = (String) data.get("loginedUserEmail");
        System.out.println("reason = " + reason);
        System.out.println("reportedUserEmail = " + reportedUserEmail);
        System.out.println("loginedUserEmail = " + loginedUserEmail);

        User selectedUser = underManagementService.findByUserEmail(reportedUserEmail);
        System.out.println("selectedUser = " + selectedUser);
        User loginedUser = underManagementService.findByUserEmail(loginedUserEmail);
        System.out.println("loginedUser = " + loginedUser);

        ReportUserDTO reportUserDTO = new ReportUserDTO();
        reportUserDTO.setDescription(reason);
        reportUserDTO.setReportedUserId(selectedUser.getUserId()); // 신고의 대상이 되는 유저의 아이디
        reportUserDTO.setUserId(loginedUser.getUserId()); // 신고자의 아이디

        System.out.println("reportUserDTO = " + reportUserDTO);

        try {
            boolean result = underManagementService.reportUser(reportUserDTO);
            if (result) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.ok(false);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버와의 통신에서 오류 발생: " + e.getMessage());
        }
    }

    /** 로그인시 해당 유저의 제재 여부를 확인하는 컨트롤러 */
    @PostMapping("/sanction-verification")
    public ResponseEntity<Object> sanctionVerification(@RequestBody Map<String, Object> requestBody) {
        System.out.println("requestBody = " + requestBody);
        String currentUserId = (String) requestBody.get("userId");

        boolean isStillVanned = underManagementService.checkSanction(currentUserId);

        // 유저의 상태가 밴이면 true를 리턴한다.
        return ResponseEntity.ok(isStillVanned);
    }

    /** 관리자가 특정한 유저의 제재 이력을 초기화하는 컨트롤러 */
    @GetMapping("/selected-user-reset-state")
    public ResponseEntity<Object> getSelectedUserResetState(@RequestParam int id) {
        System.out.println("idididididididididididid = " + id);
        try {
            UnderManagement selectedUnderManagement = underManagementService.selectedUserResetState(id);
            if(selectedUnderManagement != null) {
                return ResponseEntity.ok(true);
            }else {
                return ResponseEntity.ok(false);
            }
        }catch (Exception e) {
            return ResponseEntity.status(500).body("서버와의 통신에서 오류 발생: " + e.getMessage());
        }
    }

    /** 관리자가 특정한 유저에게 벌점을 부과하는 컨트롤러 */
    @GetMapping("/sanctions-count-up")
    public ResponseEntity<Object> getSanctionsCountUp(@RequestParam int userId) {
        System.out.println("userId = " + userId);
        int selectedUserId = userId;

        boolean result = underManagementService.sanctionCountUp(selectedUserId);

        return ResponseEntity.ok(result);
    }

    /**
     * 사용자들의 신고 기록을 가져오는 컨트롤러
     */
    @GetMapping("/report-user-list")
    public ResponseEntity<Object> getReportUserList() {
        List<ReportUser> reportUserList = underManagementService.bringReportUserList();
        if (reportUserList.isEmpty()) {
            return ResponseEntity.status(500).body("아직 신고된 기록이 없습니.");
        }
        return ResponseEntity.ok(reportUserList);
    }

    /**
     * 사용자로부터 token을 받아 해당 유저의 ADMIN 권한을 체크하는 컨트롤러
     */
    @GetMapping("/users")
    public ResponseEntity<Object> getAdminUsers(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) { // 요청에 토큰이 담겨 있는지, 형식에 맞게 잘 왔는지 확인
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없거나 유효하지 않습니다.");
        }

        String jwtToken = token.substring(7); // 대가리("Bearer ") 따고 토큰만 추출

        String email = jwtTokenUtil.extractEmail(jwtToken); // 해당 토큰에서 email 추출

        User user = userRepository.findByUserEmail(email); // email로 해당 유저의 정보를 추출(ADMIN 권한을 이제 알 수 있다.)
        return ResponseEntity.ok(user);
//        if (user.getRole().equals(ADMIN)) {
//            return ResponseEntity.ok(true); // 해당 유저의 권한이 ADMIN이면 true를 리턴
//            // List<User> adminUsers = underManagementService.getAdminUsers();
//        } else {
//            return ResponseEntity.ok(false); // 해당 유저의 권한이 ADMIN이 아니면 false를 리턴
//        }
    }

    /**
     * 해당 유저의 상세 정보를 가져오는 컨트롤러. 사용자로부터 String 형식의 아이디를 받아 해당 유저의 int 형식의 id 값을 추출하여 조인 컬럼을 불러온다.
     */
    @GetMapping("/detail-info")
    public ResponseEntity<Object> detailInfo(@RequestParam String reportedUserId) {
        System.out.println("reportedUserId: " + reportedUserId);



        User selectedUser = userRepository.findByUserId(reportedUserId);
//        if (selectedUser == null) {
//            return ResponseEntity.ok("해당 유저는 이미 삭제되었습니다.");
//        }
        System.out.println("selectedUser.getId(): " + selectedUser.getId());
        UnderManagement detailInfo = underManagementService.findByUserId(selectedUser.getId());
        System.out.println("detailInfo = " + detailInfo);
        if (detailInfo == null) {
            return ResponseEntity.status(500).body("해당 유저는 존재하지 않습니다.");
        } else {
            return ResponseEntity.ok(detailInfo);
        }
    }

    /**
     * 신고 당한 유저의 제재 정보를 수정하는 컨트롤러
     */
    @Transactional // 예외 발생 시 데이터베이스 변경 사항을 자동을 롤백
    @PostMapping("/reported-info-update")
    public ResponseEntity<Object> reportedInfoUpdate(@RequestBody Map<String, String> data) {
        try {
            int id = Integer.parseInt(data.get("id"));

            UnderManagement selectedInfo = underManagementService.findByUserId(id);
            User selectedUser = userRepository.findById(id).orElse(null);

            selectedUser.setStatus(UserStatus.valueOf(data.get("status")));
            selectedInfo.setUser(selectedUser);
            selectedInfo.setStartDateAt(LocalDateTime.parse(data.get("startDateAt")));
            selectedInfo.setEndDateAt(LocalDateTime.parse(data.get("endDateAt")));
            selectedInfo.setUpdateAt(LocalDateTime.now());

            UnderManagement result = underManagementRepository.save(selectedInfo);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            // 잘못된 입력 값에 대한 처리
            return ResponseEntity.badRequest().body("입력 값이 유효하지 않습니다: " + e.getMessage());
        } catch (Exception e) {
            // 데이터베이스 저장 실패 등 일반적인 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버에서 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }

    /**
     * 신고 당한 유저와 관련된 정보들을 삭제하는 컨트롤러
     */
    @Transactional
    @DeleteMapping("/reported-info-delete")
    public ResponseEntity<Object> reportedInfoDelete(@RequestBody Map<String, String> data) {
        try {
            int id = Integer.parseInt(data.get("id"));
            UnderManagement selectedInfo = underManagementService.findByUserId(id);
            if (selectedInfo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 데이터가 존재하지 않습니다.");
            }
            // 정수형 id로 유저를 찾고, 찾은 유저의 문자열 id를 추출하고, 문자열 id로 ReportUser 클래스의 해당 컬럼을 삭제한다.
            String userId = selectedInfo.getUser().getUserId();
            List<ReportUser> reports = reportUserRepository.findByReportedUserId(userId);
            for (ReportUser report : reports) {
                reportUserRepository.delete(report);
            }
            underManagementRepository.delete(selectedInfo);
            return ResponseEntity.ok("해당 데이터가 정상적으로 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
}
