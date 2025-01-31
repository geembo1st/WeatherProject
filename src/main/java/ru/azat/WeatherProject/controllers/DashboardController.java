package ru.azat.WeatherProject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.azat.WeatherProject.dto.LocationDTO;
import ru.azat.WeatherProject.dto.WeatherDTO;
import ru.azat.WeatherProject.model.User;
import ru.azat.WeatherProject.service.AuthService;
import ru.azat.WeatherProject.service.LocationService;
import ru.azat.WeatherProject.service.UserService;
import ru.azat.WeatherProject.service.WeatherService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final WeatherService weatherService;
    private final UserService userService;
    private final LocationService locationService;
    private final AuthService authService;

    @Autowired
    public DashboardController(WeatherService weatherService, UserService userService, LocationService locationService, AuthService authService) {
        this.weatherService = weatherService;
        this.userService = userService;
        this.locationService = locationService;
        this.authService = authService;
    }

    @GetMapping
    public String getDashboard(@CookieValue(value = "SESSIONID", required = false) String sessionId, Model model) {
        if (sessionId == null || !authService.isSessionValid(UUID.fromString(sessionId))) {
            return "redirect:/auth/login";
        }

        User user = authService.getUserBySession(UUID.fromString(sessionId));
        List<LocationDTO> locations = locationService.getLocationsByUserId(user.getId());
        List<WeatherDTO> weatherData = locations.stream()
                .map(location -> {
                    try {
                        return weatherService.getWeatherFromLocation(location.getId());
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("locations", locations);
        model.addAttribute("weatherData", weatherData);
        return "dashboard";
    }

    @DeleteMapping("/location/delete")
    public String deleteLocation(@RequestParam Long locationId, @CookieValue(value = "SESSIONID", required = false) String sessionId) {
        if(sessionId == null) {
            return "redirect:/auth/login";
            }

        User user = authService.getUserBySession(UUID.fromString(sessionId));
        locationService.deleteLocationForUser(locationId, user.getId());

        return "redirect:/dashboard";
    }

    @PatchMapping("/location/updateWeather")
    public String updateWeatherForLocation(@RequestParam Long locationId, Model model) throws IOException, InterruptedException {
        WeatherDTO weatherDTO = weatherService.getWeatherFromLocation(locationId);
        model.addAttribute("weather", weatherDTO);
        return "redirect:/dashboard";
    }

    @GetMapping("/locations")
    public String goToLocations() {
        return "redirect:/locations";
    }
}
