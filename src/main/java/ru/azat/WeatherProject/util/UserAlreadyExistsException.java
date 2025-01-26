package ru.azat.WeatherProject.util;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String mes) {
        super(mes);
    }
}
