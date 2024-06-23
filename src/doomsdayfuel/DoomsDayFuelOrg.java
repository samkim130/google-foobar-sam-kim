package doomsdayfuel;

import java.util.Arrays;

public class DoomsDayFuelOrg {
    /*
    public static void main(String args[]) {
        int[][] m =  {
                { 0,1,0,0,0,1 },  // s0, the initial state, goes to s1 and s5 with equal probability
                { 4,0,0,3,2,0 },  // s1 can become s0, s3, or s4, but with different probabilities
                { 0,0,0,0,0,0 },  // s2 is terminal, and unreachable (never observed in practice)
                { 0,0,0,0,0,0 },  // s3 is terminal
                { 0,0,0,0,0,0 },  // s4 is terminal
                { 0,0,0,0,0,0 },  // s5 is terminal
        };

        System.out.println(Arrays.toString(solution(m)));


        //int[][] m0 = { {0} };
        //System.out.println(Arrays.toString(solution(m0)));
        //int[][] m2 = {{0, 2, 1, 0, 0}, {0, 0, 0, 3, 4}, {0, 0, 0, 0, 0}, {0, 0, 0, 0,0}, {0, 0, 0, 0, 0}};
        //System.out.println(Arrays.toString(solution(m2)));


        //int[][] m3 = {{8, 0, 0, 0, 0}, {0, 0, 0, 3, 4}, {0, 0, 0, 0, 0}, {0, 0, 0, 0,0}, {0, 0, 0, 0, 0}};
        //System.out.println(Arrays.toString(solution(m3)));

        //int[][] m4 = {{0, 0, 0, 8, 0}, {0, 0, 0, 3, 4}, {0, 0, 0, 0, 0}, {0, 0, 0, 0,0}, {0, 0, 0, 0, 0}};
        //System.out.println(Arrays.toString(solution(m4)));

        //int[][] m5 = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0,0}, {0, 0, 0, 0, 0}};
        //System.out.println(Arrays.toString(solution(m5)));

        //int[][] m6 = {{0, 1, 4, 5, 2}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0,0}, {0, 0, 0, 0, 0}};
        //System.out.println(Arrays.toString(solution(m6)));

        int[][] m7 = { {1, 1, 1, 1, 1,},  {0, 0, 0, 0, 0,}, { 1, 1, 1, 1, 1,}, {0, 0, 0, 0, 0,}, {1, 1, 1, 1, 1,} };
        System.out.println(Arrays.toString(solution(m7)));

        int[][] m8 =  {
                { 0,1,0,3,0,1,3 },
                { 4,12,0,3,2,0,2 },
                { 0,4,3,0,0,21,1 },
                { 3,3,0,0,2,0,3 },
                { 0,0,0,0,0,0,0 },
                { 1,0,1,0,7,9,3 },
                { 0,0,0,0,0,0,0 },
        };
        System.out.println(Arrays.toString(solution(m8)));

    }
     */

    public static int[] solution(int[][] m) {
        // fill in tracking variables
        int[] sumrows = new int[m.length];
        int numTermStates = initSumrows(m, sumrows);
        int[] tranStates = initTrackingArrs(sumrows, m.length - numTermStates);

        // check for edge cases,
        // esp since algorithm wouldn't work unless state0 is transient
        if ( numTermStates == m.length || sumrows[0] == 0) {
            // 1. all are terminal cases
            // 2. row 0 is terminal case
            //System.out.println("edge 1 or 2 triggered");
            int[] sol = new int[numTermStates + 1];
            sol[0] = 1;
            sol[numTermStates] = 1;
            return sol;
        } else if ( sumrows[0] == m[0][0]  ||
                (numTermStates == m.length - 1 && sumrows[0] > 0) ) {
            // 3. row 0 is an absorbing row (just keeps returning to itself)
            // 4. all rows minus row 0 is terminal

            return extrapolateSol(m[0], tranStates);
        }  else if (numTermStates == 1) {
            // 5. saves computation
            int[] sol = {1,1};
            return sol;
        }

        //Engel's algorithm (1979) implementation
        int[] finalState = engelsAlgorithmForV(tranStates, sumrows, m);

        return extrapolateSol(finalState, tranStates);
    }

    // O(n)
    public static int initSumrows(int[][] m, int[] sumrows) {
        int count = 0;
        for(int i = 0; i < m.length; i ++) {
            int sum = 0;

            for(int j = 0; j < m[i].length; j++) {
                sum += m[i][j];
            }

            sumrows[i] = sum;

            if (sum == 0) {
                count++;
            }
        }
        return count;
    }

    // O(r) where r<n, so around O(n)
    public static int[] initTrackingArrs(int[] sumrows, final int r) {
        int[] tranStates = new int[r];
        int tranCounter = 0;
        for (int i = 0; i < sumrows.length; i++) {
            if (sumrows[i] != 0) {
                tranStates[tranCounter++] = i;
            }
        }

        return tranStates;
    }

    public static int[] engelsAlgorithmForV(int[] tranStates, int[] sumrows, int[][] m) {
        final int u = 0, r = tranStates.length;
        int[] critLoading = getCritLoadingChips(tranStates, sumrows, u);
        int[] startStateR = Arrays.copyOfRange(critLoading,0,r);
        int[] chipDealtTracker = new int[r];

        do {
            // do "move 1"
            //System.out.println("move1 initiating: " + Arrays.toString(startStateR));
            for (int i = 0; i < r; i++) {
                int tranIdx = tranStates[i];

                // skip if there are not enough chips
                if (startStateR[i] < sumrows[tranIdx]) {
                    continue;
                }
                int multiplier = startStateR[i] / sumrows[tranIdx];

                for (int j = 0; j < r; j++) {
                    int r_ij = m[tranIdx][tranStates[j]] * multiplier;
                    startStateR[j] += r_ij;
                }
                startStateR[i] -= sumrows[tranIdx] * multiplier;
                chipDealtTracker[i] += multiplier;
            }
            //System.out.println("move2 initiating: " + Arrays.toString(startStateR));

            int move_2_Diff = sumrows[u] - startStateR[u];
            if (move_2_Diff > 0) {
                startStateR[u] += move_2_Diff;
            }

            // rerun if critical loading hasn't been satisfied
        } while(!checkRState(startStateR, critLoading));

        int[] moveState = new int[m.length];
        for (int i = 0; i < r; i++) {
            int tranIdx = tranStates[i];
            int tranTracker = 0;
            for (int j = 0; j < m.length; j++) {
                if (tranTracker >= r || tranStates[tranTracker] != j) {
                    moveState[j] += m[tranIdx][j] * chipDealtTracker[i];
                }
            }
        }
        return moveState;
    }

    // O(r) where r<n, so around O(n)
    public static int[] getCritLoadingChips(int[] tranStates, int[] sumrows, final int u) {
        final int r = tranStates.length;
        int critChips[] = new int[r];
        for (int i = 0; i < r; i++) {
            int index = tranStates[i];
            critChips[i] = sumrows[index];

            //c_i = r_i - 1 unless i == u
            if (index != u) {
                critChips[i] -= 1;
            }
        }

        return critChips;
    }

    // O(n)
    public static boolean checkRState(int[] RState, int[] critLoading) {
        for (int i = 0; i < RState.length; i++) {
            // if critLoading cond isn't met for a transient state move holder
            if (RState[i] != critLoading[i]) {
                return false;
            }
        }

        return true;
    }

    //O(n-r) where r < n, so around O(n)
    public static int[] extrapolateSol(int[] finalState, int[] tranStates) {
        int[] sol = new int[finalState.length - tranStates.length + 1];
        int tranCounter = 1; // since we skip 0
        int solCounter = 0;
        int solsum = 0;

        for (int i = 1; i < finalState.length; i++) {
            if (tranCounter < tranStates.length &&
                    i == tranStates[tranCounter]) {

                tranCounter++;
            } else {
                sol[solCounter] = finalState[i];
                solsum += finalState[i];
                solCounter++;
            }
        }
        sol[sol.length - 1] = solsum;

        simplifySolution(sol);

        return sol;
    }

    public static void simplifySolution(int[] sol) {
        int gcd = 0;
        for (int i = 0; i < sol.length; i++) {
            gcd = findGCD(gcd, sol[i]);

            if (gcd == 1) {
                return;
            }
        }

        if (gcd == 0) {
            return;
        }

        for (int i = 0; i < sol.length; i++) {
            sol[i] = sol[i] / gcd;
        }
    }

    // Function to return gcd of a and b
    static int findGCD(int a, int b) {
        if (a == 0)
            return b;
        return findGCD(b % a, a);
    }

}