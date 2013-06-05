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
import java.util.LinkedList;
import java.util.ListIterator;

public class MovementOfSnake extends MyAPlugin {
    private Regression regressionX, regressionY;
    private LineExtrapolation lineExtrapolation;
    private LineExtrapolation.SplineTuple[] splinesX, splinesY;
    private String typeFunction, typeMove;
    private int deviation, penaltyThreshold, polynomialDegree, baseSetPointsSize;
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

        snake = new Snake<>(baseSetPointsSize);
        snakeX = new ShortSnake<>(baseSetPointsSize);
        snakeY = new ShortSnake<>(baseSetPointsSize);

        if(typeMove.equals("ols")) {
            regressionX = new Regression();
            regressionY = new Regression();
        } else {
            lineExtrapolation = new LineExtrapolation();
        }

        penalty = new Penalty(penaltyThreshold);

        LinkedList<Integer> line = DataCollection.INSTANCE.getLine();
        //в 0 точка, с которой начали рисовать
        for(int i = 0; i < line.size(); i++) {
            int id = line.get(i);
            if(!snake.addElementToBaseSetPoints(new LinePoint(i, id)) || i == (line.size() - 1)) {
                buildPoints();

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

    public void buildPoints() {
        int nx, ny, nz;

        defineBaseSetPointsForXY();
        calculateFunctionParameters();

        ListIterator<LinePoint> iteratorX = snakeX.getBaseSetPoints().listIterator();
        ListIterator<LinePoint> iteratorY = snakeY.getBaseSetPoints().listIterator();
        while(iteratorX.hasNext()) {
            LinePoint pointX = getPoint(iteratorX.next(), "x");
            LinePoint pointY = getPoint(iteratorY.next(), "y");
            nx = pointX.getY();
            ny = pointY.getY();
            int id = nx + ny*width;

            if(nx >= width || nx < 0 || ny >= height || ny < 0) {
                if((snakeX.getHead().size() > 0 && snakeY.getHead().size() > 0) && (snakeX.getHead().getFirst().getY() ==  width-1 || snakeY.getHead().getFirst().getY() == height-1)) {
                    System.err.println("Край изображения");
                    return;
                }
                System.err.println("Вышли за размеры изображения");
                return;
            } else {
                if(snake.getHeadSize() > 0) {
                    defineVariantsOfDirection(id);
                }
                nz = getLuminance(id);
                if(nz < 0) {
                    // превысили штраф
                    System.err.println("Превысили штраф пытаемся найти другое направление");
                    id = recover(id);
                    if (stop) {
                        System.err.println("Рассмотрели все возможные варианты. Мы в тупике.");
                        return;
                    }
                }
            }
            if(snake.headContains(id)) {
                //надо увеличить шаг, но какой? по  х или y или оба? и на сколько?
                System.err.println("Точка уже есть в голове змеи");
                /*if(snakeX.increaseStep() && snakeY.increaseStep()) {
                    iteratorX.previous();
                    iteratorY.previous();
                    continue;
                }*/
                continue;
            }
            if(id >= 0) {
                snake.addElementToHead(new LinePoint(snake.getHeadId() + snake.getStep(), id));

                //snakeX.addElementToHead(new LinePoint(snakeX.getHeadId(), pointX.getY()));
                snakeX.addElementToHead(new LinePoint(pointX.getX(), pointX.getY()));

                //snakeY.addElementToHead(new LinePoint(snakeY.getHeadId(), pointY.getY()));
                snakeY.addElementToHead(new LinePoint(pointY.getX(), pointY.getY()));
            }
        }
    }

    private void defineVariantsOfDirection(int nextPoint) {
        LinePoint point = snake.getLastElementFromHead();
        point.createStack();
        Direction direction = Direction.defineDirection(point.getY(), nextPoint, width);

        //проверяем перпендикулярные направления
        int id = PixelsMentor.defineNeighbourId(point.getY(), direction.opposite1(), width, height);
        if(id >= 0 && checkLuminance(id)) {
            point.pushNeighbour(id);
        }
        id = PixelsMentor.defineNeighbourId(point.getY(), direction.opposite2(), width, height);
        if(id >= 0 && checkLuminance(id)) {
            point.pushNeighbour(id);
        }

        //проверяем сонаправленные направления по диагонали
        id = PixelsMentor.defineNeighbourId(point.getY(), direction.collinear1(), width, height);
        if(id >= 0 && checkLuminance(id)) {
            point.pushNeighbour(id);
        }
        id = PixelsMentor.defineNeighbourId(point.getY(), direction.collinear2(), width, height);
        if(id >= 0 && checkLuminance(id)) {
            point.pushNeighbour(id);
        }

        id = PixelsMentor.defineNeighbourId(point.getY(), direction, width, height);
        if(id >= 0 && checkLuminance(id)) {
         //   point.pushNeighbour(id);
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
        snake.addElementToHead(new LinePoint(snake.getHeadId(), id));
        snakeX.addElementToHead(new LinePoint(snakeX.getHeadId(), nx));
        snakeY.addElementToHead(new LinePoint(snakeY.getHeadId(), ny));

        moveSnake();
    }

    private int recover(int badPoint) {
        //превысили лимит штрафов
        if(penalty.countPenalty() > penalty.getThreshold()) {
            int neighbour = -1, luminance = -1;
            LinePoint point = new LinePoint(-1,-1);
            HEAD: {
                while (snake.getHeadSize() > 0) {
                    point = snake.removeElementFromHead();
                    while(point.getStackSize() > 0) {
                        neighbour = point.popNeighbour();
                        luminance = getLuminance(neighbour);
                        if(luminance > 0) {
                            break HEAD;
                        }
                        neighbour = luminance = -1;
                    }
                }
            }
            if(luminance > 0) {
                snake.addElementToHead(point);
                return neighbour;
            }
        }
        return -1;

    }

    private LinePoint getPreviousPoint(ShortSnake<LinePoint> snake) {
        LinePoint prevPoint;
        if(snake.getHead().size() > 0) {
            prevPoint =  snake.getHead().getLast();//First();
        } else {
            prevPoint = snake.getBaseSetPoints().getLast();//First();
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
            penalty.addPenalty(Math.abs(DataCollection.INSTANCE.getLuminance(id) - ShowStaticstics.mean));
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

        switch (typeFunction) {
            case "spline" : {
                splinesX = new LineExtrapolation.SplineTuple[snakeX.getBaseSetPoints().size()];
                splinesY = new LineExtrapolation.SplineTuple[snakeY.getBaseSetPoints().size()];

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

    private void defineBaseSetPointsForXY() {
        snakeX.getBaseSetPoints().clear();
        snakeY.getBaseSetPoints().clear();

        int size = snake.getBaseSetPointsSize();
        int end = size;
       /* if(typeFunction.equals("lagrange")) {
            end = Math.min(5, start);
        }*/
        for(int j = 0; j < end ; j++) {
            LinePoint point = snake.getBaseSetPoint(j);
            snakeX.addElementToBaseSetPoints(new LinePoint(point.getX(), point.getY() % width));
            snakeY.addElementToBaseSetPoints(new LinePoint(point.getX(), point.getY() / width));
        }
    }

    protected void showDialog(String name) {
        ParameterComboBox functionCombo = new ParameterComboBox("Choose ols or approximation", new String[]{"ols", "approximation"});
        ParameterComboBox approximationCombo = new ParameterComboBox("Choose approximation function", new String[]{"lagrange", "spline"});
        ParameterComboBox olsCombo = new ParameterComboBox("Or choose ols function", new String[]{"polynomial", "parabola", "sin"});
        ParameterSlider polynomDegreeSlider = new ParameterSlider("Polynom degree", 1, 7, 3);
        ParameterSlider penaltySlider = new ParameterSlider("Count of penaltys", 0, 1000, 100);
        ParameterSlider lyminanceSlider = new ParameterSlider("Luminance deviation", 0, 100, 10);
        ParameterSlider baseSetPointSizeSlider = new ParameterSlider("Size of set base points", 3, Math.min(30, DataCollection.INSTANCE.getLine().size()),  Math.min(15,DataCollection.INSTANCE.getLine().size()-1));

        ChoiceDialog cd = new ChoiceDialog();
        cd.setTitle(name);
        cd.addParameterComboBox(functionCombo);
        cd.addParameterComboBox(olsCombo);
        cd.addParameterComboBox(approximationCombo);
        cd.addParameterSlider(polynomDegreeSlider);
        cd.addParameterSlider(penaltySlider);
        cd.addParameterSlider(lyminanceSlider);
        cd.addParameterSlider(baseSetPointSizeSlider);

        cd.pack();

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
        baseSetPointsSize = cd.getValueSlider(baseSetPointSizeSlider);
    }
}
