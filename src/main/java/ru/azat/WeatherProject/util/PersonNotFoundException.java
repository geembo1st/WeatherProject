package ru.azat.WeatherProject.util;

public class PersonNotFoundException extends RuntimeException{
    public PersonNotFoundException(String mes) {
        super(mes);
    }
}
