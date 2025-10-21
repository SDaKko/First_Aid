package com.example.first_aid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends BaseActivity {

    private SharedPreferences userSession;
    private static final int SETTINGS_REQUEST_CODE = 1;
    private static final int ABOUT_REQUEST_CODE = 2;
    private DatabaseHelper databaseHelper;

    private TextView tvUserName, tvUserPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSession = getSharedPreferences("UserSession", MODE_PRIVATE);
        databaseHelper = new DatabaseHelper(this);

        if (!userSession.getBoolean("is_logged_in", false)) {
            goToLogin();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initUserInfoViews();

        displayCurrentUserInfo();

        setupNavigation();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initUserInfoViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserPosition = findViewById(R.id.tvUserPosition);
    }

    private void updateUserInfo() {
        String currentLogin = userSession.getString("user_login", "");

        if (!currentLogin.isEmpty()) {
            User currentUser = databaseHelper.getUser(currentLogin);

            if (currentUser != null) {
                SharedPreferences.Editor editor = userSession.edit();
                editor.putString("user_login", currentUser.getLogin());
                editor.putString("user_position", currentUser.getPosition());
                editor.apply();

                displayUserInfo(currentUser);

                Toast.makeText(this, "Информация о пользователе обновлена", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void displayCurrentUserInfo() {
        String login = userSession.getString("user_login", "");
        String position = userSession.getString("user_position", "");

        if (tvUserName != null) {
            tvUserName.setText("Пользователь: " + login);
        }
        if (tvUserPosition != null) {
            tvUserPosition.setText("Должность: " + position);
        }
    }

    private void displayUserInfo(User user) {
        if (tvUserName != null) {
            tvUserName.setText("Пользователь: " + user.getLogin());
        }
        if (tvUserPosition != null) {
            tvUserPosition.setText("Должность: " + user.getPosition());
        }
    }



    private void setupNavigation() {
        findViewById(R.id.incidentsButton).setOnClickListener(v -> goToIncidentsActivity());

        findViewById(R.id.settingsButton).setOnClickListener(v -> openSettings());

        findViewById(R.id.aboutButton).setOnClickListener(v -> openAbout());
    }

    // Явное намерение с ожиданием результата
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);

        // Передаем данные в дочернюю активность
        intent.putExtra("user_name", userSession.getString("user_login", "Пользователь"));
        intent.putExtra("user_id", 12345);

        startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }

    // Явное намерение с ожиданием результата
    private void openAbout() {
        Intent intent = new Intent(this, AboutActivity.class);

        intent.putExtra("user_name", userSession.getString("user_login", "Пользователь"));
        intent.putExtra("user_position", userSession.getString("user_position", "Универсальная"));

        startActivityForResult(intent, ABOUT_REQUEST_CODE);
    }

    // Обработка результата из дочерних активностей
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SETTINGS_REQUEST_CODE:
                    handleSettingsResult(data);
                    break;

                case ABOUT_REQUEST_CODE:
                    handleAboutResult(data);
                    break;
            }
        }
    }

    private void handleSettingsResult(Intent data) {
        if (data != null) {
            String action = data.getStringExtra("action");
            String message = data.getStringExtra("message");

            if ("language_changed".equals(action)) {
                Toast.makeText(this, "Язык изменен: " + message, Toast.LENGTH_SHORT).show();
                recreate();
            } else if ("user_updated".equals(action)) {
                updateUserInfo();
                Toast.makeText(this, "Данные пользователя обновлены: " + message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Настройки сохранены: " + message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleAboutResult(Intent data) {
        if (data != null) {
            boolean appShared = data.getBooleanExtra("app_shared", false);
            String notSharedText = data.getStringExtra("message");
            if (appShared) {
                Toast.makeText(this, "Спасибо, что поделились приложением!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, notSharedText, Toast.LENGTH_SHORT).show();
            }
        }
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

    public void goToIncidentsActivity() {
        Intent intent = new Intent(this, Incidents.class);
        startActivity(intent);
    }

}