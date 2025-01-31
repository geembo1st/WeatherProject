package ru.azat.WeatherProject.util;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String mes) {
        super(mes);
    }
}
