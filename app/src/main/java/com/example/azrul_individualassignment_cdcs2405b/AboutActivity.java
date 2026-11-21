package com.example.azrul_individualassignment_cdcs2405b;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    Button btnBackAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Connect Back button
        btnBackAbout = findViewById(R.id.btnBackAbout);

        // Return to Calculator
        btnBackAbout.setOnClickListener(v -> finish());
    }
}
