package plugins.snake;

import gui.dialog.ChoiceDialog;
import gui.dialog.ParameterComboBox;
import gui.dialog.ParameterSlider;
import ij.ImagePlus;
import plugins.*;
import plugins.nonlinearRegression.Regression;
import road.Direction;
import workers.PixelsMentor;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class MovementOfSnake extends MyAPlugin {
    private Regression regressionX, regressionY;
    private LineExtrapolation lineExtrapolation;
    private LineExtrapolation.SplineTuple[] splinesX, splinesY;
    private String typeFunction, typeMove;
    private int deviation, penaltyThreshold, polynomialDegree;
    private Snake<LinePoint> snake;
    private ShortSnake<LinePoint> snakeX, snakeY;
    private Penalty penalty;
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

        snake = new Snake<>();
        snakeX = new ShortSnake<>();
        snakeY = new ShortSnake<>();

        if(typeMove.equals("ols")) {
            regressionX = new Regression();
            regressionY = new Regression();
        } else {
            lineExtrapolation = new LineExtrapolation();
        }

        penalty = new Penalty(penaltyThreshold);

        LinkedList<Integer> line = DataCollection.INSTANCE.getLine();

        for(int i = 0; i < line.size(); i++) {
            int id = line.get(i);
            if(!snake.addFirstElementToBaseSetPoints(new LinePoint(i, id)) || i == (line.size() - 1)) {
                //build points
                defineBaseSetPoints();
                correctPoints();

                if(i == (line.size() - 1)) {
                    break;
                }
                //remove
                i--;
                snake.removeElementsFromBaseSetPoints(line.size() - i);
            }
        }


        //moveSnake();

        snake.merge();

        for(LinePoint id : snake.getLine()) {
            imageProcessor.set(id.getY(), Color.RED.getRGB());
        }

    //    ShowStaticstics.showLuminanceChanging(snake.getLineValues(), true);
    }

    public void correctPoints() {
        int nx, ny, nz;
        calculateFunctionParameters();
        Iterator<LinePoint> iterator = snake.getBaseSetPoints().descendingIterator();
        Iterator<LinePoint> iteratorX = snakeX.getBaseSetPoints().descendingIterator();
        Iterator<LinePoint> iteratorY = snakeY.getBaseSetPoints().descendingIterator();
        while(iterator.hasNext()) {
            iterator.next();
            LinePoint pX = iteratorX.next();
            LinePoint pY = iteratorY.next();

            LinePoint pointX = getPoint(pX, "x");
            LinePoint pointY = getPoint(pY, "y");
            nx = pointX.getY();
            ny = pointY.getY();
            int id = nx + ny*width;

            if(nx >= width || nx < 0 || ny >= height || ny < 0) {
                if(snakeX.getHead().getFirst().getY() ==  width-1 || snakeY.getHead().getFirst().getY() == height-1) {
                    System.err.println("конец изображения");
                    return;
                }
                //вышли за пределы изображения
            /*id = recover();
            if (stop) {
                System.err.println("Out of bounds (" + nx + ", " + ny + ") Don't have any options");
                return;
            }*/
                System.err.println("вышли за пределы изображения");
                return;
            } else {
                nz = getLuminance(id);
                if(nz < 0) {
                    // превысили штраф
                    id = recover(id);
                    if (stop) {
                        System.err.println("Don't have any options");
                        return;
                    }
                }
            }
            if(id < 0 /*|| snake.headContains(id) || snake.tailContains(id)*/) {
                System.err.println("точка уже встречалась");
                return;
            }
            snake.addFirstElementToHead(new LinePoint(snake.getHeadId(), id));
            snakeX.addFirstElementToHead(new LinePoint(snakeX.getHeadId(), nx));
            snakeY.addFirstElementToHead(new LinePoint(snakeY.getHeadId(), ny));
        }
    }

    private LinePoint getPoint(LinePoint prevPoint, String coordinate) {
        LinePoint point;
        double x = prevPoint.getX();
        if(coordinate.equals("x")) {
            x += snakeX.getStep();
        } else {
            x += snakeY.getStep();
        }

        int y = calculatePoint(x, coordinate);

        while(Math.abs(y - prevPoint.getY()) > 1) {
            if(coordinate.equals("x")) {
                if(!snakeX.reduceStep()) {
                    break;
                }
                x = prevPoint.getX() + snakeX.getStep();
            } else {
                if(!snakeY.reduceStep()) {
                    break;
                }
                x = prevPoint.getX() + snakeY.getStep();
            }
            y = calculatePoint(x, coordinate);
        }
        point = new LinePoint(x, y);

        return point;
    }

    private int calculatePoint(double x, String coordinate) {
        int point = -1;

        switch (typeFunction) {
            case "spline" : {
                try {
                    if(coordinate.equals("x")) {
                        point = (int) lineExtrapolation.interpolateSpline(x, splinesX);
                    } else {
                        point = (int) lineExtrapolation.interpolateSpline(x, splinesY);
                    }
                } catch (NullPointerException ex) {
                    exit = true;
                    setErrMessage(ex.getMessage());
                }
                break;
            }
            case "lagrange" : {
                if(coordinate.equals("x")) {
                    point = lineExtrapolation.lagrange(x, snakeX.getBaseSetPoints());
                } else {
                    point = lineExtrapolation.lagrange(x, snakeY.getBaseSetPoints());
                }
                break;
            }
            case "polynomial" :
            case "parabola" :
            case "sin" : {
                if(coordinate.equals("x")) {
                    point = regressionX.getY(x);
                } else {
                    point = regressionY.getY(x);
                }
                break;
            }
        }
        return point;
    }


    public void moveSnake() {
        int nx, ny, nz;

        if(snake.isRecount()) {
            calculateFunctionParameters();
        }

        switch (typeFunction) {
            case "spline" : {
                nx = moveSpline(snakeX, splinesX);
                ny = moveSpline(snakeY, splinesY);
                if(exit)   {
                    System.err.println("spline err");
                    return;
                }
                break;
            }
            case "lagrange" : {
                nx = moveLagrange(snakeX);
                ny = moveLagrange(snakeY);
                break;
            }
            case "polynomial" :
            case "parabola" :
            case "sin" : {
                nx = moveRegression(snakeX, regressionX);
                ny = moveRegression(snakeY, regressionY);
                break;
            }
            default:  {
                System.err.println("Fail type function");
                return;
            }
        }

        int id = nx + ny*width;

        if(nx >= width || nx < 0 || ny >= height || ny < 0) {
            if(snakeX.getHead().getFirst().getY() ==  width-1 || snakeY.getHead().getFirst().getY() == height-1) {
                System.err.println("конец изображения");
                return;
            }
            /*id = recover();
            if (stop) {
                System.err.println("Out of bounds (" + nx + ", " + ny + ") Don't have any options");
                return;
            }*/
            System.err.println("вышли за пределы изображения");
            return;
        } else {
            nz = getLuminance(id);
            if(nz < 0) {
                id = recover(id);
                if (stop) {
                    System.err.println("Don't have any options");
                    return;
                }
            }
        }
        if(id < 0 || snake.headContains(id) || snake.tailContains(id)) {
            System.err.println("точка уже встречалась");
            return;
        }
        snake.addFirstElementToHead(new LinePoint(snake.getHeadId(), id));
        snakeX.addFirstElementToHead(new LinePoint(snakeX.getHeadId(), nx));
        snakeY.addFirstElementToHead(new LinePoint(snakeY.getHeadId(), ny));

        moveSnake();
    }

    private int recover(int badPoint) {
        int y;
        //либо мы вышли за границы изборажения, либо превысили лимит штрафов
        if(penalty.countPenalty() > penalty.getThreshold()) {
            //превысили лимит штрафов
            LinePoint point = snake.getHead().removeFirst();
            if(!checkLuminance(point.getY())) {
                //предыдущая точка тоже была со штрафом, вернуться до точки в которой нет штрафа
                while(!checkLuminance(point.getY())) {
                    point = snake.getHead().removeFirst();
                }
            }

            //нашли точку на дороге, Возвращаем ее в начало головы змеи.
            //Надо выбрать для продолжения одного из ее соседей
            snake.getHead().addFirst(point);

            //сначала пробуем соседние точки
            y = turnHead();
            if(y > 0) {
                //snake.getHead().removeFirst();
                return y;
            }
        }

        //делаем откат, проверить, что мы пойдем другим путем.
        y = undertow();

        return y;
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
        Direction d = Direction.defineDirection(snake.getHead().get(2).getY(), snake.getHead().get(0).getY(), width);
        //проверяем в этом же направлении точку
        int id = PixelsMentor.defineNeighbourId(snake.getHead().get(0).getY(), d, width, height);
        if(checkLuminance(id) && !snake.headContains(id)) {
            return id;
        }

        //проверяем сонаправленные направления по диагонали
        id = PixelsMentor.defineNeighbourId(snake.getHead().get(0).getY(), d.collinear1(), width, height);
        if(checkLuminance(id) && !snake.headContains(id)) {
            return id;
        }
        id = PixelsMentor.defineNeighbourId(snake.getHead().get(0).getY(), d.collinear2(), width, height);
        if(checkLuminance(id) && !snake.headContains(id)) {
            return id;
        }

        //проверяем перпендикулярные направления
        id = PixelsMentor.defineNeighbourId(snake.getHead().get(0).getY(), d.opposite1(), width, height);
        if(checkLuminance(id) && !snake.headContains(id)) {
            return id;
        }
        id = PixelsMentor.defineNeighbourId(snake.getHead().get(0).getY(), d.opposite2(), width, height);
        if(checkLuminance(id) && !snake.headContains(id)) {
            return id;
        }

        return -1;
    }



    private LinePoint getPreviousPoint(ShortSnake<LinePoint> snake) {
        LinePoint prevPoint;
        if(snake.getHead().size() > 0) {
            prevPoint =  snake.getHead().getFirst();
        } else {
            prevPoint = snake.getBaseSetPoints().getFirst();
        }
        return prevPoint;
    }

    private int moveRegression(ShortSnake<LinePoint> snake, Regression regression) {
        LinePoint prevPoint = getPreviousPoint(snake);
        double i = prevPoint.getX() + snake.getStep();
        int point = regression.getY(i);
        while(Math.abs(point - prevPoint.getY()) > 1) {
            if(!snake.reduceStep()) {
                break;
            }
            i = prevPoint.getX() + snake.getStep();
            point = regression.getY(i);
        }
        while(Math.abs(point - prevPoint.getY()) >= 0 && Math.abs(point - prevPoint.getY()) < 1) {
            if(!snake.increaseStep()) {
                break;
            }
            i = prevPoint.getX() + snake.getStep();
            point = regression.getY(i);
        }
        return point;
    }

    private int moveLagrange(ShortSnake<LinePoint> snake) {
        LinePoint prevPoint = getPreviousPoint(snake);
        double i = prevPoint.getX() + snake.getStep();
        int point = lineExtrapolation.lagrange(i, snake.getBaseSetPoints());
        while(Math.abs(point - prevPoint.getY()) > 1) {
            if(!snake.reduceStep()) {
                break;
            }
            i = prevPoint.getX() + snake.getStep();
            point = lineExtrapolation.lagrange(i, snake.getBaseSetPoints());
        }
        while(Math.abs(point - prevPoint.getY()) > 0 && Math.abs(point - prevPoint.getY()) < 1) {
            if(!snake.increaseStep()) {
                break;
            }
            i = prevPoint.getX() + snake.getStep();
            point = lineExtrapolation.lagrange(i, snake.getBaseSetPoints());
        }
        return point;
    }

    private int moveSpline(ShortSnake<LinePoint> snake, LineExtrapolation.SplineTuple[] splines) {
        LinePoint prevPoint = getPreviousPoint(snake);
        double i = prevPoint.getX() + snake.getStep();
        int point;
        try {
            point = (int) lineExtrapolation.interpolateSpline(i, splines);
            while(Math.abs(point - prevPoint.getY()) > 1) {
                if(!snake.reduceStep()) {
                    break;
                }
                i = prevPoint.getX() + snake.getStep();
                point = (int) lineExtrapolation.interpolateSpline(i, splines);
            }
            while(Math.abs(point - prevPoint.getY()) > 0 && Math.abs(point - prevPoint.getY()) < 1) {
                if(!snake.increaseStep()) {
                    break;
                }
                i = prevPoint.getX() + snake.getStep();
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
        if(!checkLuminance(id)) {
            penalty.addPenalty(Math.abs(DataCollection.INSTANCE.getLuminance(id) - ShowStaticstics.mean), snake.getHead());
            if(penalty.countPenalty() > penalty.getThreshold()) {
                return -1;
            }
            return DataCollection.INSTANCE.getLuminance(id);
        }
        penalty.addPenalty(0);
        return DataCollection.INSTANCE.getLuminance(id);
    }

    private void calculateFunctionParameters() {

        updateStatistics();
        //defineBaseSetPoints();

        switch (typeFunction) {
            case "spline" : {
                splinesX = new LineExtrapolation.SplineTuple[snake.getBaseSetPoints().size()];
                splinesY = new LineExtrapolation.SplineTuple[snake.getBaseSetPoints().size()];

                lineExtrapolation.buildSpline(snakeX.getBaseSetPoints(), splinesX);
                lineExtrapolation.buildSpline(snakeY.getBaseSetPoints(), splinesY);
                break;
            }
            case "polynomial" :
            case "parabola" :
            case "sin" : {
                regressionX.calcFitParams(snakeX.getBaseSetPoints(), typeFunction, polynomialDegree);
                regressionY.calcFitParams(snakeY.getBaseSetPoints(), typeFunction, polynomialDegree);
                break;
            }
        }
    }

    private void updateStatistics() {
        if(snake.getTailValues().size() > 0) {
            ShowStaticstics.showHistogram(snake.getTailValues(), false);
            ShowStaticstics.showLuminanceChanging(snake.getTailValues(), false);
        } else {
            ShowStaticstics.showHistogram(snake.getBaseSetPointsValues(), false);
            ShowStaticstics.showLuminanceChanging(snake.getBaseSetPointsValues(), false);
        }
    }

    private void defineBaseSetPoints() {
        snakeX.getBaseSetPoints().clear();
        snakeY.getBaseSetPoints().clear();

        int size = snake.getBaseSetPoints().size();
        int start = size-1;
        if(typeFunction.equals("lagrange")) {
            start = Math.min(4, start);
        }
        for(int j = start; j >= 0 ; j--) {
            LinePoint point = snake.getBaseSetPoints().get(j);
            snakeX.addFirstElementToBaseSetPoints(new LinePoint(point.getX(), point.getY() % width));
            snakeY.addFirstElementToBaseSetPoints(new LinePoint(point.getX(), point.getY() / width));
        }
    }

    protected void showDialog(String name) {
        ParameterComboBox functionCombo = new ParameterComboBox("Choose ols or approximation", new String[]{"ols", "approximation"});
        ParameterComboBox approximationCombo = new ParameterComboBox("Choose approximation function", new String[]{"lagrange", "spline"});
        ParameterComboBox olsCombo = new ParameterComboBox("Or choose ols function", new String[]{"polynomial", "parabola", "sin"});
        ParameterSlider polynomDegreeSlider = new ParameterSlider("Polynom degree", 1, 12, 1);
        ParameterSlider penaltySlider = new ParameterSlider("Count of penaltys", 0, 100000, 100);
        ParameterSlider lyminanceSlider = new ParameterSlider("Luminance deviation", 0, 100, 10);

        ChoiceDialog cd = new ChoiceDialog();
        cd.setTitle(name);
        cd.addParameterComboBox(functionCombo);
        cd.addParameterComboBox(olsCombo);
        cd.addParameterComboBox(approximationCombo);
        cd.addParameterSlider(polynomDegreeSlider);
        cd.addParameterSlider(penaltySlider);
        cd.addParameterSlider(lyminanceSlider);

        cd.setVisible(true);
        if (cd.wasCanceled()) {
            exit = true;
            setErrMessage("canceled");
        }

        typeMove = cd.getValueComboBox(functionCombo);
        if(typeMove.equals("ols")) {
            typeFunction =  cd.getValueComboBox(olsCombo);
            polynomialDegree = cd.getValueSlider(polynomDegreeSlider);
        } else {
            typeFunction = cd.getValueComboBox(approximationCombo);
        }

        penaltyThreshold = cd.getValueSlider(penaltySlider);
        deviation = cd.getValueSlider(lyminanceSlider);
    }
}
