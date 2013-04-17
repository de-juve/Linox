package plugins;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;

import java.awt.*;
import java.util.LinkedList;
import java.util.ListIterator;

public class LineExtrapolation extends MyAPlugin implements DialogListener {
    SplineTuple[] splinesX, splinesY, splinesZ;
    String typeApproximation;

    @Override
    public ImagePlus getResult(boolean addToStack) {
        result = new ImagePlus("line extrapolation " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
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

        ShowStaticstics.showHistogram(DataCollection.INSTANCE.getLine(), false);
        ShowStaticstics.showLuminanceChanging(DataCollection.INSTANCE.getLine(), true);


        LinkedList<Integer> line = DataCollection.INSTANCE.getLine();
        LinkedList<Integer> x = new LinkedList<>();
        LinkedList<Integer> y = new LinkedList<>();
        LinkedList<Integer> z = new LinkedList<>();
        double[] arX = new double[line.size()];
        double[] arY = new double[line.size()];
        double[] arZ = new double[line.size()];
        double[] arT = new double[line.size()];

        int i = 0;
        ListIterator<Integer> iterator = line.listIterator();
        while (iterator.hasNext()) {
            if(typeApproximation.equals("lagrange") && iterator.nextIndex() < line.size() - 5) {
                iterator.next();
                continue;
            }

            int id = iterator.next();
            x.add(id%width);
            y.add(id/width);
            z.add(DataCollection.INSTANCE.getLuminance(id));

            arT[i] = i;
            arX[i] = id%width;
            arY[i] = id/width;
            arZ[i] = DataCollection.INSTANCE.getLuminance(id);
            i++;
        }

        if(typeApproximation.equals("lagrange")) {
            for(i = 0; i < x.size() + 10; i++) {
                int nx = lagrange(i, x);
                int ny = lagrange(i, y);
                int nz = lagrange(i, z);

                if(nx >= width || nx < 0 || ny >= height || ny < 0) {
                    continue;
                }
                int value = (((nz & 0xff) << 16) +
                        ((nz & 0xff) << 8) +
                        (nz & 0xff));
                System.out.println(i + ": " + x + " " + nx);
                System.out.println(i + ": " + y + " " + ny);
                System.out.println(i + ": " + z + " " + nz);
                System.out.println();

                imageProcessor.set(nx, ny, Color.RED.getRGB());

            }
        } else {
            splinesX = new SplineTuple[arT.length];
            splinesY = new SplineTuple[arT.length];
            splinesZ = new SplineTuple[arT.length];
            buildSpline(arT, arX, arT.length, splinesX);
            buildSpline(arT, arY, arT.length, splinesY);
            buildSpline(arT, arZ, arT.length, splinesZ);

            for(i = 0; i < x.size() + 10; i++) {

                int nx, ny, nz;
                try {
                    nx = (int) interpolateSpline(i, splinesX);
                    ny = (int) interpolateSpline(i, splinesY);
                    nz = (int) interpolateSpline(i, splinesZ);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                System.out.println(i + ": " + x + " " + nx);
                System.out.println(i + ": " + y + " " + ny);
                System.out.println(i + ": " + z + " " + nz);
                System.out.println();

                if(nx >= width || nx < 0 || ny >= height || ny < 0) {
                    continue;
                }
                int value = (((nz & 0xff) << 16) +
                        ((nz & 0xff) << 8) +
                        (nz & 0xff));
                imageProcessor.set(nx, ny, Color.RED.getRGB());

            }
        }

    }

    private Integer lagrange(Integer x, LinkedList<Integer> line) {
        Integer L;
        double l;

        L = 0;

        ListIterator<Integer> iterator = line.listIterator();
        while (iterator.hasNext()) {
            int j = iterator.nextIndex();

            l = 1;
            for(int i = 0; i < line.size(); i++) {
                if(i != j) {
                    l *= (x - i) * 1d / (j-i);
                }
            }
            L += (int) (iterator.next() * l);
        }

        return L;
    }

    private class SplineTuple {
        public double  a,b,c,d,x;
    }

    private void buildSpline(double[] x, double[] y, int n,  SplineTuple[] splines) {
        // Инициализация массива сплайнов
        //splines = new SplineTuple[n];
        for (int i = 0; i < n; ++i)
        {
            splines[i] = new SplineTuple();
            splines[i].x = x[i];
            splines[i].a = y[i];
        }
        splines[0].c = splines[n - 1].c = 0.0;

        // Решение СЛАУ относительно коэффициентов сплайнов c[i] методом прогонки для трехдиагональных матриц
        // Вычисление прогоночных коэффициентов - прямой ход метода прогонки
        double[] alpha = new double[n - 1];
        double[] beta  = new double[n - 1];
        alpha[0] = beta[0] = 0.0;
        for (int i = 1; i < n - 1; ++i)
        {
            double hi  = x[i] - x[i - 1];
            double hi1 = x[i + 1] - x[i];
            double A = hi;
            double C = 2.0 * (hi + hi1);
            double B = hi1;
            double F = 6.0 * ((y[i + 1] - y[i]) / hi1 - (y[i] - y[i - 1]) / hi);
            double z = (A * alpha[i - 1] + C);
            alpha[i] = -B / z;
            beta[i] = (F - A * beta[i - 1]) / z;
        }

        // Нахождение решения - обратный ход метода прогонки
        for (int i = n - 2; i > 0; --i)
        {
            splines[i].c = alpha[i] * splines[i + 1].c + beta[i];
        }

        // По известным коэффициентам c[i] находим значения b[i] и d[i]
        for (int i = n - 1; i > 0; --i)
        {
            double hi = x[i] - x[i - 1];
            splines[i].d = (splines[i].c - splines[i - 1].c) / hi;
            splines[i].b = hi * (2.0 * splines[i].c + splines[i - 1].c) / 6.0 + (y[i] - y[i - 1]) / hi;
        }
    }

    // Вычисление значения интерполированной функции в произвольной точке
    public double interpolateSpline(double x, SplineTuple[] splines) throws Exception {
        if (splines == null)
        {
            throw  new Exception("Doesn't exists any spline"); // Если сплайны ещё не построены - возвращаем NaN
        }

        int n = splines.length;
        SplineTuple s;

        if (x <= splines[0].x) // Если x меньше точки сетки x[0] - пользуемся первым эл-тов массива
        {
            s = splines[1];
        }
        else if (x >= splines[n - 1].x) // Если x больше точки сетки x[n - 1] - пользуемся последним эл-том массива
        {
            s = splines[n - 1];
        }
        else // Иначе x лежит между граничными точками сетки - производим бинарный поиск нужного эл-та массива
        {
            int i = 0;
            int j = n - 1;
            while (i + 1 < j)
            {
                int k = i + (j - i) / 2;
                if (x <= splines[k].x)
                {
                    j = k;
                }
                else
                {
                    i = k;
                }
            }
            s = splines[j];
        }

        double dx = x - s.x;
        // Вычисляем значение сплайна в заданной точке по схеме Горнера (в принципе, "умный" компилятор применил бы схему Горнера сам, но ведь не все так умны, как кажутся)
        return s.a + (s.b + (s.c / 2.0 + s.d * dx / 6.0) * dx) * dx;
    }

    protected void showDialog(String name) {
        GenericDialog gd = new GenericDialog(name, IJ.getInstance());
        gd.addChoice("Type of approximation", new String[]{"lagrange", "spline"}, "lagrange");
        typeApproximation = "lagrange";
        gd.addDialogListener(this);

        gd.showDialog();
        if (gd.wasCanceled()) {
            exit = true;
            return;
        }
    }

    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        typeApproximation = gd.getNextChoice();
        return true;
    }
}
