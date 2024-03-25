package com.example.srproject;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

public class floor1 extends AppCompatActivity {

    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floor1);

        // Initialize MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // Set callback for when the map is ready
        mapView.getMapAsync(this::onMapReady);
        // Set click listener for the back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate back to MapsActivity
            Intent intent = new Intent(floor1.this, MapsActivity.class);
            startActivity(intent);
            finish(); // Optional, to close this activity after navigating back
        });
    }

    private void setOverlayPosition(ImageView overlayImageView) {
        // Set the position of the overlay ImageView to align with the center of the parent

        // Get the dimensions of the overlay image
        int imageWidth = overlayImageView.getWidth(); // Width of the ImageView
        int imageHeight = overlayImageView.getHeight(); // Height of the ImageView

        // Get the dimensions of the parent container
        ViewGroup parentView = (ViewGroup) overlayImageView.getParent();
        int parentWidth = parentView.getWidth(); // Width of the parent container
        int parentHeight = parentView.getHeight(); // Height of the parent container

        // Calculate the new margins to center the image
        int leftMargin = (parentWidth - imageWidth) / 2;
        int topMargin = (parentHeight - imageHeight) / 2;

        // Set the new margins
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) overlayImageView.getLayoutParams();
        layoutParams.leftMargin = leftMargin;
        layoutParams.topMargin = topMargin;
        overlayImageView.setLayoutParams(layoutParams);
    }







    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void onMapReady(GoogleMap map) {
        googleMap = map;
        // Customize map settings here

        // Disable gesture controls
         googleMap.getUiSettings().setAllGesturesEnabled(false);

        // Get the LatLng object for Rhode Hall
        LatLng rhodeHallLatLng = new LatLng(27.526105, -97.882826);

        // Move the camera to the desired location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rhodeHallLatLng, 20));

        // Convert the LatLng coordinates to screen coordinates
        Point point = googleMap.getProjection().toScreenLocation(rhodeHallLatLng);
        ImageView overlayImageView = findViewById(R.id.overlayImageView);

        // Set the initial position of the overlay ImageView
        setOverlayPosition(overlayImageView);

        // Listen for camera movement events
        googleMap.setOnCameraMoveListener(() -> {
            // Get the new camera position
            LatLng newCameraPosition = googleMap.getCameraPosition().target;

            // Convert the new camera position to screen coordinates
            Point newPoint = googleMap.getProjection().toScreenLocation(newCameraPosition);

            // Update the position of the overlay ImageView
            setOverlayPosition(overlayImageView);
        });
    }
}
