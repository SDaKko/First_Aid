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

public class AdminActivity extends AppCompatActivity {
    private ListView lvUsers;
    private EditText etNewLogin, etNewPassword;
    private Spinner spinnerNewPosition;
    private Button btnAddUser, btnLogout;
    private DatabaseHelper databaseHelper;
    private List<User> userList;
    private UserAdapter userAdapter;

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

    private void initViews() {
        lvUsers = findViewById(R.id.lvUsers);
        etNewLogin = findViewById(R.id.etNewLogin);
        etNewPassword = findViewById(R.id.etNewPassword);
        spinnerNewPosition = findViewById(R.id.spinnerNewPosition);
        btnAddUser = findViewById(R.id.btnAddUser);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupSpinner() {
        String[] positions = getResources().getStringArray(R.array.positions_array);
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
    }

    private void addNewUser() {
        String login = etNewLogin.getText().toString().trim();
        String password = etNewPassword.getText().toString().trim();
        String position = spinnerNewPosition.getSelectedItem().toString();

        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверяем, не пытаемся ли создать второго админа
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
            loadUsers(); // Обновляем список
        } else {
            Toast.makeText(this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputFields() {
        etNewLogin.setText("");
        etNewPassword.setText("");
        spinnerNewPosition.setSelection(0);
    }

    private void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Адаптер для списка пользователей
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

            // Обработчик удаления пользователя
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