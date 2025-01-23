package ru.azat.WeatherProject.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.azat.WeatherProject.dto.LocationDTO;
import ru.azat.WeatherProject.model.Location;
import ru.azat.WeatherProject.repository.LocationRepository;
import ru.azat.WeatherProject.repository.UserRepository;
import ru.azat.WeatherProject.util.LocationNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final ModelMapper modelMapper;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    @Autowired
    public LocationServiceImpl(ModelMapper modelMapper, LocationRepository locationRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        log.debug("LocationServiceImpl создан");
    }

    public LocationDTO convertToLocationDTO(Location location) {
        log.debug("Конвертация Location в DTO: {}", location.getId());
        LocationDTO locationDTO = modelMapper.map(location, LocationDTO.class);

        locationDTO.setUserIds(location.getUsers().stream()
                .map(loc -> loc.getId())
                .collect(Collectors.toSet()));
        return locationDTO;
    }

    public Location convertToLocation(LocationDTO locationDTO) {
        log.debug("Конвертации из DTO в Location: {}", locationDTO.getId());
        Location location = modelMapper.map(locationDTO, Location.class);

        location.setUsers(locationDTO.getUserIds().stream()
                .map(id -> userRepository.showUserById(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));

        return location;
    }


    @Override
    public List<LocationDTO> getAllLocations() {
        log.info("Получение всех локаций");
        List<Location> locations = locationRepository.getAllLocations();
        log.debug("Найдено {} локаций" ,locations.size());
        return locations.stream()
                .map(location -> convertToLocationDTO(location))
                .collect(Collectors.toList());
    }

    @Override
    public void addLocation(LocationDTO locationDTO) {
        log.info("Добавление новой локации");
        Location location = convertToLocation(locationDTO);
        locationRepository.addLocation(location);
        log.info("Локация с id: {} добавлена", location.getId());
    }

    @Override
    public void deleteLocation(Long id) {
        log.info("Удаление новой локации");
        locationRepository.deleteLocation(id);
        log.info("Локация с id: {} удалена", id);
    }

    @Override
    public LocationDTO updateLocation(Long id, LocationDTO locationDTO) {
        log.info("Обновление новой локации");
        Location location = locationRepository.showLocationById(id);
        if(location == null) {
            log.error("Локация с id: {} не найдена для обновления", id);
            throw new LocationNotFoundException("Location not found");
        }
        location.setName(locationDTO.getName());
        location.setLongitude(locationDTO.getLongitude());
        location.setLatitude(locationDTO.getLatitude());
        location.setUsers(locationDTO.getUserIds().stream()
                .map(userRepository::showUserById)
                .collect(Collectors.toSet()));
        log.info("Локация с id: {} обновлена", id);
        return convertToLocationDTO(location);
    }

    @Override
    public LocationDTO showLocationById(Long id) {
        log.info("Поиск локации");
        Location location = locationRepository.showLocationById(id);
        if(location == null) {
            throw new LocationNotFoundException("Location not found");
        }
        log.info("Локация с id:{} найдена", id);
        return convertToLocationDTO(location);
    }
}
