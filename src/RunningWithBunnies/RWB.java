package RunningWithBunnies;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class RWB {
    public static void main(String[] args) {

        //System.out.println(Solution.powerSet(4));
        /*
        List<List<Integer>> powerSet = Solution.powerSet(4);
        for (List<Integer> set : powerSet) {
            List<List<Integer>> perms = Solution.genPerm(set);
            System.out.println(perms);
        }
*/
        //System.out.println(Arrays.toString(Solution.powerSetIdx(4)));

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

    public static void printSol(int[][] arr, int time_limit) {
        int[] sol = Solution.solution(arr, time_limit);
        System.out.println(Arrays.toString(sol));
    }
}
class Solution {
    public static int[][] shortestPaths;
    public static int time_limit;

    public static int[] solution(int[][] times, int times_limit) {
        // find floyd shortest paths first
        time_limit = times_limit;
        shortestPaths = floydWarshall(times);
        if (negCycleSol(shortestPaths) ) {
            int[] sol = new int[times.length - 2];
            for (int j = 1; j < times.length - 1; j++) {
                sol[j-1] = j-1;
            }

            return sol;
        }

        List<Integer> sol = checkCombinations();

        return sol.stream().mapToInt(Integer::intValue).toArray();
    }

    // classic floyd warshall O(n^3)
    public static int[][] floydWarshall(int[][] times) {
        final int len = times.length;
        int[][] shortestPaths = new int[len][len];

        for (int i=0; i < len; i++) {
            for (int j=0; j < len; j++) {
                shortestPaths[i][j] = times[i][j];
            }
        }

        for (int k=0; k < len; k++) {
            for (int i=0; i < len; i++) {
                for (int j=0; j < len; j++) {
                    if (shortestPaths[i][k] + shortestPaths[k][j] < shortestPaths[i][j]) {
                        shortestPaths[i][j] = shortestPaths[i][k] + shortestPaths[k][j];
                    }
                }
            }
        }

        return shortestPaths;
    }

    // check if a distance to itself is less than 0, O(n)
    public static boolean negCycleSol(int[][] shortestPaths) {
        final int len = shortestPaths.length;

        for (int i=0; i < len; i++) {
            if (shortestPaths[i][i] < 0) {
                return true;
            }
        }

        return false;
    }

    // check permutations of all possible rabbit saves
    public static List<Integer> checkCombinations() {
        final int len = shortestPaths.length;
        List<List<Integer>> powerSet = powerSet(len - 2);
        // through simple combination calculation,
        // easy to find out where each sizes of n would start
        int[] idx_arr = powerSetIdx(len - 2);
        int check_idx = (len - 2)/2, prev_check = check_idx;
        List<Integer> optimalPath = new ArrayList<>();

        // start in the middle and check sets of n sizes first
        while (check_idx >= 0 && check_idx < len - 2) {
            List<Integer> validSet = checkSetsOfSizeN(check_idx, idx_arr, powerSet);

            // if solution was found, save the solution
            if (optimalPath.size() < validSet.size()) {
                optimalPath = validSet;

                // if was decrementing the n size,
                // then this can be the only solution
                // we'd be in a infinite loop without this
                if (check_idx < prev_check) {
                    return optimalPath;
                }
                // if not, then check sizes of n+1 next
                prev_check = check_idx;
                check_idx++;
            } else {
                // if was incrementing the n size,
                // then this can be the only solution
                // we'd be in a infinite loop without this
                if (check_idx > prev_check) {
                    return optimalPath;
                }
                // if not, then check sizes of n-1 next
                prev_check = check_idx;
                check_idx--;
            }
        }

        // if no solution was found this way, size of 0 is it
        return optimalPath;
    }

    // check permutations of size N components of the powerset
    public static List<Integer> checkSetsOfSizeN(final int n, int[] idx_arr, List<List<Integer>> powerSet) {
        List<Integer> optimalPath = new ArrayList<>();
        int start_idx = 0, len = idx_arr.length + 2;
        if (n > 0) {
            start_idx = idx_arr[n-1];
        }

        // define range of size n
        for (int i = start_idx; i < idx_arr[n]; i++) {
            List<Integer> set = powerSet.get(i);

            List<List<Integer>> perms = genPerm(new ArrayList<>(set));

            // go through each permutation of traveling
            for (List<Integer> perm : perms) {

                int prevPos = 0, nextPos = 0, w_dist = 0;

                for ( Integer bunny_row : perm) {
                    nextPos = bunny_row.intValue() + 1;
                    w_dist += shortestPaths[prevPos][nextPos];
                    prevPos = nextPos;
                }
                w_dist += shortestPaths[prevPos][len - 1];

                // if size n worked, let's return the og set
                if ( w_dist <= time_limit ) {
                    return set;
                }
            }
        }

        return optimalPath;
    }

    // O(2^n)
    public static List<List<Integer>> powerSet(int numBunnies) {
        List<List<Integer>> set = new ArrayList<>();

        List<Integer> init_list = new ArrayList<>();
        addCombinations(0, numBunnies, init_list, set);

        Collections.sort(set, Comparator.comparingInt(List::size));

        return set;
    }

    public static void addCombinations(int i, int numBunnies, List<Integer> init_list, List<List<Integer>> comb) {
        for (int j = i; j < numBunnies; j++) {
            List<Integer> list = new ArrayList<>(init_list);
            list.add(Integer.valueOf(j));
            comb.add(list);

            addCombinations(j + 1, numBunnies, list, comb);
        }
    }

    // get permutation of a set O(n!) -- most expensive
    public static List<List<Integer>> genPerm(List<Integer> original) {
        if (original.isEmpty()) {
            List<List<Integer>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }

        Integer firstElement = original.remove(0);
        List<List<Integer>> returnValue = new ArrayList<>();
        List<List<Integer>> permutations = genPerm(original);
        for (List<Integer> smallerPermutated : permutations) {
            for (int index = 0; index <= smallerPermutated.size(); index++) {
                List<Integer> temp = new ArrayList<>(smallerPermutated);
                temp.add(index, firstElement);
                returnValue.add(temp);
            }
        }

        return returnValue;
    }

    // O(n)
    public static int[] powerSetIdx(int n) {
        int[] idx_arr = new int[n];

        int comb = n;
        idx_arr[0] = comb;

        for(int i = 1; i<n; i++){
            comb *= (n - i);
            comb /= (i+1);

            idx_arr[i] = idx_arr[i-1] + comb;
        }

        return idx_arr;
    }
}
