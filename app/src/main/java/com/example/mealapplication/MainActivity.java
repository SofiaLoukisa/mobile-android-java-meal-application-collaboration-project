package com.example.testmealplanner;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.testmealplanner.ui.auth.LoginActivity;
import com.example.testmealplanner.ui.browse.BrowseActivity;
import com.example.testmealplanner.utils.UserPreferences;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserPreferences userPrefs = new UserPreferences(this);
        Intent intent;
        if (userPrefs.isLoggedIn()) {
            intent = new Intent(this, BrowseActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
