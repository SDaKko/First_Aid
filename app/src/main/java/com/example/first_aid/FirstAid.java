package com.example.first_aid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class FirstAid extends BaseActivity {
    private DatabaseHelper databaseHelper;
    private int incidentId;
    private String incidentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_aid);

        databaseHelper = new DatabaseHelper(this);

        // Получаем данные из Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            incidentId = extras.getInt("incident_id", -1);
            incidentTitle = extras.getString("incident_title", "");
        }

        initViews();
        loadIncidentDetails();
        setupClickListeners();
    }

    private void initViews() {
        Button btnBack = findViewById(R.id.btnBack);
    }

    private void loadIncidentDetails() {
        if (incidentId == -1) {
            Toast.makeText(this, "Ошибка: происшествие не найдено", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Incident incident = databaseHelper.getIncidentById(incidentId);

        if (incident != null) {
            displayIncidentDetails(incident);
        } else {
            Toast.makeText(this, "Не удалось загрузить информацию о происшествии", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayIncidentDetails(Incident incident) {
        TextView tvTitle = findViewById(R.id.tvIncidentTitle);
        TextView tvDescription = findViewById(R.id.tvIncidentDescription);
        ImageView ivImage = findViewById(R.id.ivIncidentImage);

        // Устанавливаем заголовок
        tvTitle.setText(incident.getTitle());

        // Устанавливаем описание
        tvDescription.setText(incident.getDescription());

        // Загружаем изображение из assets
        if (incident.getImageUrl() != null && !incident.getImageUrl().isEmpty()) {
            ivImage.setVisibility(View.VISIBLE);
            loadImageFromAssets(ivImage, incident.getImageUrl());
        } else {
            ivImage.setVisibility(View.GONE);
        }
    }

    private void loadImageFromAssets(ImageView imageView, String imageName) {
        try {
            // Открываем поток напрямую из assets (без папки images)
            InputStream inputStream = getAssets().open(imageName);

            // Создаем Bitmap из InputStream
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                android.util.Log.d("FirstAid", "Image loaded successfully: " + imageName);
            } else {
                imageView.setImageResource(R.drawable.pic_error);
                android.util.Log.e("FirstAid", "Bitmap is null for: " + imageName);
                Toast.makeText(this, "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show();
            }

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            imageView.setImageResource(R.drawable.pic_error);
            android.util.Log.e("FirstAid", "File not found: " + imageName);
            Toast.makeText(this, "Файл не найден: " + imageName, Toast.LENGTH_SHORT).show();
        }
    }
    private void setupClickListeners() {
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

}