package com.example.mealapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserPreferences {

    private static final String PREFS_NAME = "meal_planner_users";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_CURRENT_EMAIL = "current_email";

    private final SharedPreferences prefs;

    public UserPreferences(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Hash password using SHA-256 */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; // fallback
        }
    }

// ── Registration ──────────────────────────────────────────────────────────

    public boolean isEmailRegistered(String email) {
        return prefs.contains("user_" + email.toLowerCase() + "_password");
    }

    public boolean registerUser(String name, String email, String password) {
        if (isEmailRegistered(email)) return false;
        String normalEmail = email.toLowerCase().trim();
        prefs.edit()
                .putString("user_" + normalEmail + "_name", name.trim())
                .putString("user_" + normalEmail + "_email", normalEmail)
                .putString("user_" + normalEmail + "_password", hashPassword(password))
                .apply();
        return true;
    }

