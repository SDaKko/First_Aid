package com.example.first_aid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends BaseActivity {
    private String userName;
    private String userPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Получаем данные из родительской активности
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString("user_name", "Пользователь");
            userPosition = extras.getString("user_position", "Универсальная");

            TextView tvAppInfo = findViewById(R.id.tvAppInfo);
            tvAppInfo.setText(getResources().getText(R.string.about_first_aid) + "\n\n" + getResources().getText(R.string.welcome) + " " +
                    userName + " (" + userPosition + ")!");
        }

        Button btnShareApp = findViewById(R.id.btnShareApp);
        Button btnBack = findViewById(R.id.btnBack);

        // Неявное намерение
        btnShareApp.setOnClickListener(v -> shareApp());

        // Возврат с результатом
        btnBack.setOnClickListener(v -> goBackWithResult());
    }

    // Неявное намерение - отправка email
    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "First Aid - Приложение первой помощи");
        intent.putExtra(Intent.EXTRA_TEXT,
                "Рекомендую попробовать приложение First Aid! \n" +
                        "Рекомендовал: " + userName + " (" + userPosition + ")\n\n" +
                        "Скачайте в Google Play: [ссылка на приложение]");

        try {
            Intent chooser = Intent.createChooser(intent, "Поделиться приложением через");
            startActivity(chooser);

            returnWithResult(true, "Приложение успешно рекомендовано");
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка при рекомендации приложения", Toast.LENGTH_SHORT).show();
        }
    }

    private void goBackWithResult() {
        returnWithResult(false, "Возврат из информации о приложении");
    }

    private void returnWithResult(boolean appShared, String message) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("app_shared", appShared);
        resultIntent.putExtra("message", message);
        resultIntent.putExtra("user_name", userName);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

}