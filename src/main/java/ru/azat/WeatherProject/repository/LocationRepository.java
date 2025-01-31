package ru.azat.WeatherProject.repository;

import ru.azat.WeatherProject.model.Location;

import java.util.List;

public interface LocationRepository {
    List<Location> getAllLocations();
    void addLocation(Location location);
    void deleteLocation(Long id);
    Location updateLocation(Long id, Location location);
    Location showLocationById(Long id);
    List<Location> findByUserId(Long userId);
}
