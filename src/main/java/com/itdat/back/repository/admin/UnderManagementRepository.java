package com.itdat.back.repository.admin;

import com.itdat.back.entity.admin.UnderManagement;
import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.auth.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnderManagementRepository extends JpaRepository<UnderManagement, Integer> {
    UnderManagement findByuserId(User userId);

    @Query("SELECT um FROM UnderManagement um " +
            "JOIN um.user u " +
            "WHERE u.status <> 'ACTIVE'")
    List<Object> findAllByUserStatusNotACTIVE();

    UnderManagement findByUserId(int reportedUserId);

    Optional<UnderManagement> findByUser_UserId(String currentUserId);

    // UnderManagement findByReportedUserId(String reportedUserId);

    // UnderManagement findByUserId(User user);

    // UnderManagement findByReportedUserId(String reportedUserId);
}
