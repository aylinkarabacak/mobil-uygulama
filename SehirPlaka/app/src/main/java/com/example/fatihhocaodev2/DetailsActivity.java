package com.example.fatihhocaodev2;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.View;
public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView textView = findViewById(R.id.textView);

        Intent intent = getIntent();
        String city = intent.getStringExtra("city");
        int assignedNumber = intent.getIntExtra("assignedNumber", -1);
        int realPlate = intent.getIntExtra("realPlate", -1);

        String message = "Şehir: " + city + "\nAtanan Plaka: " + assignedNumber + "\nGerçek Plaka: " + realPlate;
        textView.setText(message);

        if (assignedNumber == realPlate) {
            Toast.makeText(this, "Doğru! " + city + " şehrinin plakasıdır.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Yanlış! " + city + " şehrinin plakası " + realPlate + " olmalıydı.", Toast.LENGTH_LONG).show();
        }

        Button btnGeriDon = findViewById(R.id.btnGeriDon);
        btnGeriDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    }
