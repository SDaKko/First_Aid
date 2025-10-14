package com.example.first_aid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(updateBaseContextLocale(newBase));
    }

    private Context updateBaseContextLocale(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Settings", MODE_PRIVATE);
        String language = preferences.getString("app_language", "ru");

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Восстанавливаем тему перед созданием
        restoreTheme();
        super.onCreate(savedInstanceState);
    }

    protected void restoreTheme() {
        SharedPreferences preferences = getSharedPreferences("app_settings", MODE_PRIVATE);
        int savedTheme = preferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(savedTheme);
    }

    protected void setAppTheme(int themeMode) {
        SharedPreferences preferences = getSharedPreferences("app_settings", MODE_PRIVATE);
        preferences.edit().putInt("theme_mode", themeMode).apply();
        AppCompatDelegate.setDefaultNightMode(themeMode);
        recreate();
    }

}