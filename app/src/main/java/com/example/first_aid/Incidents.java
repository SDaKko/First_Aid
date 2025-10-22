package com.example.first_aid;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class Incidents extends BaseActivity {

    private List<Incident> incidentsList;
    private DatabaseHelper databaseHelper;
    private String userPosition;
    private ListView lvIncidents;
    private TextView tvEmptyState;
    private IncidentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().setWindowAnimations(0);

        setContentView(R.layout.activity_incidents);

        databaseHelper = new DatabaseHelper(this);

        // Получаем должность пользователя из сессии
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        userPosition = prefs.getString("user_position", "Универсальный");

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();

        // Загружаем происшествия для текущей должности
        loadIncidentsForPosition();

        setupListView();
    }

    private void initViews() {
        lvIncidents = findViewById(R.id.lvIncidents);
        tvEmptyState = findViewById(R.id.tvEmptyState);
    }

    private void loadIncidentsForPosition() {
        incidentsList = databaseHelper.getIncidentsForPosition(userPosition);

        // Обновляем заголовок с информацией о количестве
//        updateToolbarTitle();
    }

    private void updateToolbarTitle() {
        TextView tvTitle = findViewById(R.id.textViewFirstAid2);
        if (tvTitle != null) {
            String title = getString(R.string.incidents) + " (" + incidentsList.size() + ")";
            tvTitle.setText(title);
        }
    }

    private void setupListView() {
        if (incidentsList.isEmpty()) {
            // Показываем сообщение о пустом списке
            lvIncidents.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
            tvEmptyState.setText("Для должности \"" + userPosition + "\" нет доступных происшествий");
        } else {
            // Настраиваем адаптер и ListView
            lvIncidents.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);

            adapter = new IncidentAdapter(this, incidentsList);
            lvIncidents.setAdapter(adapter);

            // Обработчик клика по элементу списка
            lvIncidents.setOnItemClickListener((parent, view, position, id) -> {
                Incident selectedIncident = incidentsList.get(position);
                openFirstAidActivity(selectedIncident);
            });

            // Дополнительные настройки ListView
            lvIncidents.setDivider(getResources().getDrawable(android.R.drawable.divider_horizontal_bright));
            lvIncidents.setDividerHeight(1);
        }
    }

    private void openFirstAidActivity(Incident incident) {
        Intent intent = new Intent(this, FirstAid.class);
        intent.putExtra("incident_id", incident.getId());
        intent.putExtra("incident_title", incident.getTitle());
        startActivity(intent);

        // Добавляем анимацию перехода
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
        // Для API 16+
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            ActivityOptions options = ActivityOptions.makeCustomAnimation(this,
//                    R.anim.zoom_in, R.anim.zoom_out);
//            startActivity(intent, options.toBundle());
//        } else {
//            startActivity(intent);
//            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
//        }
    }

//    private void openFirstAidActivity(Incident incident) {
//        Log.d("ANIM_DEBUG", "=== STARTING ACTIVITY WITH ACTIVITY OPTIONS ===");
//
//        Intent intent = new Intent(this, FirstAid.class);
//        intent.putExtra("incident_id", incident.getId());
//        intent.putExtra("incident_title", incident.getTitle());
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            ActivityOptions options = ActivityOptions.makeCustomAnimation(
//                    this,
//                    R.anim.zoom_in,
//                    R.anim.zoom_out
//            );
//            startActivity(intent, options.toBundle());
//            Log.d("ANIM_DEBUG", "Activity started with ActivityOptions");
//        } else {
//            startActivity(intent);
//            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
//            Log.d("ANIM_DEBUG", "Activity started with overridePendingTransition");
//        }
//    }

//    private void openFirstAidActivity(Incident incident) {
//        Log.d("ANIM_DEBUG", "=== STARTING FirstAid ACTIVITY ===");
//
//        Intent intent = new Intent(this, FirstAid.class);
//        intent.putExtra("incident_id", incident.getId());
//        intent.putExtra("incident_title", incident.getTitle());
//
//        // Для Android 5.0+ используем ActivityOptions
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            ActivityOptions options = ActivityOptions.makeCustomAnimation(
//                    this,
//                    R.anim.zoom_in,    // анимация входа для FirstAid
//                    R.anim.zoom_out    // анимация выхода для Incidents
//            );
//            startActivity(intent, options.toBundle());
//            Log.d("ANIM_DEBUG", "Started with ActivityOptions (Lollipop+)");
//        }
//        // Для Android 4.1-4.4 используем другой метод
//        else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            ActivityOptions options = ActivityOptions.makeCustomAnimation(
//                    this,
//                    R.anim.zoom_in,
//                    R.anim.zoom_out
//            );
//            startActivity(intent, options.toBundle());
//            Log.d("ANIM_DEBUG", "Started with ActivityOptions (JellyBean)");
//        }
//        // Для старых версий Android
//        else {
//            startActivity(intent);
//            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
//            Log.d("ANIM_DEBUG", "Started with overridePendingTransition");
//        }
//    }



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
        } else if (id == R.id.theme_light) {
            setAppTheme(AppCompatDelegate.MODE_NIGHT_NO);
            return true;
        } else if (id == R.id.theme_dark) {
            setAppTheme(AppCompatDelegate.MODE_NIGHT_YES);
            return true;
        } else if (id == R.id.logout) {
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

    private void performLogout() {
        clearUserSession();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void clearUserSession() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Вы вышли из системы", Toast.LENGTH_SHORT).show();
    }
}