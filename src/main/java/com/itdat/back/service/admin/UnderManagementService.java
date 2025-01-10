package com.itdat.back.service.admin;

import com.itdat.back.entity.admin.ReportUser;
import com.itdat.back.entity.admin.UnderManagement;
import com.itdat.back.entity.auth.Role;
import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.auth.UserStatus;
import com.itdat.back.model.dto.ReportUserDTO;
import com.itdat.back.repository.admin.ReportUserRepository;
import com.itdat.back.repository.admin.UnderManagementRepository;
import com.itdat.back.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.itdat.back.entity.auth.UserStatus.*;


@Service
public class UnderManagementService {

    @Autowired
    private UnderManagementRepository underManagementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportUserRepository reportUserRepository;

//    public List<User> reportedUserListDetail() {
//        System.out.println("-------------------------------- 신고된 유저의 상세 정보 리스트를 가져오는 서비스 --------------------------------");
//        List<User> reportedUserList = new ArrayList<>();
//
//        // reportedUserList = userRepository.findByStatusIn(List.of(REPORTED, BANNED)); 두 개 이상을 찾을 땐 findBy~In
//        reportedUserList = userRepository.findByStatusNot(ACTIVE); // 해당하는 매개 변수를 제외하고 찾을 땐 findBy~not
//        if (reportedUserList.isEmpty()) {
//            System.out.println("신고된 유저 List가 비어있습니다.");
//            return reportedUserList;
//        }
//        return reportedUserList;
//
//
//    }

    /**
     * 신고된 유저의 정보를 가져오는 서비스
     */
    public List<Object> getReportedUsers() {
        List<Object> underManagements = underManagementRepository.findAllByUserStatusNotACTIVE();
        return underManagements; // Object 형태로 변환
        // return new ArrayList<>(underManagements); // underManagements를 새로 하나 더 만드는 행위.. 그럴 필요가 없다..
    }

    /**
     * 사용자가 특정 유저를 신고하는 서비스
     */
    public boolean reportUser(ReportUserDTO reportUserDTO) {
        try {
            // reportUserDTO.getReportedUserId(); = 스트링이다.
            ReportUser reportUser = new ReportUser();
            User selectedUser = userRepository.findByUserId(reportUserDTO.getReportedUserId());
            UnderManagement selectedUnderManagement = underManagementRepository.findByUserId(selectedUser.getId());

            // 사용자가 입력한 값을 대입 시키는 로직
            reportUser.setReportedUserId(reportUserDTO.getReportedUserId());
            reportUser.setDescription(reportUserDTO.getDescription());
            reportUser.setUserId(reportUserDTO.getUserId());
            reportUser.setReportDateAt(reportUserDTO.getReportDateAt());
            ReportUser insertedReportUser = reportUserRepository.save(reportUser);

            // 신고 당한 유저의 신고 카운트를 누적 증가 시키는 로직
            int currentReportedCount = selectedUnderManagement.getReportedCount();
            selectedUnderManagement.setReportedCount(currentReportedCount + 1);
            if (selectedUnderManagement.getUser().getStatus() == UserStatus.ACTIVE) {
                selectedUnderManagement.getUser().setStatus(REPORTED);
            }
            // 신고 당한 유저의 벌점을 누적 증가 시키는 로직
            int currentDemerit = selectedUnderManagement.getDemerit();
            selectedUnderManagement.setDemerit(currentDemerit + 1);
            if (selectedUnderManagement.getDemerit() > 2){
                selectedUnderManagement.getUser().setStatus(BANNED);
                selectedUnderManagement.setStartDateAt(LocalDateTime.now());
                selectedUnderManagement.setEndDateAt(LocalDateTime.now().plusDays(7));
            }else if (selectedUnderManagement.getDemerit() > 6){
                selectedUnderManagement.getUser().setStatus(BANNED);
                selectedUnderManagement.setStartDateAt(LocalDateTime.now());
                selectedUnderManagement.setEndDateAt(LocalDateTime.now().plusYears(100));
            }

            underManagementRepository.save(selectedUnderManagement);

            if (insertedReportUser == null) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            System.out.println("ReportUser 저장 실패: " + e.getMessage());
            return false; // 저장 실패
        }
    }

    /**
     * 사용자들의 신고 기록을 가져오는 서비스
     */
    public List<ReportUser> bringReportUserList() {
        List<ReportUser> reportUserList = reportUserRepository.findAll();
        return reportUserList;

    }

    public List<User> getAdminUsers() {
        return userRepository.findByRole(Role.ADMIN);
    }

    /** pk 값으로 엔티티를 조회하는 서비스 */
    public UnderManagement findByUserId(int reportedUserId) {
        UnderManagement selectedUserInfo = underManagementRepository.findByUserId(reportedUserId);
        return selectedUserInfo;
    }

    /** 로그인 시 해당 유저의 벤 상태를 체크하는 서비스 */
    public boolean checkSanction(String currentUserId) {
        UnderManagement currentUserUnderManagement = underManagementRepository.findByUser_UserId(currentUserId);
        User currentUser = userRepository.findByUserId(currentUserId);

        // 현재 날짜와 제재 종료일자를 비교하여 유저의 벤 상태를 변경하는 로직
        if(currentUserUnderManagement.getEndDateAt() != null
                && currentUserUnderManagement.getEndDateAt().isBefore(LocalDateTime.now())) {
            currentUser.setStatus(REPORTED);
            currentUserUnderManagement.setUser(currentUser);
            currentUserUnderManagement.setDemerit(null);
        }
        if (currentUserUnderManagement.getUser().getStatus() == BANNED) {
            return true;
        } else {
            return false;
        }

    }

    /** 관리자가 벌점을 부과하고 그에 따른 적당한 제재를 자동으로 부과하는 로직 */
    public boolean sanctionCountUp(int selectedUserId) {
        try {
            UnderManagement selectedUnderManagement = underManagementRepository.findByUserId(selectedUserId);
            int currentCount = selectedUnderManagement.getDemerit(); //벌점 기존 값
            selectedUnderManagement.setDemerit(currentCount + 1); // 기존 벌점에 +1
            underManagementRepository.save(selectedUnderManagement);

            int currentBannedCount = selectedUnderManagement.getBannedCount(); // 제재 횟수 기존 값
            // 벌점 3점 = 7일 제재, 7점 = 100년(영구) 제재.
            // 제재 횟수 7일 제재는 +1, 영구 제재는 +1000.
            if (selectedUnderManagement.getDemerit() > 2) {
                selectedUnderManagement.getUser().setStatus(BANNED);
                selectedUnderManagement.setBannedCount(currentBannedCount + 1);
                selectedUnderManagement.setStartDateAt(LocalDateTime.now());
                selectedUnderManagement.setEndDateAt(selectedUnderManagement.getStartDateAt().plusDays(7));
            } else if (selectedUnderManagement.getDemerit() > 6) {
                selectedUnderManagement.getUser().setStatus(BANNED);
                selectedUnderManagement.setBannedCount(currentBannedCount + 1000);
                selectedUnderManagement.setStartDateAt(LocalDateTime.now());
                selectedUnderManagement.setEndDateAt(selectedUnderManagement.getStartDateAt().plusYears(100));
            } else {
                selectedUnderManagement.getUser().setStatus(ACTIVE);
            }
            UnderManagement updatedUnderManagement = underManagementRepository.save(selectedUnderManagement);
            if (updatedUnderManagement == null) {
                return false;
            }
            // 벌점을 성공적으로 부과시 true를 리턴
            return true;
        } catch (Exception e) {
            // 예외 발생 시 로그 출력 및 실패 처리
            System.out.println("sanctionCountUp 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false; // 실패 시 false 반환
        }
    }

    /** 관리자가 특정 유저의 제재 이력을 초기화하는 서비스 */
    @Transactional
    public UnderManagement selectedUserResetState(int id) {
        UnderManagement selectedUnderManagement = underManagementRepository.findByUserId(id);
        /*selectedUnderManagement.setCumulativeCount(0);*/
        selectedUnderManagement.setDemerit(0); // 벌점 초기화
        selectedUnderManagement.getUser().setStatus(ACTIVE); // 신고 상태 초기화
        selectedUnderManagement.setUpdateAt(LocalDateTime.now());
        selectedUnderManagement.setStartDateAt(null);
        selectedUnderManagement.setEndDateAt(null);
        underManagementRepository.save(selectedUnderManagement);

        return selectedUnderManagement;
    }


    public User findByUserEmail(String reportedUserEmail) {
//        UnderManagement selectecUnderManagement = UnderManagementRepository.findByUserEmail(reportedUserEmail);
        User selectedUser = userRepository.findByUserEmail(reportedUserEmail);

        return selectedUser;
    }
}
