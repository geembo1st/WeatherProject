package ru.azat.WeatherProject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.azat.WeatherProject.dto.WeatherDTO;
import ru.azat.WeatherProject.model.Location;
import ru.azat.WeatherProject.repository.LocationRepository;
import ru.azat.WeatherProject.util.LocationNotFoundException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class WeatherService {
    private static final String API_KEY = "9278f9e97f618e24ce0d1bceffd28670";
    private static final String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final LocationRepository locationRepository;

    @Autowired
    public WeatherService(ObjectMapper objectMapper, LocationRepository locationRepository) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
        this.locationRepository = locationRepository;
    }

    public WeatherDTO getWeatherFromLocation(Long locationId) throws IOException, InterruptedException {
        Location location = locationRepository.showLocationById(locationId);

        if(location == null) {
            throw new LocationNotFoundException("Location with id: " + locationId + " not found");
        }

        String url = String.format("%s?lat=%f&lon=%f&units=metric&appid=%s", WEATHER_API_URL, location.getLatitude(), location.getLongitude(), API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode rootNode = objectMapper.readTree(response.body());
        double temperature = rootNode.path("main").path("temp").asDouble();
        boolean isRaining = rootNode.path("weather").get(0).path("main").asText().equalsIgnoreCase("Rain");

        return new WeatherDTO(temperature, isRaining);
    }
}
