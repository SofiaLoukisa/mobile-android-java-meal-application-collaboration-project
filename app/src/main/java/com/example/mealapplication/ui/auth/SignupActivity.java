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

    private void attemptSignup() {
        String name = text(nameEditText);
        String email = text(emailEditText);
        String password = text(passwordEditText);
        String confirm = text(confirmPasswordEditText);

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError(getString(R.string.error_empty_fields));
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(getString(R.string.error_invalid_email));
            return;
        }
        if (password.length() < 6) {
            showError(getString(R.string.error_password_short));
            return;
        }
        if (!password.equals(confirm)) {
            showError(getString(R.string.error_passwords_mismatch));
            return;
        }
        if (userPrefs.isEmailRegistered(email)) {
            showError(getString(R.string.error_email_taken));
            return;
        }

        userPrefs.registerUser(name, email, password);
        userPrefs.login(email, password);

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
