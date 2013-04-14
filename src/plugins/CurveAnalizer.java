package plugins;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import workers.GetterNeighboures;
import workers.NodeWorker;
import workers.PixelsMentor;
import workers.ShedWorker;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class CurveAnalizer extends MyAPlugin {
	double alpha = 0.1;

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            result = new ImagePlus("curv result " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);

            if(addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }
        return result;
    }

	@Override
	public void run() {

		countParams();

		createMeanCurvature("curvature colors curve");

		createMeanPotential();

		createMeanVariationPotential();

		createMeanContrast();
	}

	private void countParams() {
        LuminanceCalculator luminanceCalculator = new LuminanceCalculator();
        luminanceCalculator.initProcessor(imageProcessor);
        luminanceCalculator.run();

		for(NodeWorker.Node node : NodeWorker.getInstance().getNodes().values()) {
			node.countElementsCurvature(width);
			node.countMeanAndMeanVariationPotential(DataCollection.INSTANCE.getLuminances());
			countMeanContrast(node);
            //ShowStaticstics.showHistogram(node.getLine());
		}
	}

	private void countMeanContrast(NodeWorker.Node node) {
		ArrayList<Integer> elements = node.getElements();
		int n = elements.size();
		node.setMeanContrast(0);
		GetterNeighboures getterN = new GetterNeighboures(DataCollection.INSTANCE.getWshPoints());
		double mc = 0;
		for(Integer e : elements) {
		//	ArrayList<Integer> nids = getterN.getCorrectNeighbouresIds(e, width, height, DataCollection.INSTANCE.getWshPoints());
            ArrayList<Integer> nids = PixelsMentor.defineNeighboursIds(e, width, height);//getterN.getCorrectNeighbouresIds(e, width, height, DataCollection.INSTANCE.getWshPoints());

            mc += node.countMeanContrast(e, nids, DataCollection.INSTANCE.getLuminances());
		}
		mc/=n;
		node.setMeanContrast(mc);

	}

	private void createMeanContrast() {
		Integer[] colors = new Integer[DataCollection.INSTANCE.getShedLabels().length];
		for(int i = 0; i < colors.length; i++) {
			colors[i] = 0;
		}
		ArrayList<Double> curvData = new ArrayList<>();

		for(NodeWorker.Node node : NodeWorker.getInstance().getNodes().values()) {
			curvData.add(node.getMeanContrast());
		}
		double max = Collections.max(curvData);
		double min = Collections.min(curvData);
		for(NodeWorker.Node node : NodeWorker.getInstance().getNodes().values()) {
			//int value = (int) ((node.getMeanContrast() - min)*255/(max-min));
			int value = (int) (Math.pow((node.getMeanContrast() - min)/(max-min), alpha)*255);

			//System.out.println("mean variance contrast "+ node.getLabel()+" " + value);
			for(Integer element : node.getElements()) {
				colors[element] = value;

			}
		}

		create(imageProcessor, colors);
        DataCollection.INSTANCE.addtoHistory(new ImagePlus("mean var contrast curve " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor));
    }

	private void createMeanVariationPotential() {
		Integer[] colors = new Integer[DataCollection.INSTANCE.getShedLabels().length];
		for(int i = 0; i < colors.length; i++) {
			colors[i] = 0;
		}
		ArrayList<Double> curvData = new ArrayList<>();

		for(NodeWorker.Node node : NodeWorker.getInstance().getNodes().values()) {
			curvData.add(node.getMeanVariationPotential());
		}
		double max = Collections.max(curvData);
		double min = Collections.min(curvData);

		for(NodeWorker.Node node : NodeWorker.getInstance().getNodes().values()) {
			//int value = (int) ((node.getMeanVariationPotential() - min)*255/(max-min));
			int value = (int) (Math.pow((node.getMeanVariationPotential() - min)/(max-min), alpha)*255);
			//	System.out.println("mean variance potential "+ node.getLabel()+" " + value);
			for(Integer element : node.getElements()) {
				colors[element] = value;

			}
		}

		create(imageProcessor, colors);
        DataCollection.INSTANCE.addtoHistory(new ImagePlus("mean var pontential curve " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor));
    }

	private void createMeanPotential() {
		Integer[] colors = new Integer[DataCollection.INSTANCE.getShedLabels().length];
		for(int i = 0; i < colors.length; i++) {
			colors[i] = 0;
		}

		ArrayList<Double> curvData = new ArrayList<>();

		for(NodeWorker.Node node : NodeWorker.getInstance().getNodes().values()) {
			curvData.add(node.getMeanPotential());
		}
		double max = Collections.max(curvData);
		double min = Collections.min(curvData);

		for(NodeWorker.Node node : NodeWorker.getInstance().getNodes().values()) {
			//int value = (int) ((node.getMeanPotential() - min)*255/(max-min));
			int value = (int) (Math.pow((node.getMeanPotential() - min)/(max-min), alpha)*255);
			//System.out.println("mean potential "+ node.getLabel()+" " + value);
			for(Integer element : node.getElements()) {
				colors[element] = value;

			}
		}
		create(imageProcessor, colors);
        DataCollection.INSTANCE.addtoHistory(new ImagePlus("mean potential curve " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor));
	}

	private void createMeanCurvature(String title) {
		Integer[] colors = new Integer[DataCollection.INSTANCE.getShedLabels().length];
		for(int i = 0; i < colors.length; i++) {
			colors[i] = 0;
		}

		ArrayList<Double> curvData = new ArrayList<>();
		for(NodeWorker.Node node : NodeWorker.getInstance().getNodes().values()) {
			curvData.add(node.getMeanCurvature());
		}
		double max = Collections.max(curvData);
		double min = Collections.min(curvData);
		for(NodeWorker.Node node : NodeWorker.getInstance().getNodes().values()) {
			//int value = (int) ((node.getMeanCurvature() - min)*255/(max-min));// < 10 ? 0: 255;
			int value = (int) (Math.pow((node.getMeanCurvature() - min)/(max-min), alpha)*255);
			//System.out.println("curv "+ node.getLabel()+" "+ value);
			for(Integer element : node.getElements()) {
				colors[element] = value;

			}
		}
		create(imageProcessor, colors);
        DataCollection.INSTANCE.addtoHistory(new ImagePlus("mean curvature " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor));
	}
}
