package plugins;

import gui.Linox;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.NewImage;
import ij.plugin.Histogram;
import ij.process.ImageStatistics;
import ij.process.StackStatistics;

import java.awt.*;
import java.util.LinkedList;
import java.util.ListIterator;

public class ShowStaticstics {
    public static double mean, stdDev;

    public static void showHistogram(LinkedList<Integer> line, boolean show) {
        ImagePlus imp;

        imp = NewImage.createRGBImage("Show Statistics", line.size(), 1, 1, NewImage.FILL_BLACK);
        ListIterator<Integer> iterator = line.listIterator();
        int j = 0;
        while (iterator.hasNext()) {
            int i = iterator.next();
            int value = (((DataCollection.INSTANCE.getLuminance(i) & 0xff) << 16) +
                    ((DataCollection.INSTANCE.getLuminance(i) & 0xff) << 8) +
                    (DataCollection.INSTANCE.getLuminance(i) & 0xff));
            imp.getProcessor().set(j, value);
            j++;
        }

        WindowManager.setTempCurrentImage(imp);
        Histogram histogram = new Histogram();
        histogram.run("");
        ImageStatistics stats;
        int nBins = 256;
        double xMin, xMax;
        xMin = 0.0;
        xMax = 256.0;
        stats = new StackStatistics(imp, nBins, xMin, xMax);
        mean = stats.mean;
        stdDev = stats.stdDev;

        if (IJ.getImage() != null && WindowManager.getCurrentWindow() != null) {
            DataCollection.INSTANCE.setImageResult((ImagePlus) IJ.getImage().clone());
            if (show) {
                (Linox.getInstance().getImageStore()).addImageTab(WindowManager.getCurrentWindow().getTitle(), DataCollection.INSTANCE.getImageResult());
            }
            WindowManager.closeAllWindows();
        }
    }

    public static void showLuminanceChanging(LinkedList<Integer> line, boolean show) {
        ImagePlus imp;

        imp = NewImage.createRGBImage("Show Luminance changing", line.size() + 100, 350, 1, NewImage.FILL_WHITE);
        imp.getProcessor().setColor(Color.BLACK);
        imp.getProcessor().drawRect(40, 10, line.size(), 255);
        imp.getProcessor().drawRect(25, 10, 10, 255);
        imp.getProcessor().drawString("0", 10, 265);
        imp.getProcessor().drawString("255", 1, 20);
        imp.getProcessor().drawString("0", 40 - 2, 285);
        imp.getProcessor().drawString(String.valueOf(line.size()), 40 + line.size() - 2, 285);

        ListIterator<Integer> iterator = line.listIterator();

        while (iterator.hasNext()) {
            int id = iterator.nextIndex();
            int i = iterator.next();
            imp.getProcessor().drawLine(40 + id, 265, 40 + id, 265 - DataCollection.INSTANCE.getLuminance(i));
        }

        for (int i = 0; i < 255; i++) {
            int value = (((255 - i & 0xff) << 16) +
                    ((255 - i & 0xff) << 8) +
                    (255 - i & 0xff));
            imp.getProcessor().setColor(value);
            imp.getProcessor().drawLine(25, i + 10, 35, i + 10);
        }
        if (show) {
            (Linox.getInstance().getImageStore()).addImageTab(imp.getTitle(), imp);
        }

    }


}
