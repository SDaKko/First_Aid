package com.example.first_aid;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.regex.Pattern;

public class RegistrationActivity extends BaseActivity {
    private TextInputEditText etLogin, etPassword;
    private TextInputLayout textInputLayoutLogin, textInputLayoutPassword;
    private Spinner spinnerPosition;
    private DatabaseHelper databaseHelper;

    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z]{3,}[a-zA-Z0-9_]*$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).{5,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        databaseHelper = new DatabaseHelper(this);

        initViews();
        setupSpinner();
        setupClickListeners();
        setupTextWatchers();
    }

    private void initViews() {
        textInputLayoutLogin = findViewById(R.id.textInputLayoutLogin);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);

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

    private void setupTextWatchers() {
        etLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateLoginLive(s.toString());
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validatePasswordLive(s.toString());
            }
        });
    }

    private void registerUser() {
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String position = spinnerPosition.getSelectedItem().toString();

        boolean isLoginValid = validateLogin(login);
        boolean isPasswordValid = validatePassword(password);

        if (!isLoginValid || !isPasswordValid) {
            Toast.makeText(this, "Исправьте ошибки в форме", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("admin".equals(login)) {
            textInputLayoutLogin.setError("Логин 'admin' зарезервирован для системного администратора");
            return;
        }

        if (databaseHelper.isLoginExists(login)) {
            textInputLayoutLogin.setError("Логин уже существует");
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

    private void validateLoginLive(String login) {
        if (login.isEmpty()) {
            textInputLayoutLogin.setError(null);
            return;
        }

        if (login.length() < 3) {
            textInputLayoutLogin.setError("Минимум 3 символа");
        } else if (login.length() > 20) {
            textInputLayoutLogin.setError("Максимум 20 символов");
        } else if (!LOGIN_PATTERN.matcher(login).matches()) {
            textInputLayoutLogin.setError("Начинается с 3+ латинских букв, затем буквы/цифры/_");
        } else {
            textInputLayoutLogin.setError(null);
        }
    }

    private void validatePasswordLive(String password) {
        if (password.isEmpty()) {
            textInputLayoutPassword.setError(null);
            return;
        }

        if (password.length() < 5) {
            textInputLayoutPassword.setError("Минимум 5 символов");
        } else if (password.length() > 30) {
            textInputLayoutPassword.setError("Максимум 30 символов");
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            textInputLayoutPassword.setError("Нужна хотя бы 1 буква и 1 цифра");
        } else if (isWeakPassword(password)) {
            textInputLayoutPassword.setError("Слишком простой пароль");
        } else {
            textInputLayoutPassword.setError(null);
        }
    }

    private boolean validateLogin(String login) {
        if (login.isEmpty()) {
            textInputLayoutLogin.setError("Логин не может быть пустым");
            return false;
        }

        if (login.length() < 3) {
            textInputLayoutLogin.setError("Логин должен содержать минимум 3 символа");
            return false;
        }

        if (login.length() > 20) {
            textInputLayoutLogin.setError("Логин не может быть длиннее 20 символов");
            return false;
        }

        if (!LOGIN_PATTERN.matcher(login).matches()) {
            textInputLayoutLogin.setError("Логин должен:\n- Начинаться с 3+ латинских букв\n- Затем могут идти буквы, цифры или _\n- Не может содержать спецсимволы кроме _");
            return false;
        }

        textInputLayoutLogin.setError(null);
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            textInputLayoutPassword.setError("Пароль не может быть пустым");
            return false;
        }

        if (password.length() < 5) {
            textInputLayoutPassword.setError("Пароль должен содержать минимум 5 символов");
            return false;
        }

        if (password.length() > 30) {
            textInputLayoutPassword.setError("Пароль не может быть длиннее 30 символов");
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            textInputLayoutPassword.setError("Пароль должен содержать хотя бы одну букву и одну цифру");
            return false;
        }

        if (isWeakPassword(password)) {
            textInputLayoutPassword.setError("Пароль слишком простой. Используйте буквы в разных регистрах, цифры и специальные символы");
            return false;
        }

        textInputLayoutPassword.setError(null);
        return true;
    }

    private boolean isWeakPassword(String password) {

        if (password.matches("^[0-9]+$")) {
            return true;
        }


        if (password.matches("^[a-zA-Z]+$")) {
            return true;
        }


        String[] weakPatterns = {
                "12345", "123456", "1234567", "12345678", "123456789", "1234567890",
                "password", "qwerty", "admin", "11111", "00000", "aaaaa"
        };

        for (String pattern : weakPatterns) {
            if (password.equalsIgnoreCase(pattern)) {
                return true;
            }
        }

        return false;
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}