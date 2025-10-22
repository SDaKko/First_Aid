package com.example.first_aid;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.regex.Pattern;

public class AdminActivity extends AppCompatActivity {
    private ListView lvUsers;
    private Spinner spinnerNewPosition;
    private Button btnAddUser, btnLogout, btnIncidents;
    private DatabaseHelper databaseHelper;
    private List<User> userList;
    private UserAdapter userAdapter;
    private InputFieldsFragment inputFieldsFragment;

    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).{5,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        databaseHelper = new DatabaseHelper(this);
        initViews();
        setupSpinner();
        loadUsers();
        setupClickListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Получаем ссылку на фрагмент после создания активности
        inputFieldsFragment = (InputFieldsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.adminLoginFieldsFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLanguageIfNeeded();
    }

    private void updateLanguageIfNeeded() {
        String currentLanguage = getResources().getConfiguration().locale.getLanguage();
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String savedLanguage = prefs.getString("app_language", "ru");

        if (!currentLanguage.equals(savedLanguage)) {
            recreate();
        }
    }

    private void initViews() {
        lvUsers = findViewById(R.id.lvUsers);
        spinnerNewPosition = findViewById(R.id.spinnerNewPosition);
        btnAddUser = findViewById(R.id.btnAddUser);
        btnLogout = findViewById(R.id.btnLogout);
        btnIncidents = findViewById(R.id.btnIncidents);
    }

    private void setupSpinner() {
        // Получаем список должностей из БД
        List<String> positions = databaseHelper.getAllPositions();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, positions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNewPosition.setAdapter(adapter);
    }

    private void loadUsers() {
        userList = databaseHelper.getAllUsers();
        userAdapter = new UserAdapter(userList);
        lvUsers.setAdapter(userAdapter);
    }

    private void setupClickListeners() {
        btnAddUser.setOnClickListener(v -> addNewUser());
        btnLogout.setOnClickListener(v -> logout());
        btnIncidents.setOnClickListener(v -> goToIncidents());
    }

    private void addNewUser() {
        if (inputFieldsFragment == null) {
            Toast.makeText(this, "Фрагмент не загружен", Toast.LENGTH_SHORT).show();
            return;
        }

        String login = inputFieldsFragment.getLogin();
        String password = inputFieldsFragment.getPassword();
        String position = spinnerNewPosition.getSelectedItem().toString();

        if (!validateLogin(login) || !validatePassword(password)) {
            return;
        }

        if ("admin".equals(login)) {
            Toast.makeText(this, "Логин 'admin' зарезервирован", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.isLoginExists(login)) {
            Toast.makeText(this, "Логин уже существует", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User(login, password, position);
        if (databaseHelper.addUser(newUser)) {
            Toast.makeText(this, "Пользователь добавлен", Toast.LENGTH_SHORT).show();
            clearInputFields();
            loadUsers();
        } else {
            Toast.makeText(this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateLogin(String login) {
        if (login.isEmpty()) {
            showValidationError("Логин не может быть пустым");
            return false;
        }

        if (login.length() < 3) {
            showValidationError("Логин должен содержать минимум 3 символа");
            return false;
        }

        if (login.length() > 20) {
            showValidationError("Логин не может быть длиннее 20 символов");
            return false;
        }

        if (!LOGIN_PATTERN.matcher(login).matches()) {
            showValidationError("Логин может содержать только буквы (a-z, A-Z), цифры (0-9) и символ подчеркивания (_)");
            return false;
        }

        return true;
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            showValidationError("Пароль не может быть пустым");
            return false;
        }

        if (password.length() < 5) {
            showValidationError("Пароль должен содержать минимум 5 символов");
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            showValidationError("Пароль должен содержать хотя бы одну букву и одну цифру");
            return false;
        }

        return true;
    }

    private void showValidationError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ошибка валидации")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void clearInputFields() {
        if (inputFieldsFragment != null) {
            inputFieldsFragment.setLogin("");
            inputFieldsFragment.setPassword("");
        }
        spinnerNewPosition.setSelection(0);
    }

    private void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void goToIncidents() {
        Intent intent = new Intent(this, Incidents.class);
        startActivity(intent);
    }

    private class UserAdapter extends ArrayAdapter<User> {
        UserAdapter(List<User> users) {
            super(AdminActivity.this, R.layout.item_user, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_user, parent, false);
            }

            User user = getItem(position);
            TextView tvLogin = convertView.findViewById(R.id.tvLogin);
            TextView tvPosition = convertView.findViewById(R.id.tvPosition);
            Button btnDelete = convertView.findViewById(R.id.btnDelete);

            tvLogin.setText(user.getLogin());
            tvPosition.setText(user.getPosition());

            btnDelete.setOnClickListener(v -> showDeleteConfirmation(user));

            return convertView;
        }
    }

    private void showDeleteConfirmation(User user) {
        // Не позволяем удалить самого админа
        if ("admin".equals(user.getLogin())) {
            Toast.makeText(this, "Нельзя удалить администратора", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление пользователя")
                .setMessage("Вы уверены, что хотите удалить пользователя " + user.getLogin() + "?")
                .setPositiveButton("Удалить", (dialog, which) -> deleteUser(user))
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void deleteUser(User user) {
        if (databaseHelper.deleteUser(user.getLogin())) {
            Toast.makeText(this, "Пользователь удален", Toast.LENGTH_SHORT).show();
            loadUsers(); // Обновляем список
        } else {
            Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
        }
    }
}