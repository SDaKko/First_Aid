package com.example.first_aid;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class IncidentAdapter extends ArrayAdapter<Incident> {

    private Context context;
    private List<Incident> incidents;
    private LayoutInflater inflater;

    public IncidentAdapter(@NonNull Context context, List<Incident> incidents) {
        super(context, R.layout.item_incident, incidents);
        this.context = context;
        this.incidents = incidents;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_incident, parent, false);
            holder = new ViewHolder();
            holder.ivIcon = convertView.findViewById(R.id.ivIncidentIcon);
            holder.tvTitle = convertView.findViewById(R.id.tvIncidentTitle);
            holder.tvCategory = convertView.findViewById(R.id.tvIncidentCategory);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Incident incident = incidents.get(position);

        // Устанавливаем данные
        holder.tvTitle.setText(incident.getTitle());
        holder.tvCategory.setText(getCategoryDisplayName(incident.getCategory()));

        // Устанавливаем иконку в зависимости от категории
        holder.ivIcon.setImageResource(getIconForCategory(incident.getCategory()));

        // ВАЖНО: Добавляем обработчик клика на весь элемент
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Открываем активность FirstAid с выбранным происшествием
                Intent intent = new Intent(context, FirstAid.class);
                intent.putExtra("incident_id", incident.getId());
                intent.putExtra("incident_title", incident.getTitle());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    // Метод для получения отображаемого имени категории
    private String getCategoryDisplayName(String category) {
        switch (category) {
            case "medical":
                return "Медицинская помощь";
            case "technical":
                return "Техническая помощь";
            case "transport":
                return "Транспорт";
            case "education":
                return "Образование";
            case "universal":
                return "Универсальная";
            default:
                return "Другая категория";
        }
    }

    // Метод для получения иконки по категории
    private int getIconForCategory(String category) {
        switch (category) {
            case "medical":
                return R.drawable.ic_medical;
            case "technical":
                return R.drawable.ic_technical;
            case "transport":
                return R.drawable.ic_transport;
            case "education":
                return R.drawable.ic_education;
            case "universal":
                return R.drawable.ic_universal;
            default:
                return R.drawable.ic_incident;
        }
    }

    // ViewHolder для оптимизации
    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvCategory;
    }
}