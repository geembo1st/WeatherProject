package ru.azat.WeatherProject.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.azat.WeatherProject.service.AuthService;

import java.util.UUID;

@Component
@Slf4j
public class SessionInterceptor implements HandlerInterceptor {
    private final AuthService authService;

    @Autowired
    public SessionInterceptor(AuthService authService) {
        this.authService = authService;
    }

    public boolean preHandler (HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("SESSIONID")) {
                    UUID sessionId = UUID.fromString(cookie.getValue());
                    if (authService.isSessionValid(sessionId)) {
                        log.info("SESSIONID в куки: {}", cookie.getValue());
                        return true;
                    }
                }
            }
        }
        response.sendRedirect("/auth/login");
        return false;
    }
}
