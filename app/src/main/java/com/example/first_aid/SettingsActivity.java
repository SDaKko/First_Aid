package com.example.first_aid;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class SettingsActivity extends BaseActivity {
    private String userName;
    private int userId;
    private TextInputEditText etNewPassword;
    private Spinner spinnerNewPosition;
    private DatabaseHelper databaseHelper;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString("user_name", "Пользователь");
            userId = extras.getInt("user_id", 0);

            currentUser = databaseHelper.getUser(userName);

            TextView tvUserInfo = findViewById(R.id.tvUserInfo);
            if (currentUser != null) {
                tvUserInfo.setText(getResources().getText(R.string.user) + " " + currentUser.getLogin() +
                        "\n" + getResources().getText(R.string.position) + " " +  currentUser.getPosition());
            } else {
                tvUserInfo.setText(getResources().getText(R.string.user) + " " + userName);
            }
        }

        initViews();
        setupSpinner();
        setupClickListeners();
    }

    private void initViews() {
        etNewPassword = findViewById(R.id.etNewPassword);
        spinnerNewPosition = findViewById(R.id.spinnerNewPosition);
        Button btnContactSupport = findViewById(R.id.btnContactSupport);
        Button btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        Button btnBackToMain = findViewById(R.id.btnBackToMain);
    }

    private void setupSpinner() {
        // Получаем список должностей из БД
        List<String> positions = databaseHelper.getAllPositions();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, positions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNewPosition.setAdapter(adapter);

        // Устанавливаем текущую должность как выбранную
        if (currentUser != null && currentUser.getPosition() != null) {
            String currentPosition = currentUser.getPosition();
            for (int i = 0; i < positions.size(); i++) {
                if (positions.get(i).equals(currentPosition)) {
                    spinnerNewPosition.setSelection(i);
                    break;
                }
            }
        }
    }

    private void setupClickListeners() {
        Button btnContactSupport = findViewById(R.id.btnContactSupport);
        Button btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        Button btnBackToMain = findViewById(R.id.btnBackToMain);

        btnContactSupport.setOnClickListener(v -> contactSupport());
        btnUpdateProfile.setOnClickListener(v -> updateUserProfile());
        btnBackToMain.setOnClickListener(v -> goBackWithResult());
    }

    private void updateUserProfile() {
        if (currentUser == null) {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        String newPassword = etNewPassword.getText().toString().trim();
        String newPosition = spinnerNewPosition.getSelectedItem().toString();

        if (!newPassword.isEmpty() && !isValidPassword(newPassword)) {
            showPasswordValidationError();
            return;
        }

        String finalPassword = newPassword.isEmpty() ? currentUser.getPassword() : newPassword;
        String finalPosition = newPosition.equals(currentUser.getPosition()) ?
                currentUser.getPosition() : newPosition;

        User updatedUser = new User(
                currentUser.getLogin(),
                finalPassword,
                finalPosition
        );

        boolean success = databaseHelper.updateUser(userName, updatedUser);

        if (success) {
            StringBuilder message = new StringBuilder("Профиль обновлен");

            if (!newPassword.isEmpty()) {
                message.append(", пароль изменен");
            }
            if (!newPosition.equals(currentUser.getPosition())) {
                message.append(", должность изменена на: ").append(newPosition);
            }

            if (newPassword.isEmpty() && newPosition.equals(currentUser.getPosition())) {
                message.append(" (изменений нет)");
            }

            returnWithResult("user_updated", message.toString());

            etNewPassword.setText("");

        } else {
            Toast.makeText(this, "Ошибка при обновлении профиля", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 5) {
            return false;
        }

        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }

        return hasLetter && hasDigit;
    }

    private void showPasswordValidationError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ошибка валидации пароля")
                .setMessage("Пароль должен:\n- Содержать минимум 5 символов\n- Содержать хотя бы одну букву и одну цифру")
                .setPositiveButton("OK", null)
                .show();
    }

    // Неявное намерение
    private void contactSupport() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@firstaidapp.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Вопрос по приложению First Aid");
        intent.putExtra(Intent.EXTRA_TEXT,
                "Здравствуйте, команда поддержки First Aid!\n\n" +
                        "У меня вопрос по работе приложения от пользователя: " + userName + "\n\n" +
                        "С уважением,\n" + userName);

        try {
            Intent chooser = Intent.createChooser(intent, "Отправить сообщение поддержки через:");
            startActivity(chooser);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка при отправке сообщения", Toast.LENGTH_SHORT).show();
        }
    }

    private void goBackWithResult() {
        returnWithResult("settings_closed", "Настройки закрыты без изменений");
    }

    private void returnWithResult(String action, String message) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("action", action);
        resultIntent.putExtra("message", message);
        resultIntent.putExtra("user_name", userName);
        resultIntent.putExtra("user_id", userId);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

}