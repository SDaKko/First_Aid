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

    // Добавляем TextView для отображения информации о пользователе
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

        // Инициализируем UI элементы
        initUserInfoViews();

        // Показываем текущую информацию о пользователе
        displayCurrentUserInfo();

        setupNavigation();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initUserInfoViews() {
        // Находим TextView для отображения информации о пользователе
        tvUserName = findViewById(R.id.tvUserName);
        tvUserPosition = findViewById(R.id.tvUserPosition);
    }

    // ОБНОВЛЕНИЕ ИНФОРМАЦИИ О ПОЛЬЗОВАТЕЛЕ НА UI И В БАЗЕ ДАННЫХ
    // Оставляем только общий метод обновления UI
    private void updateUserInfo() {
        String currentLogin = userSession.getString("user_login", "");

        if (!currentLogin.isEmpty()) {
            User currentUser = databaseHelper.getUser(currentLogin);

            if (currentUser != null) {
                // Обновляем SharedPreferences
                SharedPreferences.Editor editor = userSession.edit();
                editor.putString("user_login", currentUser.getLogin());
                editor.putString("user_position", currentUser.getPosition());
                editor.apply();

                // Обновляем UI
                displayUserInfo(currentUser);

                Toast.makeText(this, "Информация о пользователе обновлена", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ОБНОВЛЕНИЕ ДАННЫХ ПОЛЬЗОВАТЕЛЯ В БАЗЕ (используя метод из DatabaseHelper)
    private boolean updateUserInDatabase(String oldLogin, User updatedUser) {
        return databaseHelper.updateUser(oldLogin, updatedUser);
    }


    // ОТОБРАЖЕНИЕ ТЕКУЩЕЙ ИНФОРМАЦИИ О ПОЛЬЗОВАТЕЛЕ
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

    // ОТОБРАЖЕНИЕ ИНФОРМАЦИИ О КОНКРЕТНОМ ПОЛЬЗОВАТЕЛЕ
    private void displayUserInfo(User user) {
        if (tvUserName != null) {
            tvUserName.setText("Пользователь: " + user.getLogin());
        }
        if (tvUserPosition != null) {
            tvUserPosition.setText("Должность: " + user.getPosition());
        }
    }



    private void setupNavigation() {
        // Кнопка перехода в Настройки
        findViewById(R.id.settingsButton).setOnClickListener(v -> openSettings());

        // Кнопка перехода "О приложении"
        findViewById(R.id.aboutButton).setOnClickListener(v -> openAbout());
    }

    // ЯВНОЕ НАМЕРЕНИЕ с ожиданием результата
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);

        // Передаем данные в дочернюю активность
        intent.putExtra("user_name", userSession.getString("user_login", "Пользователь"));
        intent.putExtra("user_id", 12345);

        // Запускаем активность с ожиданием результата
        startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }

    // ЯВНОЕ НАМЕРЕНИЕ с ожиданием результата
    private void openAbout() {
        Intent intent = new Intent(this, AboutActivity.class);

        // Передаем данные в дочернюю активность
        intent.putExtra("user_name", userSession.getString("user_login", "Пользователь"));
        intent.putExtra("user_position", userSession.getString("user_position", "Гость"));

        startActivityForResult(intent, ABOUT_REQUEST_CODE);
    }

    // ОБРАБОТКА РЕЗУЛЬТАТА ИЗ ДОЧЕРНИХ АКТИВНОСТЕЙ
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SETTINGS_REQUEST_CODE:
                    // Обработка возврата из настроек
                    handleSettingsResult(data);
                    break;

                case ABOUT_REQUEST_CODE:
                    // Обработка возврата из "О приложении"
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
                recreate(); // Пересоздаем для применения языка
            } else if ("profile_updated".equals(action)) {
                // ОБНОВЛЯЕМ ИНФОРМАЦИЮ О ПОЛЬЗОВАТЕЛЕ
                updateUserInfo();
                Toast.makeText(this, "Профиль обновлен: " + message, Toast.LENGTH_SHORT).show();
            } else if ("user_updated".equals(action)) {
                // ОБНОВЛЯЕМ ИНФОРМАЦИЮ О ПОЛЬЗОВАТЕЛЕ
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
            if (appShared) {
                Toast.makeText(this, "Спасибо, что поделились приложением!", Toast.LENGTH_SHORT).show();
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

    public void goToSecondActivity(View v) {
        Intent intent = new Intent(this, Incidents.class);
        startActivity(intent);
    }

}