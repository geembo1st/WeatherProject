package ru.azat.WeatherProject.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.azat.WeatherProject.dto.LocationDTO;
import ru.azat.WeatherProject.model.Location;
import ru.azat.WeatherProject.model.User;
import ru.azat.WeatherProject.repository.LocationRepository;
import ru.azat.WeatherProject.repository.UserRepository;
import ru.azat.WeatherProject.util.LocationNotFoundException;
import ru.azat.WeatherProject.util.UserNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class LocationServiceTest {
   private final static Long LOCATION_ID = 1L;
   private final static Long USER_ID = 1L;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    @Test
    void showLocationById_Success() {
       Location location = Location.builder()
               .id(LOCATION_ID)
               .name("Test Location")
               .latitude(new BigDecimal("10.123456"))
               .longitude(new BigDecimal("20.123456"))
               .users(new HashSet<>())
               .build();

       LocationDTO locationDTO = LocationDTO.builder()
               .id(LOCATION_ID)
               .name("Test Location")
               .latitude(new BigDecimal("10.123456"))
               .longitude(new BigDecimal("20.123456"))
               .userIds(new HashSet<>())
               .build();

       when(locationRepository.showLocationById(LOCATION_ID)).thenReturn(location);
       when(modelMapper.map(location, LocationDTO.class)).thenReturn(locationDTO);

       LocationDTO result = locationService.showLocationById(LOCATION_ID);

       assertNotNull(result);
       assertEquals(LOCATION_ID, result.getId());
       assertEquals("Test Location", result.getName());
       assertEquals("Test Location", result.getName());
       assertEquals(new BigDecimal("10.123456"), result.getLatitude());
       assertEquals(new BigDecimal("20.123456"), result.getLongitude());

       verify(locationRepository, times(1)).showLocationById(LOCATION_ID);
       verify(modelMapper, times(1)).map(location, LocationDTO.class);
    }

    @Test
    void showLocationById_NotFoundException() {
        when(locationRepository.showLocationById(LOCATION_ID)).thenThrow(new LocationNotFoundException("Location not found"));
        LocationNotFoundException locationNotFoundException = assertThrows(LocationNotFoundException.class, () -> locationService.showLocationById(LOCATION_ID));
        assertEquals("Location not found", locationNotFoundException.getMessage());
        verify(locationRepository, times(1)).showLocationById(LOCATION_ID);
    }

    @Test
    void getAllLocations() {
        Location location = Location.builder()
                .id(LOCATION_ID)
                .name("Test Location")
                .latitude(new BigDecimal("10.123456"))
                .longitude(new BigDecimal("20.123456"))
                .users(new HashSet<>())
                .build();

        LocationDTO locationDTO = LocationDTO.builder()
                .id(LOCATION_ID)
                .name("Test Location")
                .latitude(new BigDecimal("10.123456"))
                .longitude(new BigDecimal("20.123456"))
                .userIds(new HashSet<>())
                .build();

        List<Location> locations = Collections.singletonList(location);

        when(locationRepository.getAllLocations()).thenReturn(locations);
        when(modelMapper.map(location, LocationDTO.class)).thenReturn(locationDTO);

        List<LocationDTO> result = locationService.getAllLocations();

        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals("Test Location", result.get(0).getName());
        assertEquals(new BigDecimal("10.123456"), result.get(0).getLatitude());
        assertEquals(new BigDecimal("20.123456"), result.get(0).getLongitude());

        verify(locationRepository, times(1)).getAllLocations();
        verify(modelMapper, times(1)).map(location, LocationDTO.class);
    }

    @Test
    void addLocation() {
        Location location = Location.builder()
                .id(LOCATION_ID)
                .name("Test Location")
                .latitude(new BigDecimal("10.123456"))
                .longitude(new BigDecimal("20.123456"))
                .users(new HashSet<>())
                .build();

        LocationDTO locationDTO = LocationDTO.builder()
                .id(LOCATION_ID)
                .name("Test Location")
                .latitude(new BigDecimal("10.123456"))
                .longitude(new BigDecimal("20.123456"))
                .userIds(new HashSet<>())
                .build();

        when(modelMapper.map(locationDTO, Location.class)).thenReturn(location);

        locationService.addLocation(locationDTO);

        verify(locationRepository, times(1)).addLocation(location);
    }

    @Test
    void deleteLocation_Success() {
        Location location = Location.builder()
                .id(LOCATION_ID)
                .name("Test Location")
                .latitude(new BigDecimal("10.123456"))
                .longitude(new BigDecimal("20.123456"))
                .users(new HashSet<>())
                .build();

        locationService.deleteLocation(location.getId());

        verify(locationRepository, times(1)).deleteLocation(location.getId());
    }

    @Test
    void deleteLocation_NotFoundException() {
        when(locationRepository.showLocationById(LOCATION_ID)).thenThrow(new LocationNotFoundException("Location not found"));
        LocationNotFoundException locationNotFoundException = assertThrows(LocationNotFoundException.class, () -> locationService.showLocationById(LOCATION_ID));
        assertEquals("Location not found", locationNotFoundException.getMessage());
        verify(locationRepository, times(1)).showLocationById(LOCATION_ID);
    }

    @Test
    void getLocationByUserId_Success() {
        Location location = Location.builder()
                .id(LOCATION_ID)
                .name("Test Location")
                .latitude(new BigDecimal("10.123456"))
                .longitude(new BigDecimal("20.123456"))
                .users(new HashSet<>())
                .build();

        LocationDTO locationDTO = LocationDTO.builder()
                .id(LOCATION_ID)
                .name("Test Location")
                .latitude(new BigDecimal("10.123456"))
                .longitude(new BigDecimal("20.123456"))
                .userIds(new HashSet<>())
                .build();

        when(locationRepository.findByUserId(USER_ID)).thenReturn(Collections.singletonList(location));
        when(modelMapper.map(location, LocationDTO.class)).thenReturn(locationDTO);

        List<LocationDTO> result = locationService.getLocationsByUserId(USER_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(locationDTO, result.get(0));

        verify(locationRepository, times(1)).findByUserId(USER_ID);
        verify(modelMapper, times(1)).map(location, LocationDTO.class);
    }

    @Test
    void deleteLocationForUser_Success() {
        Location location = Location.builder()
                .id(LOCATION_ID)
                .name("Test Location")
                .latitude(new BigDecimal("10.123456"))
                .longitude(new BigDecimal("20.123456"))
                .users(new HashSet<>())
                .build();

        User user = User.builder()
                        .id(USER_ID)
                        .login("login")
                        .password("password")
                        .sessions(new ArrayList<>())
                        .locations(new ArrayList<>())
                        .build();

        when(locationRepository.showLocationById(LOCATION_ID)).thenReturn(location);
        when(userRepository.showUserById(USER_ID)).thenReturn(user);

        locationService.deleteLocationForUser(LOCATION_ID, USER_ID);

        verify(locationRepository, times(1)).showLocationById(LOCATION_ID);
        verify(userRepository, times(1)).showUserById(USER_ID);
    }

    @Test
    void deleteLocationForUser_NotFoundLocationException() {
        when(locationRepository.showLocationById(LOCATION_ID)).thenThrow(new LocationNotFoundException("Location not found"));
        LocationNotFoundException locationNotFoundException = assertThrows(LocationNotFoundException.class, () -> locationService.deleteLocationForUser(LOCATION_ID, USER_ID));
        assertEquals("Location not found", locationNotFoundException.getMessage());
        verify(locationRepository, times(1)).showLocationById(LOCATION_ID);
    }

    @Test
    void deleteLocationForUser_UserNotFoundException() {
        when(locationRepository.showLocationById(LOCATION_ID)).thenReturn(mock(Location.class));
        when(userRepository.showUserById(USER_ID)).thenThrow(new UserNotFoundException("User not found"));
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> locationService.deleteLocationForUser(LOCATION_ID, USER_ID));
        assertEquals("User not found", userNotFoundException.getMessage());
        verify(locationRepository, times(1)).showLocationById(LOCATION_ID);
        verify(userRepository, times(1)).showUserById(USER_ID);
    }

    @Test
    void updateLocation_Success() {
        Location existingLocation = Location.builder()
                .id(LOCATION_ID)
                .name("Old Location")
                .latitude(new BigDecimal("0.0"))
                .longitude(new BigDecimal("0.0"))
                .users(new HashSet<>())
                .build();

        LocationDTO locationDTO = LocationDTO.builder()
                .id(LOCATION_ID)
                .name("Old Location")
                .latitude(new BigDecimal("10.123456"))
                .longitude(new BigDecimal("20.123456"))
                .userIds(new HashSet<>(Collections.singletonList(USER_ID)))
                .build();

        User user = User.builder()
                .id(USER_ID)
                .login("login")
                .password("password")
                .sessions(new ArrayList<>())
                .locations(new ArrayList<>())
                .build();

        when(locationRepository.showLocationById(LOCATION_ID)).thenReturn(existingLocation);
        when(userRepository.showUserById(USER_ID)).thenReturn(user);
        when(modelMapper.map(existingLocation, LocationDTO.class)).thenReturn(locationDTO);

        LocationDTO result = locationService.updateLocation(LOCATION_ID, locationDTO);

        assertNotNull(result);
        assertEquals(locationDTO.getName(), result.getName());
        assertEquals(locationDTO.getLatitude(), result.getLatitude());
        assertEquals(locationDTO.getLongitude(), result.getLongitude());
        assertEquals(locationDTO.getUserIds(), result.getUserIds());

        verify(locationRepository, times(1)).showLocationById(LOCATION_ID);
        verify(userRepository, times(1)).showUserById(USER_ID);
        verify(modelMapper, times(1)).map(existingLocation, LocationDTO.class);
    }
}
