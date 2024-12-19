package com.itdat.back.repository.auth;

import com.itdat.back.entity.auth.SocialLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {

    // 소셜 로그인 정보를 프로바이더와 프로바이더 ID로 조회
    SocialLogin findByProviderAndProviderId(String provider, String providerId);
}
