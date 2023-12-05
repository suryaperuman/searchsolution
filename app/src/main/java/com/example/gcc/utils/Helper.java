package com.example.gcc.utils;

import android.widget.Toast;

import com.example.gcc.RegisterActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    /*
    Username must:
    Be 4 characters long
    Only include characters, numbers, underscores, & periods
     */
    private final String USERNAME_REGEX = "^[A-Za-z0-9_.]{4,}$";

    /*
    At least one letter.
    At least one digit.
    At least one special character (in this case, @, #, $, %, ^, &, +, or =).
    Minimum length of 8 characters.
    */
    private final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";

    public boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\\\d)(?=.*[@$!%*?&])[A-Za-z\\\\d@$!%*?&]{6,}$");
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public boolean validatePace(double pace) {
        return pace > 0 && pace <= 30;
    }

    public boolean validateAge(int age) {
        return age > 0 && age <= 80;
    }

    public boolean validatePhoneNum(String phoneNum) {
        /**
         * Checks for an plus sign for country code
         * Checks for an optional country code
         * Checks for an optional area code
         * Checks for 10 digits
         */
        Pattern pattern = Pattern.compile("^\\+?1?\\s*\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$");
        Matcher matcher = pattern.matcher(phoneNum);

        return matcher.matches();
    }

    public boolean validateEmail(String email) {
        String regex = "^[a-zA-Z0-9_][a-zA-Z0-9_]+@[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9_]+)";
        Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public boolean validateInstagramLink(String link) {
        String regex = "^(https?:\\/\\/)?(www\\.)?instagram\\.com\\/[a-zA-Z0-9_]+\\/?$";
        Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(link);

        return matcher.matches();
    }

    public boolean validateUsername(String username) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9_.]{4,}$");
        Matcher matcher = pattern.matcher(username);

        return matcher.matches();
    }

    public String validateFields(String username, String password) {
        if (username.length() < 4) {
            return "Username must be at least 4 characters long";
        } else if (!username.matches(USERNAME_REGEX)) {
            return "Username can only include letters, numbers, periods, & underscores";
        } else if (!password.matches(PASSWORD_REGEX)) {
            return "Make sure password meets all requirements";
        }
        return "Registration Successful";
    }
}
