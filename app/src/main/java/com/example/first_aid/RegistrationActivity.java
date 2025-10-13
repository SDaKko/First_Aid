package com.example.first_aid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.textfield.TextInputEditText;
import java.util.regex.Pattern;

public class RegistrationActivity extends BaseActivity {
    private TextInputEditText etLogin, etPassword;
    private Spinner spinnerPosition;
    private DatabaseHelper databaseHelper;

    // Регулярные выражения для валидации
    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).{5,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        databaseHelper = new DatabaseHelper(this);

        initViews();
        setupSpinner();
        setupClickListeners();
    }

    private void initViews() {
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        spinnerPosition = findViewById(R.id.spinnerPosition);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);
    }

    private void setupSpinner() {
        String[] positions = getResources().getStringArray(R.array.positions_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, positions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPosition.setAdapter(adapter);
    }

    private void setupClickListeners() {
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);

        btnRegister.setOnClickListener(v -> registerUser());
        tvLoginLink.setOnClickListener(v -> goToLogin());
    }

    private void registerUser() {
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String position = spinnerPosition.getSelectedItem().toString();

        // ВАЛИДАЦИЯ ДАННЫХ
        if (!validateLogin(login) || !validatePassword(password)) {
            return;
        }

        // Проверяем, не пытаемся ли создать админа
        if ("admin".equals(login)) {
            showValidationError("Логин 'admin' зарезервирован для системного администратора");
            return;
        }

        if (databaseHelper.isLoginExists(login)) {
            Toast.makeText(this, "Логин уже существует", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User(login, password, position);
        if (databaseHelper.addUser(newUser)) {
            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
            goToLogin();
        } else {
            Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
        }
    }

    // ВАЛИДАЦИЯ ЛОГИНА
    private boolean validateLogin(String login) {
        if (login.isEmpty()) {
            showValidationError("Логин не может быть пустым");
            etLogin.requestFocus();
            return false;
        }

        if (login.length() < 3) {
            showValidationError("Логин должен содержать минимум 3 символа");
            etLogin.requestFocus();
            return false;
        }

        if (login.length() > 20) {
            showValidationError("Логин не может быть длиннее 20 символов");
            etLogin.requestFocus();
            return false;
        }

        if (!LOGIN_PATTERN.matcher(login).matches()) {
            showValidationError("Логин может содержать только:\n• Латинские буквы (a-z, A-Z)\n• Цифры (0-9)\n• Символ подчеркивания (_)\n\nНе допускаются пробелы и специальные символы");
            etLogin.requestFocus();
            return false;
        }

        return true;
    }

    // ВАЛИДАЦИЯ ПАРОЛЯ
    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            showValidationError("Пароль не может быть пустым");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 5) {
            showValidationError("Пароль должен содержать минимум 5 символов");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() > 30) {
            showValidationError("Пароль не может быть длиннее 30 символов");
            etPassword.requestFocus();
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            showValidationError("Пароль должен содержать:\n- Минимум одну букву (a-z, A-Z)\n- Минимум одну цифру (0-9)\n- Минимум 5 символов\n\nМогут использоваться любые символы");
            etPassword.requestFocus();
            return false;
        }

        // Дополнительная проверка на слабый пароль
        if (isWeakPassword(password)) {
            showValidationError("Слишком слабый пароль. Рекомендуем использовать комбинацию из букв в разных регистрах, цифр и специальных символов");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    // ПРОВЕРКА НА СЛАБЫЙ ПАРОЛЬ
    private boolean isWeakPassword(String password) {
        // Проверяем, состоит ли пароль только из цифр
        if (password.matches("^[0-9]+$")) {
            return true;
        }

        // Проверяем, состоит ли пароль только из букв
        if (password.matches("^[a-zA-Z]+$")) {
            return true;
        }

        // Проверяем простые последовательности
        String[] weakPatterns = {
                "12345", "123456", "1234567", "12345678", "123456789",
                "password", "qwerty", "admin", "11111", "00000"
        };

        for (String pattern : weakPatterns) {
            if (password.equalsIgnoreCase(pattern)) {
                return true;
            }
        }

        return false;
    }

    private void showValidationError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ошибка ввода данных")
                .setMessage(message)
                .setPositiveButton("Понятно", null)
                .show();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}