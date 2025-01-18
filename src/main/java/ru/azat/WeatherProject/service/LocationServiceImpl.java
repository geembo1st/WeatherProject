package ru.azat.WeatherProject.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.azat.WeatherProject.dto.LocationDTO;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LocationServiceImpl(ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<LocationDTO> getAllLocations() {
        return null;
    }

    @Override
    public void addLocation(LocationDTO locationDTO) {

    }

    @Override
    public void deleteLocation(Long id) {

    }

    @Override
    public LocationDTO updateLocation(Long id, LocationDTO locationDTO) {
        return null;
    }

    @Override
    public LocationDTO showLocationById(Long id) {
        return null;
    }
}
