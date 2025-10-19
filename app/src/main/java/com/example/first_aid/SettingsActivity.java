package com.example.first_aid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity {
    private String userName;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Получаем данные из родительской активности
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString("user_name", "Пользователь");
            userId = extras.getInt("user_id", 0);

            TextView tvUserInfo = findViewById(R.id.tvUserInfo);
            tvUserInfo.setText("Пользователь: " + userName + "\nID: " + userId);
        }

        Button btnContactSupport = findViewById(R.id.btnContactSupport);
        Button btnBackToMain = findViewById(R.id.btnBackToMain);

        // НЕЯВНОЕ НАМЕРЕНИЕ - Отправка email в поддержку
        btnContactSupport.setOnClickListener(v -> contactSupport());

        // Возврат с результатом
        btnBackToMain.setOnClickListener(v -> goBackWithResult());

    }

    // НЕЯВНОЕ НАМЕРЕНИЕ - отправка email в поддержку
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

            // После отправки сообщения
            returnWithResult("support_contacted", "Сообщение поддержки отправлено");
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка при отправке сообщения", Toast.LENGTH_SHORT).show();
        }
    }

    private void goBackWithResult() {
        // Возвращаем результат родительской активности
        returnWithResult("settings_closed", "Настройки закрыты");
    }

    // Метод для возврата результата родительской активности
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