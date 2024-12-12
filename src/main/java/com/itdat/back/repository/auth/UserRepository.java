package com.itdat.back.repository.auth;

import com.itdat.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserId(String userId);
    User findByUserEmail(String email);
}