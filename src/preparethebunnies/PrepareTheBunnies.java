package preparethebunnies;

import java.util.Set;
import java.util.TreeSet;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Comparator;

import java.util.List;
import java.util.ArrayList;



public class PrepareTheBunnies {
    public static void main(String args[]) {
        Solution.Node node1 = new Solution.Node(1,1,1,false);
        Solution.Node node2 = new Solution.Node(1,1,1,false);
        Set<Solution.Node> hashSet = new TreeSet<>(new Comparator<Solution.Node>() {
            @Override
            public int compare(Solution.Node o1, Solution.Node o2) {
                if (o1.x != o2.x) {
                    return o1.x - o2.x;
                } else if (o1.y != o2.y) {
                    return o1.y - o2.y;
                } else if (o1.throughWall != o2.throughWall) {
                    return (o1.throughWall ? 1: 0) -
                            (o2.throughWall ? 1: 0);
                }
                return 0;
            }
        });
        hashSet.add(node1);
        if (hashSet.contains(node2)) {
            System.out.println("exists!");
        }
        hashSet.add(node2);

        int[][] map1 = {{0, 1, 1, 0},
                        {0, 0, 0, 1},
                        {1, 1, 0, 0},
                        {1, 1, 1, 0}};
        printSol(map1);

        int[][] map2 = {{0, 0, 0, 0, 0, 0},
                        {1, 1, 1, 1, 1, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 1, 1, 1, 1, 1},
                        {0, 1, 1, 1, 1, 1},
                        {0, 0, 0, 0, 0, 0}};
        printSol(map2);

        int[][] map3 = {
                {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        printSol(map3);
    }

    public static void printSol(int[][] map) {
        System.out.println(Solution.solution(map));
    }
}

class Solution {
    // accessible globally for convenience
    public static int[][] globalMap;
    public static Set<Node> nodes;

    public static int solution(int[][] map) {
        // Your code here
        globalMap = map;
        // create a custom comparator to track "efficiently"
        nodes = new TreeSet<>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.x != o2.x) {
                    return o1.x - o2.x;
                } else if (o1.y != o2.y) {
                    return o1.y - o2.y;
                } else if (o1.throughWall != o2.throughWall) {
                    return (o1.throughWall ? 1: 0) -
                            (o2.throughWall ? 1: 0);
                }
                // if all 3 fields are equal, they are the same
                return 0;
            }
        });

        return BFSTraverse();
    }

    public static class Node {
        public final int x;
        public final int y;
        public int dist;
        // if the node is already through wall, it cannot cross a wall
        // else, it can cross the wall, but the new node should have this flag set to true
        public final boolean throughWall;
        // this flag allows two of the same to be placed into the Treeset


        public Node(int x, int y, int dist, boolean throughWall) {
            this.x = x;
            this.y = y;
            this.dist = dist + 1;
            this.throughWall = throughWall;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof Node)) {
                return false;
            }

            Node other = (Node) o;

            return this.x == other.x && this.y == other.y
                    && this.throughWall == other.throughWall;
        }
    }

    public static int BFSTraverse() {
        Queue<Node> queue = new ArrayDeque<>();

        Node head = new Node(0, 0, 0, false);

        int h = globalMap.length, w = globalMap[0].length;
        // start from 0,0 and do a BFS
        while (head != null) {
            int x = head.x, y = head.y;
            // earliest appearance of the last item is the solution
            if (x == h - 1 && y == w - 1) {
                return head.dist;
            }

            // check neighbors
            if (x - 1 >= 0) {
                addToQueue(x-1,y,head, queue);
            }
            if (y - 1 >= 0) {
                addToQueue(x,y-1,head, queue);
            }
            if (x + 1 < h) {
                addToQueue(x+1,y,head, queue);
            }
            if (y + 1 < w) {
                addToQueue(x,y+1,head, queue);
            }

            head = queue.poll();
        }

        return -1;
    }

    // creates a neighbor and adds to queue if allowed to cross
    public static void addToQueue(int i, int j, Node head, Queue queue) {
        Node node = null;
        if (globalMap[i][j] == 1) {
            if (!head.throughWall) {
                node = new Node(i,j, head.dist, true);
            }
        } else {
            node = new Node(i,j, head.dist, head.throughWall);
        }

        // if cannot be cross or already in TreeSet, don't add to queue
        if (node != null && !nodes.contains(node)) {
            nodes.add(node);
            queue.add(node);
        }
    }
}

class BackUpSolution {
    public static int[][] globalMap;
    public static Node[][] nodeMap;
    public static List<Node> walls;

    public static int solution(int[][] map) {
        // Your code here
        globalMap = map;
        nodeMap = new Node[globalMap.length][globalMap.length];
        walls = new ArrayList<>();

        BFSTraverse(0,0, true);
        BFSTraverse(map.length-1, map[0].length-1, false);

        return findDistance();
    }

    public static int findDistance() {
        int dist = 9999999;
        if (nodeMap[0][0].isVisited(false)) {
            dist = nodeMap[0][0].getDist(false);
        }

        for (Node node : walls) {
            if (node.isVisited(true) && node.isVisited(false)) {
                int combined = node.getDist(true) + node.getDist(false);
                if (combined < dist) {
                    dist = combined;
                }
            }
        }

        return dist + 1;
    }

    public static void BFSTraverse(int i, int j, boolean fromDoor) {
        Queue<Node> queue = new ArrayDeque<>();

        Node head = nodeMap[i][j];
        if (nodeMap[i][j] == null) {
            nodeMap[i][j] = new Node(i, j, globalMap[i][j] == 1);
            head = nodeMap[i][j];
        }
        head.setDist(0, fromDoor);

        int h = globalMap.length, w = globalMap[0].length;
        while (head != null) {
            int x = head.x, y = head.y;

            if (x - 1 >= 0) {
                visitOrInitNode(x-1,y,head.getDist(fromDoor), fromDoor, queue);
            }
            if (y - 1 >= 0) {
                visitOrInitNode(x,y-1,head.getDist(fromDoor), fromDoor, queue);
            }
            if (x + 1 < h) {
                visitOrInitNode(x+1,y,head.getDist(fromDoor), fromDoor, queue);
            }
            if (y + 1 < w) {
                visitOrInitNode(x,y+1,head.getDist(fromDoor), fromDoor, queue);
            }

            head = queue.poll();
        }

    }

    public static void visitOrInitNode(int i, int j, int dist, boolean fromDoor, Queue queue) {
        Node node = nodeMap[i][j];
        if ( node == null) {
            node = new Node(i,j, globalMap[i][j] == 1);
            nodeMap[i][j] = node;
        } else if ( node.isVisited(fromDoor)) {
            return;
        }

        node.setDist(dist + 1, fromDoor);
        node.visit(fromDoor);

        if (!node.isWall) {
            queue.add(node);
        }
    }

    private static class Node {
        public final int x;
        public final int y;
        public int g_dist;
        public int h_dist;
        public boolean visited_from_door = false;
        public boolean visited_from_exit = false;
        public final boolean isWall;

        public Node(int x, int y, boolean isWall) {
            this.x = x;
            this.y = y;
            this.isWall = isWall;
            if (isWall) {
                walls.add(this);
            }
        }

        public boolean isVisited(boolean fromDoor) {
            if (fromDoor) {
                return visited_from_door;
            } else {
                return visited_from_exit;
            }
        }

        public void visit(boolean fromDoor) {
            if (fromDoor) {
                visited_from_door = true;
            } else {
                visited_from_exit = true;
            }
        }

        public int getDist(boolean fromDoor) {
            if (fromDoor) {
                return g_dist;
            } else {
                return h_dist;
            }
        }

        public void setDist(int dist, boolean fromDoor) {
            if (fromDoor) {
                g_dist = dist;
            } else {
                h_dist = dist;
            }
        }
    }


    /*
    public static void sortNodesByDistance(List<Node> nodeList) {
        Collections.sort(nodeList, new Comparator<Node>(){
            @Override
            public int compare(final Node node1, final Node node2) {
                if()
            }
        })
    }
    */

    /*
    public static List<Node> createUnvisitedList() {
        nodeMap = new Node[globalMap.length][globalMap.length];

        List<Node> nodeList = new ArrayList<>();
        addNeighbors(0,0, nodeList);

        return nodeList;
    }

    public static void addNeighbors(int i, int j, List<Node> nodeList) {
        Node node = new Node(i, j, globalMap[i][j] == 1 );
        nodeList.add(node);
        nodeMap[i][j] = node;

        if (i - 1 >= 0 && nodeMap[i-1][j] == null) {
            addNeighbors(i-1, j, nodeList);
        } else if (j - 1 >= 0 && nodeMap[i][j-1] == null) {
            addNeighbors(i, j-1, nodeList);
        } else if (i + 1 < nodeMap.length && nodeMap[i+1][j] == null) {
            addNeighbors(i+1, j, nodeList);
        } else if (j + 1 < nodeMap[0].length && nodeMap[i][j+1] == null) {
            addNeighbors(i, j+1, nodeList);
        }
    }
    */
}