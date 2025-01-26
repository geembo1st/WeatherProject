package ru.azat.WeatherProject.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.azat.WeatherProject.dto.UserDTO;
import ru.azat.WeatherProject.service.AuthService;
import ru.azat.WeatherProject.service.UserService;

import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("userDTO") @Valid UserDTO userDTO, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            return "register";
        }
        try {
            authService.registerUser(userDTO.getLogin(), userDTO.getPassword());
            model.addAttribute("successMessage", "User registered successfully");
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String getLoginForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String login, @RequestParam String password, HttpServletResponse response, Model model) {
        try {
            UUID sessionId = authService.loginUser(login, password);
            Cookie sessionCookie = new Cookie("SESSIONID", sessionId.toString());
            sessionCookie.setHttpOnly(true);
            sessionCookie.setMaxAge(7200);
            response.addCookie(sessionCookie);
            return "redirect:/dashboard";
        } catch (RuntimeException e) {
        model.addAttribute("errorMessage", e.getMessage());
        return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(@CookieValue(value = "SESSIONID", required = false) String sessionId, HttpServletResponse response) {
        if (sessionId != null) {
            authService.logoutUser(UUID.fromString(sessionId));

            Cookie cookie = new Cookie("SESSIONID", null);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }

        return "redirect:/auth/login";
    }
}
