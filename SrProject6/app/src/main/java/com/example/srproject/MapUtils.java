package com.example.srproject;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.android.gms.maps.model.LatLng;
import android.util.Pair;

import java.util.HashMap;
import java.util.Map;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class MapUtils {

    private static List<List<Marker>> markerGrid;

    public static Pair<List<List<Marker>>, List<List<LatLng>>> generateWaypoints(Context context, GoogleMap googleMap, LatLngBounds bounds, int numRows, int numCols) {
        if (googleMap == null || bounds == null || numRows <= 0 || numCols <= 0) {
            return null;
        }

        double latRange = bounds.northeast.latitude - bounds.southwest.latitude;
        double lngRange = bounds.northeast.longitude - bounds.southwest.longitude;
        double latIncrement = latRange / numRows;
        double lngIncrement = lngRange / numCols;

        int markerWidth = 22;
        int markerHeight = 22;

        List<List<Marker>> markerGrid = new ArrayList<>();
        List<List<LatLng>> latLngGrid = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            List<Marker> rowMarkers = new ArrayList<>();
            List<LatLng> rowLatLngs = new ArrayList<>();
            for (int j = 0; j < numCols; j++) {
                double lat = bounds.southwest.latitude + i * latIncrement;
                double lng = bounds.southwest.longitude + j * lngIncrement;
                LatLng latLng = new LatLng(lat, lng);
                Bitmap smallMarker = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.blackmarker), markerWidth, markerHeight, false);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                        .zIndex(10000);
                Marker marker = googleMap.addMarker(markerOptions);
                rowMarkers.add(marker);
                rowLatLngs.add(latLng);
            }
            markerGrid.add(rowMarkers);
            latLngGrid.add(rowLatLngs);
        }
        return new Pair<>(markerGrid, latLngGrid);
    }

    public static List<List<Marker>> getMarkerGrid() {
        return markerGrid;
    }

    public static Polyline drawLineBetweenLatLngs(GoogleMap googleMap, List<LatLng> latLngs) {
        if (googleMap == null || latLngs == null || latLngs.size() < 2) {
            return null;
        }

        PolylineOptions polylineOptions = new PolylineOptions();
        for (LatLng latLng : latLngs) {
            polylineOptions.add(latLng);
        }
        // Add markers on the first and last locations
        if (!latLngs.isEmpty()) {
            // First location
            LatLng firstLocation = latLngs.get(0);
            googleMap.addMarker(new MarkerOptions().position(firstLocation).title("Start"));

            // Last location
            LatLng lastLocation = latLngs.get(latLngs.size() - 1);
            googleMap.addMarker(new MarkerOptions().position(lastLocation).title("End"));
        }
        return googleMap.addPolyline(polylineOptions);
    }

    public static void changeGroundOverlayImage(GoogleMap googleMap, List<GroundOverlay> groundOverlays, Polyline polyline, int resourceId) {
        if (googleMap != null) {
            googleMap.clear();
            if (polyline != null) {
                polyline.remove();
            }

            LatLng bottomLeft = new LatLng(27.525847, -97.883013);
            LatLng topRight = new LatLng(27.526364, -97.882575);
            LatLngBounds overlayBounds = new LatLngBounds(bottomLeft, topRight);

            for (GroundOverlay overlay : groundOverlays) {
                overlay.remove();
            }
            groundOverlays.clear();

            GroundOverlayOptions overlayOptions = new GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(resourceId))
                    .positionFromBounds(overlayBounds)
                    .transparency(0.0f);
            GroundOverlay overlay = googleMap.addGroundOverlay(overlayOptions);
            groundOverlays.add(overlay);
        }
    }

    public static List<LatLng> findShortestPath(List<List<LatLng>> latlngGrid, LatLng start, LatLng end) {
        Graph graph = constructGraph(latlngGrid);
        return dijkstra(graph, start, end);
    }

    public static Graph constructGraph(List<List<LatLng>> latlngGrid) {
        Graph graph = new Graph();

        for (int i = 0; i < latlngGrid.size(); i++) {
            for (int j = 0; j < latlngGrid.get(i).size(); j++) {
                LatLng currentMarker = latlngGrid.get(i).get(j);
                if (j < latlngGrid.get(i).size() - 1) {
                    LatLng rightNeighbor = latlngGrid.get(i).get(j + 1);
                    graph.addEdge(currentMarker, rightNeighbor, calculateDistance(currentMarker, rightNeighbor));
                }
                if (i < latlngGrid.size() - 1) {
                    LatLng bottomNeighbor = latlngGrid.get(i + 1).get(j);
                    graph.addEdge(currentMarker, bottomNeighbor, calculateDistance(currentMarker, bottomNeighbor));
                }
            }
        }
        return graph;
    }

    private static double calculateDistance(LatLng point1, LatLng point2) {
        double latDiff = point1.latitude - point2.latitude;
        double lngDiff = point1.longitude - point2.longitude;
        return Math.sqrt(latDiff * latDiff + lngDiff * lngDiff);
    }

    private static List<LatLng> dijkstra(Graph graph, LatLng start, LatLng end) {
        PriorityQueue<Vertex> priorityQueue = new PriorityQueue<>();
        Map<LatLng, Double> shortestDistances = new HashMap<>();
        Map<LatLng, LatLng> previousVertices = new HashMap<>();

        for (LatLng vertex : graph.getVertices()) {
            if (vertex.equals(start)) {
                shortestDistances.put(vertex, 0.0);
                priorityQueue.offer(new Vertex(vertex, 0.0));
            } else {
                shortestDistances.put(vertex, Double.MAX_VALUE);
                priorityQueue.offer(new Vertex(vertex, Double.MAX_VALUE));
            }
            previousVertices.put(vertex, null);
        }

        while (!priorityQueue.isEmpty()) {
            Vertex currentVertex = priorityQueue.poll();
            LatLng currentLocation = currentVertex.getLocation();
            double currentDistance = currentVertex.getDistance();

            if (currentDistance > shortestDistances.get(currentLocation)) {
                continue;
            }

            for (Map.Entry<LatLng, Double> neighbor : graph.getNeighbors(currentLocation).entrySet()) {
                LatLng neighborLocation = neighbor.getKey();
                double edgeWeight = neighbor.getValue();
                double newDistance = currentDistance + edgeWeight;

                if (newDistance < shortestDistances.get(neighborLocation)) {
                    shortestDistances.put(neighborLocation, newDistance);
                    priorityQueue.offer(new Vertex(neighborLocation, newDistance));
                    previousVertices.put(neighborLocation, currentLocation);
                }
            }
        }

        // Reconstruct the shortest path as a list of LatLng points representing markers
        List<LatLng> shortestPathMarkers = new ArrayList<>();
        LatLng currentLocation = end;
        while (currentLocation != null) {
            shortestPathMarkers.add(currentLocation);
            currentLocation = previousVertices.get(currentLocation);
        }
        Collections.reverse(shortestPathMarkers);

        return shortestPathMarkers;
    }

    // Method to calculate the length of a path given a list of LatLng coordinates
    public static double calculatePathLength(List<LatLng> path) {
        double length = 0.0;

        // Iterate through the list of LatLng coordinates
        for (int i = 0; i < path.size() - 1; i++) {
            LatLng currentPoint = path.get(i);
            LatLng nextPoint = path.get(i + 1);

            // Calculate the distance between the current point and the next point
            double distance = calculateDistance(currentPoint, nextPoint);

            // Add the distance to the total length
            length += distance;
        }

        return length;
    }


    public static class Graph {
        private final Map<LatLng, Map<LatLng, Double>> adjacencyList;

        public Graph() {
            adjacencyList = new HashMap<>();
        }

        public void addEdge(LatLng source, LatLng destination, double weight) {
            // Add edge from source to destination
            Map<LatLng, Double> sourceNeighbors = adjacencyList.containsKey(source) ? adjacencyList.get(source) : new HashMap<>();
            sourceNeighbors.put(destination, weight);
            adjacencyList.put(source, sourceNeighbors);

            // Add edge from destination to source (assuming undirected graph)
            Map<LatLng, Double> destNeighbors = adjacencyList.containsKey(destination) ? adjacencyList.get(destination) : new HashMap<>();
            destNeighbors.put(source, weight);
            adjacencyList.put(destination, destNeighbors);
        }

        public Map<LatLng, Double> getNeighbors(LatLng vertex) {
            return adjacencyList.containsKey(vertex) ? adjacencyList.get(vertex) : new HashMap<>();
        }

        public Iterable<LatLng> getVertices() {
            return adjacencyList.keySet();
        }
    }

    private static class Vertex implements Comparable<Vertex> {
        private final LatLng location;
        private final double distance;

        public Vertex(LatLng location, double distance) {
            this.location = location;
            this.distance = distance;
        }

        public LatLng getLocation() {
            return location;
        }

        public double getDistance() {
            return distance;
        }

        @Override
        public int compareTo(Vertex other) {
            return Double.compare(this.distance, other.distance);
        }
    }
}
