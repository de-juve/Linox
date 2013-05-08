package plugins;

import ij.ImagePlus;
import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.awt.*;

public class OLS extends MyAPlugin {
    public OLS() {
        title = "OLS";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        result = new ImagePlus("OLS " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
        if(addToStack) {
            DataCollection.INSTANCE.addtoHistory(result);
        }

        return result;
    }

    @Override
    public void run() {
        LuminanceCalculator luminanceCalculator = new LuminanceCalculator();
        luminanceCalculator.initProcessor(imageProcessor);
        luminanceCalculator.run();

        int size = DataCollection.INSTANCE.getLine().size();
        SimpleRegression regressionX = new SimpleRegression();
        SimpleRegression regressionY = new SimpleRegression();

        for(int i = 0; i < size; i++) {
            int id = DataCollection.INSTANCE.getLine().get(i);
            double x = id % width;
            double y = id / width;
            regressionX.addData(i, x);
            regressionY.addData(i, y);
        }
      /*  System.out.println(regressionX.getIntercept());
        System.out.println(regressionX.getSlope());
        System.out.println(regressionX.getSlopeStdErr());*/


        for(int i = 0; i < size; i++) {
            int id = DataCollection.INSTANCE.getLine().get(i);
            double x = id % width;
            double y = id / width;
            int xx = (int)regressionX.predict(i);
            int yy = (int)regressionY.predict(i);
            System.out.println("x= " + x + " -> " + xx);
            System.out.println("y= " + y + " -> " + yy);
            imageProcessor.set(xx, yy, Color.RED.getRGB());
        }


       // RegressionResults results = regression.regress();

    }


}
