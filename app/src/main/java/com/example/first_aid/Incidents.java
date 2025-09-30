package com.example.first_aid;

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


        // ПЕРЕЗАГРУЖАЕМ ТОЛЬКО ТЕКУЩУЮ АКТИВНОСТЬ
        Intent intent = new Intent(this, Incidents.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // Без анимации
        startActivity(intent);
        finish();
    }



    public void goToFirstActivity(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
}