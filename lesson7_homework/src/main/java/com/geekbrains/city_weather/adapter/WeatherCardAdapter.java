package com.geekbrains.city_weather.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekbrains.city_weather.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeatherCardAdapter extends RecyclerView.Adapter<WeatherCardAdapter.CardViewHolder> {

    private ArrayList<DataForecast> dataForecast = new ArrayList<>();

    public WeatherCardAdapter(ArrayList<DataForecast> data) {
        if (data != null) {
            dataForecast = data;
        }
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_forecast, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.textViewDay.setText(dataForecast.get(position).day);
        holder.imageViewWhether.setImageDrawable(dataForecast.get(position).weatherDrawble);
        holder.textViewTemper.setText(dataForecast.get(position).temp);
    }

    @Override
    public int getItemCount() {
        return dataForecast.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDay;
        ImageView imageViewWhether;
        TextView textViewTemper;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDay = itemView.findViewById(R.id.textViewDay);
            imageViewWhether = itemView.findViewById(R.id.imageViewWhether);
            textViewTemper = itemView.findViewById(R.id.textViewTemper);
        }
    }
}
