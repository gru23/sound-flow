package org.unibl.etf.soundflow.util;

public class EmailConstants {
    // Verification
    public static final String VERIFICATION_ENDPOINT = "http://localhost:8080/auth/verify";
    public static final String VERIFICATION_SUBJECT = "Account Verification";
    public static final String VERIFICATION_MESSAGE = "Please click the link below to verify your account: ";

    // Password Reset
    public static final String RESET_PASSWORD_SUBJECT = "Password Reset Request";
    public static final String RESET_PASSWORD_MESSAGE = "Please click the link below to reset your password: ";

    // General
    public static final String SIGNATURE = "<br><br>Best regards,<br>Sound Flow";

    public static String greeting(String name) {
        return "Hello " + name + ",<br>";
    }
}
