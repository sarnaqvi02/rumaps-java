package rumaps;

import java.util.*;

/**
 * This class represents the information that can be attained from the Rutgers University Map.
 * 
 * The RUMaps class is responsible for initializing the network, streets, blocks, and intersections in the map.
 * 
 * You will complete methods to initialize blocks and intersections, calculate block lengths, find reachable intersections,
 * minimize intersections between two points, find the fastest path between two points, and calculate a path's information.
 * 
 * Provided is a Network object that contains all the streets and intersections in the map
 * 
 * @author Vian Miranda
 * @author Anna Lu
 */
public class RUMaps {
    
    private Network rutgers;

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Constructor for the RUMaps class. Initializes the streets and intersections in the map.
     * For each block in every street, sets the block's length, traffic factor, and traffic value.
     * 
     * @param mapPanel The map panel to display the map
     * @param filename The name of the file containing the street information
     */
    public RUMaps(MapPanel mapPanel, String filename) {
        StdIn.setFile(filename);
        int numIntersections = StdIn.readInt();
        int numStreets = StdIn.readInt();
        StdIn.readLine();
        rutgers = new Network(numIntersections, mapPanel);
        ArrayList<Block> blocks = initializeBlocks(numStreets);
        initializeIntersections(blocks);

        for (Block block: rutgers.getAdjacencyList()) {
            Block ptr = block;
            while (ptr != null) {
                ptr.setLength(blockLength(ptr));
                ptr.setTrafficFactor(blockTrafficFactor(ptr));
                ptr.setTraffic(blockTraffic(ptr));
                ptr = ptr.getNext();
            }
        }
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Overloaded constructor for testing.
     * 
     * @param filename The name of the file containing the street information
     */
    public RUMaps(String filename) {
        this(null, filename);
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Overloaded constructor for testing.
     */
    public RUMaps() { 
        
    }

    /**
     * Initializes all blocks, given a number of streets.
     * the file was opened by the constructor - use StdIn to continue reading the file
     * @param numStreets the number of streets
     * @return an ArrayList of blocks
     */
    public ArrayList<Block> initializeBlocks(int numStreets) {
        ArrayList<Block> b = new ArrayList<>();

        for (int i = 0; i < numStreets; i++){
            String strt = StdIn.readLine();
            int numBlocks = StdIn.readInt();
            StdIn.readLine();

            for (int j = 0; j < numBlocks; j++){
                int blockNum = StdIn.readInt();
                int numPoints = StdIn.readInt();
                double rSize = StdIn.readDouble();
                StdIn.readLine();
                Block block = new Block(rSize, strt, blockNum);

                for (int k = 0; k < numPoints; k++){
                    int x = StdIn.readInt();
                    int y = StdIn.readInt();
                    Coordinate c = new Coordinate(x, y);

                    if (k == 0){
                        block.startPoint(c);
                    } else {
                        block.nextPoint(c);
                    }
                }

                ArrayList<Coordinate> coords = block.getCoordinatePoints();
                Intersection first = new Intersection(coords.get(0));
                Intersection last = new Intersection(coords.get(coords.size() - 1));
                if (coords.size() == 0) continue;

                block.setFirstEndpoint(first);
                block.setLastEndpoint(last);
                
                StdIn.readLine();
                b.add(block);
            }
        }
        return b;
    }

    /**
     * This method traverses through each block and finds
     * the block's start and end points to create intersections. 
     * 
     * It then adds intersections as vertices to the "rutgers" graph if
     * they are not already present, and adds UNDIRECTED edges to the adjacency
     * list.
     * 
     * Note that .addEdge(__) ONLY adds edges in one direction (a -> b). 
     */
    public void initializeIntersections(ArrayList<Block> blocks) {
        for (Block block : blocks){
            Coordinate startCoord = block.getFirstEndpoint().getCoordinate();
            Coordinate endCoord = block.getLastEndpoint().getCoordinate();

            Intersection start = new Intersection(startCoord);
            Intersection end = new Intersection(endCoord);

            int startIndex = rutgers.findIntersection(startCoord.getX(), startCoord.getY());

            if (startIndex == -1){
                rutgers.addIntersection(start);
                startIndex = rutgers.findIntersection(startCoord.getX(), startCoord.getY());
            }

            int endIndex = rutgers.findIntersection(endCoord.getX(), endCoord.getY());

            if (endIndex == -1){
                rutgers.addIntersection(end);
                endIndex = rutgers.findIntersection(endCoord.getX(), endCoord.getY());
            }

            Block blockA = block.copy();

            blockA.setFirstEndpoint(rutgers.getIntersections()[startIndex]);
            blockA.setLastEndpoint(rutgers.getIntersections()[endIndex]);
            rutgers.addEdge(startIndex, blockA);

            Block blockB = block.copy();

            blockB.setFirstEndpoint(rutgers.getIntersections()[endIndex]);
            blockB.setLastEndpoint(rutgers.getIntersections()[startIndex]);

            rutgers.addEdge(endIndex, blockB);
        }
     }

    /**
     * Calculates the length of a block by summing the distances between consecutive points for all points in the block.
     * 
     * @param block The block whose length is being calculated
     * @return The total length of the block
     */
    public double blockLength(Block block) {
        ArrayList<Coordinate> c = block.getCoordinatePoints();

        double length = 0.0;

        for (int i = 0; i < c.size() - 1; i++){
            length += coordinateDistance(c.get(i), c.get(i + 1));
        }
        return length;
    }

    /**
     * Use a DFS to traverse through blocks, and find the order of intersections
     * traversed starting from a given intersection (as source).
     * 
     * Implement this method recursively, using a helper method.
     */
    public ArrayList<Intersection> reachableIntersections(Intersection source) {
        ArrayList<Intersection> vis = new ArrayList<>();
        boolean[] mark = new boolean[rutgers.getIntersections().length];
        int index = rutgers.findIntersection(source.getCoordinate().getX(), source.getCoordinate().getY());
        if (index < 0 || index >= rutgers.getIntersections().length) return vis;
        dfs(index, mark, vis);


        return vis;
    }

    private void dfs(int index, boolean[] mark, ArrayList<Intersection> vis){
        if (index < 0 || index >= rutgers.getIntersections().length){
            return;
        }        
        
        if (mark[index]){
            return;
        }

        mark[index] = true;

        Intersection curr = rutgers.getIntersections()[index];
        vis.add(curr);

        if (index < 0 || index >= rutgers.getIntersections().length) return;
        Block ptr = rutgers.adj(index);

        while (ptr != null){
            Intersection next = ptr.other(curr);
            int nextIndex = rutgers.findIntersection(next.getCoordinate().getX(), next.getCoordinate().getY());

            if (nextIndex >= 0 && nextIndex < rutgers.getIntersections().length) {
                dfs(nextIndex, mark, vis);
            }

            ptr = ptr.getNext();
        }
    }
     

    /**
     * Finds and returns the path with the least number of intersections (nodes) from the start to the end intersection.
     * 
     * - If no path exists, return an empty ArrayList.
     * - This graph is large. Find a way to eliminate searching through intersections that have already been visited.
     * 
     * @param start The starting intersection
     * @param end The destination intersection
     * @return The path with the least number of turns, or an empty ArrayList if no path exists
     */
    public ArrayList<Intersection> minimizeIntersections(Intersection start, Intersection end) {
        HashMap<Intersection, Intersection> edgeTo = new HashMap<>();
        HashSet<Intersection> vis = new HashSet<>();
        Queue<Intersection> queue = new Queue<>();
        queue.enqueue(start);
        vis.add(start);

        while (!queue.isEmpty()){
            Intersection curr = queue.dequeue();
            if (curr.equals(end)){
                break;
            }

            int index = rutgers.findIntersection(curr.getCoordinate().getX(), curr.getCoordinate().getY());

            if (index < 0 || index >= rutgers.getIntersections().length) continue;
            Block ptr = rutgers.adj(index);

            while (ptr != null){
                Intersection neighbor = ptr.other(curr);
                if (!vis.contains(neighbor)){
                    edgeTo.put(neighbor, curr);
                    vis.add(neighbor);
                    queue.enqueue(neighbor);
                }
                ptr = ptr.getNext();
            }
        }
        ArrayList<Intersection> p = new ArrayList<>();

        if (!edgeTo.containsKey(end)){
            return p;
        }

        for (Intersection at = end; at != null; at = edgeTo.get(at)){
            p.add(at);
        }
        Collections.reverse(p);
        return p;
    }

    /**
     * Finds the path with the least traffic from the start to the end intersection using a variant of Dijkstra's algorithm.
     * The traffic is calculated as the sum of traffic of the blocks along the path.
     * 
     * What is this variant of Dijkstra?
     * - We are using traffic as a cost - we extract the lowest cost intersection from the fringe.
     * - Once we add the target to the done set, we're done. 
     * 
     * @param start The starting intersection
     * @param end The destination intersection
     * @return The path with the least traffic, or an empty ArrayList if no path exists
     */
    public ArrayList<Intersection> fastestPath(Intersection start, Intersection end) {
        HashMap<Intersection, Double> d = new HashMap<>();
        HashMap<Intersection, Intersection> pred = new HashMap<>();
        HashSet<Intersection> done = new HashSet<>();
        ArrayList<Intersection> fringe = new ArrayList<>();
        
        for (Intersection i : rutgers.getIntersections()){
            if (i != null){
                d.put(i, Double.POSITIVE_INFINITY);
            }
        }

        d.put(start, 0.0);
        fringe.add(start);

        while (!fringe.isEmpty()){
            Intersection min = fringe.get(0);
            
            for (Intersection i : fringe){
                if (d.get(i) < d.get(min)){
                    min = i;
                }
            }
            fringe.remove(min);
            if (min.equals(end)){
                break;
            }
            done.add(min);

            int index = rutgers.findIntersection(min.getCoordinate().getX(), min.getCoordinate().getY());
            
            if (index < 0 || index >= rutgers.getIntersections().length) continue;
            Block ptr = rutgers.adj(index);

            while (ptr != null){
                Intersection neighbor = ptr.other(min);
                if (done.contains(neighbor)){
                    ptr = ptr.getNext();
                    continue;
                }

                double alt = d.get(min) + ptr.getTraffic();

                if (alt < d.get(neighbor)){
                    d.put(neighbor, alt);
                    pred.put(neighbor, min);

                    if (!fringe.contains(neighbor)){
                        fringe.add(neighbor);
                    }
                }
                ptr = ptr.getNext();
            }
        }
        ArrayList<Intersection> p = new ArrayList<>();

        if (!pred.containsKey(end)){
            return p;
        }

        for (Intersection at = end; at != null; at = pred.get(at)){
            p.add(at);
        }

        Collections.reverse(p);
        return p;
    }

    /**
     * Calculates the total length, average experienced traffic factor, and total traffic for a given path of blocks.
     * 
     * You're given a list of intersections (vertices); you'll need to find the edge in between each pair.
     * 
     * Compute the average experienced traffic factor by dividing total traffic by total length.
     *  
     * @param path The list of intersections representing the path
     * @return A double array containing the total length, average experienced traffic factor, and total traffic of the path (in that order)
     */
    public double[] pathInformation(ArrayList<Intersection> path) {
        double totLen = 0;
        double totTraf = 0;
        
        for (int i = 0; i < path.size() - 1; i++){
            Intersection f = path.get(i);
            Intersection t = path.get(i + 1);
            int index = rutgers.findIntersection(f.getCoordinate().getX(), f.getCoordinate().getY());

            if (index < 0 || index >= rutgers.getIntersections().length) continue;
            Block ptr = rutgers.adj(index);

            while (ptr != null){
                if (ptr.getLastEndpoint().equals(t)){
                    totLen += ptr.getLength();
                    totTraf += ptr.getTraffic();
                    break;
                }
                ptr = ptr.getNext();
            }
        }

        double avgTrafficFactor = (totLen == 0) ? 0 : totTraf / totLen;

        return new double[] {totLen, avgTrafficFactor, totTraf};
    }

    /**
     * Calculates the Euclidean distance between two coordinates.
     * PROVIDED - do not modify
     * 
     * @param a The first coordinate
     * @param b The second coordinate
     * @return The Euclidean distance between the two coordinates
     */
    private double coordinateDistance(Coordinate a, Coordinate b) {
        // PROVIDED METHOD

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Calculates and returns a randomized traffic factor for the block based on a Gaussian distribution.
     * 
     * This method generates a random traffic factor to simulate varying traffic conditions for each block:
     * - < 1 for good (faster) conditions
     * - = 1 for normal conditions
     * - > 1 for bad (slower) conditions
     * 
     * The traffic factor is generated with a Gaussian distribution centered at 1, with a standard deviation of 0.2.
     * 
     * Constraints:
     * - The traffic factor is capped between a minimum of 0.5 and a maximum of 1.5 to avoid extreme values.
     * 
     * @param block The block for which the traffic factor is calculated
     * @return A randomized traffic factor for the block
     */
    public double blockTrafficFactor(Block block) {
        double rand = StdRandom.gaussian(1, 0.2);
        rand = Math.max(rand, 0.5);
        rand = Math.min(rand, 1.5);
        return rand;
    }

    /**
     * Calculates the traffic on a block by the product of its length and its traffic factor.
     * 
     * @param block The block for which traffic is being calculated
     * @return The calculated traffic value on the block
     */
    public double blockTraffic(Block block) {
        // PROVIDED METHOD
        
        return block.getTrafficFactor() * block.getLength();
    }

    public Network getRutgers() {
        return rutgers;
    }




    
    








}
