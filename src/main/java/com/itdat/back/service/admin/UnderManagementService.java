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

    /** 신고된 유저의 정보를 가져오는 서비스 */
    public List<Object> getReportedUsers() {
        List<Object> underManagements = underManagementRepository.findAllByUserStatusNotACTIVE();
        return underManagements; // Object 형태로 변환
        // return new ArrayList<>(underManagements); // underManagements를 새로 하나 더 만드는 행위.. 그럴 필요가 없다..
    }

    /** 사용자가 특정 유저를 신고하는 서비스 */
    public boolean reportUser(ReportUserDTO reportUserDTO) {
        // reportUserDTO.getReportedUserId(); = 스트링이다.
        ReportUser reportUser = new ReportUser();
        User selectedUser = userRepository.findByUserId(reportUserDTO.getReportedUserId());
        UnderManagement selectedUnderManagement = underManagementRepository.findByUserId(selectedUser.getId());

        reportUser.setReportedUserId(reportUserDTO.getReportedUserId());
        reportUser.setDescription(reportUserDTO.getDescription());
        reportUser.setUserId(reportUserDTO.getUserId());
        reportUser.setReportDateAt(reportUserDTO.getReportDateAt());

        ReportUser insertedReportUser = reportUserRepository.save(reportUser);

        // 신고된 내용이 저장됐으니 해당 유저의 신고 누적 횟수를 유저 정보에 대입하자!!
        // 신고된 유저의 모든 신고 리스트 조회
        List<ReportUser> reportList = reportUserRepository.findAllByReportedUserId(reportUserDTO.getReportedUserId());
        // 신고 횟수를 계산하여 담는 변수
        int reportCount = reportList.size();
        // 유저 정보에 카운트를 대입
        selectedUnderManagement.setCumulativeCount(reportCount);
        // 저장~!!!!!!
        underManagementRepository.save(selectedUnderManagement);

        if (insertedReportUser == null) {
            return false;
        }
        return true;
    }

    /** 사용자들의 신고 기록을 가져오는 서비스 */
    public List<ReportUser> bringReportUserList() {
        List<ReportUser> reportUserList = reportUserRepository.findAll();
        return reportUserList;

    }

    public List<User> getAdminUsers() {
        return userRepository.findByRole(Role.ADMIN);
    }

    public UnderManagement findByUserId(int reportedUserId) {
        System.out.println("reportedUserId service = " + reportedUserId);
        UnderManagement selectedUserInfo = underManagementRepository.findByUserId(reportedUserId);
        System.out.println("selectedUserInfo = " + selectedUserInfo);


        return selectedUserInfo;
    }
}
