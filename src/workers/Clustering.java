package workers;

import ij.process.ImageProcessor;
import plugins.DataCollection;
import workers.GetterNeighboures;
import workers.PixelsMentor;
import workers.ShedWorker;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Clustering {
    public static void fillSheds(int width, int height) {
        ShedWorker sh = ShedWorker.getInstance();
        sh.clear();

        boolean[] create = new boolean[width*height];
        Queue<Integer> queue = new LinkedList<>();
        boolean[] analyzed = new boolean[width*height];
        Random rand = new Random(1);

        for(int i = 0; i < width*height; i++) {
            if(analyzed[i])
                continue;
            create[i] = true;
            queue.add(i);
            int label = i;
            while(!queue.isEmpty()) {
                int p = queue.remove();
                if(analyzed[p])
                    continue;
                analyzed[p] = true;

                //ArrayList<Integer> nids = PixelsMentor.defineNeighboursIdsWithSameValueDeviation(p, 10, DataCollection.INSTANCE.getStatuses(), width, height);
                ArrayList<Integer> nids = PixelsMentor.defineNeighboursIdsWithSameValue(p, DataCollection.INSTANCE.getStatuses(), width, height);

                if(create[p]) {
                    createNewShed(rand, p);
                    label = p;
                    create[p] = false;
                }
                else {
                    DataCollection.INSTANCE.setShedLabel(p, label);
                    sh.addElementToShed(label, p);
                }
                for(Integer nid : nids) {
                    create[nid] = false;
                    queue.add(nid);
                }
            }
        }
    }

    private static void createNewShed(Random rand, int p) {
        ShedWorker sh = ShedWorker.getInstance();
        DataCollection.INSTANCE.setShedLabel(p, p);
        sh.addShed(p, DataCollection.INSTANCE.getStatus(p), new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
        sh.addElementToShed(p, p);
    }

    public static void fillShedsWithDiagonalNeighboureCondition(int width, int height) {
        ShedWorker sh = ShedWorker.getInstance();
        sh.clear();

        boolean[] create = new boolean[width*height];
        Queue<Integer> queue = new LinkedList<>();
        boolean[] analyzed = new boolean[width*height];
        Random rand = new Random(1);

        for(int i = 0; i < width*height; i++) {
            if(analyzed[i])
                continue;
            create[i] = true;
            queue.add(i);
            int label = i;
            while(!queue.isEmpty()) {
                int p = queue.remove();
                if(analyzed[p])
                    continue;
                analyzed[p] = true;

                ArrayList<Integer> nids = PixelsMentor.defineNeighboursIdsWidthDiagonalCondition(p, DataCollection.INSTANCE.getStatuses(), width, height);

                if(create[p]) {
                    createNewShed(rand, p);
                    label = p;
                    create[p] = false;
                }
                else {
                    DataCollection.INSTANCE.setShedLabel(p, label);
                    sh.addElementToShed(label, p);
                }
                for(Integer nid : nids) {
                    create[nid] = false;
                    queue.add(nid);
                }
            }
        }
    }
}
