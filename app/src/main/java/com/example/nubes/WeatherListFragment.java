package com.example.nubes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherListFragment extends Fragment {

    private RecyclerView recyclerView;
    private WeatherAdapter adapter;

    private static final String PREFS_NAME = "WeatherAppPrefs";
    private static final String LAST_CITY_KEY = "last_searched_city";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewWeather);
        FloatingActionButton fabChangeCity = view.findViewById(R.id.fabChangeCity);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WeatherAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        fabChangeCity.setOnClickListener(v -> showChangeCityDialog());

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastCity = prefs.getString(LAST_CITY_KEY, "São Paulo,SP");

        fetchWeatherData(lastCity);

        return view;
    }

    private void showChangeCityDialog() {
        if (getActivity() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Trocar cidade");

        final EditText input = new EditText(getActivity());
        input.setHint("Ex: Rio de Janeiro, RJ");
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(48, 24, 48, 24);
        layout.addView(input);
        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String city = input.getText().toString().trim();
            if (!city.isEmpty()) {
                fetchWeatherData(city);
            } else {
                Toast.makeText(getContext(), "Cidade não pode estar vazia", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void fetchWeatherData(String city) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.hgbrasil.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService service = retrofit.create(WeatherApiService.class);
        String apiKey = "99c4a43d";

        service.getWeather(apiKey, city).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().results == null) {
                    Toast.makeText(getContext(), "Erro ao obter dados. Verifique a cidade.", Toast.LENGTH_LONG).show();
                    return;
                }

                SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(LAST_CITY_KEY, city);
                editor.apply();

                WeatherResponse.Results data = response.body().results;
                if (data.forecast == null || data.forecast.isEmpty()) {
                    Toast.makeText(getContext(), "Cidade não encontrada ou sem previsão.", Toast.LENGTH_LONG).show();
                    return;
                }

                List<WeatherItem> newForecasts = new ArrayList<>();
                int diasParaExibir = 7;
                int diasAdicionados = 0;
                for (WeatherResponse.Forecast f : data.forecast) {
                    if (diasAdicionados >= diasParaExibir) {
                        break;
                    }

                    int iconResId = getWeatherIcon(f.description);

                    String infoTemp = "Máx: " + f.max + "°C | Mín: " + f.min + "°C";

                    newForecasts.add(new WeatherItem(
                            data.city + " (" + f.weekday + ")",
                            infoTemp,
                            f.description,
                            iconResId
                    ));
                    diasAdicionados++;
                }

                adapter.setWeatherItems(newForecasts);
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Falha de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private int getWeatherIcon(String condition) {
        if (condition == null) return R.drawable.default_weather;

        String lowerCaseCondition = condition.toLowerCase();

        if (lowerCaseCondition.contains("nublado")) {
            return R.drawable.cloudy;
        } else if (lowerCaseCondition.contains("chuva") || lowerCaseCondition.contains("tempestade")) {
            return R.drawable.rain;
        } else if (lowerCaseCondition.contains("sol") || lowerCaseCondition.contains("limpo") || lowerCaseCondition.contains("ensolarado")) {
            return R.drawable.sun;
        } else {
            return R.drawable.default_weather;
        }
    }
}