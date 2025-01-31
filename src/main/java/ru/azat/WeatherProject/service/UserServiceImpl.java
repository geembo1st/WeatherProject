package ru.azat.WeatherProject.service;


import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.azat.WeatherProject.dto.UserDTO;
import ru.azat.WeatherProject.model.Location;
import ru.azat.WeatherProject.model.Session;
import ru.azat.WeatherProject.model.User;
import ru.azat.WeatherProject.repository.LocationRepository;
import ru.azat.WeatherProject.repository.SessionRepository;
import ru.azat.WeatherProject.repository.UserRepository;
import ru.azat.WeatherProject.util.LocationNotFoundException;
import ru.azat.WeatherProject.util.PasswordUtils;
import ru.azat.WeatherProject.util.PersonNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final PasswordUtils passwordEncoder;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final SessionRepository sessionRepository;

    @Autowired
    public UserServiceImpl(ModelMapper modelMapper, PasswordUtils passwordEncoder, UserRepository userRepository, LocationRepository locationRepository, SessionRepository sessionRepository) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.sessionRepository = sessionRepository;
        log.debug("создан UserServiceImpl");
    }

    private User convertToUser(UserDTO userDTO) {
        log.debug("Конвертация DTO в User: {}", userDTO.getId());
        User user = modelMapper.map(userDTO, User.class);
        List<Session> sessions = userDTO.getSessionsIds().stream()
                .map(sessionId -> sessionRepository.showSessionById(sessionId))
                .collect(Collectors.toList());
        user.setSessions(sessions);

        List<Location> locations = userDTO.getLocationIds().stream()
                .map(locationId -> locationRepository.showLocationById(locationId))
                .collect(Collectors.toList());
        user.setLocations(locations);
        return user;
    }

    private UserDTO convertToUserDTO(User user) {
        log.debug("Конвертация User в DTO: {}", user.getId());
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        userDTO.setLocationIds(user.getLocations().stream()
                .map(Location::getId)
                .collect(Collectors.toSet()));

        userDTO.setSessionsIds(user.getSessions().stream()
                .map(Session::getId)
                .collect(Collectors.toSet()));

        return userDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.info("Получение всех пользователей");
        List<User> users = userRepository.getAllUsers();
        log.debug("Найдено {} пользователей", users.size());
        return users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addUser(UserDTO userDTO) {
        log.info("Добавление пользователя с id:{}", userDTO.getId());
        User user = convertToUser(userDTO);
        user.setPassword(passwordEncoder.hashPassword(user.getPassword()));
        userRepository.addUser(user);
        log.info("Пользователя с id:{} добавлен", userDTO.getId());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с id:{}", id);
        userRepository.deleteUser(id);
        log.info("Пользователя с id:{} удален", id);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("Обновление пользователя с id:{}", id);
        User existingUser = userRepository.showUserById(id);

        if (existingUser == null) {
            log.error("Пользователь с id:{} не найден", existingUser.getId());
            throw new PersonNotFoundException("User with ID " + id + " not found");
        }

        if (userDTO.getLogin() != null) {
            existingUser.setLogin(userDTO.getLogin());
        }

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.hashPassword(userDTO.getPassword()));
        }

        if (userDTO.getLocationIds() != null) {
            List<Location> locations = userDTO.getLocationIds().stream()
                    .map(locationRepository::showLocationById)
                    .collect(Collectors.toList());
            existingUser.setLocations(locations);
        }

        if (userDTO.getSessionsIds() != null) {
            List<Session> sessions = userDTO.getSessionsIds().stream()
                    .map(sessionRepository::showSessionById)
                    .collect(Collectors.toList());
            existingUser.setSessions(sessions);
        }
        userRepository.updateUser(id, existingUser);
        log.info("Пользователя с id:{} изменен", id);
        return convertToUserDTO(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO showUserById(Long id) {
        log.info("Поиск пользователя с id:{}", id);
        User userFounded = userRepository.showUserById(id);
        if (userFounded == null) {
            log.error("Пользователь не найден");
            throw new PersonNotFoundException("Person not found");
        }
        log.info("Пользователя с id:{} найден", id);
        return convertToUserDTO(userFounded);
    }

    @Override
    @Transactional
    public UserDTO getUserByLogin(String login) {
        log.info("Поиск пользователя с login:{}", login);
        User user = userRepository.findByLogin(login);
            if(user == null) {
                log.error("Пользователь не найден");
                throw  new PersonNotFoundException("Person not found");
            }
        log.info("Пользователь с login:{} найден", login);
        return convertToUserDTO(user);
    }

    @Override
    @Transactional
    public UserDTO addLocationToUser(Long userId, Long locationId) {
        log.info("Добавление локации с id:{} пользователю с id:{}", locationId, userId);

        User existingUser = userRepository.showUserById(userId);
        if (existingUser == null) {
            log.error("Пользователь с id:{} не найден", userId);
            throw new PersonNotFoundException("User with ID " + userId + " not found");
        }

        Location location = locationRepository.showLocationById(locationId);
        if (location == null) {
            log.error("Локация с id:{} не найдена", locationId);
            throw new LocationNotFoundException("Location with ID " + locationId + " not found");
        }

        existingUser.getLocations().add(location);
        userRepository.updateUser(userId, existingUser);
        log.info("Локация с id:{} добавлена пользователю с id:{}", locationId, userId);

        return convertToUserDTO(existingUser);
    }

    @Override
    @Transactional
    public UserDTO removeLocationFromUser(Long userId, Long locationId) {
        log.info("Удаление локации с id:{} у пользователя с id:{}", locationId, userId);

        User existingUser = userRepository.showUserById(userId);
        if (existingUser == null) {
            log.error("Пользователь с id:{} не найден", userId);
            throw new PersonNotFoundException("User with ID " + userId + " not found");
        }

        Location location = locationRepository.showLocationById(locationId);
        if (location == null) {
            log.error("Локация с id:{} не найдена", locationId);
            throw new LocationNotFoundException("Location with ID " + locationId + " not found");
        }

        existingUser.getLocations().remove(location);

        userRepository.updateUser(userId, existingUser);
        log.info("Локация с id:{} удалена у пользователя с id:{}", locationId, userId);

        return convertToUserDTO(existingUser);
    }

}
