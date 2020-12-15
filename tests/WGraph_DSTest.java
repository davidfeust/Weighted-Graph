import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link WGraph_DS}
 * every method has a test, and has some more general tests
 */
class WGraph_DSTest {

    private static weighted_graph g;

    @BeforeEach
    void setUp() {
        g = new WGraph_DS();
    }

    @Test
    void test0() {
        assertEquals(0, g.nodeSize());
        assertEquals(0, g.edgeSize());
        assertEquals(0, g.getMC());
        assertEquals(0, g.getV().size());
    }

    @Test
    void test1() {
        g.addNode(0);
        assertEquals(1, g.nodeSize());
        assertEquals(0, g.edgeSize());
        assertEquals(1, g.getMC());
        assertEquals(1, g.getV().size());
    }

    @Test
    void test2() {
        g.addNode(0);
        g.addNode(0);
        g.addNode(1);
        g.connect(0, 1, 1.5);
        g.connect(1, 0, 3.5);
        assertEquals(2, g.nodeSize());
        assertEquals(1, g.edgeSize());
        assertEquals(4, g.getMC());
    }

    @Test
    void getNode() {
        g.addNode(5);
        assertEquals(5, g.getNode(5).getKey());
        assertEquals("", g.getNode(5).getInfo());
        assertEquals(-1, g.getNode(5).getTag());
        node_info n = g.getNode(5);
        n.setTag(7);
        assertEquals(7, g.getNode(5).getTag());
    }

    @Test
    void hasEdge() {
        assertFalse(g.hasEdge(0, 1));
        g.addNode(0);
        g.addNode(1);
        assertFalse(g.hasEdge(0, 1));
        g.connect(0, 1, 1.5);
        g.connect(0, 1, 1.5);
        g.connect(1, 0, 2.5);
        assertTrue(g.hasEdge(0, 1));
        g.removeEdge(1, 0);
        assertFalse(g.hasEdge(0, 1));
    }

    @Test
    void getEdge() {
        assertEquals(-1, g.getEdge(0, 1));
        g.addNode(2);
        g.addNode(3);
        g.connect(2, 3, 1.5);
        assertEquals(1.5, g.getEdge(2, 3));
        g.removeEdge(2, 3);
        assertEquals(-1, g.getEdge(3, 2));
        g.connect(2, 3, 2.5);
        g.connect(2, 3, 3.5);
        assertEquals(3.5, g.getEdge(3, 2));
        g.addNode(4);
        assertEquals(-1, g.getEdge(2, 4));
        g.connect(2, 4, 7);
        assertEquals(7, g.getEdge(2, 4));
    }

    @Test
    void addNode() {
        assertEquals(0, g.nodeSize());
        for (int i = 0; i < 123; i++) {
            g.addNode(i);
            g.addNode(i);
        }
        assertEquals(123, g.nodeSize());
    }

    @Test
    void connect() {
        g.addNode(1);
        g.addNode(2);
        g.addNode(3);
        g.connect(1, 2, 0.1);
        assertNotEquals(-1, g.getEdge(1, 2));
        assertEquals(-1, g.getEdge(3, 2));
        g.connect(2, 3, 0.2);
        g.connect(2, 3, 0.3);
        assertEquals(0.3, g.getEdge(3, 2));
    }

    @Test
    void getV() {
        assertNotNull(g.getV());
        for (int i = 0; i < 10; i++) {
            g.addNode(i);
        }
        Collection<node_info> c = g.getV();
        for (int i = 0; i < 10; i++) {
            assertTrue(c.contains(g.getNode(i)));
        }
    }

    @Test
    void testGetV() {
        g = Ex1Test.graph_creator(10, 30, 1);
        Collection<node_info> c = g.getV(2);
        assertEquals(7, c.size());
        g.connect(2, 0, 0.1);
        g.connect(0, 2, 0.1);
        c = g.getV(2);
        assertEquals(7, c.size());
        g.removeEdge(2, 4);
        c = g.getV(2);
        assertEquals(6, c.size());
        g.removeNode(2);
        c = g.getV(2);
        assertNull(c);
        g.addNode(18);
        c = g.getV(18);
        assertEquals(0, c.size());
    }

    @Test
    void removeNode() {
        g.addNode(0);
        g.addNode(1);
        g.addNode(2);
        assertEquals(3, g.nodeSize());
        g.removeNode(0);
        g.removeNode(0);
        assertEquals(2, g.nodeSize());
        node_info rem = null;
        for (node_info i : g.getV()) {
            if (i.getKey() == 0)
                rem = i;
        }
        assertNull(rem);
    }

    @Test
    void removeEdge() {
        g.addNode(0);
        g.addNode(1);
        g.addNode(2);
        g.connect(0, 1, 1);
        g.connect(0, 1, 1);
        g.connect(0, 2, 1);
        g.connect(2, 1, 1);
        g.removeEdge(1, 5);
        g.removeEdge(1, 2);
        assertEquals(2, g.edgeSize());
    }

    @Test
    void nodeSize() {
        int r1 = Ex1Test.nextRnd(0, 100);
        for (int i = 0; i < r1; i++) {
            g.addNode(i);
        }
        assertEquals(r1, g.nodeSize());
        int r2 = Ex1Test.nextRnd(0, r1);
        for (int i = 0; i < r2; i++) {
            g.removeNode(i);
        }
        assertEquals(r1 - r2, g.nodeSize());
    }

    @Test
    void edgeSize() {
        int n = Ex1Test.nextRnd(10, 100), e = Ex1Test.nextRnd(n, n * 3);
        g = Ex1Test.graph_creator(n, e, 5);
        assertEquals(e, g.edgeSize());
        int r = Ex1Test.nextRnd(0, e);
        while (g.edgeSize() > e - r) {
            g.removeEdge(Ex1Test.nextRnd(0, e), Ex1Test.nextRnd(0, e));
        }
        assertEquals(e - r, g.edgeSize());
    }

    @Test
    void getMC() {
        int r1 = Ex1Test.nextRnd(5, 30);
        for (int i = 0; i < r1; i++) {
            g.addNode(i);
        }
        assertEquals(r1, g.getMC());
        int t1 = g.getV(r1 - 1).size();
        g.removeNode(r1 - 1);
        assertEquals(r1 + t1, g.getMC());
        int t2 = g.getV(r1 - 2).size();
        g.removeNode(r1 - 2);
        assertEquals(r1 + t1 + t2, g.getMC());
        g.connect(0, 1, 1);
        g.connect(0, 2, 1);
        g.connect(0, 3, 1);
        assertEquals(r1 + t1 + t2 + 3, g.getMC());
        int t3 = g.getV(0).size();
        g.removeNode(0);
        assertEquals(r1 + t1 + t2 + 3 + t3, g.getMC());
        g.connect(3, 2, 8);
        g.removeEdge(2, 3);
        assertEquals(r1 + t1 + t2 + 3 + t3 + 2, g.getMC());
    }

    @Test
    void equalNode() {
        g.addNode(17);
        node_info n17 = g.getNode(17);
        n17.setTag(5.2);
        n17.setInfo("hi");
        assertEquals(n17, n17);
        assertSame(n17, n17);
        weighted_graph g1 = new WGraph_DS();
        g1.addNode(17);
        node_info n17g1 = g1.getNode(17);
        n17g1.setTag(5.2);
        n17g1.setInfo("hi");
        assertEquals(n17, n17g1);
        assertNotSame(n17, n17g1);
        n17g1.setInfo("hi!");
        assertNotEquals(n17, n17g1);
        weighted_graph_algorithms ga = new WGraph_Algo(g);
        weighted_graph g2 = ga.copy();
        assertEquals(g.getNode(17), g2.getNode(17));
        assertNotSame(g.getNode(17), g2.getNode(17));
        n17.setInfo("hi!");
        assertNotEquals(g.getNode(17), g2.getNode(17));
    }

    @Test
    void equalGraph() {
        g.addNode(0);
        g.addNode(1);
        g.addNode(2);
        g.connect(0, 1, 2);
        g.connect(0, 2, 1);
        g.connect(1, 2, 3);
        weighted_graph g1 = new WGraph_DS();
        g1.addNode(0);
        g1.addNode(1);
        g1.addNode(2);
        g1.connect(0, 1, 2);
        g1.connect(0, 2, 1);
        g1.connect(1, 2, 3);
        assertNotSame(g, g1);
        assertEquals(g, g1);
        g1.getNode(0).setInfo("#@");
        assertNotEquals(g, g1);
        g.getNode(0).setInfo("#@");
        g1.getNode(0).setTag(78);
        assertNotEquals(g, g1);
        g.getNode(0).setTag(78);
        g1.removeEdge(0, 1);
        assertNotEquals(g, g1);
        g.removeEdge(0, 1);
        assertEquals(g, g1);
    }
}