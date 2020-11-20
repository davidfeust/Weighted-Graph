package ex1.tests;

import ex1.src.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link WGraph_Algo}
 * every method has a test
 */
class WGraph_AlgoTest {

    private static weighted_graph_algorithms ga;
    private static weighted_graph g;

    /**
     * init a small graph
     */
    @BeforeEach
    void setUp() {
        g = new WGraph_DS();
        ga = new WGraph_Algo(g);
        for (int i = 1; i < 7; i++) {
            g.addNode(i);
        }
        g.connect(1, 2, 7);
        g.connect(1, 6, 14);
        g.connect(1, 3, 9);
        g.connect(2, 3, 10);
        g.connect(2, 4, 15);
        g.connect(3, 6, 2);
        g.connect(3, 4, 11);
        g.connect(4, 5, 6);
        g.connect(5, 6, 9);
    }

    @Test
    void init() {
        g = new WGraph_DS();
        ga = new WGraph_Algo();
        assertNull(ga.getGraph());
        ga.init(g);
        assertNotNull(ga.getGraph());
        assertEquals(g, ga.getGraph());
    }

    @Test
    void getGraph() {
        weighted_graph g1 = ga.getGraph();
        assertEquals(g1, g);
        assertSame(g1, g);
    }

    @Test
    void copy() {
        g.removeEdge(2, 4);
        weighted_graph g1 = ga.copy();
        assertEquals(g.edgeSize(), g1.edgeSize());
        assertEquals(g.nodeSize(), g1.nodeSize());
        assertEquals(g.getMC(), g1.getMC());
        assertEquals(g, g1);
        assertNotSame(g, g1);
        g.getNode(2).setTag(5);
        g1.getNode(2).setTag(4);
        assertEquals(5, g.getNode(2).getTag());
        assertEquals(4, g1.getNode(2).getTag());
        g.connect(2, 4, 1.5);
        assertEquals(1.5, g.getEdge(2, 4));
        assertEquals(-1, g1.getEdge(2, 4));
        g1.connect(3, 5, 1.9);
        assertEquals(-1, g.getEdge(3, 5));
        assertEquals(1.9, g1.getEdge(5, 3));
    }

    @Test
    void isConnected() {
        assertTrue(ga.isConnected());
        g.addNode(9);
        assertFalse(ga.isConnected());
        g.connect(9, 9, 0.5);
        assertFalse(ga.isConnected());
        g.connect(9, 2, 0.5);
        assertTrue(ga.isConnected());
    }

    @Test
    void shortestPathDist() {
        assertEquals(20, ga.shortestPathDist(1, 5));
        assertEquals(20, ga.shortestPathDist(5, 1));
        assertEquals(11, ga.shortestPathDist(6, 1));
        assertEquals(0, ga.shortestPathDist(1, 1));
        assertEquals(0, ga.shortestPathDist(5, 5));
        assertEquals(-1, ga.shortestPathDist(10, 5));
        assertEquals(-1, ga.shortestPathDist(5, 55));
        assertEquals(13, ga.shortestPathDist(4, 6));
        assertEquals(13, ga.shortestPathDist(6, 4));
        assertEquals(21, ga.shortestPathDist(5, 2));
        assertEquals(21, ga.shortestPathDist(2, 5));
    }

    @Test
    void shortestPath() {
        assertNull(ga.shortestPath(2, 10));
        assertNull(ga.shortestPath(10, 2));
        g.addNode(10);
        assertNull(ga.shortestPath(10, 2));
        g.connect(10, 2, 5.6);
        assertEquals(5.6, ga.shortestPathDist(10, 2));
        assertArrayEquals(new node_info[]{g.getNode(10), g.getNode(2)}, ga.shortestPath(10, 2).toArray());
        node_info[] path15 = new node_info[]{g.getNode(1), g.getNode(3), g.getNode(6), g.getNode(5)};
        node_info[] path51 = new node_info[]{g.getNode(5), g.getNode(6), g.getNode(3), g.getNode(1)};
        assertArrayEquals(path15, ga.shortestPath(1, 5).toArray());
        assertArrayEquals(path51, ga.shortestPath(5, 1).toArray());

    }

    @Test
    void saveAndLoad() {
        String file_name = "saveTestG";
        ga.save(file_name);
        WGraph_Algo ga1 = new WGraph_Algo();
        ga1.load(file_name);
        assertEquals(ga.getGraph(), ga1.getGraph());
        assertNotSame(ga.getGraph(), ga1.getGraph());
    }

    @Test
    void dijkstraAlgoTest() {
        g = new WGraph_DS();
        for (int i = 0; i <= 4; i++) {
            g.addNode(i);
        }
        g.connect(0, 1, 6);
        g.connect(0, 3, 1);
        g.connect(1, 3, 2);
        g.connect(1, 4, 2);
        g.connect(1, 2, 5);
        g.connect(2, 4, 5);
        g.connect(3, 4, 1);
        WGraph_Algo ga = new WGraph_Algo(g);
        assertEquals(7, ga.shortestPathDist(0, 2));
        assertArrayEquals(new node_info[]{g.getNode(0), g.getNode(3), g.getNode(4), g.getNode(2)},
                ga.shortestPath(0, 2).toArray());
        assertTrue(ga.isConnected());
    }
}