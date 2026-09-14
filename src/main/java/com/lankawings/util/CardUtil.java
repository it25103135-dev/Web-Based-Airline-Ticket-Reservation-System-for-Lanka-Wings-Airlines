package com.lankawings.util;

import java.time.YearMonth;

public final class CardUtil {
    private CardUtil() {}

    public static String digitsOnly(String input) {
        return input == null ? "" : input.replaceAll("\\D", "");
    }

    public static boolean isValidCardNumber(String input) {
        String s = digitsOnly(input);
        if (s.length() < 13 || s.length() > 19) return false;
        int sum = 0;
        boolean doubleDigit = false;
        for (int i = s.length() - 1; i >= 0; i--) {
            int d = s.charAt(i) - '0';
            if (doubleDigit) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            sum += d;
            doubleDigit = !doubleDigit;
        }
        return sum % 10 == 0;
    }

    public static boolean isValidExpiry(String value) {
        if (value == null || !value.matches("(0[1-9]|1[0-2])/\\d{2}")) return false;
        int month = Integer.parseInt(value.substring(0, 2));
        int year = 2000 + Integer.parseInt(value.substring(3, 5));
        return !YearMonth.of(year, month).isBefore(YearMonth.now());
    }

    public static boolean isValidCvv(String value) {
        return value != null && value.matches("\\d{3,4}");
    }
}
