package ru.azat.WeatherProject.service;

import ru.azat.WeatherProject.dto.LocationDTO;

import java.util.List;

public interface LocationService {
    List<LocationDTO> getAllLocations();
    void addLocation(LocationDTO locationDTO);
    void deleteLocation(Long id);
    LocationDTO updateLocation(Long id, LocationDTO locationDTO);
    LocationDTO showLocationById(Long id);
    List<LocationDTO> getLocationsByUserId(Long userId);
    void deleteLocationForUser(Long locationId, Long userId);
}
