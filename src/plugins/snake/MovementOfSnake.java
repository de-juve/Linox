package plugins.snake;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import plugins.*;
import road.Direction;
import workers.PixelsMentor;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

public class MovementOfSnake extends MyAPlugin implements DialogListener {
    private LineExtrapolation lineExtrapolation;
    private LineExtrapolation.SplineTuple[] splinesX, splinesY;
    private String typeApproximation;
    private int deviation, penaltyThreshold;
    private Snake<LinePoint> snakeX, snakeY, snake;
    private Penalty penalty;
    private boolean recount = false;
    private boolean stop = false;

    public MovementOfSnake() {
        title = "Movement Of Snake";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        result = new ImagePlus("Movement Of Snake " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
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

        lineExtrapolation = new LineExtrapolation();
        snake = new Snake<>(tail, head);
        penalty = new Penalty(penaltyThreshold);
        recount = true;

        moveSnake();

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


    }

    public void moveSnake() {
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
        //return id;
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
        moveSnake();
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
        int point = lineExtrapolation.lagrange(i, snake.getHead());
        while(Math.abs(point - snake.getHead().getFirst().getY()) > 1) {
            snake.reduceStep();
            i = snake.getHead().getFirst().getX() + snake.getStep();
            point = lineExtrapolation.lagrange(i, snake.getHead());
        }
        while(Math.abs(point - snake.getHead().getFirst().getY()) > 0 && Math.abs(point - snake.getHead().getFirst().getY()) < 1) {
            snake.increaseStep();
            i = snake.getHead().getFirst().getX() + snake.getStep();
            point = lineExtrapolation.lagrange(i, snake.getHead());
        }
        System.out.println(snake.getHead().getFirst().getY() + " " + point);
        return point;
    }

    private int moveSpline(Snake<LinePoint> snake, LineExtrapolation.SplineTuple[] splines) {
        double i = snake.getHead().getFirst().getX() + snake.getStep();
        int point;
        try {
            point = (int) lineExtrapolation.interpolateSpline(i, splines);
            while(Math.abs(point - snake.getHead().getFirst().getY()) > 1) {
                snake.reduceStep();
                i = snake.getHead().getFirst().getX() + snake.getStep();
                point = (int) lineExtrapolation.interpolateSpline(i, splines);
            }
            while(Math.abs(point - snake.getHead().getFirst().getY()) > 0 && Math.abs(point - snake.getHead().getFirst().getY()) < 1) {
                snake.increaseStep();
                i = snake.getHead().getFirst().getX() + snake.getStep();
                point = (int) lineExtrapolation.interpolateSpline(i, splines);
            }
        } catch (NullPointerException ex) {
            exit = true;
            setErrMessage(ex.getMessage());
            return -1;
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
            splinesX = new LineExtrapolation.SplineTuple[snake.getHead().size()];
            splinesY = new LineExtrapolation.SplineTuple[snake.getHead().size()];

            lineExtrapolation.buildSpline(snakeX.getHead(), splinesX);
            lineExtrapolation.buildSpline(snakeY.getHead(), splinesY);
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

    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        penaltyThreshold = (int) gd.getNextNumber();
        deviation = (int) gd.getNextNumber();
        typeApproximation = gd.getNextChoice();
        return true;
    }
}
