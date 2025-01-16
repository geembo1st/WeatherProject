package ru.azat.WeatherProject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;

    @NotBlank(message = "Login must not be empty")
    private String login;

    @NotBlank(message = "Password must not be empty")
    @Size(min = 3, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    private Set<Long> locationIds;
}
