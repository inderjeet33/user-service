package com.prerana.userservice.filter;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.repository.UserRepository;
import com.prerana.userservice.service.JwtService;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class JwtFilter extends OncePerRequestFilter {
//
//    private final JwtService jwt;
//    private final UserRepository repo;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
//            throws ServletException, IOException {
//
//        String authHeader = req.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            var claims = jwt.extractClaims(token);
//            String mobile = jwt.extractMobile(token);
//
//            Long userId = claims.get("userId", Long.class);
//
//            req.setAttribute("userId", userId);
//            req.setAttribute("userType", claims.get("userType",String.class));
//            req.setAttribute("role", claims.get("role", String.class));
//            UserEntity user = repo.findByMobileNumber(mobile).orElse(null);
//
//// Spring Security requires ROLE_ prefix
//            if (user != null) {
//                String role = claims.get("role", String.class);
//                String userType = claims.get("userType", String.class);
//
//                List<GrantedAuthority> authorities = new ArrayList<>();
//                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
//                authorities.add(new SimpleGrantedAuthority("TYPE_" + userType));
//
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(
//                                user, null, authorities
//                        );
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//
//        chain.doFilter(req, res);
//    }
//}
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UserRepository repo;

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain
    ) throws ServletException, IOException {

        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        try {
            String token = authHeader.substring(7);

            if (!jwt.validateToken(token)) {
                chain.doFilter(req, res);
                return;
            }

            var claims = jwt.extractClaims(token);

            Long userId = claims.get("userId", Long.class);
            String role = claims.get("role", String.class);
            String userType = claims.get("userType", String.class);
            String mobile = jwt.extractMobile(token);

            if (userId == null) {
                chain.doFilter(req, res);
                return;
            }

            // ✅ make available in controller
            req.setAttribute("userId", userId);
            req.setAttribute("role", role);
            req.setAttribute("userType", userType);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserEntity user = repo.findByMobileNumber(mobile).orElse(null);

                if (user != null) {
                    List<GrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role),
                            new SimpleGrantedAuthority("TYPE_" + userType)
                    );

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    user, null, authorities
                            );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

        } catch (Exception e) {
            // token invalid / expired → ignore
        }

        chain.doFilter(req, res);
    }
}
