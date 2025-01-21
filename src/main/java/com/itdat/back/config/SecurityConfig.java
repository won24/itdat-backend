package com.itdat.back.config;

import com.itdat.back.filter.JwtAuthenticationFilter;
import com.itdat.back.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;

    public SecurityConfig(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .requiresChannel(channel -> channel
                        .anyRequest()
                        .requiresSecure()
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login",
                                         "/api/auth/register",
                                         "/api/auth/check-availability",
                                         "/nfc/**",
                                         "/card/**",
                                         "/uploads/**",
                                         "/api/mywallet/**",
                                         "/api/email/**",
                                        "/board/**").permitAll()
                        .requestMatchers("/api/oauth/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/admin/users").hasRole("ADMIN")
//                        .requestMatchers("/qna/**").authenticated()
                                .requestMatchers("/qna/**").permitAll()
                        .requestMatchers("/card/public/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless 설정
                .addFilterBefore(cspFilter(), JwtAuthenticationFilter.class); // Content-Security-Policy 필터 추가

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://192.168.0.31:3000",
                "http://10.0.2.2:8082",
                "http://192.168.0.37:3000",
                "http://localhost:8082",
                "http://192.168.0.19:3000",
                "https://www.itdat.store",
                "https://www.namewallet.store"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-Requested-With", "Origin", "Accept"));
        configuration.setExposedHeaders(List.of("Authorization", "Cross-Origin-Opener-Policy", "Cross-Origin-Embedder-Policy"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public OncePerRequestFilter cspFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                response.setHeader("Content-Security-Policy",
                        "script-src 'self' https://apis.google.com https://accounts.google.com https://developers.kakao.com 'unsafe-inline'; " +
                        "frame-src 'self' https://accounts.google.com https://apis.google.com https://developers.kakao.com https://nid.naver.com;" +
                        "connect-src 'self' https://oauth2.googleapis.com");
                response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
                response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");
                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
