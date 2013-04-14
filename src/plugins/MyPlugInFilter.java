package plugins;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public interface MyPlugInFilter extends Runnable {
    void initProcessor(ImageProcessor ip);
    ImagePlus getResult(boolean addToStack);
	boolean exit();
    void run();
}
