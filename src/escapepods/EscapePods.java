package escapepods;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;

public class EscapePods {
    public static void main(String[] args) {
        int[] entrances1 = {0, 1};
        int[] exits1 = {4, 5};
        int[][] path1 = {
                {0, 0, 4, 6, 0, 0}, // # Room 0: Bunnies
                {0, 0, 5, 2, 0, 0}, // # Room 1: Bunnies
                {0, 0, 0, 0, 4, 4}, // # Room 2: Intermediate room
                {0, 0, 0, 0, 6, 6}, // # Room 3: Intermediate room
                {0, 0, 0, 0, 0, 0}, // # Room 4: Escape pods
                {0, 0, 0, 0, 0, 0}, // # Room 5: Escape pods
        };
        printSol(entrances1, exits1, path1);
        // 16

        int[] entrances2 = {0};
        int[] exits2 = {3};
        int[][] path2 = {{0, 7, 0, 0}, {0, 0, 6, 0}, {0, 0, 0, 8}, {9, 0, 0, 0}};
        printSol(entrances2, exits2, path2);

        //6




    }

    public static void printSol(int[] entrances, int[] exits, int[][] path) {
        System.out.println(Solution.solution(entrances, exits, path));
    }
}

class Solution {
    // global static vars
    public static int[] g_entrances;
    public static int[] g_exits;
    public static Node[] node_map;

    public static int solution(int[] entrances, int[] exits, int[][] path) {
        // Your code here
        g_entrances = entrances;
        g_exits = exits;

        // simplify to only have one entrance and one exit
        int[][] updatedPath = simplifyPath(entrances, path);

        return dinic(updatedPath);
    }

    /**
     * if there's multiple entrance/exit nodes, it simplifies the matrix to have one entrance or exit
     * first cleans up entrance, then updates the entrances and exits array to save the right index
     * lastly cleans up the exits
     *
     * @param entrances original entrance so that the entrance and the exit can be recalibrated
     * @param path original path
     * @return updated path
     */
    public static int[][] simplifyPath(int[] entrances, int[][] path) {
        int[][] updatedPath = simplifyEntrance(path);
        updateEntrancesAndExit(entrances);
        return simplifyExit(updatedPath);
    }

    /**
     * does nothing if there's only one entrance
     * otherwise, sums up rows across each column and places the entrance in index 0
     *
     * @param path og path
     * @return the updated path
     */
    public static int[][] simplifyEntrance(int[][] path) {
        if (g_entrances.length == 1) {
            return path;
        }

        // first simplify the entrance row
        int new_size = path.length - g_entrances.length + 1;
        int[] entrancePath = new int[new_size];
        for (int i = 0; i < g_entrances.length; i++) {
            int entranceIdx = g_entrances[i];

            int idx_c = 1, ent_c = 0;
            for (int j = 0; j < path.length; j++) {
                if (ent_c < g_entrances.length
                        && g_entrances[ent_c] == j) {
                    ent_c++;
                } else {
                    entrancePath[idx_c] += path[entranceIdx][j];
                    idx_c++;
                }
            }
        }

        // init the newpath with the first row being the one just created
        int[][] updatedPath = new int[new_size][new_size];
        updatedPath[0] = entrancePath;

        // every next row should have none of the prev entrance nodes
        int idx_c = 1, ent_c = 0;
        for (int i = 0; i < path.length; i++) {
            if (ent_c < g_entrances.length
                    && g_entrances[ent_c] == i) {
                ent_c++;
            } else {
                int nested_idx_c = 1, nested_ent_c = 0;
                for (int j = 0; j < path.length; j++) {
                    if (nested_ent_c < g_entrances.length
                            && g_entrances[nested_ent_c] == j) {
                        nested_ent_c++;
                    } else {
                        updatedPath[idx_c][nested_idx_c] = path[i][j];
                        nested_idx_c++;
                    }
                }

                idx_c++;
            }
        }

        g_entrances = new int[]{0};

        return updatedPath;
    }

    /**
     * does nothing if there's only one exit
     * otherwise, sums up across the exit index columns for each row
     * represents a single stream of flow towards the exit for each "room" if applicable
     *
     * @param path "og" path
     * @return the updated path
     */
    public static int[][] simplifyExit(int[][] path) {
        if (g_exits.length == 1) {
            return path;
        }
        int new_size = path.length - g_exits.length + 1;
        int[][] updatedPath = new int[new_size][new_size];

        // every row (minus the last one) should have the paths leading to the exit summed up
        // otherwise left alone
        int idx_c = 0, exit_c = 0;
        for (int i = 0; i < path.length; i++) {
            if (exit_c < g_exits.length
                    && g_exits[exit_c] == i) {
                exit_c++;
            } else {
                int sumOfExit = 0;

                int nested_idx_c = 0, nested_exit_c = 0;
                for (int j = 0; j < path.length; j++) {
                    if (nested_exit_c < g_exits.length
                            && g_exits[nested_exit_c] == j) {
                        sumOfExit += path[i][j];
                        nested_exit_c++;
                    } else {
                        updatedPath[idx_c][nested_idx_c] = path[i][j];
                        nested_idx_c++;
                    }
                }
                updatedPath[idx_c][nested_idx_c] = sumOfExit;

                idx_c++;
            }
        }

        g_exits = new int[]{updatedPath.length - 1};

        return updatedPath;
    }

    /**
     * order may get messed up if entrance and exit nodes are placed in between each other
     * and not nicely like 0,1 entrances and n-2,n-1 exits
     * @param entrances the og entrance array
     */
    public static void updateEntrancesAndExit(int[] entrances) {
        if (entrances.length == 1) {
            // in case there's an entrance that's before exits that will be cleaned up
            // not sure if this would ever happen, but to cover grounds
            if (g_exits.length > 1) {
                int entrance = entrances[0];
                for(int i = 0; i < g_exits.length; i++) {
                    if (entrance > g_exits[i]) {
                        entrance--;
                    } else {
                        break;
                    }
                }
                g_entrances = new int[]{ entrance };
            } else {
                return;
            }
        }

        // since the entrance index got deleted, the exit indexes need to be recalibrated
        int[] updatedExits = new int[g_exits.length];
        for(int i = 0; i < g_exits.length; i++) {
            int exit = g_exits[i];
            for(int j = 0; j < entrances.length; j++) {
                if (entrances[j] < exit) {
                    exit--;
                } else {
                    break;
                }
            }
            updatedExits[i] = exit + 1;
        }

        g_exits = updatedExits;
    }

    /**
     * classic Dinic's algorithm
     *
     * @param path the updated path
     * @return maximum flow
     */
    public static int dinic(int[][] path) {
        int[][] progress = new int[path.length][path.length];
        int s = g_entrances[0], t = g_exits[0];
        int total = 0;
        while(modBFS(path, progress)) {
            int flow;
            do {
                flow = modDFSIteration(s,t, Integer.MAX_VALUE, path, progress);
                total += flow;
            } while (flow > 0);
        }
        return total;
    }

    /**
     * BFS with a slight modification
     * only traverses to another node if the flow capacity has not been met
     *
     * @param path updated path, or "the capacity"
     * @param progress the flow progress
     * @return true if possible to reach the exit
     */
    public static boolean modBFS(int[][] path, int[][] progress) {
        Queue<Node> queue = new ArrayDeque<>();
        Node head = new Node(g_entrances[0], 0);
        node_map = new Node[path.length];
        node_map[g_entrances[0]] = head;

        while (head != null) {
            for(int i = 0; i < path.length; i++) {
                int curr = head.roomNum;
                if ( progress[curr][i] < path[curr][i] ) {
                    Node toVisit = node_map[i];
                    if (toVisit == null) {
                        toVisit = new Node(i);
                        node_map[i] = toVisit;
                    } else if (toVisit.level >= 0) {
                        continue;
                    }
                    toVisit.level = head.level + 1;
                    queue.add(toVisit);
                }
            }

            head = queue.poll();
        }

        // check that we can still reach the destination
        Node dest = node_map[g_exits[0]];
        if (dest == null || dest.level == -1) {
            return false;
        }
        return true;
    }

    /**
     * Classic Dinic's modified DFS iteration
     * recursive method
     *
     * @param s starting pos
     * @param t always the exit node
     * @param flow represents the bottleneck flow
     * @param path og path
     * @param progress the progress that gets updated
     * @return maximum allowed bottleneck flow in a traversal
     */
    public static int modDFSIteration(int s, int t, int flow, int[][] path, int[][] progress) {
        // traversed through the last node
        if (s == t) {
            return flow;
        }

        for (int i = 0; i < path.length; i++) {
            // check if the capacity hasn't been met and that the next node level is one above
            if (node_map[i].level != node_map[s].level + 1
                    || progress[s][i] >= path[s][i]) {
                continue;
            }

            // check for bottleneck
            int min_flow = Math.min(flow, path[s][i] - progress[s][i]);
            // recursively check the next possible iteration
            int allowed_flow = modDFSIteration(i, t, min_flow, path, progress);

            // save the flow progress for the next BFS iteration
            if (allowed_flow > 0) {
                progress[s][i] += allowed_flow;
                progress[i][s] -= allowed_flow;

                return allowed_flow;
            }
        }

        return 0;
    }

    public static class Node {
        final public int roomNum;
        public int level = -1;

        public Node(int roomNum) {
            this.roomNum = roomNum;
        }

        public Node(int roomNum, int level) {
            this.roomNum = roomNum;
            this.level = level;
        }
    }
}
