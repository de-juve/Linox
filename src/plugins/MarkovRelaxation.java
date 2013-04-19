package plugins;
//don't usage!

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import workers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 04.05.12
 * Time: 15:41
 * To change this template use File | Settings | File Templates.
 */
public class MarkovRelaxation extends MyAPlugin{
   // ImageProcessor ip;
    ImageStack stack;
	int width, height;
    Integer[] luminance;
    TreeMap<Integer, Double> u_k, u_p, u_delta_p, u_sigma_k, u_mean_c, u_d;

    public MarkovRelaxation() {
        title = "Markov relaxation";
    }

    @Override
    public void run() {

        stack = new ImageStack(width, height);

        LuminanceCalculator lumCalc = new LuminanceCalculator();
        lumCalc.initProcessor(imageProcessor);
        lumCalc.run();
        luminance =  DataCollection.INSTANCE.getLuminances();

        GACBuilder builder = new GACBuilder();
        builder.initProcessor(imageProcessor);
        builder.run();

      //  watershed = DataCollection.INSTANCE.getWshPoints();//builder.getWatershed();
        result = builder.getResult(false);
        stack = result.getStack();
       // stack.addSlice(result.getTitle(), result.getProcessor().duplicate());

        nodeCount();
        energyTerms();
        dataEnergy();
        Integer[] color = new Integer[luminance.length];
        for(int i = 0; i < color.length; i++)
            color[i] = 0;
        double max = Collections.max(u_d.values());
        double min = Collections.min(u_d.values());
        NodeWorker nw = NodeWorker.getInstance();
        for(NodeWorker.Node node : nw.getNodes().values()) {
            Integer label = node.getLabel();
            ArrayList<Integer> elements = node.getElements();
            for(Integer element : elements)
            {
                color[element] = (int) ((u_d.get(label) - min)*255/(max - min));
            }
        }

        create(imageProcessor, color);
        stack.addSlice("Markov Relax", imageProcessor);
        result = new ImagePlus("Markov Relax result", stack);
    }

    private void dataEnergy() {
        u_d = new TreeMap<Integer, Double>();

        NodeWorker nw = NodeWorker.getInstance();
        for(NodeWorker.Node node : nw.getNodes().values()) {
            Integer label = node.getLabel();
            DataEnergy dataEnergy = new DataEnergy(40, 1, u_k.get(label), u_sigma_k.get(label), u_p.get(label), u_delta_p.get(label), u_mean_c.get(label));
            AnnealingOptimization optimization = new AnnealingOptimization();
            u_d.put(label, optimization.optimize(1.3E5, 7.0E-3, 15D, dataEnergy));


            //U_D[s] = annealingOptimaze.Optimize(99.99E300, 1.11E-7959, 1000, dataEnergy);

            /* U_D[s] = PixelWorker.ParametersToFilter.a1*U_p[s] + PixelWorker.ParametersToFilter.a2*U_delta_p[s] +
            PixelWorker.ParametersToFilter.a3*U_k[s] + PixelWorker.ParametersToFilter.a3*U_sigma_k[s] +
            PixelWorker.ParametersToFilter.a4*U_mean_c[s];*/
        }
    }

    private void energyTerms() {
        u_k = new TreeMap<Integer, Double>();
        u_p = new TreeMap<>();
        u_delta_p = new TreeMap<Integer, Double>();
        u_sigma_k = new TreeMap<Integer, Double>();
        u_mean_c = new TreeMap<Integer, Double>();

        NodeWorker nw = NodeWorker.getInstance();
        CurvatureCalculator curvatureCalculator = CurvatureCalculator.getInstance();
        ContrastCalculator contrastCalculator = ContrastCalculator.getInstance();
        for(NodeWorker.Node node : nw.getNodes().values()) {
            int label = node.getLabel();
           /* if(node.getElements().size() == 1) {
                u_k.put(label, curvatureCalculator.getCurvature().get(0));
                u_sigma_k.put(label,0D);
                u_p.put(label, luminance[node.getElements().get(0)].doubleValue());
                u_delta_p.put(label,  0D);
                u_mean_c.put(label, contrastCalculator.getContrast().get(0));
            }
            else {*/
                u_k.put(label, 1 - node.getMeanCurvature());//node.getMeanCurvature());
                u_sigma_k.put(label, 1 - node.getVarianceCurvature());//node.getVarianceCurvature());
                u_p.put(label, node.getMeanPotential());//1 - node.getMeanPotential());
                u_delta_p.put(label,  node.getMeanVariationPotential());//1 - node.getMeanVariationPotential());
                u_mean_c.put(label, node.getMeanContrast());
            //}
        }
    }

    private void nodeCount() {
        NodeWorker nw = NodeWorker.getInstance();
        for(NodeWorker.Node node : nw.getNodes().values()) {
            node.countElementsCurvature(width);
            node.countVarianceCurvature();
            node.countMeanAndMeanVariationPotential(luminance);
            countMeanContrast(node);
        }
    }

    private void countMeanContrast(NodeWorker.Node node) {
        ArrayList<Integer> elements = node.getElements();
        int n = elements.size();
        node.setMeanContrast(0);
        GetterNeighboures getterN = new GetterNeighboures(DataCollection.INSTANCE.getWshPoints());
        double mc = 0;
        for(Integer e : elements) {
            ArrayList<Integer> nids = getterN.getCorrectNeighbouresIds(e, width, height, DataCollection.INSTANCE.getWshPoints());
            mc += node.countMeanContrast(e,nids, luminance);
        }
        mc/=n;
        node.setMeanContrast(mc);

    }


}
