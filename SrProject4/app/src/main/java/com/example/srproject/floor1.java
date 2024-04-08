package com.example.srproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

public class floor1 extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private final List<GroundOverlay> groundOverlays = new ArrayList<>(); // List to keep track of ground overlays

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    // Declare markerGrid
    private List<List<Marker>> markerGrid = new ArrayList<>();
    private String currentFloor = "rfloor1"; // Initialize to indicate no overlay displayed
    private String destinationFloor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floor1);

        // Initialize MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // Set callback for when the map is ready
        mapView.getMapAsync(this);

        // Initialize SearchView
        // SearchView declaration
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    int input = Integer.parseInt(query);

                    if (input >= 100 && input < 200) {
                        destinationFloor = "rfloor1";
                    } else if (input >= 200 && input < 300) {
                        destinationFloor = "rfloor2";
                    } else if (input >= 300 && input < 400) {
                        destinationFloor = "rfloor3";
                    } else {
                        Log.e("Search Error", "Invalid floor number entered: " + query);
                        return false; // Invalid input, do nothing
                    }
                    // Use destinationFloor as needed
                    Log.d("Destination Floor", "Destination Floor: " + destinationFloor);
                    //MapUtils.drawLineBetweenMarkers(googleMap, markerGrid);

                } catch (NumberFormatException e) {
                    // Handle the case where the input is not a valid integer
                    Log.e("Search Error", "Invalid floor number entered: " + query);
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // Implement the text change behavior here if needed
                return false;
            }        });

        // Set click listener for the back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate back to MapsActivity
            Intent intent = new Intent(floor1.this, MapsActivity.class);
            startActivity(intent);
            finish(); // Optional, to close this activity after navigating back
        });

        Button changeOverlay1Button = findViewById(R.id.changeOverlay1Button);
        changeOverlay1Button.setOnClickListener(v -> {
            if (!"rfloor1".equals(currentFloor)) {
                MapUtils.changeGroundOverlayImage(googleMap, groundOverlays, polyline, R.drawable.rhode1);
                // Replace the above line with appropriate method call
                currentFloor = "rfloor1";
                Log.d("Current Overlay", "Current Overlay Identifier: " + currentFloor);
                drawPolylineBasedOnFloors(destinationFloor); // Draw polyline after changing the overlay
            }
        });

        Button changeOverlay2Button = findViewById(R.id.changeOverlay2Button);
        changeOverlay2Button.setOnClickListener(v -> {
            if (!"rfloor2".equals(currentFloor)) {
                MapUtils.changeGroundOverlayImage(googleMap, groundOverlays, polyline, R.drawable.rhode2);
                // Replace the above line with appropriate method call
                currentFloor = "rfloor2";
                Log.d("Current Overlay", "Current Overlay Identifier: " + currentFloor);
                drawPolylineBasedOnFloors(destinationFloor); // Draw polyline after changing the overlay
                Log.d("Destination Floor", "Destination Floor: " + destinationFloor);
            }
        });

        Button changeOverlay3Button = findViewById(R.id.changeOverlay3Button);
        changeOverlay3Button.setOnClickListener(v -> {
            if (!"rfloor3".equals(currentFloor)) {
                MapUtils.changeGroundOverlayImage(googleMap, groundOverlays, polyline, R.drawable.rhode3);
                // Replace the above line with appropriate method call
                currentFloor = "rfloor3";
                Log.d("Current Overlay", "Current Overlay Identifier: " + currentFloor);
                drawPolylineBasedOnFloors(destinationFloor); // Draw polyline after changing the overlay
            }
        });
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

    // Method to handle button click events
    private void handleButtonClick(String destinationFloor) {
        drawPolylineBasedOnFloors(destinationFloor);
    }

    // Method to draw polyline based on destination floor and current floor (currentFloor)
    private void drawPolylineBasedOnFloors(String destinationFloor) {
        // Compare destinationFloor with currentFloor
        if (destinationFloor.equals(currentFloor)) {
            // They match
            Log.d("Floor Comparison", "Yes");
        } else {
            // They don't match
            Log.d("Floor Comparison", "No");
        }
    }

    private Polyline polyline; // Declare class-level variable

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.setBuildingsEnabled(false);

        // Enable the My Location layer if the permission has been granted.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true); // Change mMap to googleMap
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        LatLng bottomLeft = new LatLng(27.525847, -97.883013);
        LatLng topRight = new LatLng(27.526364, -97.882575);
        LatLngBounds bounds = new LatLngBounds(bottomLeft, topRight);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));

        // Call generateWaypoints from MapUtils to generate markers and capture the markerGrid
        markerGrid = MapUtils.generateWaypoints(this, googleMap, bounds, 24, 24);

        // Define the LatLngBounds for the area covered by the overlay
        LatLngBounds overlayBounds = new LatLngBounds(bottomLeft, topRight);

        // Add the ground overlay
        GroundOverlayOptions overlayOptions = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.rhode1))
                .positionFromBounds(overlayBounds)
                .transparency(0.0f);
        googleMap.addGroundOverlay(overlayOptions);

        // Print positions of markers for debugging
        for (List<Marker> rowMarkers : markerGrid) {
            for (Marker marker : rowMarkers) {
                System.out.println(marker.getPosition());
            }
        }
    }
}
