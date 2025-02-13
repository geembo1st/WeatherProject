package ru.azat.WeatherProject.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.azat.WeatherProject.service.AuthService;
import ru.azat.WeatherProject.service.UserService;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.azat.WeatherProject.util.UserAlreadyExistsException;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private  AuthService authService;

    @MockitoBean
    private  UserService userService;


    @Test
    void showRegisterForm_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/register"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("register"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userDTO"));
    }

    @Test
    void register_Success() throws Exception {
        doNothing().when(authService).registerUser(anyString(), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .param("login", "testUser")
                .param("password", "testPassword"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/auth/login"));
    }

    @Test
    void register_RuntimeException() throws Exception {
        doThrow(new UserAlreadyExistsException("User already exists")).when(authService).registerUser(anyString(), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .param("login", "testUser")
                .param("password", "testPassword"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("register"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"));
    }

    @Test
    void getLoginForm_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/login"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login"))
                .andExpect(MockMvcResultMatchers.model().attributeExists(("userDTO")));
    }

    @Test
    void login_Success() throws Exception {
        when(authService.loginUser(anyString(), anyString())).thenReturn(UUID.randomUUID());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .param("login", "testUser")
                .param("password", "testPassword"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/dashboard"));
    }

    @Test
    void logout_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/auth/login"));
    }
}
