package com.example.first_aid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class Incidents extends BaseActivity {

    List<Incident> incidentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_incidents);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        incidentsList = new ArrayList<>();

        incidentsList.add(new Incident(getString(R.string.incident_1),
                getString(R.string.cut_description), R.id.incident1));
        incidentsList.add(new Incident(getString(R.string.incident_2),
                getString(R.string.burn_description), R.id.incident2));
        incidentsList.add(new Incident(getString(R.string.incident_3),
                getString(R.string.fracture_description), R.id.incident3));

        setupIncidentViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.incidents_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.language_russian) {
            setLanguage("ru");
            return true;
        } else if (id == R.id.language_english) {
            setLanguage("en");
            return true;
        } else if (id == R.id.logout) {
            // ВЫХОД ИЗ ПРИЛОЖЕНИЯ
            performLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setLanguage(String languageCode) {
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);

        String currentLanguage = preferences.getString("app_language", "ru");
        if (currentLanguage.equals(languageCode)) {
            Toast.makeText(this,
                    languageCode.equals("en") ? "Language is already English" : "Язык уже русский",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("app_language", languageCode);
        editor.apply();

        recreate();
    }

    public void goToFirstActivity(View v){
        finish();
    }

    private void setupIncidentViews() {
        for (Incident incident : incidentsList) {
            TextView textView = findViewById(incident.getViewId());

            if (textView != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showInfo(incident.getDescription());
                    }
                });
            }
        }

    }

    private void showInfo(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

//    private void showLogoutConfirmation() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(getString(R.string.logout))
//                .setMessage(getString(R.string.logout_confirmation))
//                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        performLogout();
//                    }
//                })
//                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
//    }

    private void performLogout() {
        // 1. Очищаем сессию пользователя
        clearUserSession();

        // 2. Закрываем ВСЕ активности и переходим на LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Завершаем текущую активность
    }

    private void clearUserSession() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Очищаем ВСЕ данные сессии
        editor.apply();

        // Можно также показать сообщение
        Toast.makeText(this, "Вы вышли из системы", Toast.LENGTH_SHORT).show();
    }
}