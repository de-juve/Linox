package workers;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 05.05.12
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */
public class ContrastCalculator {
    TreeMap<Integer,Double> contrast;
    private volatile static ContrastCalculator calculator;


    private ContrastCalculator() {
        contrast = new TreeMap<Integer, Double>();
    }

    public static ContrastCalculator getInstance() {
        if(calculator == null) {
            synchronized (ContrastCalculator.class) {
                if(calculator == null) {
                    calculator = new ContrastCalculator();
                }
            }
        }
        return calculator;
    }

    public TreeMap<Integer, Double> getContrast() {
        return contrast;
    }



    public double countContrast(int p, ArrayList<Integer> neigh, Integer[] luminance) {
        double contr = 0;
        for(Integer i : neigh) {
            contr += luminance[p] - luminance[i];
        }
        contr /= neigh.size();
        contrast.put(p, contr);

        return contr;
    }
}
