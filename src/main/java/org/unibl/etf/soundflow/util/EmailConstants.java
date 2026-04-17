package org.unibl.etf.soundflow.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailConstants {
    // Verification
    public static final String VERIFICATION_ENDPOINT = "http://localhost:8080/auth/verify";
    public static final String VERIFICATION_SUBJECT = "Account Verification";
    public static final String VERIFICATION_MESSAGE = "Please click the link below to verify your account: ";

    // Password Reset
    public static final String RESET_PASSWORD_ENDPOINT = "http://localhost:8080/auth/reset-confirm";
    public static final String RESET_PASSWORD_SUBJECT = "Password Reset Request";
    public static final String RESET_PASSWORD_MESSAGE = "Please click the link below to reset your password: ";

    // General
    private static final String SIGNATURE = "<br><br>Best regards,<br>Sound Flow";
    public static final String FOOTER = SIGNATURE + "<br><br>" + getTime();

    public static String greeting(String name) {
        return "Hello " + name + ",<br>";
    }

    private static String getTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm, dd.MM.yyyy."));
    }
}
