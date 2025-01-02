package com.itdat.back.repository.admin;

import com.itdat.back.entity.admin.ReportUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportUserRepository extends JpaRepository<ReportUser, Integer> {
    List<ReportUser> findByReportedUserId(String userId);
}
