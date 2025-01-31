package ru.azat.WeatherProject.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.azat.WeatherProject.model.User;
import ru.azat.WeatherProject.service.AuthService;
import ru.azat.WeatherProject.service.LocationService;
import ru.azat.WeatherProject.service.UserService;

import java.util.UUID;


@Controller
@Slf4j
@RequestMapping("/locations")
public class LocationController {
    private final LocationService locationService;
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public LocationController(LocationService locationService, UserService userService, AuthService authService) {
        this.locationService = locationService;
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    public String viewLocations(Model model) {
        model.addAttribute("locations", locationService.getAllLocations());
        return "locations";
    }

    @PostMapping({"/{id}/add"})
    public String addLocation(@PathVariable Long id, @CookieValue(value = "SESSIONID", required = false) String sessionId) {
        if(sessionId == null) {
            return "redirect:/auth/login";
        }

        UUID uuid = UUID.fromString(sessionId);
        User user = authService.getUserBySession(uuid);

        if(user == null) {
            return "redirect:/auth/login";
        }

        userService.addLocationToUser(user.getId(), id);

        return "redirect:/dashboard";
    }
}
