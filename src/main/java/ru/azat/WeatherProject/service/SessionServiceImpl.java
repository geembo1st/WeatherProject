package ru.azat.WeatherProject.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.azat.WeatherProject.dto.SessionDTO;

import java.util.List;
import java.util.UUID;

@Service
public class SessionServiceImpl implements SessionService {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SessionServiceImpl(ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<SessionDTO> getAllSessions() {
        return null;
    }

    @Override
    public void addSession(SessionDTO sessionDTO) {

    }

    @Override
    public void deleteSession(UUID id) {

    }

    @Override
    public SessionDTO updateSessions(UUID id, SessionDTO sessionDTO) {
        return null;
    }

    @Override
    public SessionDTO showSessionById(UUID id) {
        return null;
    }
}
