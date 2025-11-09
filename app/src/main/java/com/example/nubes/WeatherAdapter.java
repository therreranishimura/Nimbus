package com.example.nubes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private List<WeatherItem> weatherList;

    public WeatherAdapter(List<WeatherItem> weatherList) {
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherItem item = weatherList.get(position);
        holder.textCity.setText(item.getCity());
        holder.textTemperature.setText(item.getTemperature());
        holder.textCondition.setText(item.getCondition());
        holder.weatherIcon.setImageResource(item.getIconResId());
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textCity, textTemperature, textCondition;
        ImageView weatherIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCity = itemView.findViewById(R.id.textCity);
            textTemperature = itemView.findViewById(R.id.textTemperature);
            textCondition = itemView.findViewById(R.id.textCondition);
            weatherIcon = itemView.findViewById(R.id.imageViewWeatherIcon);
        }
    }

    public void setWeatherItems(List<WeatherItem> newWeatherItems) {
        this.weatherList.clear();
        this.weatherList.addAll(newWeatherItems);
        notifyDataSetChanged();
    }
}