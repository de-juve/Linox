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

        // imageProcessor = mCompilation.getResult(false).getProcessor();

        ShedColoring shedColoring = new ShedColoring();
        shedColoring.initProcessor(mCompilation.getResult(false).getProcessor());
        shedColoring.run();
        Color[] colors = new Color[width * height];
        for(ShedWorker.Shed shed : ShedWorker.getInstance().getSheds().values()) {
            for(Integer el : shed.getElements()) {
                if(shed.size() > 10) {
                    colors[el] = shed.getColor();
                } else {
                    colors[el] = Color.white;
                }
            }
        }


        create(imageProcessor, colors);

        //imageProcessor = shedColoring.getResult(false).getProcessor();

      /*  Integer biggestShedLabel = ShedWorker.getInstance().getLabelOfBiggestShed();
        Integer[] colors = new Integer[width*height];
        for(int i = 0; i < colors.length; i++) {
            colors[i] = 255;
        }
        for(Integer el : ShedWorker.getInstance().getShedElements(biggestShedLabel)) {
            colors[el] = 0;
        }
        for(ShedWorker.Shed shed : ShedWorker.getInstance().getSheds().values()) {
            if(shed.size() > 50) {
                for(Integer el : shed.getElements()) {
                    colors[el] = 0;
                }
            }
        }

        ShedWorker.getInstance().removeShed(biggestShedLabel);

        create(imageProcessor, colors);*/
    }
}
