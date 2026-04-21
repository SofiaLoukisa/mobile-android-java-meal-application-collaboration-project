package com.example.mealapplication.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mealapplication.R;
import com.example.mealapplication.local.AppDatabase;
import com.example.mealapplication.ui.auth.LoginActivity;
import com.example.mealapplication.utils.UserPreferences;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private UserPreferences userPrefs;
    private TextView avatarText, profileName, profileEmail, favoritesCount, planCount;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userPrefs = new UserPreferences(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        avatarText = findViewById(R.id.avatarText);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        favoritesCount = findViewById(R.id.favoritesCount);
        planCount = findViewById(R.id.planCount);

        loadUserInfo();
        loadStats();

        findViewById(R.id.changeNameRow).setOnClickListener(v -> showChangeNameDialog());
        findViewById(R.id.changePasswordRow).setOnClickListener(v -> showChangePasswordDialog());
        findViewById(R.id.logoutRow).setOnClickListener(v -> confirmLogout());
        findViewById(R.id.deleteAccountButton).setOnClickListener(v -> confirmDeleteAccount());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
        loadStats();
    }

    private void loadUserInfo() {
        String name = userPrefs.getCurrentName();
        String email = userPrefs.getCurrentEmail();
        profileName.setText(name.isEmpty() ? "User" : name);
        profileEmail.setText(email);
        avatarText.setText(name.isEmpty() ? "U" : String.valueOf(Character.toUpperCase(name.charAt(0))));
    }

    private void loadStats() {
        executor.execute(() -> {
            int favs = AppDatabase.getInstance(this).favoriteMealDao().getAllFavorites().size();
            int plans = AppDatabase.getInstance(this).mealPlanDao().getAllMealPlans().size();
            runOnUiThread(() -> {
                favoritesCount.setText(String.valueOf(favs));
                planCount.setText(String.valueOf(plans));
            });
        });
    }

    private void showChangeNameDialog() {
        View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
        EditText input = new EditText(this);
        input.setHint(getString(R.string.hint_name));
        input.setText(userPrefs.getCurrentName());
        input.setPadding(48, 24, 48, 24);

        new AlertDialog.Builder(this)
                .setTitle("Edit Name")
                .setView(input)
                .setPositiveButton("Save", (d, w) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        userPrefs.changeName(newName);
                        loadUserInfo();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

