import java.io.*;
import java.util.*;

/**
 * This class represents an Undirected (positive) Weighted Graph Theory algorithms implements weighted_graph_algorithms
 * including:
 * 0. clone(); (copy)
 * 1. init(graph);
 * 2. isConnected();
 * 3. double shortestPathDist(int src, int dest);
 * 4. List<node_data> shortestPath(int src, int dest);
 * 5. Save(file);
 * 6. Load(file);
 */
public class WGraph_Algo implements weighted_graph_algorithms {

    private weighted_graph _current_graph;


    /**
     * Constructor.
     * init a weighted_graph to this set of algorithms.
     *
     * @param g weighted_graph
     */
    public WGraph_Algo(weighted_graph g) {
        init(g);
    }

    /**
     * Empty constructor.
     */
    public WGraph_Algo() {
    }

    /**
     * Init the graph on which this set of algorithms operates on.
     * init _current_graph to point on g
     *
     * @param g weighted_graph
     */
    @Override
    public void init(weighted_graph g) {
        _current_graph = g;
    }

    /**
     * Return the underlying graph of which this class works.
     *
     * @return _current_graph
     */
    @Override
    public weighted_graph getGraph() {
        return _current_graph;
    }

    /**
     * Compute a deep copy of this weighted graph.
     * uses copy constructor in {@link WGraph_DS}.
     *
     * @return a copy of _current_graph
     */
    @Override
    public weighted_graph copy() {
        return new WGraph_DS(_current_graph);
    }

    /**
     * Returns true if and only if (iff) there is a valid path from EVERY node to each other node.
     * This method starts with a specific node that it receives from the iterator and adds it to the queue.
     * As long as the queue is not empty, a node comes out of the queue,
     * and the tag of all its unmarked neighbors is marked, and they enter the queue.
     * Each poll is counted, so if in the end the counter == nodeSize -> the graph connected.
     * if counter != nodeSize -> the graph disconnected.
     *
     * @return true if the graph connected, and false if the graph disconnected.
     */
    @Override
    public boolean isConnected() {
        Iterator<node_info> it = _current_graph.getV().iterator();

        if (!it.hasNext())
            return true;
        setTagsToNeg1();
        node_info n = it.next();
        n.setTag(0);

        int counter = 0;
        Queue<node_info> queue = new LinkedList<>();
        queue.add(n);

        while (!queue.isEmpty()) {
            node_info current = queue.poll();
            counter++;

            for (node_info i : _current_graph.getV(current.getKey())) {
                if (i.getTag() == -1) {
                    queue.add(i);
                    i.setTag(0);
                }
            }
        }
        return counter == _current_graph.nodeSize();
    }

    /**
     * returns the length of the shortest path between src to dest
     * if no such path -> returns -1
     * This method uses dijkstraAlgo that gives every node the shortest distance from dest on tag,
     * so by taking the tag of dest node we get the result.
     *
     * @param src  - start node
     * @param dest - end (target) node
     * @return the shortest path distance between src to dest
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        node_info start = this._current_graph.getNode(src);
        node_info end = this._current_graph.getNode(dest);

        if (start == null || end == null)
            return -1;

        dijkstraAlgo(start, end);
        return end.getTag();
    }

    /**
     * returns the the shortest path between src to dest - as an ordered List of nodes:
     * src--> n1-->n2-->...dest
     * if no such path -> returns null
     * This method uses dijkstraAlgo that return {@link TempNode} of the dest node.
     * To restore the path, the method push to the list the node corresponding to the previous {@link TempNode}
     * until we get the src node.
     *
     * @param src  - start node
     * @param dest - end (target) node
     * @return List<node_info> contains all the nodes in the path in the order
     */
    @Override
    public List<node_info> shortestPath(int src, int dest) {
        node_info start = this._current_graph.getNode(src);
        node_info end = this._current_graph.getNode(dest);

        if (start == null || end == null)
            return null;

        TempNode curr = dijkstraAlgo(start, end);

        if (end.getTag() == -1 || curr == null)
            return null;

        LinkedList<node_info> path = new LinkedList<>();

        while (curr.getPrev() != null) {
            path.push(curr.getN());
            curr = curr.getPrev();
        }
        path.push(curr.getN());
        return path;
    }


    /**
     * Saves this weighted (undirected) graph to the give file name
     * this method uses {@link ObjectInputStream} and {@link FileInputStream},
     * and that possible because {@link WGraph_DS} and NodeInfo implements {@link Serializable}
     *
     * @param file the file name (may include a relative path).
     * @return true - iff the file was successfully saved
     */
    @Override
    public boolean save(String file) {
        boolean isSaved = false;
        ObjectOutputStream oos;
        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(_current_graph);
            isSaved = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSaved;
    }

    /**
     * This method load a graph to this graph algorithm.
     * if the file was successfully loaded - the underlying graph
     * of this class will be changed (to the loaded one), in case the
     * graph was not loaded the original graph should remain "as is".
     * this method uses {@link FileInputStream} and {@link ObjectInputStream}
     *
     * @param file - file name
     * @return true - iff the graph was successfully loaded.
     */
    @Override
    public boolean load(String file) {
        boolean isLoaded = false;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            init((weighted_graph) ois.readObject());
            isLoaded = true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return isLoaded;
    }

    @Override
    public String toString() {
        return "WGraph_Algo{" +
                "current_graph=" + _current_graph +
                '}';
    }

    ////////////////////// Private /////////////////////

    /**
     * Inner private class.
     * Used for dijkstraAlgo, because it is required to hold several identical nodes
     * that came from different node, and with different distance from the sources.
     * {@link TempNode} contains NodeInfo, the previous {@link TempNode} that represent the previous node,
     * and the current distance from the source node.
     * This class implements Comparable<TempNode>, for dijkstraAlgo can uses {@link PriorityQueue}.
     */
    private static class TempNode implements Comparable<TempNode> {
        private final node_info _n;
        private final double _dist;
        private final TempNode _prev;

        /**
         * Constructor.
         * init _dist by the Tag of the current NodeInfo
         *
         * @param n    the current node_info
         * @param prev the previous TempNode
         */
        public TempNode(node_info n, TempNode prev) {
            this._n = n;
            this._dist = n.getTag();
            this._prev = prev;
        }

        /**
         * compare this {@link TempNode} to o by _dist, according to the natural order for {@link Double}
         *
         * @param o TempNode
         * @return 1 -> (_dist > o._dist); -1 -> (_dist < o._dist); 0 -> (_dist == o._dist)
         */
        @Override
        public int compareTo(TempNode o) {
            return Double.compare(_dist, o._dist);
        }

        /**
         * get the current NodeInfo
         *
         * @return node_info
         */
        public node_info getN() {
            return _n;
        }

        /**
         * get the previous {@link TempNode}
         *
         * @return TempNode
         */
        public TempNode getPrev() {
            return _prev;
        }

    }

    /**
     * Implementation for dijkstra algorithm.
     * The algorithm uses {@link PriorityQueue} to save the next nodes it needs to visit,
     * and {@link HashSet} to know which nodes it has already visited.
     * At first srs enters the queue. In each iteration it passes through all the neighbors of the current node
     * that not visited and if the neighbor's distance from the src is smaller than it was before,
     * it is updated the tag and enters the queue, when curr is marked as prev of this node.
     * The queue contains {@link TempNode} objects to allow the insertion of identical nodes
     * that came from different sources and with different distances.
     * The algorithm stops when it reaches dest,
     * or if it finishes going through everything and does not find dest, then returns null.
     *
     * @param src  source node_info
     * @param dest destination node_info
     * @return TempNode contains dest node, or null if there no path.
     */
    private TempNode dijkstraAlgo(node_info src, node_info dest) {
        setTagsToNeg1();

        PriorityQueue<TempNode> queue = new PriorityQueue<>();
        HashSet<Integer> visited = new HashSet<>();

        queue.add(new TempNode(src, null));
        src.setTag(0);

        while (!queue.isEmpty()) {
            TempNode curr = queue.poll();

            if (!visited.contains(curr.getN().getKey())) {
                visited.add(curr.getN().getKey());

                if (curr.getN().getKey() == dest.getKey()) {
                    return curr;
                }
                for (node_info n : _current_graph.getV(curr.getN().getKey())) {
                    if (!visited.contains(n.getKey())) {
                        double t = curr.getN().getTag() + _current_graph.getEdge(curr.getN().getKey(), n.getKey());
                        if (n.getTag() == -1 || t <= n.getTag() && n.getTag() != 0) {
                            n.setTag(t);
                            queue.add(new TempNode(n, curr));
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Initialize all the tags of the vertices in the graph to -1
     */
    private void setTagsToNeg1() {
        for (node_info i : _current_graph.getV())
            i.setTag(-1);
    }
}