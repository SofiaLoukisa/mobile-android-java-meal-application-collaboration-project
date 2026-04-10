package com.example.mealapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mealapplication.model.CategoryResponse;
import com.example.mealapplication.remote.ApiClient;
import com.example.mealapplication.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "API_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // API CALL
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    int count = response.body().categories.size();
                    Log.d(TAG, "Categories loaded: " + count);

                } else {
                    Log.d(TAG, "Response failed");
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }
}