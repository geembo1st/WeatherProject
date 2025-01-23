package ru.azat.WeatherProject.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDTO {

    private String id;

    @NotNull(message = "User Id must not be null")
    private Long userId;

    @NotNull(message = "Expiration time must not be null")
    private LocalDateTime expiresAt;
}
