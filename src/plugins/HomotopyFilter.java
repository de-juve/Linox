package plugins;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.process.ImageProcessor;
import workers.PixelsMentor;

import java.awt.*;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Vector;

public class HomotopyFilter extends MyAPlugin implements DialogListener {
    private int areaSizeX, areaSizeY;
    private int deviation;
    private boolean shift = false;
    private boolean[] visited;
    private HashSet<Integer> visited2;
    private HashSet<Integer> borders;
    private HashSet<Integer> area;
    private PriorityQueue<Integer> queue;
    private int backgroudColor;
    private int foregroundColor;
    private int leftX, upY;
    private boolean finish = false;

    public HomotopyFilter() {
        title = "Homotopy filter";
    }

    public void setAreaSizeX(int areaSizeX) {
        this.areaSizeX = areaSizeX;
    }

    public void setAreaSizeY(int areaSizeY) {
        this.areaSizeY = areaSizeY;
    }

    public void setDeviation(int deviation) {
        this.deviation = deviation;
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if (result == null) {
            create(imageProcessor, DataCollection.INSTANCE.getLuminances());
            result = new ImagePlus("homotopy " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if (addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        showDialog("Homotopy");
        if (exit) {
            return;
        }
        LuminanceCalculator luminanceCalculatorPlugin = new LuminanceCalculator();
        luminanceCalculatorPlugin.initProcessor(imageProcessor);
        luminanceCalculatorPlugin.run();

        leftX = upY = 0;

        while (leftX + areaSizeX <= width && upY + areaSizeY <= height) {
            deleteArea();
            defineNewBounds();
            if (finish) {
                break;
            }
        }
    }

    private void deleteArea() {
        visited = new boolean[width * height];
        for (int iy = upY; iy <= upY + areaSizeY; iy++) {
            X:
            for (int ix = leftX; ix <= leftX + areaSizeX; ix++) {
                int id = PixelsMentor.getId(ix, iy, width, height);
                if (!visited[id]) {
                    visited[id] = true;
                    if (isBorderOfArea(id)) {
                        continue;
                    }
                    area = new HashSet<Integer>();
                    borders = new HashSet<Integer>();
                    queue = new PriorityQueue<>();
                    visited2 = new HashSet<Integer>();

                    foregroundColor = DataCollection.INSTANCE.getLuminance(id);
                    area.add(id);
                    queue.add(id);
                    visited2.add(id);

                    while (!queue.isEmpty()) {
                        id = queue.remove();
                        fillArea(id);
                    }
                    if (!area.isEmpty()) {
                        backgroudColor = -1;
                        for (Integer border : borders) {
                            if (!isBackgroundPixel(border)) {
                                borders.clear();
                                area.clear();
                                continue X;
                            }
                        }
                        for (Integer i : area) {
                            DataCollection.INSTANCE.setLuminance(i, backgroudColor);
                        }
                    }
                }
            }
        }
    }

    private void fillArea(int id) {
        int x = PixelsMentor.getX(id, width);
        int y = PixelsMentor.getY(id, width);
        for (int iy = y - 1; iy <= y + 1; iy++) {
            for (int ix = x - 1; ix <= x + 1; ix++) {
                if (iy < upY || iy > upY + areaSizeY || ix < leftX || ix > leftX + areaSizeX) {
                    continue;
                }
                int neigh = PixelsMentor.getId(ix, iy, width, height);
                if (visited2.contains(neigh)) {
                    continue;
                }
                if (isBorderOfArea(neigh) && isForegroundPixel(neigh)) {
                    visited[neigh] = true;
                    queue.clear();
                    borders.clear();
                    area.clear();
                    visited2.clear();
                    return;
                }

                visited[neigh] = true;
                visited2.add(neigh);
                if (isForegroundPixel(neigh)) {
                    area.add(neigh);
                    queue.add(neigh);
                } else {
                    borders.add(neigh);
                }
            }
        }
    }

    private boolean isBorderOfArea(int id) {
        int x = PixelsMentor.getX(id, width);
        int y = PixelsMentor.getY(id, width);
        return (x == leftX) || (x == leftX + areaSizeX) || (y == upY) || (y == upY + areaSizeY) ? true : false;
    }

    private boolean isForegroundPixel(int id) {
        return (DataCollection.INSTANCE.getLuminance(id) <= foregroundColor + deviation) && (DataCollection.INSTANCE.getLuminance(id) >= foregroundColor - deviation) ? true : false;
    }

    private boolean isBackgroundPixel(int id) {
        if (backgroudColor < 0) {
            backgroudColor = DataCollection.INSTANCE.getLuminance(id);
        }
        return (DataCollection.INSTANCE.getLuminance(id) <= backgroudColor + deviation) && (DataCollection.INSTANCE.getLuminance(id) >= backgroudColor - deviation) ? true : false;
    }

    private void defineNewBounds() {
        if (leftX + 2 * areaSizeX < width) {
            leftX += areaSizeX;
        } else if (upY + 2 * areaSizeY > height) {
            finish = true;
        } else {
            if (shift) {
                leftX = areaSizeX / 2;
            } else {
                leftX = 0;
            }
            upY += areaSizeY / 2;
            shift = !shift;
        }
    }

    protected void showDialog(String name) {

        GenericDialog gd = new GenericDialog(name, IJ.getInstance());
        gd.addSlider("Area size x", Math.min(3, width), width, 3);
        areaSizeX = 3;
        gd.addSlider("Area size y", Math.min(3, height), height, 3);
        areaSizeY = 3;
        gd.addSlider("Deviation", 1, 50, 1);
        deviation = 1;
        gd.addDialogListener(this);
        gd.showDialog();
        if (gd.wasCanceled()) {
            exit = true;
            setErrMessage("canceled");
            return;
        }
    }

    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        areaSizeX = (int) gd.getNextNumber();
        areaSizeY = (int) gd.getNextNumber();
        deviation = (int) gd.getNextNumber();
        return true;
    }
}
