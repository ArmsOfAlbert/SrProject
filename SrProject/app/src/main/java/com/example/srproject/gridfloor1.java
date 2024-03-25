package com.example.srproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

public class gridfloor1 extends AppCompatActivity {
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridfloor1);

        gridView = this.<GridView>findViewById(R.id.nodeGridView);



        // Set callback for when the map is ready
        // Set click listener for the back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate back to MapsActivity
            Intent intent = new Intent(gridfloor1.this, MapsActivity.class);
            startActivity(intent);
            finish(); // Optional, to close this activity after navigating back
        // Here you can proceed with setting up the GridView, populating data, etc.
        // For now, let's focus on the GridView setup only.
        });

    }}
