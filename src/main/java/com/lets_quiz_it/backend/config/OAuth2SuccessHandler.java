package com.lets_quiz_it.backend.config;

import com.lets_quiz_it.backend.entity.User;
import com.lets_quiz_it.backend.service.UserService;
import com.lets_quiz_it.backend.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name  = oAuth2User.getAttribute("name");

        // Check whitelist
        if (!userService.isWhitelisted(email)) {
            response.sendRedirect(frontendUrl + "/unauthorized");
            return;
        }

        // Find or create user in DB
        User user = userService.findOrCreateUser(email, name);

        // Generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // Send token as HttpOnly cookie
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);   // set true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(86400);   // 1 day
        response.addCookie(cookie);

        // Redirect to frontend dashboard
        response.sendRedirect(frontendUrl + "/dashboard");
    }
}