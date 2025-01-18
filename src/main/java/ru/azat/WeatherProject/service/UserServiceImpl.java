package ru.azat.WeatherProject.service;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.azat.WeatherProject.dto.UserDTO;
import ru.azat.WeatherProject.model.Location;
import ru.azat.WeatherProject.model.Session;
import ru.azat.WeatherProject.model.User;
import ru.azat.WeatherProject.repository.LocationRepository;
import ru.azat.WeatherProject.repository.SessionRepository;
import ru.azat.WeatherProject.repository.UserRepository;
import ru.azat.WeatherProject.util.PersonNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final SessionRepository sessionRepository;

    @Autowired
    public UserServiceImpl(ModelMapper modelMapper, PasswordEncoder passwordEncoder, UserRepository userRepository, LocationRepository locationRepository, SessionRepository sessionRepository) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.sessionRepository = sessionRepository;
    }

    private User convertToUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);

        List<Session> sessions = userDTO.getSessionsIds().stream()
                .map(sessionId -> sessionRepository.showSessionById(sessionId))
                .collect(Collectors.toList());
        user.setSessions(sessions);

        Set<Location> locations = userDTO.getLocationIds().stream()
                .map(locationId -> locationRepository.showLocationById(locationId))
                .collect(Collectors.toSet());
        user.setLocations(locations);
        return user;
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        userDTO.setLocationIds(user.getLocations().stream()
                .map(Location::getId)
                .collect(Collectors.toSet()));

        userDTO.setSessionsIds(user.getSessions().stream()
                .map(Session::getId) // Теперь возвращает UUID
                .collect(Collectors.toSet()));


        return userDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addUser(UserDTO userDTO) {
        User user = convertToUser(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.addUser(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.showUserById(id);

        if (existingUser == null) {
            throw new PersonNotFoundException("User with ID " + id + " not found");
        }

        if (userDTO.getLogin() != null) {
            existingUser.setLogin(userDTO.getLogin());
        }

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        if (userDTO.getLocationIds() != null) {
            Set<Location> locations = userDTO.getLocationIds().stream()
                    .map(locationRepository::showLocationById)
                    .collect(Collectors.toSet());
            existingUser.setLocations(locations);
        }

        if (userDTO.getSessionsIds() != null) {
            List<Session> sessions = userDTO.getSessionsIds().stream()
                    .map(sessionRepository::showSessionById)
                    .collect(Collectors.toList());
            existingUser.setSessions(sessions);
        }

        userRepository.updateUser(id, existingUser);
        return convertToUserDTO(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO showUserById(Long id) {
        User userFounded = userRepository.showUserById(id);
        if (userFounded == null) {
            throw new PersonNotFoundException("Person not found");
        }
        return convertToUserDTO(userFounded);
    }
}
