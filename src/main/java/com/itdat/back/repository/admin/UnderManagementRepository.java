package com.itdat.back.repository.admin;

import com.itdat.back.entity.admin.UnderManagement;
import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.auth.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnderManagementRepository extends JpaRepository<UnderManagement, Integer> {
    UnderManagement findByuserId(User userId);

    @Query("SELECT um FROM UnderManagement um " +
            "JOIN um.user u " +
            "WHERE u.status <> 'BANNED'") // 후에 'ACTIVE' 로 변경해야 함. 지금은 테스트를 위해 BANNED를 제외한 값들을 출력.. (DB에 값이 없어서링..)
    List<UnderManagement> findAllByUserStatusNotActive();


}
