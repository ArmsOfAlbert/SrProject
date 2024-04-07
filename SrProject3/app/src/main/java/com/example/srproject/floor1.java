package com.example.srproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
                if (Integer.parseInt(query) > 100) {
                    // Draw polyline between markers
                    //drawPolylineBetweenMarkers(0, 0, 10, 23);
                    // Call this method where you want to draw a line between two markers
                    // Call the drawPolylineBetweenMarkers method from MapUtils

                    // Retrieve the markers at specific positions from the markerGrid list
                    List<Marker> markerList = new ArrayList<>();
                    markerList.add(markerGrid.get(0).get(0)); // Change the indices according to your requirements
                    markerList.add(markerGrid.get(10).get(23)); // Change the indices according to your requirements

                    // Call the drawLineBetweenMarkers method from MapUtils to draw a polyline
                    Polyline polyline = MapUtils.drawLineBetweenMarkers(googleMap, markerList);

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text changes in search query
                // Update search results dynamically here
                return false;
            }
        });

        // Set click listener for the back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate back to MapsActivity
            Intent intent = new Intent(floor1.this, MapsActivity.class);
            startActivity(intent);
            finish(); // Optional, to close this activity after navigating back
        });

        // Set click listener for changing ground overlay image buttons
        Button changeOverlay1Button = findViewById(R.id.changeOverlay1Button);
        changeOverlay1Button.setOnClickListener(v -> changeGroundOverlayImage(R.drawable.rhode1));

        Button changeOverlay2Button = findViewById(R.id.changeOverlay2Button);
        changeOverlay2Button.setOnClickListener(v -> changeGroundOverlayImage(R.drawable.rhode2));

        Button changeOverlay3Button = findViewById(R.id.changeOverlay3Button);
        changeOverlay3Button.setOnClickListener(v -> changeGroundOverlayImage(R.drawable.rhode3));
    }

    // Method to change ground overlay image
    private void changeGroundOverlayImage(int resourceId) {
        if (googleMap != null) {
            // Default overlay image

            LatLng bottomLeft = new LatLng(27.525847, -97.883013);
            LatLng topRight = new LatLng(27.526364, -97.882575);
            LatLngBounds overlayBounds = new LatLngBounds(bottomLeft, topRight);

            // Remove existing overlays
            for (GroundOverlay overlay : groundOverlays) {
                overlay.remove();
            }
            groundOverlays.clear();

            // Add the new ground overlay
            GroundOverlayOptions overlayOptions = new GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(resourceId))
                    .positionFromBounds(overlayBounds)
                    .transparency(0.0f);
            GroundOverlay overlay = googleMap.addGroundOverlay(overlayOptions);
            groundOverlays.add(overlay);
        }
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
