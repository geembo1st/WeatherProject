package ru.azat.WeatherProject.util;

public class SessionNotFoundException extends RuntimeException{
    public SessionNotFoundException(String mes) {
        super(mes);
    }
}
