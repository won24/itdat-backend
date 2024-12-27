package com.itdat.back.service.admin;

import com.itdat.back.entity.admin.ReportUser;
import com.itdat.back.entity.admin.UnderManagement;
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

    public List<User> reportedUserListDetail() {
        System.out.println("-------------------------------- 신고된 유저의 상세 정보 리스트를 가져오는 서비스 --------------------------------");
        List<User> reportedUserList = new ArrayList<>();

        // reportedUserList = userRepository.findByStatusIn(List.of(REPORTED, BANNED)); 두 개 이상을 찾을 땐 findBy~In
        reportedUserList = userRepository.findByStatusNot(BANNED); // 해당하는 매개 변수를 제외하고 찾을 땐 findBy~not // 추후 BANNED 수정 예정
        if (reportedUserList.isEmpty()) {
            System.out.println("신고된 유저 List가 비어있습니다.");
            return reportedUserList;
        }
        return reportedUserList;


    }

    public List<Object> getReportedUsers() {
        System.out.println("-------------------------------- 신고된 유저의 상세 정보 리스트를 가져오는 서비스 --------------------------------");

        List<Object> underManagements = underManagementRepository.findAllByUserStatusNotBanned();
        return new ArrayList<>(underManagements); // Object 형태로 변환
    }

    public boolean reportUser(ReportUserDTO reportUserDTO) {
        System.out.println("-------------------------------- 사용자가 특정 유저를 신고하는 서비스 --------------------------------");

        ReportUser reportUser = new ReportUser();

        reportUser.setReportedUserId(reportUserDTO.getReportedUserId());
        reportUser.setDescription(reportUserDTO.getDescription());
        reportUser.setUserId(reportUserDTO.getUserId());
        reportUser.setReportDateAt(reportUserDTO.getReportDateAt());

        ReportUser insertedReportUser = reportUserRepository.save(reportUser);

        if (insertedReportUser == null) {
            return false;
        }
        return true;

    }

    public List<ReportUser> bringReportUserList() {
        System.out.println("-------------------------------- 사용자들의 신고 기록을 가져오는 서비스 --------------------------------");
        List<ReportUser> reportUserList = reportUserRepository.findAll();
        return reportUserList;

    }

//    public List<User> getAdminUsers() {
//        return userRepository.findByRole(Role.ADMIN);
//    }
}
