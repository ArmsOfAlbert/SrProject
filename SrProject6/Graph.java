private static class Graph {
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