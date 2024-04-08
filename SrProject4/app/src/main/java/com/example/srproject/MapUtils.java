package com.example.srproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;



public class MapUtils {

    private static List<List<Marker>> markerGrid; // Class-level variable to hold the markerGrid

    public static List<List<Marker>> generateWaypoints(Context context, GoogleMap googleMap, LatLngBounds bounds, int numRows, int numCols) {
        if (googleMap == null || bounds == null || numRows <= 0 || numCols <= 0) {
            return null;
        }

        double latRange = bounds.northeast.latitude - bounds.southwest.latitude;
        double lngRange = bounds.northeast.longitude - bounds.southwest.longitude;
        double latIncrement = latRange / numRows;
        double lngIncrement = lngRange / numCols;
        // Define marker size (you can adjust these values as needed)
        int markerWidth = 22;
        int markerHeight = 22;

        markerGrid = new ArrayList<>(); // Initialize markerGrid

        for (int i = 0; i < numRows; i++) {
            List<Marker> rowMarkers = new ArrayList<>();
            for (int j = 0; j < numCols; j++) {
                double lat = bounds.southwest.latitude + i * latIncrement;
                double lng = bounds.southwest.longitude + j * lngIncrement;
                // Create a smaller marker icon
                Bitmap smallMarker = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.blackmarker), markerWidth, markerHeight, false);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                        .zIndex(10000);
                Marker marker = googleMap.addMarker(markerOptions);
                rowMarkers.add(marker);
            }
            markerGrid.add(rowMarkers);
        }
        return markerGrid;
    }

    // Method to get the markerGrid
    public static List<List<Marker>> getMarkerGrid() {
        return markerGrid;
    }


    public static Polyline drawLineBetweenMarkers(GoogleMap googleMap, List<Marker> markers) {
        if (googleMap == null || markers == null || markers.size() < 2) {
            return null;
        }

        PolylineOptions polylineOptions = new PolylineOptions();
        for (Marker marker : markers) {
            polylineOptions.add(marker.getPosition());
        }

        return googleMap.addPolyline(polylineOptions);
    }
    // Method to change ground overlay image
    public static void changeGroundOverlayImage(GoogleMap googleMap, List<GroundOverlay> groundOverlays, Polyline polyline, int resourceId) {
        if (googleMap != null) {
            // Remove existing polyline
            if (polyline != null) {
                polyline.remove();
            }

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



    }







