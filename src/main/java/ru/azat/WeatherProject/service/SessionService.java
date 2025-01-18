package ru.azat.WeatherProject.service;

import ru.azat.WeatherProject.dto.SessionDTO;

import java.util.List;
import java.util.UUID;

public interface SessionService {
    List<SessionDTO> getAllSessions();
    void addSession(SessionDTO sessionDTO);
    void deleteSession(UUID id);
    SessionDTO updateSessions(UUID id, SessionDTO sessionDTO);
    SessionDTO showSessionById(UUID id);
}
