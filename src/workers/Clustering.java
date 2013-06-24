package workers;

import plugins.DataCollection;

import java.awt.*;
import java.util.*;

public class Clustering {
    public static void fillSheds(int width, int height) {
        DataCollection.INSTANCE.newShedLabels(width*height);
        ShedWorker.getInstance().clear();

        boolean[] create = new boolean[width * height];
        Queue<Integer> queue = new LinkedList<>();
        boolean[] analyzed = new boolean[width * height];
        Random rand = new Random(1);

        for (int i = 0; i < width * height; i++) {
            if (analyzed[i]) {
                continue;
            }
            create[i] = true;
            queue.add(i);
            int label = i;
            while (!queue.isEmpty()) {
                int p = queue.remove();
                if (analyzed[p]) {
                    continue;
                }
                analyzed[p] = true;

                if (create[p]) {
                    createNewShed(rand, p);
                    label = p;
                    create[p] = false;
                } else {
                    DataCollection.INSTANCE.setShedLabel(p, label);
                    ShedWorker.getInstance().addElementToShed(label, p);
                }

                //ArrayList<Integer> nids = PixelsMentor.defineNeighboursIdsWithSameValueDeviation(p, 10, DataCollection.INSTANCE.getStatuses(), width, height);
                ArrayList<Integer> nids = PixelsMentor.defineNeighboursIdsWithSameValue(p, DataCollection.INSTANCE.getStatuses(), width, height);
                for (Integer nid : nids) {
                    if(!analyzed[nid])  {
                        create[nid] = false;
                        queue.add(nid);
                    }
                }
            }
        }
    }

    public static void fillShedsWithDiagonalNeighboureCondition(int width, int height) {
        DataCollection.INSTANCE.newShedLabels(width*height);
        ShedWorker.getInstance().clear();

        boolean[] create = new boolean[width * height];
        Queue<Integer> queue = new LinkedList<>();
        boolean[] analyzed = new boolean[width * height];
        Random rand = new Random(1);

        for (int i = 0; i < width * height; i++) {
            if (analyzed[i]) {
                continue;
            }

            create[i] = true;
            queue.add(i);
            int label = i;

            while (!queue.isEmpty()) {
                int p = queue.remove();

                if (analyzed[p]) {
                    continue;
                }

                analyzed[p] = true;

                if (create[p]) {
                    createNewShed(rand, p);
                    label = p;
                    create[p] = false;
                } else {
                    DataCollection.INSTANCE.setShedLabel(p, label);
                    ShedWorker.getInstance().addElementToShed(label, p);
                }

                ArrayList<Integer> nids = PixelsMentor.defineNeighboursIdsWidthDiagonalCondition(p, DataCollection.INSTANCE.getStatuses(), width, height);

                for (Integer nid : nids) {
                    if(!analyzed[nid])  {
                        create[nid] = false;
                        queue.add(nid);
                    }
                }
            }
        }
    }

    private static void createNewShed(Random rand, int p) {
        DataCollection.INSTANCE.setShedLabel(p, p);
        ShedWorker.getInstance().addShed(p, DataCollection.INSTANCE.getStatus(p), new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
        ShedWorker.getInstance().addElementToShed(p, p);
    }
}
