package com.example.plakaoyunu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private ListView listView;
    private Button returnButton;
    private static ArrayList<String> correctAnswers = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        listView = findViewById(R.id.resultListView);
        returnButton = findViewById(R.id.returnButton);

        String city = getIntent().getStringExtra("city");
        double time = getIntent().getDoubleExtra("time", 0.0);

        correctAnswers.add(city + " - " + time + " saniye");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, correctAnswers);
        listView.setAdapter(adapter);

        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
