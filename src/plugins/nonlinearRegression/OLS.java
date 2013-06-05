package plugins.nonlinearRegression;

import gui.dialog.ChoiceDialog;
import gui.dialog.ParameterComboBox;
import gui.dialog.ParameterSlider;
import ij.ImagePlus;
import jaolho.data.lma.LMAFunction;
import plugins.DataCollection;
import plugins.LuminanceCalculator;
import plugins.MyAPlugin;
import plugins.snake.LinePoint;

import java.awt.*;
import java.util.LinkedList;

public class OLS extends MyAPlugin {
    private String typeFunction;
    private int polynomialDegree;

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
        showDialog("choose type");
        if(exit)   {
            return;
        }

        LuminanceCalculator luminanceCalculator = new LuminanceCalculator();
        luminanceCalculator.initProcessor(imageProcessor);
        luminanceCalculator.run();

        int size = DataCollection.INSTANCE.getLine().size();
        Regression regressionX = new Regression();
        Regression regressionY = new Regression();
        LinkedList<LinePoint> x = new LinkedList<>();
        LinkedList<LinePoint> y = new LinkedList<>();

        for(int i = 0; i < size; i++) {
            int id = DataCollection.INSTANCE.getLine().get(i);
            x.add(new LinePoint(i, id % width));
            y.add(new LinePoint(i, id / width));
        }

        regressionX.calcFitParams(x, typeFunction, polynomialDegree);
        regressionY.calcFitParams(y, typeFunction, polynomialDegree);

        int[] xx = new int[size + 40];
        int[] yy = new int[size + 40];

        for(int i = 0; i < size + 40; i++) {
            xx[i] = regressionX.getY(i);
            yy[i] = regressionY.getY(i);
            if(xx[i] >= width || xx[i] < 0|| yy[i] >= height || yy[i] < 0) {
                break;
            }
            imageProcessor.set(xx[i], yy[i], Color.RED.getRGB());
        }
    }

    protected void showDialog(String name) {
        ParameterComboBox typeFunctionParameter = new ParameterComboBox("Type of function", new String[]{"polynomial", "parabola", "sin"});
        ParameterSlider polynomDegreeSlider = new ParameterSlider("Polynom degree", 1, 12, 1);

        ChoiceDialog cd = new ChoiceDialog();
        cd.setTitle(name);
        cd.addParameterComboBox(typeFunctionParameter);
        cd.addParameterSlider(polynomDegreeSlider);

        cd.pack();
        cd.setVisible(true);
        if (cd.wasCanceled()) {
            exit = true;
            setErrMessage("canceled");
        }
        typeFunction = cd.getValueComboBox(typeFunctionParameter);
        polynomialDegree = cd.getValueSlider(polynomDegreeSlider);
    }

    public static class PolynomFunction extends LMAFunction {
        @Override
        public double getY(double x, double[] a) {
            Double accumulator = a[a.length-1];
            for (int i = a.length-2; i >= 0; i--) {
                accumulator = (accumulator * x) + a[i];
            }
            return accumulator;
        }

        @Override
        public double getPartialDerivate(double x, double[] a, int parameterIndex) {
            return Math.pow(x, parameterIndex);
        }
    }

    public static class ParabolaFunction extends LMAFunction {
        @Override
        public double getY(double x, double[] a) {
            return a[2] * x * x + a[1] * x + a[0];
        }
        @Override
        public double getPartialDerivate(double x, double[] a, int parameterIndex) {
            switch (parameterIndex) {
                case 0: return 1;
                case 1: return x;
                case 2: return x*x;
            }
            throw new RuntimeException("No such parameter index: " + parameterIndex);
        }
    }

    public static class SinFunction extends LMAFunction {
        @Override
        public double getY(double x, double[] a) {
            return a[0] * Math.sin(x / a[1]);
        }

        @Override
        public double getPartialDerivate(double x, double[] a, int parameterIndex) {
            switch (parameterIndex) {
                case 0: return Math.sin(x / a[1]);
                case 1: return a[0] * Math.cos(x / a[1]) * (-x / (a[1] * a[1]));
            }
            throw new RuntimeException("No such fit parameter: " + parameterIndex);
        }
    };
}
