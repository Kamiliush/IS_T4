package org.example;

//pomysł porzucony
public class DataValidator {

    public Integer parseToInteger(String value) throws NumberFormatException{

        Integer result = Integer.parseInt(value);
        return result;
    }
    //catchować błędy w mainie
}
