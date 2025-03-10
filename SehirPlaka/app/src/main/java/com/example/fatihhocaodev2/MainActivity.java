package com.example.fatihhocaodev2;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {


    private ListView cityListView, numberListView;
    private Button shuffleButton;
    private List<String> cities;
    private List<Integer> cityNumbers;
    private HashMap<String, Integer> cityPlates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cityListView = findViewById(R.id.cityListView);
        numberListView = findViewById(R.id.numberListView);
        shuffleButton = findViewById(R.id.shuffleButton);

        cities = new ArrayList<>();
        cityNumbers = new ArrayList<>();
        cityPlates = new HashMap<>();

        initializeCities();
        shuffleLists();

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleLists();
            }
        });

        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = cities.get(position);
                int assignedNumber = cityNumbers.get(position);
                int realPlate = cityPlates.get(selectedCity);

                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("city", selectedCity);
                intent.putExtra("assignedNumber", assignedNumber);
                intent.putExtra("realPlate", realPlate);
                startActivity(intent);
            }
        });
    }

    private void initializeCities() {
        String[] allCities = {"Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin", "Aydın", "Balıkesir",
                "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli",
                "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari",
                "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir", "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir",
                "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir",
                "Niğde", "Ordu", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat",
                "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman",
                "Kırıkkale", "Batman", "Şırnak", "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye", "Düzce"};

        for (int i = 0; i < allCities.length; i++) {
            cityPlates.put(allCities[i], i + 1);
        }
    }

    private void shuffleLists() {
        List<String> allCityNames = new ArrayList<>(cityPlates.keySet());
        Collections.shuffle(allCityNames);

        cities.clear();
        cityNumbers.clear();

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            cities.add(allCityNames.get(i));
            cityNumbers.add(random.nextInt(81) + 1);
        }

        cityListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities));
        numberListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityNumbers));
    }
}

