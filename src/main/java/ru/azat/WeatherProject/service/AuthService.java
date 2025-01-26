package ru.azat.WeatherProject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.azat.WeatherProject.model.Session;
import ru.azat.WeatherProject.model.User;
import ru.azat.WeatherProject.repository.SessionRepository;
import ru.azat.WeatherProject.repository.UserRepository;
import ru.azat.WeatherProject.util.PasswordUtils;
import ru.azat.WeatherProject.util.UserAlreadyExistsException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordUtils passwordUtils;
    private final SessionRepository sessionRepository;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordUtils passwordUtils, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public void registerUser(String login, String password) {
        if(userRepository.findByLogin(login) != null) {
            throw new UserAlreadyExistsException("User already exist");
        }

        String hashPassword = passwordUtils.hashPassword(password);
        User user = new User();
        user.setLogin(login);
        user.setPassword(hashPassword);
        log.info("Пользователь {} успешно зарегистрирован", login);
        userRepository.addUser(user);
    }

    @Transactional
    public UUID loginUser(String login, String password) {
        User user = userRepository.findByLogin(login);
        if(user == null || !passwordUtils.checkPassword(password, user.getPassword())) {
            log.error("Не удалось войти в систему {}", login);
            throw new RuntimeException("Invalid login or password");
        }
        Session session = new Session();
        session.setUser(user);
        session.setExpiresAt(LocalDateTime.now().plusHours(2));
        sessionRepository.addSession(session);
        log.info("Пользователь {} успешно вошел в систему", login);
        return session.getId();
    }

    public boolean isSessionValid(UUID sessionId) {
        Session session = sessionRepository.showSessionById(sessionId);
        return session!=null && session.getExpiresAt().isAfter(LocalDateTime.now());
    }

    public void logoutUser(UUID sessionId) {
        sessionRepository.deleteSession(sessionId);
    }
}
