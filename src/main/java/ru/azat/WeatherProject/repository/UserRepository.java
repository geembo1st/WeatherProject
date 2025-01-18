package ru.azat.WeatherProject.repository;

import ru.azat.WeatherProject.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();
    void addUser(User user);
    void deleteUser(Long id);
    User updateUser(Long id, User user);
    User showUserById(Long id);
}
