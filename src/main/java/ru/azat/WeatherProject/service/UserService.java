package ru.azat.WeatherProject.service;



import ru.azat.WeatherProject.dto.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    void addUser(UserDTO userDTO);
    void deleteUser(Long id);
    UserDTO updateUser(Long id, UserDTO userDTO);
    UserDTO showUserById(Long id);
    UserDTO getUserByLogin(String login);
    UserDTO addLocationToUser(Long userId, Long locationId);
     UserDTO removeLocationFromUser(Long userId, Long locationId);
}
