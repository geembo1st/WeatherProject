package ru.azat.WeatherProject.repository;

import ru.azat.WeatherProject.model.Session;

import java.util.List;
import java.util.UUID;

public interface SessionRepository {
    List<Session> getAllSessions();
    void addSession(Session session);
    void deleteSession(UUID id);
    Session updateSession(UUID id, Session session);
    Session showSessionById(UUID id);
}
