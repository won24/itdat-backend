package com.itdat.back.repository.admin;

import com.itdat.back.entity.admin.ReportUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportUserRepository extends JpaRepository<ReportUser, Integer> {
}
