package com.mediconnect.config;

import com.mediconnect.service.AuthService;
import com.mediconnect.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            Optional<User> userOpt = authService.getCurrentUser(session);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Create authentication token
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
