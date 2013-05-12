package plugins.nonlinearRegression;

import com.google.common.primitives.Doubles;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import jaolho.data.lma.LMA;
import jaolho.data.lma.LMAFunction;
import plugins.DataCollection;
import plugins.LuminanceCalculator;
import plugins.MyAPlugin;

import java.awt.*;
import java.util.ArrayList;

public class OLS extends MyAPlugin implements DialogListener {
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

        ArrayList<Double> xt = new ArrayList<>();
        ArrayList<Double> x = new ArrayList<>();
        ArrayList<Double> yt = new ArrayList<>();
        ArrayList<Double> y = new ArrayList<>();

        for(int i = 0; i < size; i++) {
            int id = DataCollection.INSTANCE.getLine().get(i);
            xt.add((double) i);
            x.add((double) id % width);
            yt.add((double) i);
            y.add((double) id / width);
        }

        double[] xArr = Doubles.toArray(x);
        double[] yArr = Doubles.toArray(y);
        double[] xtArr = Doubles.toArray(xt);
        double[] ytArr = Doubles.toArray(yt);
        double[] params = new double[1];
        double[] fitParamsX, fitParamsY ;
        LMAFunction lmaFunctionX = new PolynomFunction();
        LMAFunction lmaFunctionY = new PolynomFunction();

        switch (typeFunction) {
            case "polynomial" : {
                params = new double[polynomialDegree];
                for(int i = 0; i < polynomialDegree; i++) {
                    params[i] = 1;
                }
                lmaFunctionX = new PolynomFunction();
                lmaFunctionY = new PolynomFunction();
                break;
            }
            case "parabola" : {
                params = new double[3];
                for(int i = 0; i < 3; i++) {
                    params[i] = 1;
                }
                lmaFunctionX = new ParabolaFunction();
                lmaFunctionY = new ParabolaFunction();
                break;
            }
            case "sin" : {
                params = new double[2];
                for(int i = 0; i < 2; i++) {
                    params[i] = 1;
                }
                lmaFunctionX = new SinFunction();
                lmaFunctionY = new SinFunction();
                break;
            }
        }

        LMA lmaParX = new LMA(
                lmaFunctionX,
                params.clone(),
                new double[][] { xtArr, xArr}
        );

        LMA lmaParY = new LMA(
                lmaFunctionY,
                params,
                new double[][] { ytArr, yArr}
        );

        lmaParX.fit();
        lmaParY.fit();

        fitParamsX = lmaParX.parameters;
        fitParamsY = lmaParY.parameters;

        int[] xx = new int[size];
        int[] yy = new int[size];

        for(int i = 0; i < size; i++) {
            xx[i] = (int) lmaFunctionX.getY(i, fitParamsX);
            yy[i] = (int) lmaFunctionY.getY(i, fitParamsY);
            imageProcessor.set(xx[i], yy[i], Color.RED.getRGB());
        }
    }

    protected void showDialog(String name) {
        GenericDialog gd = new GenericDialog(name, IJ.getInstance());
        gd.addChoice("Type of function", new String[]{"polynomial", "parabola", "sin"}, "polynomial");
        gd.addSlider("Polynomial degree", 1, Math.min(12, DataCollection.INSTANCE.getLine().size()-1), 1);
        typeFunction = "polynomial";
        polynomialDegree = 1;
        gd.addDialogListener(this);

        gd.showDialog();
        if (gd.wasCanceled()) {
            exit = true;
            setErrMessage("canceled");
        }
    }

    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        typeFunction = gd.getNextChoice();
        polynomialDegree = (int) gd.getNextNumber();
        return true;
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
