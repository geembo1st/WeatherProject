package ru.azat.WeatherProject.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.azat.WeatherProject.model.Session;

import java.util.List;
import java.util.UUID;

@Repository
public class SessionRepositoryImpl implements SessionRepository {
    @PersistenceContext
    EntityManager entityManager;


    @Override
    public List<Session> getAllSessions() {
        return entityManager.createQuery("From Session ").getResultList();
    }

    @Override
    public void addSession(Session session) {
        entityManager.persist(session);
    }

    @Override
    public void deleteSession(UUID id) {
        Session session = showSessionById(id);
        if(session != null) {
            entityManager.remove(session);
        } else {
            throw new EntityNotFoundException("Session not found");
        }
    }

    @Override
    public Session updateSession(UUID id, Session session) {
        return entityManager.merge(session);
    }

    @Override
    public Session showSessionById(UUID id) {
        return entityManager.find(Session.class, id);
    }
}
