package dodgethelasers;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.math.BigDecimal;
import java.math.BigInteger;

public class DodgeTheLasers {
    public static void main(String[] args) {
        //System.out.println(Solution.approximateSquareRoot(2).toString());

        System.out.println(Solution.solution("5"));
    }

}
class Solution {
    public static final BigInteger TWO = BigInteger.valueOf(2);

    public static String solution(String s) {
        // Your code here
        BigDecimal rMinusOne =
                new BigDecimal("0.4142135623730950488016887242096980785696718753769480731766797379907324784621070388503875343276415727350138462309122970249248360558507372126441214971");

        return beattySequence(rMinusOne, new BigInteger(s)).toString();
    }

    public static BigInteger beattySequence(BigDecimal rMinusOne, BigInteger n) {
        // i * sqrt(2) = 0
        if (n.equals(BigInteger.ZERO)) {
            return BigInteger.ZERO;
        }

        // n' = floor(sqrt(2) - 1)*n)
        BigInteger n1 = getN1(rMinusOne,n);

        // S(sqrt(2), n) = n*n1 + n(n+1)/2 - n1(n1+1)/2 - S(sqrt(2), n')
        BigInteger nextSum = beattySequence(rMinusOne, n1);

        BigInteger multiple1 = n.multiply(n1);
        BigInteger multiple2 = n.multiply(n.add(BigInteger.ONE)).divide(BigInteger.valueOf(2));
        BigInteger multiple3 = n1.multiply(n1.add(BigInteger.ONE)).divide(BigInteger.valueOf(2));

        // do the math and return the value
        return multiple1.add(multiple2).subtract(multiple3).subtract(nextSum);
    }

    public static BigInteger getN1(BigDecimal rMinusOne, BigInteger n) {
        //floor(sqrt(2) - 1)*n)
        return rMinusOne.multiply(new BigDecimal(n)).toBigInteger();
    }

    public static BigDecimal approximateSquareRoot(int y) {
        Fraction sqrt = new Fraction(2);
        //BigDecimal squareRoot = iteration(BigDecimal.ONE);
        for(int i = 0; i<=y;i++) {
            //squareRoot = iteration(squareRoot);
            sqrt = iteration(sqrt);
        }

        return sqrt.toBigDecimal();
    }

    public static BigDecimal iteration(BigDecimal prevIter) {
        BigDecimal BigDecimalTwo = BigDecimal.ONE.add(BigDecimal.ONE);
        return BigDecimalTwo.divide(prevIter).add(prevIter).divide(BigDecimalTwo);
    }

    public static Fraction iteration(Fraction prevIter) {
        Fraction two = new Fraction(2);
        two.divide(prevIter);
        two.add(prevIter);
        two.divide(new Fraction(2));

        return two;
    }

    public static class Fraction {
        public Integer numer;
        public Integer denom = Integer.valueOf(1);

        public Fraction(Integer numer, Integer denom) {
            this.numer = numer;
            this.denom = denom;
        }
        public Fraction(int num) {
            this.numer = Integer.valueOf(num);
        }

        public void add(Fraction f2) {
            this.numer *= f2.denom;
            this.numer += (f2.numer * this.denom);
            this.denom *= f2.denom;
        }

        public void divide(Fraction f2) {
            this.numer *= f2.denom;
            this.denom *= f2.numer;
        }

        public BigDecimal toBigDecimal() {
            BigDecimal numer = new BigDecimal(this.numer.toString());
            BigDecimal denom = new BigDecimal(this.denom.toString());

            System.out.println(numer);
            System.out.println(denom);

            return numer.divide(denom);
        }
    }
}