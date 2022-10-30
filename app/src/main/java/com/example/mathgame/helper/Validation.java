package com.example.mathgame.helper;

import java.util.regex.Pattern;

public class Validation {
    private static final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    private final boolean isCorrect;
    private final String value;

    private Validation(boolean isCorrect, String value) {
        this.isCorrect = isCorrect;
        this.value = value;
    }

    public static Validation validateNumber(String text) {
        return isNumber(text) ? new Validation(true, text)
                : new Validation(false, "Value must be a number");
    }

    private static boolean isNumber(String text) {
        if (text == null) {
            return false;
        }
        return pattern.matcher(text).matches();
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public String getValue() {
        return value;
    }
}
