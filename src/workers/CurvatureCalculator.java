package workers;

import java.util.TreeMap;

public class CurvatureCalculator {
    TreeMap<Integer, Double> curvature;
    private volatile static CurvatureCalculator calculator;


    private CurvatureCalculator() {
        curvature = new TreeMap<Integer, Double>();
    }

    public static CurvatureCalculator getInstance() {
        if (calculator == null) {
            synchronized (CurvatureCalculator.class) {
                if (calculator == null) {
                    calculator = new CurvatureCalculator();
                }
            }
        }
        return calculator;
    }

    public TreeMap<Integer, Double> getCurvature() {
        return curvature;
    }


    public double countCurvature(int i, int prev, int follow, int width) {
        int x = i % width;
        int y = i / width;
        int xp = prev % width;
        int yp = prev / width;
        int xf = follow % width;
        int yf = follow / width;
        int dx = xf - xp;
        int dy = yf - yp;
        int d2x = xf - 2 * x + xp;
        int d2y = yf - 2 * y + yp;
        double k = Math.abs(dx * d2y - dy * d2x) / Math.sqrt(Math.pow(dx * dx + dy * dy, 3));
        //double curv =  Math.sqrt(Math.pow(yp - 2*y + yf, 2) + Math.pow(xp - 2*x + xf, 2));
        curvature.put(i, k);

        return k;
    }

    public void setZeroCurvature(Integer i) {
        curvature.put(i, 0D);
    }
}
