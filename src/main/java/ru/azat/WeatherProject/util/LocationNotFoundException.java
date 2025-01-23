package ru.azat.WeatherProject.util;

public class LocationNotFoundException extends RuntimeException{
    public LocationNotFoundException(String mes) {
        super(mes);
    }
}
