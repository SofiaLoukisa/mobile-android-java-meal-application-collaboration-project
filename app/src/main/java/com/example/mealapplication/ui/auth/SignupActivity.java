package com.example.mealapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mealapplication.R;
import com.example.mealapplication.ui.browse.BrowseActivity;
import com.example.mealapplication.utils.UserPreferences;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private TextView errorTextView;
    private UserPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userPrefs = new UserPreferences(this);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        errorTextView = findViewById(R.id.errorTextView);

        confirmPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptSignup();
                return true;
            }
            return false;
        });

        findViewById(R.id.signupButton).setOnClickListener(v -> attemptSignup());

        findViewById(R.id.loginLink).setOnClickListener(v -> finish());
    }

