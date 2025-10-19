package com.example.first_aid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends BaseActivity {

    private SharedPreferences userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSession = getSharedPreferences("UserSession", MODE_PRIVATE);

        if (!userSession.getBoolean("is_logged_in", false)) {
            goToLogin();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLanguageIfNeeded();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateLanguageIfNeeded() {
        String currentLanguage = getResources().getConfiguration().locale.getLanguage();
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String savedLanguage = prefs.getString("app_language", "ru");

        if (!currentLanguage.equals(savedLanguage)) {
            recreate();
        }
    }

    public void goToSecondActivity(View v) {
        Intent intent = new Intent(this, Incidents.class);
        startActivity(intent);
    }

}