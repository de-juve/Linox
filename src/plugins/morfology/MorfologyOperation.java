package plugins.morfology;

import ij.IJ;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import plugins.DataCollection;
import plugins.LuminanceCalculator;
import plugins.MyAPlugin;
import workers.Clustering;
import workers.MassiveWorker;
import workers.PixelsMentor;

import java.awt.*;
import java.util.*;

public abstract class MorfologyOperation extends MyAPlugin implements DialogListener {
    MassiveWorker worker;
    String type = "undefined";
    String typeCompilation;

    int[] last;
    int[] representative;
    int[] area;
    final static int NONE = -300;
    final static int NOT_ANALYZED = -400;

    public void setCriteria(int value) {
        criteria = value;
    }

    protected void initialization() {
        DataCollection.INSTANCE.newStatus(width * height);
        for (int i = 0; i < width * height; i++) {
            DataCollection.INSTANCE.setStatus(i, NOT_ANALYZED);
        }
        DataCollection.INSTANCE.newShedLabels(width * height);

        area = new int[256];
        last = new int[256];
        representative = new int[256];

        for (int i = 0; i < last.length; i++) {
            last[i] = NONE;
            area[i] = 0;
            representative[i] = NONE;
        }
    }

    protected void morfRun() {
        DataCollection.INSTANCE.newStatus(width * height);
        initialization();

        LuminanceCalculator luminanceCalculatorPlugin = new LuminanceCalculator();
        luminanceCalculatorPlugin.initProcessor(imageProcessor);
        luminanceCalculatorPlugin.run();

        worker = new MassiveWorker();
        worker.sort(DataCollection.INSTANCE.getLuminances());

        if (type.equals("closing") || type.equals("equaling")) {
            for (int h = worker.getMax(); h >= worker.getMin(); h--) {
                preflood(h);
            }
        } else if (type.equals("opening")) {
            for (int h = worker.getMin(); h <= worker.getMax(); h++) {
                preflood(h);
            }
        }
        ArrayList<Integer> ids = worker.getIds();

        //start from pixels with min property
        for (Integer p : ids) {
            int root = DataCollection.INSTANCE.getStatus(p);
            if (root < 0) {
                root = p;
            }
            while (DataCollection.INSTANCE.getStatus(root) >= 0)
                root = DataCollection.INSTANCE.getStatus(root);
            int val = DataCollection.INSTANCE.getStatus(root);

            while (p != root) {
                int tmp = DataCollection.INSTANCE.getStatus(p);
                DataCollection.INSTANCE.setStatus(p, val);
                p = tmp;
            }
        }

        for (int i = 0; i < width * height; i++) {
            int bright = -DataCollection.INSTANCE.getStatus(i) - 1;
            DataCollection.INSTANCE.setStatus(i, bright);

        }

        Clustering.fillSheds(width, height);
    }

    private void preflood(int brightnesLevel) {
        TreeMap<Integer, ArrayList<Integer>> map = worker.getMap();
        if (!map.containsKey(brightnesLevel)) {
            return;
        }
        ArrayList<Integer> pixels = map.get(brightnesLevel);
        for (Integer pixelId : pixels) {
            if (DataCollection.INSTANCE.getStatus(pixelId) != NOT_ANALYZED) {
                continue;
            }
            DataCollection.INSTANCE.setStatus(pixelId, last[brightnesLevel]);
            last[brightnesLevel] = pixelId;
            representative[brightnesLevel] = pixelId;
            flood(brightnesLevel);
        }
    }

    protected int flood(int h) {
        while (last[h] != NONE) {

            //propagation at level h
            //get point from queue
            int p = last[h];
            last[h] = DataCollection.INSTANCE.getStatus(last[h]);

            //set to its representative element
            DataCollection.INSTANCE.setStatus(p, representative[h]);

            //get neighboures
            ArrayList<Integer> neighs = PixelsMentor.defineNeighboursIds(p, width, height);
            for (Integer nid : neighs) {
                if (DataCollection.INSTANCE.getStatus(nid) != NOT_ANALYZED) {
                    continue;
                }
                int m = DataCollection.INSTANCE.getLuminance(nid);
                // set representative element if none
                if (representative[m] == NONE) {
                    representative[m] = nid;
                }
                //add to queue
                DataCollection.INSTANCE.setStatus(nid, last[m]);
                last[m] = nid;
                if (type.equals("closing") || type.equals("equaling")) {
                    while (m < h) {
                        m = flood(m);
                    }
                } else if (type.equals("opening")) {
                    while (m > h) {
                        m = flood(m);
                    }
                }
            }
            area[h]++;
        }
        int m = h;
        //parent settings
        if (type.equals("closing") || type.equals("equaling")) {
            m = h + 1;
            while (m <= 255 && representative[m] == NONE) {
                ++m;
            }
            if (m <= 255) {
                if (area[h] < criteria) {
                    DataCollection.INSTANCE.setStatus(representative[h], representative[m]);
                    area[m] += area[h];
                } else {
                    //area[m] = criteria;
                    DataCollection.INSTANCE.setStatus(representative[h], -h - 1);
                }
            } else {
                DataCollection.INSTANCE.setStatus(representative[h], -h - 1);
            }
        } else if (type.equals("opening")) {
            m = h - 1;
            while (m >= 0 && representative[m] == NONE) {
                --m;
            }
            if (m >= 0) {
                if (area[h] < criteria) {
                    DataCollection.INSTANCE.setStatus(representative[h], representative[m]);
                    area[m] += area[h];
                } else {
                    // area[m] = criteria;
                    DataCollection.INSTANCE.setStatus(representative[h], -h - 1);
                }
            } else {
                DataCollection.INSTANCE.setStatus(representative[h], -h - 1);
            }
        }

        // reset attribute of extracted connected component at level h
        area[h] = 0;
        last[h] = NONE;
        representative[h] = NONE;
        return m;
    }

    protected void showDialog(String name) {
        GenericDialog gd = new GenericDialog(name, IJ.getInstance());
        gd.addSlider("Criterion", Math.min(1, width), width * height - 1, 1);
        gd.addDialogListener(this);
        gd.showDialog();
        if (gd.wasCanceled()) {
            exit = true;
            return;
        }
    }

    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        criteria = (int) gd.getNextNumber();
        return true;
    }
}
