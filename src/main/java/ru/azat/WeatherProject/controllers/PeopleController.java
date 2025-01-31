package ru.azat.WeatherProject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.azat.WeatherProject.dto.UserDTO;
import ru.azat.WeatherProject.service.LocationService;
import ru.azat.WeatherProject.service.UserService;

@Controller
@RequestMapping("/people")
public class PeopleController {
    private final UserService userService;
    private final LocationService locationService;

    @Autowired
    public PeopleController(UserService userService, LocationService locationService) {
        this.userService = userService;
        this.locationService = locationService;
    }


    @GetMapping
    public String showPeople(Model model) {
        model.addAttribute("people", userService.getAllUsers());
        return "people";
    }

    @GetMapping("/{id}")
    public String viewUserDashboard(@PathVariable Long id, Model model) {
        UserDTO user = userService.showUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("locations", user.getLocationIds().stream().map(locationService::showLocationById));
        return "dashboard";
    }
}
