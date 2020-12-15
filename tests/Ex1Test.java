import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * General tests for {@link WGraph_DS} and {@link WGraph_Algo}
 * some of this tests are based on Graph_Ex0_Test2
 * from: https://github.com/simon-pikalov/Ariel_OOP_2020.git
 */
public class Ex1Test {

    private static Random _rnd = new Random(0);

    /**
     * Simple empty test
     */
    @Test
    public void test0() {
        weighted_graph g0 = graph_creator(0, 0, 1);
        weighted_graph_algorithms ga0 = new WGraph_Algo(g0);
        assertTrue(ga0.isConnected());
    }

    /**
     * Simple single node graph
     */
    @Test
    public void test1() {
        weighted_graph g0 = graph_creator(1, 0, 1);
        weighted_graph_algorithms ga0 = new WGraph_Algo();
        ga0.init(g0);
        assertTrue(ga0.isConnected());
    }

    /**
     * graph with two nodes and no edges - not connected
     */
    @Test
    public void test2() {
        weighted_graph g0 = graph_creator(2, 0, 1);
        weighted_graph_algorithms ga0 = new WGraph_Algo();
        ga0.init(g0);
        assertFalse(ga0.isConnected());
    }

    /**
     * graph with two nodes and a single edge - connected
     */
    @Test
    public void test3() {
        weighted_graph g0 = graph_creator(2, 1, 1);
        weighted_graph_algorithms ga0 = new WGraph_Algo(g0);
        assertTrue(ga0.isConnected());
    }

    /**
     * small graph test (|V|=10, |E|=30), remove edges and a node:
     * so the updates graph will have (|V|=9, |E|=21)
     */
    @Test
    public void test4() {
        weighted_graph g10 = graph_creator(10, 30, 2);
        g10.removeEdge(0, 1);
        g10.removeEdge(2, 0);
        g10.removeEdge(2, 1);
        g10.removeNode(2);
        g10.removeNode(2);
        int re = 9;
        assertEquals(re, g10.nodeSize());
        re = 22;
        assertEquals(re, g10.edgeSize());
    }

    /**
     * test checks the algorithms on copy graph
     */
    @Test
    public void test5() {
        weighted_graph g1 = graph_creator(15, 40, 2);
        weighted_graph_algorithms ga1 = new WGraph_Algo(g1);
        weighted_graph g2 = ga1.copy();
        weighted_graph_algorithms ga2 = new WGraph_Algo(g2);
        assertEquals(ga1.shortestPath(0, 10), ga2.shortestPath(0, 10));
        assertEquals(ga1.shortestPathDist(0, 10), ga2.shortestPathDist(0, 10));
        g1.removeEdge(9,13);
        assertNotEquals(ga1.shortestPath(0, 10), ga2.shortestPath(0, 10));
        assertNotEquals(ga1.shortestPathDist(0, 10), ga2.shortestPathDist(0, 10));
    }

    /**
     * shortest path with weight 0
     */
    @Test
    public void test0dist() {
        WGraph_DS g = new WGraph_DS();
        weighted_graph_algorithms ga = new WGraph_Algo(g);
        for (int i = 0; i < 6; i++) {
            g.addNode(i);
        }
        g.connect(1, 0, 3);
        g.connect(0, 2, 0);
        g.connect(0, 3, 2.5);
        g.connect(3, 4, 5);
        g.connect(4, 5, 1);
        g.connect(1, 5, 4);
        g.connect(2, 4, 2);

        List<node_info> sp_actual1 = ga.shortestPath(0, 5);
        node_info[] sp_expected1 = {g.getNode(0), g.getNode(2), g.getNode(4), g.getNode(5)};
        assertEquals(3, ga.shortestPathDist(0, 5));
        assertArrayEquals(sp_expected1, sp_actual1.toArray());

        List<node_info> sp_actual2 = ga.shortestPath(5, 0);
        node_info[] sp_expected2 = {g.getNode(5), g.getNode(4), g.getNode(2), g.getNode(0)};
        assertEquals(3, ga.shortestPathDist(5, 0));
        assertArrayEquals(sp_expected2, sp_actual2.toArray());
    }

    /**
     * Test for shortestPathDist and shortestPath on big graph
     */
    @Test
    public void test_path() {
        WGraph_DS g1 = new WGraph_DS();
        weighted_graph_algorithms ga = new WGraph_Algo(g1);
        _rnd = new Random(1);
        int n = 500000, path_size = 50000;
        for (int i = 0; i < n; i++)
            g1.addNode(i);
        LinkedHashSet<Integer> s = new LinkedHashSet<>();
        while (s.size() != path_size)
            s.add(nextRnd(0, n - 1));
        Iterator<Integer> it = s.iterator();
        int prev = it.next(), start = prev, next = -1;
        List<node_info> sp_expected = new LinkedList<>();
        sp_expected.add(g1.getNode(prev));
        double sum = 0;
        while (it.hasNext()) {
            next = it.next();
            double w = nextRnd(0, 1.5);
            sum += w;
            g1.connect(prev, next, w);
            sp_expected.add(g1.getNode(next));
            prev = next;
        }
        for (int i = 1; i < n * 2; i++) {
            int a = nextRnd(0, n - 1);
            int b = nextRnd(0, n - 1);
            if (!g1.hasEdge(a, b)) {
                g1.connect(a, b, nextRnd(sum + 1, sum * 3));
            }
        }
        List<node_info> sp_actual = ga.shortestPath(start, next);
        assertEquals(sp_expected, sp_actual);
        assertEquals(sum, ga.shortestPathDist(start, next));
    }

    /**
     * run time test for build a big graph
     * with 10^6 nodes and 10^7 edges
     * should run in less then 5 sec
     */
    @Test
    void millionNodesTime() {
        assertTimeout(Duration.ofMillis(5000), this::millionNodes);
    }

    private void millionNodes() {
        weighted_graph g = new WGraph_DS();
        int times = 1000000;
        for (int i = 0; i < times; i++) {
            g.addNode(i);
        }
        for (int i = 10; i < g.nodeSize(); i++) {
            for (int j = 0; j < 10; j++) {
                g.connect(i - j, i, 0.1 * i);
            }
        }
        for (int j = 0; j < 100; j++) {
            g.connect(j, j + 20, 0.1 * j);
        }
    }


    ////////////////////// Private Functions /////////////////////

    /*
    private method base on Graph_Ex0_Test2
     */

    static int nextRnd(int min, int max) {
        double v = nextRnd(0.0 + min, 0.0 + max);
        return (int) v;
    }

    private static double nextRnd(double min, double max) {
        double d = _rnd.nextDouble();
        double dx = max - min;
        double ans = d * dx + min;
        DecimalFormat df = new DecimalFormat("####0.00");
        return Double.parseDouble(df.format(ans));
    }

    /**
     * Generate a random graph with v_size nodes and e_size edges
     *
     * @param v_size node size
     * @param e_size edges size
     * @param seed   random seed
     * @return weighted_graph
     */
    static weighted_graph graph_creator(int v_size, int e_size, int seed) {
        weighted_graph g = new WGraph_DS();
        _rnd = new Random(seed);
        for (int i = 0; i < v_size; i++) {
            g.addNode(i);
        }
        while (g.edgeSize() < e_size) {
            int a = nextRnd(0, v_size);
            int b = nextRnd(0, v_size);
            double r = _rnd.nextDouble();
            DecimalFormat df = new DecimalFormat("####0.00");
            r = Double.parseDouble(df.format(r));
            g.connect(a, b, r);
        }
        return g;
    }
}