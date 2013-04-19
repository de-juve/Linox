package plugins;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import workers.GetterNeighboures;
import workers.MassiveWorker;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 16.04.12
 * Time: 16:38
 * To change this template use File | Settings | File Templates.
 */
public class UnionFindComponentLabeling extends MyAPlugin{
    int label;
    int currentLabel;
    MassiveWorker worker;
    Integer[] luminance;

    Integer[] parent;

    public UnionFindComponentLabeling() {
        title = "Union find component labeling";
    }

    @Override
    public void run() {
        LuminanceCalculator lumCalc = new LuminanceCalculator();
        lumCalc.initProcessor(imageProcessor);
        lumCalc.run();
        luminance =  DataCollection.INSTANCE.getLuminances();
        parent = new Integer[luminance.length];
        worker = new MassiveWorker();
        worker.sort(luminance);
        label = 100;
        //areaOpening();
        areaClosing();

        create(imageProcessor, parent);
        result = new ImagePlus("union find", imageProcessor);
    }

    private void areaOpening() {
        firstPassOpening();
        secondPassOpening();
    }

    private void firstPassOpening()
    {
        ArrayList<Integer> ids  = worker.getIds();
        //start from pixels with max property
        for(int i = 0; i < ids.size(); i++) {
            int id = ids.get(i);

            //var current = BijectionIdAfterMaxSortingToId[i]; //get number 'id' of pixel in PixelMap
            // var currentP = PixelWorker.ImageData.PixelMap[current];

            MakeSet(id);
            GetterNeighboures getterN = new GetterNeighboures(luminance);
            ArrayList<Integer> nIds = getterN.getIds(id, imageProcessor.getWidth(), imageProcessor.getHeight());
            ArrayList<Integer> nValues = getterN.getElements(id, imageProcessor.getWidth(), imageProcessor.getHeight());
            ArrayList<Integer> neighboures = new ArrayList<Integer>();

            for(int j = 0; j < nIds.size(); j++) {
                if(luminance[id] < nValues.get(j) ||
                        (luminance[id] == nValues.get(j) && ids.indexOf(nIds.get(j)) < i)) {
                    neighboures.add(nIds.get(j));
                }
            }
            for (Integer neighboure : neighboures) {
                //opening
                // if (_specificpixelProperty.GetProperty(currentP) < _specificpixelProperty.GetProperty(neighboure)
                //     ||
                //    (_specificpixelProperty.GetProperty(currentP) == _specificpixelProperty.GetProperty(neighboure) &&
                //    neighboure.IdAfterSorting < i))
                union(neighboure, id);
            }
        }
    }

    private void secondPassOpening()
    {
        /* Resolving phase in reverse sort order */
        currentLabel = 1;

        // CreateShedWorker();
        //ShedWorker.CreateShedsMap();
        ArrayList<Integer> ids  = worker.getIds();
        //start from pixels with min property
        for(int i = ids.size() -1; i >= 0; i--) {
            int id = ids.get(i);
            if(parent[id] < 0) {
                parent[id] = luminance[id];
                // p.ShedLabel = _currlab;
                //  ShedWorker.AddShed(p);
                // _currlab++;
            }
            else {
                //p.ShedLabel = PixelWorker.ImageData.PixelMap[p.Parent].ShedLabel;
                parent[id] = parent[parent[id]];
                // p.Parent = PixelWorker.ImageData.PixelMap[p.Parent].Parent;
                // ShedWorker.AddElementToShed(p);
            }
        }
    }

    private void areaClosing() {
        firstPassClosing();
        secondPassClosing();
    }

    private void firstPassClosing() {
        ArrayList<Integer> ids  = worker.getIds();
        //start from pixels with min property
        for(int i = ids.size() -1; i >= 0; i--) {
            int id = ids.get(i);
            if(parent[id] != 0)
                continue;
            MakeSet(id);
            GetterNeighboures getterN = new GetterNeighboures(luminance);

            ArrayList<Integer> nValues = getterN.getElements(id, imageProcessor.getWidth(), imageProcessor.getHeight());
            ArrayList<Integer> nIds = getterN.getIds();//getterN.getIds(id, ip.getWidth(), ip.getHeight());
            ArrayList<Integer> neighboures = new ArrayList<Integer>();
            for(int j = 0; j < nIds.size(); j++) {
                if(luminance[id] > nValues.get(j) ||
                        (luminance[id] == nValues.get(j) && ids.indexOf(nIds.get(j)) > i)) {
                    neighboures.add(nIds.get(j));
                }
            }

            for (Integer neighboure : neighboures) {
                //closing
                // if (_specificpixelProperty.GetProperty(currentP) > _specificpixelProperty.GetProperty(neighboure) ||
                //     (_specificpixelProperty.GetProperty(currentP) == _specificpixelProperty.GetProperty(neighboure) &&
                //      neighboure.IdAfterSorting > i))
                union(neighboure, id);
            }
        }
    }

    private void secondPassClosing()
    {
        /* Resolving phase in reverse sort order */
        currentLabel = 1;

        //CreateShedWorker();
        //ShedWorker.CreateShedsMap();

        ArrayList<Integer> ids  = worker.getIds();
        //start from pixels with max property
        for(int i = 0; i < ids.size(); i++) {
            int id = ids.get(i);
            // for (Integer id : ids) {

            if (parent[id] < 0) {
                parent[id] = luminance[id];
                /*p.ShedLabel = _currlab;
                ShedWorker.AddShed(p);
                _currlab++;
                */
            } else {
                //p.ShedLabel = PixelWorker.ImageData.PixelMap[p.Parent].ShedLabel;
                parent[id] = parent[parent[id]];
                // p.Parent = PixelWorker.ImageData.PixelMap[p.Parent].Parent;
                //ShedWorker.AddElementToShed(p);
            }
        }
    }

    private void areaEqualing()
    {
        firstPassEqual();
        secondPassEqual();
    }

    private void firstPassEqual()
    {
        ArrayList<Integer> ids  = worker.getIds();
        //start from pixels with min property
        for(int i = ids.size() -1; i >= 0; i--) {
            int id = ids.get(i);
            MakeSet(id);

            GetterNeighboures getterN = new GetterNeighboures(luminance);
            ArrayList<Integer> nIds = getterN.getIds(id, imageProcessor.getWidth(), imageProcessor.getHeight());
            ArrayList<Integer> nValues = getterN.getElements(id, imageProcessor.getWidth(), imageProcessor.getHeight());
            ArrayList<Integer> neighboures = new ArrayList<Integer>();
            for(int j = 0; j < nIds.size(); j++) {
                if(luminance[id] == nValues.get(j) && ids.indexOf(nIds.get(j)) > i) {
                    neighboures.add(nIds.get(j));
                }
            }
            for (Integer neighboure : neighboures) {
                //equaling
                // if (_specificpixelProperty.GetProperty(currentP) == _specificpixelProperty.GetProperty(neighboure) &&
                //     neighboure.IdAfterSorting > i)
                union(neighboure, id);
            }
        }
    }

    private void secondPassEqual()
    {
        /* Resolving phase in reverse sort order */
        currentLabel = 1;

        //CreateShedWorker();
        //ShedWorker.CreateShedsMap();

        ArrayList<Integer> ids  = worker.getIds();
        //start from pixels with max property
        for (int i = 0; i < ids.size(); i++)
        {
            int id = ids.get(i);
            if(parent[id] < 0) {
                parent[id] = luminance[id];
                /*
                 p.ShedLabel = _currlab;
                ShedWorker.AddShed(p);
                _currlab++;
                 */
            }
            else {
                //  p.ShedLabel = PixelWorker.ImageData.PixelMap[p.Parent].ShedLabel;
                parent[id] = parent[parent[id]];
                //  ShedWorker.AddElementToShed(p);
            }
        }
    }

    private void MakeSet(int id)
    {
        parent[id] = -1;
    }

    void union(int neighboure, int p)
    {
        int rootId = findRoot(neighboure);

        if (rootId == p) return; //neighboure and p already in the same set
        if (criterion(rootId, p))
        {
            //two trees are merged
            //adding the area of root to that of p, and making p the parent of root.

            parent[p] += parent[rootId];
            parent[rootId] = p;

        }
        else
        {
            //a neighboure has a root grey level higher than I[p] and has a sufficiently large area
            parent[p] = -label;
        }
    }

    int findRoot(int id)
    {
        if(parent[id] >= 0) {
            //is child
            parent[id] = findRoot(parent[id]);
            return parent[id];
        }
        // is root
        return id;
    }

    boolean criterion(int root, int p)
    {
        //(-root.Parent < PixelWorker.Lambda) - if "true" then root is ACTIVE
        return (luminance[root] == luminance[p] || (-parent[root] < label));
    }
}
