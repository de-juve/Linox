package plugins.smartShedding;

import ij.ImagePlus;
import plugins.DataCollection;
import plugins.LuminanceCalculator;
import plugins.MyAPlugin;
import plugins.ShedColoring;
import plugins.imageOperations.ImageOperationFactory;
import plugins.morfology.MorphologyCompilation;
import workers.ShedWorker;

import java.awt.*;

public class SmartShedding extends MyAPlugin {
    public SmartShedding() {
        title = "Smart Shedding";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if (result == null) {
            result = new ImagePlus("Smart Shedding result " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);

            if (addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }
        return result;
    }

    @Override
    public void run() {
        String typeCompilation = "Smart minus Max";
        ImageOperationFactory factory = new ImageOperationFactory();
        if(!factory.contain(typeCompilation)) {
            setErrMessage("Mistake type of compilation: '" + typeCompilation + "'");
            exit = true;
            return;
        }

        LuminanceCalculator lumCalculator = new LuminanceCalculator();
        lumCalculator.initProcessor(imageProcessor);
        lumCalculator.run();


        MorphologyCompilation mCompilation = new MorphologyCompilation();
        mCompilation.initProcessor(imageProcessor.duplicate());
        mCompilation.run();
         imageProcessor = mCompilation.getResult(false).getProcessor();

        /*ShedColoring shedColoring = new ShedColoring();
        shedColoring.initProcessor(mCompilation.getResult(false).getProcessor());
        shedColoring.run();*/

        Color[] colors = new Color[width * height];
        for(ShedWorker.Shed shed : ShedWorker.getInstance().getSheds().values()) {
            shed.countMeanPotential();
        }
        int deviation = 20;
        for(ShedWorker.Shed shed : ShedWorker.getInstance().getSheds().values()) {
            int mp = shed.getMeanPotential();
            for(ShedWorker.Shed shed2 : ShedWorker.getInstance().getSheds().values()) {
                if(!shed2.equals(shed) && Math.abs(mp - shed2.getMeanPotential()) <= deviation) {
                    for(Integer el : shed2.getElements()) {
                        colors[el] = shed.getColor();
                    }
                }
            }
        }

        create(imageProcessor, colors);


    }
}
