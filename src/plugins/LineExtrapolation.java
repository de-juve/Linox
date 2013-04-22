package plugins;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import plugins.snake.LinePoint;
import plugins.snake.Penalty;
import plugins.snake.Snake;
import road.Direction;
import workers.PixelsMentor;

import java.awt.*;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeMap;

public class LineExtrapolation extends MyAPlugin implements DialogListener {
    SplineTuple[] splinesX, splinesY;
    String typeApproximation;
    int deviation, penaltyThreshold;
    Snake<LinePoint> snakeX, snakeY, snakeZ, snake;
    LinkedList<Integer> extrapolateLine;
    Penalty penalty;

    private TreeMap<Integer, LinkedList<LinePoint>> recoveryPointMap;


    public LineExtrapolation() {
        title = "Line approximation";
    }

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

        LinkedList<LinePoint> line = new LinkedList<>();
        for(int i = 0; i < DataCollection.INSTANCE.getLine().size(); i++) {
            line.addFirst(new LinePoint(i, DataCollection.INSTANCE.getLine().get(i)));
        }

        recoveryPointMap = new TreeMap<>();
        snake = new Snake<>(line, line);

        extrapolateLine = new LinkedList<>();
        penalty = new Penalty(penaltyThreshold);

        extrapolateLine();

        if(exit)   {
            return;
        }

        System.out.println("line " + line.size() + " extr " + extrapolateLine.size() +
                "\r\nline " + line +
                "\r\nextr " + extrapolateLine);

        for(Integer id : extrapolateLine) {
            imageProcessor.set(id, Color.RED.getRGB());
        }
    }

    private void extrapolateLine() {
        int nx, ny, nz;
        snakeX = new Snake<>();
        snakeY = new Snake<>();
        snakeZ = new Snake<>();

        ShowStaticstics.showHistogram(snake.getTailValues(), false);
        ShowStaticstics.showLuminanceChanging(snake.getTailValues(), true);

        defineHeadPoints(snake.getHead());

        if(typeApproximation.equals("spline")) {
            splinesX = new SplineTuple[snake.getTail().size()];
            splinesY = new SplineTuple[snake.getTail().size()];

            buildSpline(snakeX.getHead(), splinesX);
            buildSpline(snakeY.getHead(), splinesY);

            nx = moveSpline(snakeX, splinesX);
            ny = moveSpline(snakeY, splinesY);
            if(exit)   {
                return;
            }
        } else {
            nx = moveLagrange(snakeX);
            ny = moveLagrange(snakeY);
        }

        if(nx >= width || nx < 0 || ny >= height || ny < 0) {
            System.out.println("Out of bouds (" + nx + ", " + ny + ")");
            return;
        }

        int id = nx + ny*width;

        nz = getLuminance(id);

        if(nz < 0) {
            //recover
            snake.setHead(recoveryPointMap.get(0));
            //see neighbour
            int lastId = extrapolateLine.indexOf(extrapolateLine.getLast());
            Direction d = Direction.defineDirection(extrapolateLine.get(lastId-1), extrapolateLine.get(lastId), width);
            id = PixelsMentor.defineNeighbourId(extrapolateLine.get(lastId), d, width, height);
            nz = getLuminance(id);
        } else {
            snake.addElementToHead(new LinePoint(snake.getHead().size(), id));
        }
        //extrapolateLine.add(id);
    }

    private int moveLagrange(Snake<LinePoint> snake) {
        double i = snake.getHead().getLast().getX() + snake.getStep();
        return lagrange(i, snake.getHead());
    }

    private int moveSpline(Snake<LinePoint> snake, SplineTuple[] splines) {
        double i = snake.getHead().getLast().getX() + snake.getStep();
        return (int) interpolateSpline(i, splines);
    }

    private int getLuminance(int id) {
        if (Math.abs(DataCollection.INSTANCE.getLuminance(id) - ShowStaticstics.mean) >= deviation) {
            penalty.addPenalty(Math.abs(DataCollection.INSTANCE.getLuminance(id) - ShowStaticstics.mean));
            if(penalty.countPenalty() > penalty.getThreshold()) {
                return -1;
            }
            //recoveryPointMap.put(penalty, new LinkedList<>(extrapolateLine));
        }
        return DataCollection.INSTANCE.getLuminance(id);
    }

    private void defineHeadPoints(LinkedList<LinePoint> line) {
        int id;
        double i = 0;
        ListIterator<LinePoint> iterator = line.listIterator();
        while (iterator.hasNext()) {
            if(typeApproximation.equals("lagrange") && iterator.nextIndex() < line.size() - 5) {
                iterator.next();
                continue;
            }
            LinePoint point = iterator.next();
            id = point.getY();
            snakeX.addElement(new LinePoint(i, id%width));
            snakeY.addElement(new LinePoint(i, id/width));
            snakeZ.addElement(new LinePoint(i,DataCollection.INSTANCE.getLuminance(id)));
            i++;
        }
    }

    private Integer lagrange(double x, LinkedList<LinePoint> line) {
        Integer L;
        double l;
        L = 0;

        for(int j = 0; j < line.size(); j++) {
            l = 1;
            for(int i = 0; i < line.size(); i++) {
                if(i != j) {
                    l *= (x - line.get(i).getX()) / (line.get(j).getX() - line.get(i).getX());
                }
            }
            L += (int) (line.get(j).getY() * l);
        }

        return L;
    }

    private class SplineTuple {
        public double  a,b,c,d,x;
    }

    private void buildSpline(LinkedList<LinePoint> line, SplineTuple[] splines) {
        int n = line.size();
        // Инициализация массива сплайнов
        //splines = new SplineTuple[n];
        for (int i = 0; i < n; ++i)
        {
            splines[i] = new SplineTuple();
            splines[i].x = line.get(i).getX();//x.get(i);
            splines[i].a = line.get(i).getY();//y.get(i);
        }
        splines[0].c = splines[n - 1].c = 0.0;

        // Решение СЛАУ относительно коэффициентов сплайнов c[i] методом прогонки для трехдиагональных матриц
        // Вычисление прогоночных коэффициентов - прямой ход метода прогонки
        double[] alpha = new double[n - 1];
        double[] beta  = new double[n - 1];
        alpha[0] = beta[0] = 0.0;
        for (int i = 1; i < n - 1; ++i)
        {
            double hi  = line.get(i).getX() - line.get(i-1).getX();//x.get(i) - x.get(i - 1);
            double hi1 = line.get(i+1).getX() - line.get(i).getX();//x.get(i + 1) - x.get(i);
            double A = hi;
            double C = 2.0 * (hi + hi1);
            double B = hi1;
            double F = 6.0 * ((line.get(i+1).getY() - line.get(i).getY()) / hi1 - (line.get(i).getY() - line.get(i-1).getY()) / hi);//((y.get(i + 1) - y.get(i)) / hi1 - (y.get(i) - y.get(i - 1)) / hi);
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
            double hi = line.get(i).getX() - line.get(i-1).getX();//x.get(i) - x.get(i - 1);
            splines[i].d = (splines[i].c - splines[i - 1].c) / hi;
            splines[i].b = hi * (2.0 * splines[i].c + splines[i - 1].c) / 6.0 + (line.get(i).getY() - line.get(i-1).getY()) / hi;//(y.get(i) - y.get(i - 1)) / hi;
        }
    }

    // Вычисление значения интерполированной функции в произвольной точке
    public double interpolateSpline(double x, SplineTuple[] splines) {
        if (splines == null)
        {
            exit = true;
            setErrMessage("Doesn't exists any spline");
            return -1;
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
        gd.addSlider("Count of penaltys", 0, 10, 3);
        gd.addSlider("Luminance deviation", 0, 50, 10);
        gd.addChoice("Type of approximation", new String[]{"lagrange", "spline"}, "lagrange");
        penaltyThreshold = 3;
        deviation = 10;
        typeApproximation = "lagrange";
        gd.addDialogListener(this);

        gd.showDialog();
        if (gd.wasCanceled()) {
            exit = true;
            setErrMessage("canceled");
        }
    }

    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        penaltyThreshold = (int) gd.getNextNumber();
        deviation = (int) gd.getNextNumber();
        typeApproximation = gd.getNextChoice();
        return true;
    }
}
