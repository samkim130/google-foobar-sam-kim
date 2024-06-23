package RunningWithBunnies;

import java.util.*;

public class RunningWithBunnies {
    /*
    public static void main(String[] args) {

        Set<Solution.Node> set = new LinkedHashSet<>();
        Solution.Node n1 =  new Solution.Node(1);
        Solution.Node n2 =  new Solution.Node(2);
        Solution.Node n2_cp =  new Solution.Node(2);
        set.add(n1);
        set.add(n2);
        if ( !set.add(n2_cp) ) {
            System.out.println("LinkedHashSet already contains this bad boy");
        }
        Iterator<Solution.Node> it = set.iterator();
        Solution.Node n1_ref = it.next();
        if ( n1_ref.i == n1.i )  {
            System.out.println("The head is maintained!!!");
        }
        if ( set.remove(n1) ) {
            System.out.println("The head is removed!!!");
        }

        int[][] t1 = {
                {0, 1, 1, 1, 1},
                {1, 0, 1, 1, 1},
                {1, 1, 0, 1, 1},
                {1, 1, 1, 0, 1},
                {1, 1, 1, 1, 0}};
        int limit1 = 3;
        printSol(t1,limit1);
        // {0, 1}

        int[][]t2 = {
                {0, 2, 2, 2, -1},
                {9, 0, 2, 2, -1},
                {9, 3, 0, 2, -1},
                {9, 3, 2, 0, -1},
                {9, 3, 2, 2, 0}};
        int limit2 = 1;
        printSol(t2,limit2);
        // {1,2}

        int[][]t3 = {
                {0, 2, 2, 2, -1},
                {9, 0, 2, 2, -1},
                {9, 3, 0, 2, -1},
                {9, 3, 2, 0, -1},
                {9, -5, 2, 2, 0}};
        int limit3 = 1;
        printSol(t3,limit3);

    }
    */

    public static void printSol(int[][] arr, int time_limit) {
        int[] sol = BackUpSolution.solution(arr, time_limit);
        System.out.println(Arrays.toString(sol));
    }
}

class BackUpSolution {
    public static int[][] g_times;

    public static int[] solution(int[][] times, int times_limit) {
        // Your code here
        g_times = times;
        Node.node_map = new Node[times.length];

        if (hasNegCycle()) {
            System.out.println("Neg cycle detected");

            int[] sol = new int[times.length - 2];
            for (int i = 1; i < times.length - 1; i++) {
                sol[i-1] = i-1;
            }

            return sol;
        }

        DFS(times_limit);

        return new int[1];
    }

    //modified SPFA
    public static boolean hasNegCycle() {
        final int len = g_times.length;
        Set<Node> setQueue = new LinkedHashSet<>();
        Node head = new Node(0, 0);
        Node.node_map[0] = head;

        int iter = 0;

        while (head != null) {
            for (int i = 0; i < len; i++) {
                if( i == head.i ) {
                    continue;
                }

                iter ++;

                if (addAdjointToQueueOptimized(head, i, setQueue, iter)) {
                    return true;
                } else if (iter == len) {
                    // reset so that addAdjointToQueueOptimized can check properly
                    iter = 0;
                }
            }

            Iterator<Node> nextHead = setQueue.iterator();
            if (nextHead.hasNext()) {
                head = nextHead.next();
                nextHead.remove();
            } else {
                head = null;
            }
        }

        if (Node.cycleExists()) {
            return true;
        }

        return false;
    }

    public static boolean addAdjointToQueue(Node head, int i, Set setQueue) {
        int pos = head.i;
        Node node = Node.fetchOrInit(i);
        //skip if node to visit has a lesser weight-distance
        if (head.w_dist + g_times[pos][i] >= node.w_dist) {
            return false;
        }

        // add to edge length
        node.e_dist = head.e_dist + 1;
        if ( node.e_dist == g_times.length) {
            // negative cycle, if it keeps coming back after visiting everyone
            // can be optimized
            return true;
        }

        //update node new weight-distance
        node.w_dist = head.w_dist + g_times[pos][i];

        setQueue.add(node);

        return false;
    }

    public static boolean addAdjointToQueueOptimized(Node head, int i, Set setQueue, final int iter) {
        int pos = head.i;
        Node node = Node.fetchOrInit(i);
        //skip if node to visit has a lesser weight-distance
        if (head.w_dist + g_times[pos][i] >= node.w_dist) {
            return false;
        }
        node.prev = head;

        // check at least once every largest loop size which is == # of nodes
        if (iter == g_times.length) {
            if (Node.cycleExists()) {
                return true;
            }
        }

        //update node new weight-distance
        node.w_dist = head.w_dist + g_times[pos][i];

        setQueue.add(node);

        return false;
    }


    // modified DFS
    public static void DFS(int time_limit) {
        final int len = g_times.length;

        Set<Node> set = new HashSet<>();
        Node head = new Node(0, 0);
        Node.node_map[0] = head;
        resetDist(head,set);

        boolean addBulkhead = false;
        Set<Integer> currSet = new HashSet<>();
        while (set.size() > 0) {
            Node node = findMinDist(set);
            set.remove(node);

            if (node.i == len - 1) {
                addBulkhead = true;
                resetDist(node, set);
                if (node.savedBunnySet == null) {
                    node.savedBunnySet = new HashSet<>(currSet);

                }
            } else {
                node.savedBunnySet = new HashSet<>(currSet);
                if (node.i > 0) {
                    node.savedBunnySet.add(Integer.valueOf(node.i));
                    currSet = node.savedBunnySet;
                }
            }


            if ( updateDist(set, node, time_limit) ) {
                return;
            } else if (addBulkhead) {
                Node bulkheadNode = Node.node_map[len - 1];
                bulkheadNode.w_dist = Integer.MAX_VALUE;

                set.add(bulkheadNode);
                addBulkhead = false;
            }
        }
    }

    public static void resetDist(Node head, Set<Node> set) {
        int head_pos = head.i;
        for(int i = 0; i < Node.node_map.length; i++) {
            if (i == head_pos) {
                continue;
            }
            Node node = Node.node_map[i];
            if (set.add(node)){
                node.w_dist = g_times[head_pos][i] + head.w_dist;
                node.prev = head;
            }
        }
    }

    public static Node findMinDist(Set<Node> set) {
        int min = Integer.MAX_VALUE;
        Node min_node = null;
        Iterator<Node> iterator = set.iterator();
        while(iterator.hasNext()) {
            Node node = iterator.next();
            if ( min > node.w_dist ) {
                min = node.w_dist;
                min_node = node;
            }
        }

        return min_node;
    }

    public static boolean updateDist(Set<Node> set, Node visitedNode, int time_limit) {
        boolean past_time_limit = true;

        Iterator<Node> iterator = set.iterator();
        while(iterator.hasNext()) {
            Node node = iterator.next();
            int comp_dist = visitedNode.w_dist + g_times[visitedNode.i][node.i];
            if ( comp_dist < node.w_dist ) {
                node.w_dist = comp_dist;
                node.prev = visitedNode;
            }

            if ( node.w_dist <= time_limit ) {
                past_time_limit = false;
            }
        }

        return past_time_limit;
    }


    public static class Node {
        public final int i;
        public static Node[] node_map;
        public int w_dist = Integer.MAX_VALUE;
        public int e_dist = 0;
        public Node prev;
        public Set<Integer> savedBunnySet;

        protected Node (int i) {
            this.i = i;
        }
        protected Node (int i, int w_dist) {
            this.i = i;
            this.w_dist = w_dist;
        }

        public static Node fetchOrInit(int i) {
            Node node = node_map[i];
            if (node == null) {
                node = new Node(i);
                node_map[i] = node;
            }

            return node;
        }

        public static boolean cycleExists() {
            final int len = node_map.length;
            Set<Node> set = new HashSet<>();

            for (int i = 0; i < len; i++) {
                Node node = node_map[i];
                // if already was checked previously, then skip
                if (!set.add(node)) {
                    continue;
                }

                // keep looping through the prev of each Node
                Node prev = node.prev;
                int count = 0;
                while (prev != null) {
                    set.add(prev);
                    if (count == len) {
                        return true;
                    }

                    prev = prev.prev;
                    count ++;
                }
            }

            return false;
        }

        public void Update(Node node){
            this.w_dist = node.w_dist + g_times[node.i][this.i];
            this.prev = node;
            if (this.i > 0 && this.i < node_map.length - 1) {
                this.savedBunnySet = new HashSet<>(node.savedBunnySet);
                this.savedBunnySet.add(Integer.valueOf(node.i));
            }
        }

        @Override
        public boolean equals (Object o) {
            if (o == null) {
                return false;
            }
            else if (o == this) {
                return true;
            } else if (!(o instanceof Node)) {
                return false;
            }

            Node other = (Node) o;

            // since every node has a unique i
            return this.i == other.i;
        }

        @Override
        public int hashCode() {
            // since every node has a unique i
            return this.i;
        }
    }


}