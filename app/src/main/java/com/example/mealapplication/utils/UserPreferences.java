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

    // ── Login / Session ───────────────────────────────────────────────────────

    public boolean login(String email, String password) {
        String normalEmail = email.toLowerCase().trim();
        String storedHash = prefs.getString("user_" + normalEmail + "_password", null);
        if (storedHash == null) return false;
        if (!storedHash.equals(hashPassword(password))) return false;
        prefs.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_CURRENT_EMAIL, normalEmail)
                .apply();
        return true;
    }

    public void logout() {
        prefs.edit()
                .putBoolean(KEY_LOGGED_IN, false)
                .remove(KEY_CURRENT_EMAIL)
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    // ── Current user ─────────────────────────────────────────────────────────

    public String getCurrentEmail() {
        return prefs.getString(KEY_CURRENT_EMAIL, "");
    }

    public String getCurrentName() {
        String email = getCurrentEmail();
        return prefs.getString("user_" + email + "_name", "");
    }

    public boolean changePassword(String currentPassword, String newPassword) {
        String email = getCurrentEmail();
        String stored = prefs.getString("user_" + email + "_password", null);
        if (stored == null || !stored.equals(hashPassword(currentPassword))) return false;
        prefs.edit().putString("user_" + email + "_password", hashPassword(newPassword)).apply();
        return true;
    }

    public void changeName(String newName) {
        String email = getCurrentEmail();
        prefs.edit().putString("user_" + email + "_name", newName.trim()).apply();
    }

    public void deleteAccount() {
        String email = getCurrentEmail();
        prefs.edit()
                .remove("user_" + email + "_name")
                .remove("user_" + email + "_email")
                .remove("user_" + email + "_password")
                .putBoolean(KEY_LOGGED_IN, false)
                .remove(KEY_CURRENT_EMAIL)
                .apply();
    }
}
