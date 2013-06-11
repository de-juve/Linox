package plugins;

import gui.Linox;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import plugins.morfology.AreaEqualing;
import workers.Clustering;
import workers.MassiveWorker;
import workers.PixelsMentor;
import workers.ShedWorker;

import java.awt.*;
import java.util.ArrayList;
import java.util.TreeMap;


public class Watershed extends MyAPlugin implements DialogListener {
    MassiveWorker worker;
    TreeMap<Integer, ArrayList<Integer>> steepestNeighboures;
    boolean[] maximum;
    Color[] colors;

    public Watershed() {
        title = "Watershed";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if (result == null) {
            result = new ImagePlus("watershed " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if (addToStack) {
                ImageProcessor ip = imageProcessor.duplicate();
                for (int i = 0; i < DataCollection.INSTANCE.getShedLabels().length; i++) {
                    colors[i] = ShedWorker.getInstance().getShedColor(DataCollection.INSTANCE.getShedLabel(i));
                }
                create(ip, colors);
                DataCollection.INSTANCE.addtoHistory(new ImagePlus("watershed colors " + criteria + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), ip));
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }
        return result;
    }

    @Override
    public void run() {

        showDialog("watershed2");

        if (exit) {
            return;
        }

        Linox.getInstance().getStatusBar().setProgress("watershed", 0, 100);


        if (DataCollection.INSTANCE.getMaxLuminance() < 255) {
            LuminanceRedirector luminanceRedirector = new LuminanceRedirector();
            luminanceRedirector.initProcessor(imageProcessor);
            luminanceRedirector.run();
            imageProcessor = luminanceRedirector.getResult(false).getProcessor();
        }

        Linox.getInstance().getStatusBar().setProgress("watershed", 10, 100);

        int length = width * height;
        DataCollection.INSTANCE.newWshPoints(length);
        DataCollection.INSTANCE.newWaterShedPoints();
        colors = new Color[length];
        maximum = new boolean[length];

        LowerCompletion lowerCompletionPlugin = new LowerCompletion();

        ShedWorker.getInstance().clear();
        AreaEqualing areaEqualing = new AreaEqualing();
        areaEqualing.initProcessor(imageProcessor);
        areaEqualing.run();
        imageProcessor = areaEqualing.getResult(true).getProcessor();

        Linox.getInstance().getStatusBar().setProgress("watershed", 15, 100);

        lowerCompletionPlugin.initProcessor(imageProcessor);
        lowerCompletionPlugin.run();

        Linox.getInstance().getStatusBar().setProgress("watershed", 20, 100);

        constructDAG();

        Linox.getInstance().getStatusBar().setProgress("watershed", 40, 100);

        flood();

        Linox.getInstance().getStatusBar().setProgress("watershed", 80, 100);

        create(imageProcessor, DataCollection.INSTANCE.getWshPoints());

        ImageProcessor ipExtansion = extansion(imageProcessor);

        ipExtansion = binaryProcessing(ipExtansion);

        imageProcessor = shrinkage(ipExtansion);

        ipExtansion.reset();


        areaEqualing.initProcessor(imageProcessor);
        areaEqualing.run();
        Clustering.fillShedsWithDiagonalNeighboureCondition(width, height);
        imageProcessor = areaEqualing.getResult(false).getProcessor();

        recoverWatershedLines();
        create(imageProcessor, DataCollection.INSTANCE.getWshPoints());


        Linox.getInstance().getStatusBar().setProgress("watershed", 100, 100);
    }

    private void neighbouresNotMax(int id) {
        ArrayList<Integer> neighboures = PixelsMentor.defineNeighboursIds(id, width, height);
        for (Integer n : neighboures) {
            if (maximum[n] && (DataCollection.INSTANCE.getLuminance(n) < DataCollection.INSTANCE.getLuminance(id) ||
                    (DataCollection.INSTANCE.getLuminance(n).equals(DataCollection.INSTANCE.getLuminance(id)) &&
                            DataCollection.INSTANCE.getLowerCompletion(n) < DataCollection.INSTANCE.getLowerCompletion(id)))) {
                maximum[n] = false;
                neighbouresNotMax(n);
            }
        }
    }

    private void constructDAG() {
        steepestNeighboures = new TreeMap<>();
        worker = new MassiveWorker();

        worker.sort(DataCollection.INSTANCE.getLuminances(), DataCollection.INSTANCE.getLowerCompletions());

        //start from pixels with min lower completion
        ArrayList<Integer> ids = worker.getIds();

        for (int i = ids.size() - 1; i > -1; i--) {
            int id = ids.get(i);

            ArrayList<Integer> neighboures = PixelsMentor.defineNeighboursIdsWithLowerValue(id, DataCollection.INSTANCE.getLuminances(), width, height);
            if (neighboures.isEmpty()) {
                ArrayList<Integer> eqneighboures = PixelsMentor.defineNeighboursIdsWithSameValue(id, DataCollection.INSTANCE.getLuminances(), width, height);
                neighboures.clear();
                for (Integer eq : eqneighboures) {
                    if (DataCollection.INSTANCE.getLowerCompletion(eq) < DataCollection.INSTANCE.getLowerCompletion(id)) {
                        neighboures.add(eq);
                    }
                }
            }
            if (!neighboures.isEmpty()) {
                maximum[id] = true;
                steepestNeighboures.remove(id);
                steepestNeighboures.put(id, neighboures);
                neighbouresNotMax(id);
            } else {
                //define canonical element of min region if it need and if we can
                maximum[id] = false;
                int canonical = ShedWorker.getInstance().getCanonical(DataCollection.INSTANCE.getShedLabel(id));
                if (canonical == -1) {
                    canonical = id;
                    ShedWorker.getInstance().setCanonical(DataCollection.INSTANCE.getShedLabel(id), canonical);
                }
                ArrayList<Integer> ar = new ArrayList<>(1);
                ar.add(0, canonical);
                steepestNeighboures.remove(id);
                steepestNeighboures.put(id, ar);
            }
        }
    }

    private void flood() {
    /*  ImageStack stack = new ImageStack(width, height);
        ImageProcessor ip = imageProcessor.duplicate();

        int count = 0;
        for (int i = 0; i < colors.length; i++) {
            colors[i] = Color.BLACK;
        }*/

        ArrayList<Integer> ids = worker.getIds();
        //start from pixels with min property
        for (int i = ids.size() - 1; i >= 0; i--) {
            int p = ids.get(i);
            int rep = resolve(p);

            if (rep == -1) {
                DataCollection.INSTANCE.setWshPoint(p, 255);
                DataCollection.INSTANCE.addWaterShedPoint(p);

            /*  colors[p] = Color.RED;

                if (count == width * height / 1500) {
                    create(ip, colors);
                    stack.addSlice(ip.duplicate());
                    count = -1;
                }
                count++;*/
            } else {
                DataCollection.INSTANCE.setWshPoint(p, 0);
            }
        }
/*
        create(ip, colors);
        stack.addSlice(ip.duplicate());
        new ImagePlus("FLOOD w2", stack).show();*/
    }

    //Recursive function for resolving the downstream paths of the lower complete graph
    //Returns representative element of pixel p, or W if p is a watershed pixel
    private int resolve(int p) {
        int i = 0;
        int rep = -2;
        ArrayList<Integer> stN = steepestNeighboures.get(p);
        if (stN == null)
            return rep;
        int con = stN.size();
        while (i < con && rep != -1) {
            int sln = stN.get(i);
            if (sln != p && sln != -1) {
                sln = resolve(sln);
                if (sln > -1) {
                    stN.set(i, sln);
                }
            }
            if (i == 0) {
                rep = stN.get(i);
            } else if (sln != rep && sln > -1 && rep > -1) {
                colors[rep] = Color.WHITE;
                colors[sln] = Color.GREEN;
                rep = -1;
                stN.clear();
                stN.add(-1);

            }
            i++;
        }
        return rep;
    }

    private ImageProcessor binaryProcessing(ImageProcessor imageProcessor) {
        BinaryProcessor bp = new BinaryProcessor((ByteProcessor) (imageProcessor.convertToByte(false)));
        bp.invert();

        bp.dilate();

        bp.skeletonize();
        bp.invert();

        return bp.convertToRGB().duplicate();
    }

    private ImageProcessor extansion(ImageProcessor imageProcessor) {
        ImageProcessor result = imageProcessor.createProcessor(width + 10, height + 10);
        Rectangle rectangle = imageProcessor.getRoi();
        int offset, i, offset2, i2;
        for (int y = rectangle.y; y < (rectangle.y + rectangle.height); y++) {
            offset = y * width;
            offset2 = (y + 5) * (width + 10);
            for (int x = rectangle.x; x < (rectangle.x + rectangle.width); x++) {
                i = offset + x;
                i2 = offset2 + x + 5;
                int value = imageProcessor.get(i);
                result.set(i2, value);
                if (y != 0 && x != 0 && y != height - 1 && x != width - 1) {
                    continue;
                }
                if (y == 0) {
                    int z = y;
                    while (z <= y + 5) {
                        result.set(x + 5, z, value);
                        z++;
                    }
                }
                if (y == height - 1) {
                    int z = y + 5;
                    while (z <= y + 10) {
                        result.set(x + 5, z, value);
                        z++;
                    }
                }
                if (x == 0) {
                    int z = x;
                    while (z < x + 5) {
                        result.set(z, y + 5, value);
                        z++;
                    }
                }
                if (x == width - 1) {
                    int z = x + 5;
                    while (z <= x + 10) {
                        result.set(z, y + 5, value);
                        z++;
                    }
                }
            }
        }
        return result;
    }

    private ImageProcessor shrinkage(ImageProcessor imageProcessor) {
        ImageProcessor result = imageProcessor.createProcessor(width, height);
        Rectangle rectangle = result.getRoi();
        int offset, i, offset2, i2;

        for (int y = rectangle.y; y < (rectangle.y + rectangle.height); y++) {
            offset = y * width;
            offset2 = (y + 5) * (width + 10);
            for (int x = rectangle.x; x < (rectangle.x + rectangle.width); x++) {
                i = offset + x;
                i2 = offset2 + x + 5;
                int value = imageProcessor.get(i2);
                result.set(i, value);
            }
        }

        return result;
    }

    protected void showDialog(String name) {
        GenericDialog gd = new GenericDialog(name, IJ.getInstance());
        gd.addSlider("Luminance of road", 0, DataCollection.INSTANCE.getMaxLuminance(), DataCollection.INSTANCE.getMaxLuminance());

        gd.addDialogListener(this);

        gd.showDialog();
        if (gd.wasCanceled()) {
            exit = true;
            setErrMessage("canceled");
            return;
        }
    }

    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        DataCollection.INSTANCE.setMaxLuminance((int) gd.getNextNumber());
        return true;
    }
}
