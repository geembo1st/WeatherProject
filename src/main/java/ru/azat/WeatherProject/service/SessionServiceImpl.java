package ru.azat.WeatherProject.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.azat.WeatherProject.dto.SessionDTO;
import ru.azat.WeatherProject.model.Session;
import ru.azat.WeatherProject.repository.SessionRepository;
import ru.azat.WeatherProject.repository.UserRepository;
import ru.azat.WeatherProject.util.PersonNotFoundException;
import ru.azat.WeatherProject.util.SessionNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SessionServiceImpl implements SessionService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    @Autowired
    public SessionServiceImpl(ModelMapper modelMapper, UserRepository userRepository, SessionRepository sessionRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        log.debug("создан SessionServiceImpl");
    }

    public SessionDTO convertToSessionDTO(Session session) {
        log.debug("Конвертация Session в DTO: {}", session.getId());
        SessionDTO sessionDTO = modelMapper.map(session, SessionDTO.class);
        sessionDTO.setId(session.getId() != null ? session.getId().toString() : null );
        sessionDTO.setUserId(session.getUser() != null ? session.getUser().getId() : null);
        return sessionDTO;
    }

    public Session convertToSession(SessionDTO sessionDTO) {
        log.debug("Конвертация DTO в Session: {}", sessionDTO.getId());
        Session session = modelMapper.map(sessionDTO, Session.class);
        session.setId(sessionDTO.getId() != null ? UUID.fromString(sessionDTO.getId()) : null);
        session.setUser(userRepository.showUserById(sessionDTO.getUserId()));
        return session;
    }

    @Transactional(readOnly = true)
    @Override
    public List<SessionDTO> getAllSessions() {
        log.info("Получение всех сессий");
        List<Session> sessions = sessionRepository.getAllSessions();
        log.debug("Найдено {} сессий", sessions.size());
        return sessions.stream()
                .map(this::convertToSessionDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void addSession(SessionDTO sessionDTO) {
        log.info("Добавление сессии с id:{}", sessionDTO.getId());
        Session session = convertToSession(sessionDTO);
        if(sessionDTO.getId() == null) {
            log.error("Сессия с id:{} не найдена", sessionDTO.getId());
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if(session.getUser() == null) {
            log.error("Сессия с id:{} не найдена", sessionDTO.getUserId());
            throw new PersonNotFoundException("User not found with ID: " + sessionDTO.getUserId());
        }
        sessionRepository.addSession(session);
        log.info("Сессия с id:{} добавлена", sessionDTO.getId());
    }

    @Transactional
    @Override
    public void deleteSession(UUID id) {
        log.info("Удаление сессии с id:{}", id);
        sessionRepository.deleteSession(id);
        log.info("Сессия с id:{} удалена", id);
    }

    @Transactional
    @Override
    public SessionDTO updateSessions(UUID id, SessionDTO sessionDTO) {
        log.info("Обновление сессии с id:{}", id);
        Session session = sessionRepository.showSessionById(id);
        if (session == null) {
            log.error("Сессия не найдена");
            throw new SessionNotFoundException("Session not found");
        }
        session.setExpiresAt(sessionDTO.getExpiresAt());
        session.setUser(userRepository.showUserById(sessionDTO.getUserId()));
        sessionRepository.updateSession(id, session);
        log.info("Сессия с id:{} изменена", id);
        return convertToSessionDTO(session);
    }

    @Transactional(readOnly = true)
    @Override
    public SessionDTO showSessionById(UUID id) {
        log.info("Поиск Сессии с id:{}", id);
        Session session = sessionRepository.showSessionById(id);
        if (session == null) {
            log.error("Сессия не найдена");
            throw new SessionNotFoundException("Session not found");
        }
        log.info("Сессия с id:{} найдена", id);
        return convertToSessionDTO(session);
    }
}
