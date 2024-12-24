package com.itdat.back.filter;

import com.itdat.back.utils.JwtTokenUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import java.io.IOException;

public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = extractToken((HttpServletRequest) request);
//        System.out.println("Extracted Token: " + token);

        if (token != null) {
            if (jwtTokenUtil.validateToken(token)) {
                String email = jwtTokenUtil.extractEmail(token);
//                System.out.println("Valid Token. Email: " + email);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);

//                System.out.println("Security Context Authentication Set: " + SecurityContextHolder.getContext().getAuthentication());
            } else {
//                System.out.println("Invalid Token");
            }
        } else {
//            System.out.println("No Token Found");
        }

        chain.doFilter(request, response);

//        System.out.println("After Filter Chain - Security Context: " + SecurityContextHolder.getContext().getAuthentication());
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
//        System.out.println("Authorization Header: " + bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
