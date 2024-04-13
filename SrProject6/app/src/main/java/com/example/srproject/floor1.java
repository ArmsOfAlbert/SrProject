package com.example.srproject;

import static com.example.srproject.MapUtils.constructGraph;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SearchView;
import android.util.Pair;

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
import java.util.Map;

public class floor1 extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap googleMap;
    private final List<GroundOverlay> groundOverlays = new ArrayList<>(); // List to keep track of ground overlays

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    // Declare markerGrid
    private List<List<Marker>> markerGrid = new ArrayList<>();
// possibly change all these lists into listinto latlng list instead of markers
    private List<List<LatLng>> latlngGrid = new ArrayList<>();
    private List<Marker> Floor1Nodes = new ArrayList<>();
    private List<Marker> Floor2Nodes = new ArrayList<>();
    private List<Marker> Floor3Nodes = new ArrayList<>();
    private String currentFloor = "rfloor1"; // Initialize to indicate no overlay displayed
    private List<Marker> CurrentFloorNodes = Floor1Nodes;


    private String destinationFloor = null;
    private String startingFloor= null;
    private Marker desinationRoom ;
    //private Marker usingElevator = null;
    private LatLng usingElevator = null;

    private Marker startinglocationMarker;
    private int roomTofindFloor;
    private int FloorIndex;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floor1);

        // Initialize MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // Set callback for when the map is ready
        mapView.getMapAsync(this);
        MapUtils.Graph graph = constructGraph(markerGrid);

        // Initialize SearchView
        // SearchView declaration
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    // this needs to change .. destination room has to be a marker or lat lng
                     roomTofindFloor = Integer.parseInt(query);
                    FloorIndex = roomTofindFloor % 100;//last 2 digits of query;
                    startingFloor =currentFloor;
                    // Destination Room assignmentpractice
                    desinationRoom = markerGrid.get(10).get(23);
                    // starting location
                    startinglocationMarker= markerGrid.get(23).get(15); //gps curent location;
                    if (roomTofindFloor >= 100 && roomTofindFloor < 200) {
                        destinationFloor = "rfloor1";
                        //desinationRoom = Marker CurrentFloorNodes [FloorIndex];
                    } else if (roomTofindFloor >= 200 && roomTofindFloor < 300) {
                        destinationFloor = "rfloor2";
                    } else if (roomTofindFloor >= 300 && roomTofindFloor < 400) {
                        destinationFloor = "rfloor3";
                    } else {
                        Log.e("Search Error", "Invalid floor number entered: " + query);
                        return false; // Invalid input, do nothing
                    }
                    // Use destinationFloor as needed
                    Log.d("Destination Floor", "Destination Floor: " + destinationFloor);
                    drawPolylineBasedOnFloors(destinationFloor,desinationRoom.getPosition(), startinglocationMarker.getPosition(), usingElevator);
                    //drawPolylineBasedOnFloors((destinationFloor, desinationRoom));


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
                //currentFloorNodes = floor1nodes;
                Log.d("Current Overlay", "Current Overlay Identifier: " + currentFloor);
                if (destinationFloor != null) {
                    drawPolylineBasedOnFloors(destinationFloor,desinationRoom.getPosition(),startinglocationMarker.getPosition(), usingElevator );
                    Log.d("Destination Floor", "Destination Floor: " + destinationFloor);
                } else {
                    Log.e("Destination Floor", "Destination floor is null");
                    // Handle the case when destinationFloor is null
                }            }
        });

        Button changeOverlay2Button = findViewById(R.id.changeOverlay2Button);
        changeOverlay2Button.setOnClickListener(v -> {
            if (!"rfloor2".equals(currentFloor)) {
                MapUtils.changeGroundOverlayImage(googleMap, groundOverlays, polyline, R.drawable.rhode2);
                // Replace the above line with appropriate method call
                currentFloor = "rfloor2";
                //currentFloorNodes = floor2nodes;
                Log.d("Current Overlay", "Current Overlay Identifier: " + currentFloor);
                if (destinationFloor != null) {
                    drawPolylineBasedOnFloors(destinationFloor,desinationRoom.getPosition(),startinglocationMarker.getPosition(), usingElevator);
                    //drawPolylineBasedOnFloors(destinationFloor, desinationRoom);

                    Log.d("Destination Floor", "Destination Floor: " + destinationFloor);
                } else {
                    Log.e("Destination Floor", "Destination floor is null");
                    // Handle the case when destinationFloor is null
                }
            }
        });

        Button changeOverlay3Button = findViewById(R.id.changeOverlay3Button);
        changeOverlay3Button.setOnClickListener(v -> {
            if (!"rfloor3".equals(currentFloor)) {
                MapUtils.changeGroundOverlayImage(googleMap, groundOverlays, polyline, R.drawable.rhode3);
                // Replace the above line with appropriate method call
                currentFloor = "rfloor3";
                //currentFloorNodes = floor3nodes
                Log.d("Current Overlay", "Current Overlay Identifier: " + currentFloor);
                if (destinationFloor != null) {
                    drawPolylineBasedOnFloors(destinationFloor,desinationRoom.getPosition(),startinglocationMarker.getPosition(), usingElevator);
                    //drawPolylineBasedOnFloors(destinationFloor, desinationRoom);
                    Log.d("Destination Floor", "Destination Floor: " + destinationFloor);
                } else {
                    Log.e("Destination Floor", "Destination floor is null");
                    // Handle the case when destinationFloor is null
                }            }
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
        drawPolylineBasedOnFloors(destinationFloor,desinationRoom.getPosition(),startinglocationMarker.getPosition(), usingElevator);
    }
    private void  drawPolylineBasedOnFloors(String destinationFloor, LatLng destinationRoom, LatLng currentLocation, LatLng usingElevator) {
        // Check if usingElevator is not null
        if (usingElevator != null) {
            // Check if destination floor matches the current floor

            if (destinationFloor.equals(currentFloor)) {
                // They match, calculate shortest path from current location to destination room
                List<LatLng> shortestPath = MapUtils.findShortestPath(markerGrid, usingElevator, destinationRoom);
                usingElevator = null;
                Polyline shortestPathPolyline = MapUtils.drawLineBetweenLatLngs(googleMap, shortestPath);
                Log.d("Floor Comparison", "Yes");
            }
            if (startingFloor.equals(currentFloor)) {
                //usingElevator = findClosestElevator(currentLocation);
                List<LatLng> shortestPath = MapUtils.findShortestPath(markerGrid, currentLocation, usingElevator);
                Polyline shortestPathPolyline = MapUtils.drawLineBetweenLatLngs(googleMap, shortestPath);

            } else {
                // They don't match, do nothing for now
                Log.d("Floor Comparison", "No");
            }
        } else {
            if (destinationFloor.equals(currentFloor)) {
                // They match, calculate shortest path from current location to destination room
                List<LatLng> shortestPath = MapUtils.findShortestPath(markerGrid, currentLocation, destinationRoom);
                Polyline shortestPathPolyline = MapUtils.drawLineBetweenLatLngs(googleMap, shortestPath);
                Log.d("Floor Comparison", "Yes");
            }
            if (startingFloor.equals(currentFloor)) {
                usingElevator = findClosestElevator(currentLocation);
                List<LatLng> shortestPath = MapUtils.findShortestPath(markerGrid, currentLocation, usingElevator);
                Polyline shortestPathPolyline = MapUtils.drawLineBetweenLatLngs(googleMap, shortestPath);

            }else {
                // They don't match, do nothing for now
                Log.d("Floor Comparison", "No");
            }
        }
    }
    // Method to find the closest elevator to the starting location
    private LatLng findClosestElevator(LatLng startingLocation) {

        //enter elevatore/stairway list here
       //LatLng cheese = markerGrid.get(2).get(3);
        //List<LatLng> ElevatorsList = new ArrayList<>();
        //ElevatorsList.add(latlngGrid.get(2).get(3));
        //ElevatorsList.add(latlngGrid.get(4).get(3));
        // Create a list to hold the elevators or stairways
        List<Marker> elevatorsList = new ArrayList<>();
        // Add elevators or stairways to the list
        elevatorsList.add(markerGrid.get(10).get(3));
        elevatorsList.add(markerGrid.get(12).get(15));


        LatLng closestElevator = null;
        double shortestDistance = Double.MAX_VALUE;

        // Iterate through the list of elevators to find the closest one
        for (Marker elevatorMarker : elevatorsList) {
            // Calculate the shortest path to the elevator
            LatLng elevator = elevatorMarker.getPosition();
            List<LatLng> shortestPathToElevator = MapUtils.findShortestPath(markerGrid, startinglocationMarker.getPosition(), elevator);

            // Calculate the length of the shortest path
            double pathLength = MapUtils.calculatePathLength(shortestPathToElevator);

            // Update closest elevator if the current one is closer
            if (pathLength < shortestDistance) {
                closestElevator = elevator;
                shortestDistance = pathLength;
            }
        }

            usingElevator = closestElevator;
        return usingElevator;
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
        Pair<List<List<Marker>>, List<List<LatLng>>> grids = MapUtils.generateWaypoints(this, googleMap, bounds, 24, 24);
        // Extract marker grid
        this.markerGrid = grids.first;

// Extract LatLng grid
        this.latlngGrid = grids.second;

        // Define the LatLngBounds for the area covered by the overlay
        LatLngBounds overlayBounds = new LatLngBounds(bottomLeft, topRight);

        // Add the ground overlay
        GroundOverlayOptions overlayOptions = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.rhode1))
                .positionFromBounds(overlayBounds)
                .transparency(0.0f);
        googleMap.addGroundOverlay(overlayOptions);

        // Print positions of markers for debugging
       // for (List<Marker> rowMarkers : markerGrid) {
         //   for (Marker marker : rowMarkers) {
           //     System.out.println(marker.getPosition());
            }
        }
  // }
//}
