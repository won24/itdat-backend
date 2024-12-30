package com.itdat.back.repository.auth;

import com.itdat.back.entity.auth.Role;
import com.itdat.back.entity.auth.User;
import com.itdat.back.entity.auth.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserId(String userId);
    User findByUserEmail(String email);
//    List<User> findByRole(Role role);
    boolean existsByUserId(String userId);
    boolean existsByUserEmail(String email);

    List<User> findByStatusNot(UserStatus status);
}