package plugins;

import gui.Linox;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class LineExtrapolation extends MyAPlugin implements DialogListener {
    private SplineTuple[] splinesX, splinesY;
    private String typeApproximation;
    private int deviation, penaltyThreshold;
    private Snake<LinePoint> snakeX, snakeY, snake;
    private Penalty penalty;
    private boolean recount = false;
    private boolean stop = false;

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

        LinkedList<LinePoint> head = new LinkedList<>();
        LinkedList<LinePoint> tail = new LinkedList<>();
        int approxSize = 5;
        for(int i = 0; i < Math.max(0, DataCollection.INSTANCE.getLine().size() - approxSize); i++) {
            tail.addFirst(new LinePoint(i, DataCollection.INSTANCE.getLine().get(i)));
        }
        for(int i = Math.max(0, DataCollection.INSTANCE.getLine().size() - approxSize); i < DataCollection.INSTANCE.getLine().size(); i++) {
            head.addFirst(new LinePoint(i, DataCollection.INSTANCE.getLine().get(i)));
        }

        snake = new Snake<>(tail, head);
        penalty = new Penalty(penaltyThreshold);
        recount = true;

        extrapolateLine();
        if(exit) {
            return;
        }

        for(LinePoint p : snake.getTail()) {
            snake.addElementToLine(p);
        }
        for(LinePoint p : snake.getHead()) {
            snake.addElementToLine(p);
        }

        for(LinePoint id : snake.getLine()) {
            imageProcessor.set(id.getY(), Color.RED.getRGB());
        }

        ShowStaticstics.showLuminanceChanging(snake.getLineValues(), true);

        if(stop) {
            DataCollection.INSTANCE.setImageResult(getResult(true));
            (Linox.getInstance().getImageStore()).addImageTab(DataCollection.INSTANCE.getImageResult().getTitle(), DataCollection.INSTANCE.getImageResult());
            return;
        }
    }

    private void extrapolateLine() {
        int nx, ny, nz;

        if(recount) {
            recount();
            recount = !recount;
        }

        if(typeApproximation.equals("spline")) {
            nx = moveSpline(snakeX, splinesX);
            ny = moveSpline(snakeY, splinesY);
            if(exit)   {
                System.err.println("spline err");
                return;
            }
        } else {
            nx = moveLagrange(snakeX);
            ny = moveLagrange(snakeY);
        }
        int id = nx + ny*width;
        if(nx >= width || nx < 0 || ny >= height || ny < 0) {
            if(snakeX.getHead().getFirst().getY() ==  width-1 || snakeY.getHead().getFirst().getY() == height-1) {
                return;
            }
            id = recover();
            if (stop) {
                System.err.println("Out of bounds (" + nx + ", " + ny + ") Don't have any options");
                return;
            }
        } else {
            nz = getLuminance(id);
            if(nz < 0) {
                id = recover();
                if (stop) {
                    System.err.println("Don't have any options");
                    return;
                }
            }
        }
        if(id < 0 || snake.headContains(id) || snake.tailContains(id)) {
            return;
        }
        snake.addElementToHead(new LinePoint(snake.getHeadId(), id));
        recount = true;
        extrapolateLine();
    }

    private int recover() {
        int point;
        //либо мы вышли за границы изборажения, либо превысили лимит штрафов
        if(penalty.countPenalty() > penalty.getThreshold()) {
            //сначала пробуем соседние точки
            point = turnHead();
            if(point > 0) {
                //snake.getHead().removeFirst();
                return point;
            }
        }

        //делаем откат, проверить, что мы пойдем другим путем.
        point = undertow();

        return point;
    }

    private int undertow() {
        try {
            LinkedList<LinePoint> recover = penalty.recover();
            if(snake.getHead().size() == recover.size() && recover.equals(snake.getHead())) {
                return undertow();
            }
            snake.setHead(recover);
            return -1;

        } catch (NegativeArraySizeException ex) {
            System.err.println(ex.getMessage());
            stop = true;
            return -1;
        }
    }

    private int turnHead() {
       HashMap<Integer, Integer> ids = new HashMap<>();
        Direction d = Direction.defineDirection(snake.getHead().get(2).getY(), snake.getHead().get(0).getY(), width);
        int id = PixelsMentor.defineNeighbourId(snake.getHead().get(0).getY(), d, width, height);
        if(checkLuminance(id) && !snake.headContains(id)) {
            recount = true;
            return id;
        }
        id = PixelsMentor.defineNeighbourId(snake.getHead().get(0).getY(), d.collinear1(), width, height);
        if(checkLuminance(id) && !snake.headContains(id)) {
            recount = true;
            return id;
        }
        id = PixelsMentor.defineNeighbourId(snake.getHead().get(0).getY(), d.collinear2(), width, height);
        if(checkLuminance(id) && !snake.headContains(id)) {
            recount = true;
            return id;
        }
        id = PixelsMentor.defineNeighbourId(snake.getHead().get(0).getY(), d.opposite1(), width, height);
        if(checkLuminance(id) && !snake.headContains(id)) {
            recount = true;
            return id;
        }
        id = PixelsMentor.defineNeighbourId(snake.getHead().get(0).getY(), d.opposite2(), width, height);
        if(checkLuminance(id) && !snake.headContains(id)) {
            recount = true;
            return id;
        }
        return -1;
    }

    private int moveLagrange(Snake<LinePoint> snake) {
        double i = snake.getHead().getFirst().getX() + snake.getStep();
        int point = lagrange(i, snake.getHead());
        while(Math.abs(point - snake.getHead().getFirst().getY()) > 1) {
            snake.reduceStep();
            i = snake.getHead().getFirst().getX() + snake.getStep();
            point = lagrange(i, snake.getHead());
        }
        while(Math.abs(point - snake.getHead().getFirst().getY()) > 0 && Math.abs(point - snake.getHead().getFirst().getY()) < 1) {
            snake.increaseStep();
            i = snake.getHead().getFirst().getX() + snake.getStep();
            point = lagrange(i, snake.getHead());
        }
        System.out.println(snake.getHead().getFirst().getY() + " " + point);
        return point;
    }

    private int moveSpline(Snake<LinePoint> snake, SplineTuple[] splines) {
        double i = snake.getHead().getFirst().getX() + snake.getStep();
        int point = (int) interpolateSpline(i, splines);
        while(Math.abs(point - snake.getHead().getFirst().getY()) > 1) {
            snake.reduceStep();
            i = snake.getHead().getFirst().getX() + snake.getStep();
            point = (int) interpolateSpline(i, splines);
        }
        while(Math.abs(point - snake.getHead().getFirst().getY()) > 0 && Math.abs(point - snake.getHead().getFirst().getY()) < 1) {
            snake.increaseStep();
            i = snake.getHead().getFirst().getX() + snake.getStep();
            point = (int) interpolateSpline(i, splines);
        }
        return point;
    }

    private boolean checkLuminance(int id) {
        if (Math.abs(DataCollection.INSTANCE.getLuminance(id) - ShowStaticstics.mean) >= deviation) {
            return false;
        }
        return true;
    }

    private int getLuminance(int id) {
        if (Math.abs(DataCollection.INSTANCE.getLuminance(id) - ShowStaticstics.mean) >= deviation) {
            penalty.addPenalty(Math.abs(DataCollection.INSTANCE.getLuminance(id) - ShowStaticstics.mean), snake.getHead());
            if(penalty.countPenalty() > penalty.getThreshold()) {
                return -1;
            }
        }
        penalty.addPenalty(0);
        return DataCollection.INSTANCE.getLuminance(id);
    }

    private void recount() {
        snakeX = new Snake<>();
        snakeY = new Snake<>();

        ShowStaticstics.showHistogram(snake.getTailValues(), false);
        ShowStaticstics.showLuminanceChanging(snake.getTailValues(), false);

        defineHeadPoints(snake.getHead());

        if(typeApproximation.equals("spline")) {
            splinesX = new SplineTuple[snake.getHead().size()];
            splinesY = new SplineTuple[snake.getHead().size()];

            buildSpline(snakeX.getHead(), splinesX);
            buildSpline(snakeY.getHead(), splinesY);
        }
    }

    private void defineHeadPoints(LinkedList<LinePoint> line) {
        int id;
        int start = line.size()-1;
        if(typeApproximation.equals("lagrange")) {
            start = Math.min(4, start);
        }
        for(int j = start; j >= 0 ; j--) {
            LinePoint point = line.get(j);
            id = point.getY();
            snakeX.addElementToHead(new LinePoint(line.size() - j, id % width));
            snakeY.addElementToHead(new LinePoint(line.size() - j, id / width));
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
        gd.addSlider("Count of penaltys", 0, 100000, 100);
        gd.addSlider("Luminance deviation", 0, 100, 10);
        gd.addChoice("Type of approximation", new String[]{"lagrange", "spline"}, "lagrange");
        penaltyThreshold = 100;
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
