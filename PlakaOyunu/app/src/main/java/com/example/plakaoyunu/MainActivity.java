package com.example.plakaoyunu;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button startButton, checkButton;
    private SeekBar seekBar;
    private EditText cityInput;
    private TextView selectedPlakaText;
    private long startTime;
    private int selectedPlaka = 1;

    private static Map<Integer, String> plakaMap;

    static {
        plakaMap = new HashMap<>();
        String[] cities = {
                "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin", "Aydın", "Balıkesir",
                "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli",
                "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari",
                "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir", "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir",
                "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir",
                "Niğde", "Ordu", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat",
                "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman",
                "Kırıkkale", "Batman", "Şırnak", "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye",
                "Düzce"
        };
        for (int i = 0; i < cities.length; i++) {
            plakaMap.put(i + 1, cities[i].toLowerCase());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        checkButton = findViewById(R.id.checkButton);
        seekBar = findViewById(R.id.seekBar);
        cityInput = findViewById(R.id.cityInput);
        selectedPlakaText = findViewById(R.id.selectedPlakaText);

        seekBar.setMax(80); // 0-80 arası (1-81)
        selectedPlakaText.setText("Seçilen Plaka: 1");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedPlaka = progress + 1;
                selectedPlakaText.setText("Seçilen Plaka: " + selectedPlaka);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        startButton.setOnClickListener(v -> {
            startTime = SystemClock.elapsedRealtime();
            Toast.makeText(MainActivity.this, "Oyun Başladı!", Toast.LENGTH_SHORT).show();
        });

        checkButton.setOnClickListener(v -> {
            String userInput = cityInput.getText().toString().trim().toLowerCase();
            String correct = plakaMap.get(selectedPlaka);

            if (userInput.equals(correct)) {
                long elapsedMillis = SystemClock.elapsedRealtime() - startTime;
                double elapsedSeconds = elapsedMillis / 1000.0;

                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("city", plakaMap.get(selectedPlaka));
                intent.putExtra("time", elapsedSeconds);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Yanlış şehir adı. Tekrar deneyin.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
