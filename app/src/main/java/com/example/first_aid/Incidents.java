package com.example.first_aid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class Incidents extends AppCompatActivity {

    List<Incident> incidentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_incidents);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        incidentsList = new ArrayList<>();

        incidentsList.add(new Incident("Инцидент 1",
                "Порез руки - промыть рану, обработать антисептиком, наложить повязку", R.id.incident1));
        incidentsList.add(new Incident("Инцидент 2",
                "Ожог - охладить место ожога, нанести противоожоговую мазь, наложить стерильную повязку", R.id.incident2));
        incidentsList.add(new Incident("Инцидент 3",
                "Перелом - обездвижить конечность, приложить холод, доставить в травмпункт", R.id.incident3));

        setupIncidentViews();
    }

    public void goToFirstActivity(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void setupIncidentViews() {
        for (Incident incident : incidentsList) {
            TextView textView = findViewById(incident.getViewId());

            if (textView != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showInfo(incident.getDescription());
                    }
                });
            }
        }

    }

    private void showInfo(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}