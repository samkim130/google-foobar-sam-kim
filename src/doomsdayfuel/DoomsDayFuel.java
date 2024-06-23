package doomsdayfuel;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
//import java.util,

public class DoomsDayFuel {
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

        System.out.println(Arrays.toString(Solution.solution(m)));


        int[][] m0 = { {0} };
        System.out.println(Arrays.toString(Solution.solution(m0)));
        //int[][] m2 = {{0, 2, 1, 0, 0}, {0, 0, 0, 3, 4}, {0, 0, 0, 0, 0}, {0, 0, 0, 0,0}, {0, 0, 0, 0, 0}};
        //System.out.println(Arrays.toString(doomsdayfuel.Solution.solution(m2)));


        int[][] m3 = {{8, 0, 0, 0, 0}, {0, 0, 0, 3, 4}, {0, 0, 0, 0, 0}, {0, 0, 0, 0,0}, {0, 0, 0, 0, 0}};
        System.out.println(Arrays.toString(Solution.solution(m3)));

        //int[][] m4 = {{0, 0, 0, 8, 0}, {0, 0, 0, 3, 4}, {0, 0, 0, 0, 0}, {0, 0, 0, 0,0}, {0, 0, 0, 0, 0}};
        //System.out.println(Arrays.toString(doomsdayfuel.Solution.solution(m4)));

        int[][] m5 = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0,0}, {0, 0, 0, 0, 0}};
        System.out.println(Arrays.toString(Solution.solution(m5)));

        //int[][] m6 = {{0, 1, 4, 5, 2}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0,0}, {0, 0, 0, 0, 0}};
        //System.out.println(Arrays.toString(doomsdayfuel.Solution.solution(m6)));


        //int[][] m7 = { {1, 1, 1, 1, 1,},  {0, 0, 0, 0, 0,}, { 1, 1, 1, 1, 1,}, {0, 0, 0, 0, 0,}, {1, 1, 1, 1, 1,} };
        //System.out.println(Arrays.toString(doomsdayfuel.Solution.solution(m7)));

        int[][] m8 =  {
                { 0,1,0,3,0,1,3 },
                { 4,12,0,3,2,0,2 },
                { 0,4,3,0,0,21,1 },
                { 3,3,0,0,2,0,3 },
                { 0,0,0,0,0,0,0 },
                { 1,0,1,0,7,9,3 },
                { 0,0,0,0,0,0,0 },
        };
        System.out.println(Arrays.toString(Solution.solution(m8)));
    }
}

class Solution {
    public static int[] solution(int[][] m) {
        // fill in tracking variables
        int[] sumrows = new int[m.length];
        int numTermStates = initSumrows(m, sumrows);
        // fill in tracking variables
        int[] termStates = new int[numTermStates];
        int[] tranStates = new int[m.length - numTermStates];
        initTrackingArrs(sumrows, termStates, tranStates);

        // check for edge cases,
        // esp since algorithm wouldn't work unless state0 is transient
        if (numTermStates == m.length || sumrows[0] == 0) {
            // 1. all are terminal cases
            // 2. row 0 is terminal case
            //System.out.println("edge 1 or 2 triggered");
            int[] sol = new int[numTermStates + 1];
            sol[0] = 1;
            sol[numTermStates] = 1;
            return sol;
        } else if (sumrows[0] == m[0][0] ||
                (numTermStates == m.length - 1 && sumrows[0] > 0)) {
            // 3. row 0 is an absorbing row (just keeps returning to itself)
            // 4. all rows minus row 0 is terminal
            return extrapolateSol(m[0], tranStates);
        } else if (numTermStates == 1) {
            // 5. saves computation
            int[] sol = {1, 1};
            return sol;
        }

        // get the matrix prior to inversing
        MatrixRepresentation preInvFundMtx = getPreInvFundMatrix(m, tranStates, sumrows);



/*
        // Your code here
        ArrayList<Integer> termStateList = new ArrayList<Integer>();
        ArrayList<Integer> nonTermStateList = new ArrayList<Integer>();
        ArrayList<Integer> stateDenominatorList = new ArrayList<Integer>();
        for (int i = 0; i < m.length; i++) {
            boolean allZeroInState = true;
            int stateDenominatorTemp = 0;
            // loop through probability of all states for a particular state
            for (int j = 0; j < m[0].length; j++) {
                if (m[i][j] != 0) {
                    allZeroInState = false;
                    stateDenominatorTemp += m[i][j];
                }
            }
            if (allZeroInState) {
                termStateList.add(i);
            } else {
                nonTermStateList.add(i);
                stateDenominatorList.add(stateDenominatorTemp);
            }
        }
        ////system.out.println(Arrays.toString(termStateList.toArray()));
        ////system.out.println(Arrays.toString(nonTermStateList.toArray()));
        ////system.out.println(Arrays.toString(stateDenominatorList.toArray()));

        // Create I 0 R Q matrix -- may not need
        Fraction one = new Fraction(1);
        Fraction zero = new Fraction(0);

        // Create I
        ArrayList<ArrayList<Fraction>> IList = new ArrayList<ArrayList<Fraction>>();
        for (int i = 0; i < nonTermStateList.size(); i++) {
            ArrayList<Fraction> IRow = new ArrayList<Fraction>();
            for (int j = 0; j < nonTermStateList.size(); j++) {
                if (i==j) {
                    IRow.add(one);
                } else {
                    IRow.add(zero);
                }
            }
            IList.add(IRow);
        }
        Matrix I = new Matrix(IList, nonTermStateList.size(), nonTermStateList.size());
        //system.out.println("I:");
        I.print();

        // Create Q
        ArrayList<ArrayList<Fraction>> QList = new ArrayList<ArrayList<Fraction>>();
        for (int i = 0; i < nonTermStateList.size(); i++) {
            ArrayList<Fraction> QRow = new ArrayList<Fraction>();
            for (int j = 0; j < nonTermStateList.size(); j++) {
                QRow.add(new Fraction(m[nonTermStateList.get(i)][nonTermStateList.get(j)], stateDenominatorList.get(i)));
            }
            QList.add(QRow);
        }

        Matrix Q = new Matrix(QList, nonTermStateList.size(), nonTermStateList.size());
        //system.out.println("Q:");
        Q.print();

        // Create R
        ArrayList<ArrayList<Fraction>> RList = new ArrayList<ArrayList<Fraction>>();
        for (int i = 0; i < nonTermStateList.size(); i++) {
            ArrayList<Fraction> RRow = new ArrayList<Fraction>();
            for (int j = 0; j < termStateList.size(); j++) {
                RRow.add(new Fraction(m[nonTermStateList.get(i)][termStateList.get(j)], stateDenominatorList.get(i)));
            }
            RList.add(RRow);
        }

        Matrix R = new Matrix(RList, nonTermStateList.size(), termStateList.size());
        //system.out.println("R:");
        R.print();

        // Find I - Q
        Matrix IminusQ = I.minus(Q);
        //system.out.println("IminusQ:");
        IminusQ.print();
        // Find F = (I - Q)^-1
        Matrix F = IminusQ.getInverseMatrix();
        //system.out.println("F:");
        F.print();
        // Find FR
        Matrix FR = F.multiply(R);
        //system.out.println("FR:");
        FR.print();
        // Take the first row of FR
        ArrayList<Fraction> FRRow = FR.getRow(0);
        ArrayList<Fraction> numeratorList = new ArrayList<Fraction>(); // numeratorList
        int[] denomList = new int[FRRow.size()]; // denomList
        // Find the numerators and the common denominator, make it an array
        for (int i = 0; i < FRRow.size(); i++) {
            denomList[i] = FRRow.get(i).getDenominator();
            numeratorList.add(FRRow.get(i));
        }
        int lcm = getLcm(denomList);
        int[] result = new int[FRRow.size()+1];
        for (int j = 0; j < result.length-1; j++) {
            numeratorList.set(j, numeratorList.get(j).multiply(new Fraction(lcm)));
            result[j] = numeratorList.get(j).getNumerator();
        }
        result[FRRow.size()] = lcm;
        //system.out.println(Arrays.toString(result));

        return result;
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
    // O(n)
    public static void initTrackingArrs(int[] sumrows, int[] termStates, int []tranStates) {
        int termCounter = 0, tranCounter = 0;
        for (int i = 0; i < sumrows.length; i++) {
            if (sumrows[i] == 0) {
                termStates[termCounter++] = i;
            } else {
                tranStates[tranCounter++] = i;
            }
        }
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


    public static MatrixRepresentation getPreInvFundMatrix(int[][] m, int[] tranStates, int[] sumrows) {
        MatrixRepresentation mtx = new MatrixRepresentation(tranStates.length);
        Matx matx = new Matx(tranStates.length);

        for (int i = 0; i < mtx.len;i++) {
            int i_idx = tranStates[i];

            List<Frac> row = new ArrayList<>();
            for(int j = 0; j < mtx.len; j++) {
                int j_idx = tranStates[j];

                int denom = sumrows[i_idx];
                int numer = 0 - m[i_idx][j_idx];

                mtx.denom[i][j] = sumrows[i_idx];
                if (i==j) {
                    mtx.numer[i][j] = sumrows[i_idx];
                    numer += denom;
                }
                mtx.numer[i][j] -= m[i_idx][j_idx];

                row.add(new Frac(numer,denom));
            }
            matx.mat.add(row);
        }

        return mtx;
    }

    private static class MatrixRepresentation {
        public final int len;
        public int[][] numer;
        public int[][] denom;
        public int determ_num;
        public int determ_denom = 1;

        public MatrixRepresentation(int len) {
            this.len = len;
            numer = new int[len][len];
            denom = new int[len][len];
        }



        public void calculateDeterminant(int n) {
            if (len == 1) {
                determ_num = numer[0][0];
                determ_denom = denom[0][0];
                return;
            }

            for (int i = n; i > 0; i--) {

            }

        }
    }

    private static class Matx {
        public final int len;
        public List<List<Frac>> mat;
        public final Frac determ;
        public List<List<Frac>> inv;

        public Matx(int len) {
            this.len = len;
            mat = new ArrayList<>();
            determ = calculateDeterm(len, -1);
            inv = new ArrayList<>();
        }

        private Frac cofactor(int i, int j) {

        }

        private Frac calculateDeterm(int n, int k) {
            if (n == 1) {
                return mat.get(0).get(0);
            }

            Frac determinant = new Frac(0,1);
            //for (int i = n - 1; i >= 0; i--) {
            int count = 0;
                for (int j = 0; j < n; j--) {
                    if (j == k) {
                        continue;
                    }

                    int cofactor_sign = ((n - 1 + j) % 2 == 0) ? 1 : -1;
                    Frac pos = mat.get(n - 1).get(j);
                    Frac cofac = new Frac(pos.numer * cofactor_sign, pos.denom);
                    determinant.addition( cofac.multiply( calculateDeterm(n - 1, j) );
                    count++;
                }
            //}

            return determinant;
        }
    }

    private static class Frac {
        public int numer;
        public int denom;
        public Frac(int numer, int denom) {
            this.numer = numer;
            this.denom = denom;
        }

        public Frac multiply(Frac frac) {
            numer *= frac.numer;
            denom *= frac.denom;

            int gcd = findGCD(numer, denom);
            if (gcd > 0) {
                numer /= gcd;
                denom /= gcd;
            }

            return this;
        }

        public Frac addition(Frac frac) {
            numer *= frac.denom;
            numer += (frac.numer * denom);
            denom *= frac.denom;

            int gcd = findGCD(numer, denom);
            if (gcd > 0) {
                numer /= gcd;
                denom /= gcd;
            }
            return this;
        }
    }

    public static int getLcm(int arr[]) {
        int max = 0;
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            if (max < arr[i]) {
                max = arr[i];
            }
        }
        int res = 1;
        int factor = 2;
        while (factor <= max) {
            ArrayList<Integer> arrIndex = new ArrayList<Integer>();
            for (int j = 0; j < n; j++) {
                if (arr[j] % factor == 0) {
                    arrIndex.add(arrIndex.size(), j);
                }
            }
            if (arrIndex.size() >= 2) {
                // Reduce all array elements divisible
                // by factor.
                for (int j = 0; j < arrIndex.size(); j++) {
                    arr[arrIndex.get(j)] /= factor;
                }

                res *= factor;
            } else {
                factor++;
            }
        }

        // Then multiply all reduced array elements
        for (int i = 0; i < n; i++) {
            res *= arr[i];
        }

        return res;
    }

    private static class Matrix {

        private final int M;
        private final int N;
        private final Fraction det;
        private ArrayList<ArrayList<Fraction>> matrix;
        private ArrayList<ArrayList<Fraction>> inverseMatrix;

        public Matrix(ArrayList<ArrayList<Fraction>> mat, int m, int n) {
            this.matrix = mat;
            this.M = m;
            this.N = n;
            this.det = this.determinant(mat, n);
            this.inverseMatrix = this.inverse();
        }

        private void getCofactor(ArrayList<ArrayList<Fraction>> mat, ArrayList<ArrayList<Fraction>> tempMat, int p, int q, int n) {
            int i = 0;
            int j = 0;
            for (int row = 0; row < n; row++) {
                for (int col = 0; col < n; col++) {
                    if (row != p && col != q) {
                        tempMat.get(i).set(j++, mat.get(row).get(col));
                        if (j == n - 1) {
                            j = 0;
                            i++;
                        }
                    }
                }
            }
        }

        private Fraction determinant(ArrayList<ArrayList<Fraction>> mat, int n) {
            Fraction ans = new Fraction(0, 1);
            if (this.M != this.N) {
                return ans;
            }
            if (n == 1) {
                return mat.get(0).get(0);
            }
            ArrayList<ArrayList<Fraction>> tempMat = new ArrayList<ArrayList<Fraction>>();
            // Init 2d fraction arraylist
            for (int i = 0; i < this.M; i++) {
                ArrayList<Fraction> tempMatRow = new ArrayList<Fraction>();
                for (int j = 0; j < this.N; j++) {
                    tempMatRow.add(new Fraction(0, 1));
                }
                tempMat.add(tempMatRow);
            }

            int sign = 1;
            Fraction signFraction = new Fraction(sign, 1);
            for (int k = 0; k < n; k++) {
                this.getCofactor(mat, tempMat, 0, k, n);
                ans = ans.plus(signFraction.multiply(mat.get(0).get(k).multiply(determinant(tempMat, n - 1))));
                sign = -sign;
                signFraction = new Fraction(sign, 1);
            }
            return ans;
        }

        private void adjoint(ArrayList<ArrayList<Fraction>> mat, ArrayList<ArrayList<Fraction>> adj) {
            if (this.N == 1) {
                adj.get(0).set(0, new Fraction(1, 1));
                return;
            }
            int sign = 1;

            ArrayList<ArrayList<Fraction>> tempMat = new ArrayList<ArrayList<Fraction>>();
            // Init 2d fraction arraylist
            for (int i = 0; i < this.N; i++) {
                ArrayList<Fraction> tempMatRow = new ArrayList<Fraction>();
                for (int j = 0; j < this.N; j++) {
                    tempMatRow.add(new Fraction(0, 1));
                }
                tempMat.add(tempMatRow);
            }

            for (int p = 0; p < this.N; p++) {
                for (int q = 0; q < this.N; q++) {
                    this.getCofactor(mat, tempMat, p, q, this.N);
                    sign = ((p + q) % 2 == 0) ? 1 : -1;
                    Fraction signFraction = new Fraction(sign, 1);
                    adj.get(q).set(p, signFraction.multiply((this.determinant(tempMat, this.N - 1))));
                }
            }
        }

        private ArrayList<ArrayList<Fraction>> inverse() {
            ArrayList<ArrayList<Fraction>> inv = new ArrayList<ArrayList<Fraction>>();
            // Init 2d fraction arraylist
            for (int i = 0; i < this.M; i++) {
                ArrayList<Fraction> invRow = new ArrayList<Fraction>();
                for (int j = 0; j < this.N; j++) {
                    invRow.add(new Fraction(0, 1));
                }
                inv.add(invRow);
            }

            if (this.det.equals(new Fraction(0))) {
                return inv;
            }

            ArrayList<ArrayList<Fraction>> adj = new ArrayList<ArrayList<Fraction>>();
            // Init 2d fraction arraylist
            for (int i = 0; i < this.M; i++) {
                ArrayList<Fraction> adjRow = new ArrayList<Fraction>();
                for (int j = 0; j < this.N; j++) {
                    adjRow.add(new Fraction(0, 1));
                }
                adj.add(adjRow);
            }

            adjoint(this.matrix, adj);
            for (int p = 0; p < this.N; p++) {
                for (int q = 0; q < this.N; q++) {
                    Fraction temp = adj.get(p).get(q).dividedBy(this.det);
                    inv.get(p).set(q, temp);
                }
            }
            return inv;
        }

        public Matrix getInverseMatrix() {
            if (this.M != this.N) {
                //system.out.println("No inverse matrix for non-square matrices");
            }
            return new Matrix(this.inverseMatrix, this.M, this.N);
        }

        public Fraction getElement(int m, int n) {
            return this.matrix.get(m).get(n);
        }

        public ArrayList<Fraction> getRow(int m) {
            if (m <= this.M) {
                return this.matrix.get(m);
            }
            return new ArrayList<Fraction>();
        }

        public Matrix plus(Matrix mat) {
            int M_m = mat.getDimension()[0];
            int N_m = mat.getDimension()[1];
            if (this.M != M_m || this.N != N_m) {
                //system.out.println("Error in plus: Dimensions of two matrices are not equal!"); // Debug
                return mat;
            } else {
                ArrayList<ArrayList<Fraction>> sum = new ArrayList<ArrayList<Fraction>>();
                // Init 2d fraction arraylist
                for (int i = 0; i < this.M; i++) {
                    ArrayList<Fraction> sumRow = new ArrayList<Fraction>();
                    for (int j = 0; j < this.N; j++) {
                        sumRow.add(new Fraction(0, 1));
                    }
                    sum.add(sumRow);
                }
                for (int i = 0; i < this.M; i++) {
                    for (int j = 0; j < this.N; j++) {
                        // sum[i][j] = this.matrix[i][j] + mat.getElement(i, j);
                        sum.get(i).set(j, this.matrix.get(i).get(j).plus(mat.getElement(i, j)));
                    }
                }
                return new Matrix(sum, this.M, this.N);
            }
        }

        public Matrix minus(Matrix mat) {
            int M_m = mat.getDimension()[0];
            int N_m = mat.getDimension()[1];
            if (this.M != M_m || this.N != N_m) {
                //system.out.println("Error in minus: Dimensions of two matrices are not equal!"); // Debug
                return mat;
            } else {
                ArrayList<ArrayList<Fraction>> difference = new ArrayList<ArrayList<Fraction>>();
                // Init 2d fraction arraylist
                for (int i = 0; i < this.M; i++) {
                    ArrayList<Fraction> differenceRow = new ArrayList<Fraction>();
                    for (int j = 0; j < this.N; j++) {
                        differenceRow.add(new Fraction(0, 1));
                    }
                    difference.add(differenceRow);
                }
                for (int i = 0; i < this.M; i++) {
                    for (int j = 0; j < this.N; j++) {
                        // difference[i][j] = this.matrix[i][j] + mat.getElement(i, j);
                        difference.get(i).set(j, this.matrix.get(i).get(j).minus(mat.getElement(i, j)));
                    }
                }
                return new Matrix(difference, this.M, this.N);
            }
        }

        public Matrix multiply(Matrix mat) {
            // M N M N
            // X(m, n) x Y(n, p) = Z(m, p)
            int M_m = mat.getDimension()[0];
            int p_m = mat.getDimension()[1];
            if (this.N != M_m) {
                //system.out.println("Error in multiply: Dimensions of two matrices are valid for cross multiplication!"); // Debug
                return mat;
            } else {
                ArrayList<ArrayList<Fraction>> product = new ArrayList<ArrayList<Fraction>>();
                // Init 2d fraction arraylist
                for (int i = 0; i < this.M; i++) {
                    ArrayList<Fraction> productRow = new ArrayList<Fraction>();
                    for (int j = 0; j < p_m; j++) {
                        productRow.add(new Fraction(0, 1));
                    }
                    product.add(productRow);
                }
                for (int i = 0; i < this.M; i++) {
                    for (int j = 0; j < p_m; j++) {
                        for (int k = 0; k < this.N; k++) {
                            // product[i][j] += matrix[i][k] * mat.getElement(k, j);
                            Fraction temp = product.get(i).get(j);
                            product.get(i).set(j, temp.plus(this.matrix.get(i).get(k).multiply(mat.getElement(k, j))));
                        }
                    }
                }
                return new Matrix(product, this.M, p_m);
            }

        }

        public int[] getDimension() {
            return new int[] { this.M, this.N };
        }

        public void print() {
            for (int i = 0; i < this.M; i++) {
                for (int j = 0; j < this.N; j++) {
                    //system.out.print(this.matrix.get(i).get(j).toString() + "  ");
                }
                //system.out.println();
            }
        }

        public void printInverse() {
            if (this.M != this.N) {
                //system.out.println("No inverse matrix for non-square matrices");
                return;
            }
            if (this.det.equals(new Fraction(0))) {
                //system.out.println("Singular matrix, can't find its inverse");
                return;
            }
            for (int i = 0; i < this.M; i++) {
                for (int j = 0; j < this.N; j++) {
                    //system.out.print(this.inverseMatrix.get(i).get(j).toString() + "  ");
                }
                //system.out.println();
            }
        }

    }

    private static class Fraction {

        private int numerator;
        private int denominator = 1;
        private boolean sign = false; // true = negative, false = positive

        public Fraction(int num, int denom) {
            this.numerator = num;
            if (denom == 0) {
                //system.out.println("Denominator cannot be 0. Setting it to 1");
            } else {
                this.denominator = denom;
            }
            this.simplify();
        }

        public Fraction(int num) {
            this.numerator = num;
            this.simplify();
        }

        private int getGcm(int num1, int num2) {
            return num2 == 0 ? num1 : this.getGcm(num2, num1 % num2);
        }

        // Simplify fraction to simplest form, runs in constructor
        public void simplify() {
            this.sign = !(this.numerator <= 0 && this.denominator <= 0) && !(this.numerator >= 0 && this.denominator >= 0);

            this.numerator = Math.abs(this.numerator);
            this.denominator = Math.abs(this.denominator);

            int gcm = this.getGcm(this.numerator, this.denominator);
            this.numerator = this.numerator / gcm;
            this.denominator = this.denominator / gcm;
            // When fraction is zero, make sure denominator is one and no negative sign
            if (this.numerator == 0 && this.denominator != 0) {
                this.denominator = 1;
                this.sign = false;
            }
        }

        public Fraction plus(Fraction f1) {
            int num = 0;
            if (this.sign) { // this fraction is negative
                if (f1.getSign()) { // f1 is negative
                    num = (-1) * this.numerator * f1.denominator + this.denominator * (-1) * f1.numerator;
                } else { // f1 is positive
                    num = (-1) * this.numerator * f1.denominator + this.denominator * f1.numerator;
                }
            } else { // this fraction is positive
                if (f1.getSign()) { // f1 is negative
                    num = this.numerator * f1.denominator + this.denominator * (-1) * f1.numerator;
                } else { // f1 is positive
                    num = this.numerator * f1.denominator + this.denominator * f1.numerator;
                }
            }
            int denom = this.denominator * f1.getDenominator();
            return new Fraction(num, denom);
        }

        public Fraction minus(Fraction f1) {
            int num = 0;
            if (this.sign) { // this fraction is negative
                if (f1.getSign()) { // f1 is negative
                    num = (-1) * this.numerator * f1.denominator + this.denominator * f1.numerator;
                } else { // f1 is positive
                    num = (-1) * this.numerator * f1.denominator - this.denominator * f1.numerator;
                }
            } else { // this fraction is positive
                if (f1.getSign()) { // f1 is negative
                    num = this.numerator * f1.denominator + this.denominator * f1.numerator;
                } else { // f1 is positive
                    num = this.numerator * f1.denominator - this.denominator * f1.numerator;
                }
            }
            int denom = this.denominator * f1.getDenominator();
            return new Fraction(num, denom);
        }

        public Fraction multiply(Fraction f1) {
            int signInt = 1;
            // Either one fraction is negative will make the product fraction negative, but not for both fractions are negative.
            if (this.sign && !f1.getSign() || !this.sign && f1.getSign()) {
                signInt = -1;
            }
            return new Fraction(signInt * this.numerator * f1.getNumerator(), this.denominator * f1.getDenominator());
        }

        public Fraction dividedBy(Fraction f1) {
            int signInt = 1;
            // Either one fraction is negative will make the product fraction negative, but not for both fractions are negative.
            if (this.sign && !f1.getSign() || !this.sign && f1.getSign()) {
                signInt = -1;
            }
            return new Fraction(signInt *this.numerator * f1.getDenominator(), this.denominator * f1.getNumerator());
        }

        public boolean equals(Fraction f1) {
            return this.numerator == f1.getNumerator() && this.denominator == f1.getDenominator() && this.sign == f1.getSign();
        }

        public int getNumerator() {
            return this.numerator;
        }

        public int getDenominator() {
            return this.denominator;
        }

        public boolean getSign() {
            return this.sign;
        }

        public String toString() {
            String signStr = "";
            String fractionStr = "";
            if (this.sign) {
                signStr = "-";
            }
            if (numerator == denominator) {
                fractionStr = "1";
            } else if (denominator == 1) {
                fractionStr = Integer.toString(numerator);
            } else {
                fractionStr = numerator + "/" + denominator;
            }
            return signStr + fractionStr;
        }
    }
    */
}
