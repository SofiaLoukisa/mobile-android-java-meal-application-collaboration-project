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

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private TextView errorTextView;
    private UserPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userPrefs = new UserPreferences(this);

        // If already logged in, go straight to app
        if (userPrefs.isLoggedIn()) {
            goToMain();
            return;
        }

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        errorTextView = findViewById(R.id.errorTextView);

        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });

        findViewById(R.id.loginButton).setOnClickListener(v -> attemptLogin());

        findViewById(R.id.signupLink).setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
    }

    private void attemptLogin() {
        String email = text(emailEditText);
        String password = text(passwordEditText);

        if (email.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.error_empty_fields));
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(getString(R.string.error_invalid_email));
            return;
        }

        if (userPrefs.login(email, password)) {
            goToMain();
        } else {
            showError(getString(R.string.error_invalid_credentials));
        }
    }

    private void goToMain() {
        Intent intent = new Intent(this, BrowseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(String msg) {
        errorTextView.setText(msg);
        errorTextView.setVisibility(View.VISIBLE);
    }

    private String text(TextInputEditText view) {
        return view.getText() != null ? view.getText().toString().trim() : "";
    }
}
