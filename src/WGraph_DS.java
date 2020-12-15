import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

/**
 * This class represent a weighted graph, unidirectional graph, implements weighted_graph interface.
 * {@link WGraph_DS} contains inner private class {@link NodeInfo} that represent the vertices of the graph.
 * every {@link WGraph_DS} has a {@link HashMap} calls _nodes contains all the vertices in the graph
 * the keys in this hashmap are the keys of the {@link NodeInfo} keys, that has a unique key to each node.
 * the edges are represents in _edges {@link HashMap}. this hashmap are contains all the keys of the nodes
 * in the graph, and the values are {@link HashMap} consisting of keys of the neighbors,
 * and the values of the weight of this edge.
 *
 * @author davidfeust
 */
public class WGraph_DS implements weighted_graph, Serializable {

    private final HashMap<Integer, node_info> _nodes;
    private final HashMap<Integer, HashMap<Integer, Double>> _edges;
    private int _edges_size;
    private int _mode_count;

    private static class NodeInfo implements node_info, Serializable {

        private final int _key;
        private String _info;
        private double _tag;

        /**
         * Constructor for node info.
         * initializing the variables for this node.
         *
         * @param key the id for this node, will be final key.
         */
        public NodeInfo(int key) {
            this._key = key;
            this._info = "";
            this._tag = -1;
        }

        /**
         * Copy constructor for node info.
         * copy all the variables from n to this.
         *
         * @param n node info to copy.
         */
        public NodeInfo(node_info n) {
            this._key = n.getKey();
            this._info = n.getInfo();
            this._tag = n.getTag();
        }

        /**
         * Return the unique key (id) associated with this node.
         *
         * @return key
         */
        @Override
        public int getKey() {
            return _key;
        }

        /**
         * return the remark (meta data) associated with this node.
         *
         * @return info
         */
        @Override
        public String getInfo() {
            return _info;
        }

        /**
         * Allows changing the remark (meta data) associated with this node.
         *
         * @param s the new value of the info.
         */
        @Override
        public void setInfo(String s) {
            _info = s;
        }

        /**
         * Temporal data (aka distance, color, or state)
         * which can be used be algorithms
         *
         * @return tag
         */
        @Override
        public double getTag() {
            return _tag;
        }

        /**
         * Allow setting the "tag" value for temporal marking an node -
         * common practice for marking by algorithms.
         *
         * @param t the new value of the tag.
         */
        @Override
        public void setTag(double t) {
            _tag = t;
        }

        @Override
        public String toString() {
            return "(" + _key + ')';
        }

        /**
         * Equals method. compares all the variables in o.
         * return true iff o is {@link NodeInfo} and all the variables are equals in both.
         *
         * @param o object to compare
         * @return true iff o and this are equals
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeInfo nodeInfo = (NodeInfo) o;
            return _key == nodeInfo._key &&
                    Double.compare(nodeInfo._tag, _tag) == 0 &&
                    Objects.equals(_info, nodeInfo._info);
        }
    }

    /***
     * Constructor for {@link WGraph_DS}
     * initializing the variables,
     * and create new {@link HashMap} for _nodes and _edges
     */
    public WGraph_DS() {
        _nodes = new HashMap<>();
        _edges = new HashMap<>();
        _edges_size = 0;
        _mode_count = 0;
    }

    /**
     * Copy constructor for {@link WGraph_DS}.
     * executing deep copy by coping all the values in oth's _nodes
     * and then connect the same edges like oth in this.
     *
     * @param oth other weighted_graph to copy
     */
    public WGraph_DS(weighted_graph oth) {
        _nodes = new HashMap<>();
        _edges = new HashMap<>();
        for (node_info i : oth.getV()) {
            _nodes.put(i.getKey(), new NodeInfo(i));
            _edges.put(i.getKey(), new HashMap<>());
            for (node_info j : oth.getV(i.getKey())) {
                connect(i.getKey(), j.getKey(), oth.getEdge(i.getKey(), j.getKey()));
            }
        }
        _edges_size = oth.edgeSize();
        _mode_count = oth.getMC();
    }

    /**
     * return the node_data by the node_id, takes from the hashmap
     *
     * @param key - the node_id
     * @return the node_data by the node_id, null if none.
     */
    @Override
    public node_info getNode(int key) {
        return _nodes.get(key);
    }

    /**
     * return true iff (if and only if) there is an edge between node1 and node2
     * this method run in O(1) time.
     * this method use the _edges hashmap of node1, and check if it contains the key of node2.
     *
     * @param node1 node id of node1
     * @param node2 node id of node2
     * @return true iff has edge between node1 and node2
     */
    @Override
    public boolean hasEdge(int node1, int node2) {
        node_info n1 = getNode(node1);
        node_info n2 = getNode(node2);
        if (null == n1 || null == n2)
            return false;
        return _edges.get(node1).containsKey(node2);
    }

    /**
     * return the weight of the edge (node1, node1).
     * In case there is no such edge - should return -1
     * this method run in O(1) time.
     * this method get the weight from the _edges hash map.
     *
     * @param node1 node id of node1
     * @param node2 node id of node2
     * @return weight of edge (node1, node2), or -1 if no such edge
     */
    @Override
    public double getEdge(int node1, int node2) {
        if (!hasEdge(node1, node2))
            return -1;
        return _edges.get(node1).get(node2);
    }

    /**
     * Add a new node to the graph with the given key.
     * this method run in O(1) time.
     * if there is already a node with such a key -> no action will be performed.
     * this method create a new {@link NodeInfo} object by the giving key,
     * and adds to the hash maps: _node - the new {@link NodeInfo}, _edges - a new {@link HashMap}.
     *
     * @param key node id
     */
    @Override
    public void addNode(int key) {
        if (_nodes.containsKey(key))
            return;
        node_info n = new NodeInfo(key);
        _nodes.put(n.getKey(), n);
        _edges.put(n.getKey(), new HashMap<>());
        _mode_count++;
    }

    /**
     * Connect an edge between node1 and node2, with an edge with weight >=0.
     * this method run in O(1) time.
     * if the edge node1-node2 already exists - the method simply updates the weight of the edge.
     * this method add the giving weight to each _edge HashMap of node1 and node2
     *
     * @param node1 node id of node1
     * @param node2 node id of node2
     * @param w     weight
     */
    @Override
    public void connect(int node1, int node2, double w) {
        node_info n1 = getNode(node1);
        node_info n2 = getNode(node2);
        if (null == n1 || null == n2 || node1 == node2 || w < 0)
            return;

        if (!hasEdge(node1, node2)) {
            _edges.get(node1).put(node2, w);
            _edges.get(node2).put(node1, w);
            _edges_size++;
        } else {
            _edges.get(node1).replace(node2, w);
            _edges.get(node2).replace(node1, w);
        }
        _mode_count++;
    }

    /**
     * This method return a pointer (shallow copy) for a
     * Collection representing all the nodes in the graph.
     * this method run in O(1) time.
     *
     * @return Collection<node_data>
     */
    @Override
    public Collection<node_info> getV() {
        return _nodes.values();
    }

    /**
     * This method returns a Collection containing all the
     * nodes connected to node_id
     * this method run in O(k) time, k - being the degree of node_id.
     * this method take all the keys in the _edges hash map of the node id key
     *
     * @param node_id the key of the node
     * @return Collection<node_data>
     */
    @Override
    public Collection<node_info> getV(int node_id) {
        node_info n = getNode(node_id);
        if (null == n)
            return null;
        Collection<node_info> c = new LinkedList<>();
        for (int i : _edges.get(node_id).keySet()) {
            c.add(_nodes.get(i));
        }
        return c;
    }

    /**
     * Delete the node (with the given ID) from the graph -
     * and removes all edges which starts or ends at this node.
     * This method run in O(k), k - being the degree of node_id.
     * all the edges removed by remove them from _edge, for every neighbor.
     *
     * @param key the node id of the node
     * @return the data of the removed node (null if none).
     */
    @Override
    public node_info removeNode(int key) {
        node_info n = getNode(key);
        if (null == n)
            return null;

        int num_of_nei = _edges.get(key).size();
        for (int i : _edges.get(key).keySet()) {
            _edges.get(i).remove(key);
        }
        _edges.get(key).clear();
        _nodes.remove(key, n);
        _edges_size -= num_of_nei;
        _mode_count += num_of_nei;
        return n;
    }

    /**
     * Delete the edge from the graph,
     * this method run in O(1) time.
     * delete the edge from _edges hash map
     *
     * @param node1 node id of node1
     * @param node2 node id of node2
     */
    @Override
    public void removeEdge(int node1, int node2) {
        node_info n1 = getNode(node1);
        node_info n2 = getNode(node2);
        if (null == n1 || null == n2)
            return;
        if (hasEdge(node1, node2)) {
            _edges.get(node1).remove(node2);
            _edges.get(node2).remove(node1);
            _edges_size--;
            _mode_count++;
        }
    }

    /**
     * return the number of vertices (nodes) in the graph.
     * this method run in O(1) time.
     * return the size of _node hash map
     *
     * @return number of vertices
     */
    @Override
    public int nodeSize() {
        return _nodes.size();
    }

    /**
     * return the number of edges (unidirectional graph).
     * this method run in O(1) time.
     *
     * @return number of edges
     */
    @Override
    public int edgeSize() {
        return _edges_size;
    }

    /**
     * return the Mode Count - for testing changes in the graph.
     * Any change in the inner state of the graph should cause an increment in the ModeCount
     *
     * @return Mode Count
     */
    @Override
    public int getMC() {
        return _mode_count;
    }

    @Override
    public String toString() {
        return "WGraph_DS:" +
                " mode_count=" + _mode_count +
                ", edge_size=" + _edges_size +
                "\n\tnodes=" + _nodes +
                "\n\tedges=" + _edges +
                "\n";
    }

    /**
     * Equals method. compares all the variables in o.
     * return true iff o is {@link WGraph_DS} and all the variables are equals in both
     * and all the values in the hashmaps are equals.
     * if some key does not exist in the other hash map - return false
     * also if the values of the same key are not equals - return false
     *
     * @param o object to compare
     * @return true iff o and this are equals
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WGraph_DS wGraph_ds = (WGraph_DS) o;
        for (int i : _nodes.keySet()) {
            if (!wGraph_ds._nodes.containsKey(i)) return false;
            if (!wGraph_ds._nodes.get(i).equals(_nodes.get(i))) return false;
        }
        for (int i : _edges.keySet()) {
            if (!wGraph_ds._edges.containsKey(i)) return false;
            if (!wGraph_ds._edges.get(i).equals(_edges.get(i))) return false;
        }
        return _edges_size == wGraph_ds._edges_size;
    }
}