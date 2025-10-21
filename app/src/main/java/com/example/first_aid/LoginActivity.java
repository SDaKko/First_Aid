package com.example.first_aid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private InputFieldsFragment inputFieldsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isUserLoggedIn()) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // Получаем ссылки на фрагменты
        inputFieldsFragment = (InputFieldsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.inputFieldsFragment);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getBoolean("is_logged_in", false);
    }

    // Методы, вызываемые из фрагментов
    public void onLoginButtonClicked() {
        if (inputFieldsFragment == null) return;

        String login = inputFieldsFragment.getLogin();
        String password = inputFieldsFragment.getPassword();

        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.isAdmin(login)) {
            goToAdminActivity();
            return;
        }

        if (databaseHelper.checkUser(login, password)) {
            saveUserSession(login);
            Toast.makeText(this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show();
            goToMainActivity();
        } else {
            Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRegisterLinkClicked() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveUserSession(String login) {
        User user = databaseHelper.getUser(login);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_login", user.getLogin());
        editor.putString("user_position", user.getPosition());
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void goToAdminActivity() {
        Intent intent = new Intent(this, AdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}