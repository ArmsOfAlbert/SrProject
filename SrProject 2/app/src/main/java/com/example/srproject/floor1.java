package com.example.srproject;



import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.Marker;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.maps.model.PolylineOptions;
import android.graphics.Color;





public class floor1 extends AppCompatActivity implements OnMapReadyCallback {

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
        mapView.getMapAsync(this);

        // Set click listener for the back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate back to MapsActivity
            Intent intent = new Intent(floor1.this, MapsActivity.class);
            startActivity(intent);
            finish(); // Optional, to close this activity after navigating back
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


    private List<List<Marker>> markerGrid = new ArrayList<>();


    @SuppressWarnings("MissingNonNull")
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setBuildingsEnabled(false);

        LatLng bottomLeft = new LatLng(27.525847, -97.883013);
        LatLng topRight = new LatLng(27.526364, -97.882575);
        LatLngBounds bounds = new LatLngBounds(bottomLeft, topRight);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        generateWaypoints(bounds, 25, 25);
        // Define the LatLngBounds for the area covered by the overlay
        drawPolylineBetweenMarkers(0, 0, 10, 23);
        drawPolylineBetweenMarkers(10, 23, 3, 8);
        LatLngBounds overlayBounds = new LatLngBounds(bottomLeft, topRight);

        // Add the ground overlay
        GroundOverlayOptions overlayOptions = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.rhode1))  // Replace 'R.drawable.overlay_image' with the ID of your image
                .positionFromBounds(overlayBounds)
                .transparency(0.0f);  // Set the transparency as needed
                //.zIndex(1000);
        googleMap.addGroundOverlay(overlayOptions);

        for (List<Marker> rowMarkers : markerGrid) {
            for (Marker marker : rowMarkers) {
                // Access each marker in the row
                System.out.println(marker.getPosition().toString());
            }
        }

        }




    private void generateWaypoints(LatLngBounds bounds, int numRows, int numCols) {
        double latRange = bounds.northeast.latitude - bounds.southwest.latitude;
        double lngRange = bounds.northeast.longitude - bounds.southwest.longitude;
        double latIncrement = latRange / numRows;
        double lngIncrement = lngRange / numCols;
        // Define marker size (you can adjust these values as needed)
        int markerWidth = 10;
        int markerHeight = 10;


        for (int i = 0; i < numRows; i++) {
            List<Marker> rowMarkers = new ArrayList<>();
            for (int j = 0; j < numCols; j++) {
                double lat = bounds.southwest.latitude + i * latIncrement;
                double lng = bounds.southwest.longitude + j * lngIncrement;
                // Create a smaller marker icon
                Bitmap smallMarker = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.blackmarker), markerWidth, markerHeight, false);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                Marker marker = googleMap.addMarker(markerOptions);
                rowMarkers.add(marker);
            }
            markerGrid.add(rowMarkers);
        }
    }


    private void drawPolylineBetweenMarkers(int startRow, int startCol, int endRow, int endCol) {
        if (startRow < 0 || startRow >= markerGrid.size() ||
                endRow < 0 || endRow >= markerGrid.size() ||
                startCol < 0 || startCol >= markerGrid.get(0).size() ||
                endCol < 0 || endCol >= markerGrid.get(0).size()) {
            // Invalid row or column indices
            return;
        }

        Marker startMarker = markerGrid.get(startRow).get(startCol);
        Marker endMarker = markerGrid.get(endRow).get(endCol);

        PolylineOptions polylineOptions = new PolylineOptions()
                .add(startMarker.getPosition(), endMarker.getPosition())
                .color(Color.RED) // Change color as needed
                .width(5) // Change width as needed
                .zIndex(25);
        googleMap.addPolyline(polylineOptions);
    }
}

