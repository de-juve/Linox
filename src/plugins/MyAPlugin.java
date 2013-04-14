package plugins;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.*;


public abstract class MyAPlugin  implements MyPlugInFilter{
    protected ImageProcessor imageProcessor;
    protected ImagePlus result = null;
	protected int width, height;
    protected boolean exit = false;
    protected int criteria;
  //  protected int iterations;
    protected String title;

    public ImagePlus getResult(boolean addToStack) {
		return result;
	}

    @Override
    public void run() {}

    public void initProcessor(ImageProcessor ip) {
        imageProcessor = ip;
        initSize(ip.getWidth(), ip.getHeight());
    }

	protected void initSize(int w, int h) {
		width = w;
		height = h;
	}

	public boolean exit() {
		return exit;
	}

	protected void setCriteria(int criteria) {
		this.criteria = criteria;
	}

	protected int getCriteria() {
		return criteria;
	}

	/*public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}*/

	protected void create(ImageProcessor ip, Integer[] array) {
        for(int i = 0; i < array.length; i++) {
            int value = (((array[i] & 0xff) << 16) +
                    ((array[i] & 0xff) << 8) +
                    (array[i] & 0xff));
            ip.set(i,value);
        }
	}

	protected void create(ImageProcessor ip, Color[] colors) {
        for(int i = 0; i < colors.length; i++) {
            int value = colors[i].getRGB();
            ip.set(i,value);
        }
	}

	protected void recoverWatershedLines() {
        DataCollection.INSTANCE.newWaterShedPoints();
        DataCollection.INSTANCE.newWshPoints(DataCollection.INSTANCE.getLuminances().length);

		for(Integer i = 0; i < DataCollection.INSTANCE.getLuminances().length; i++) {
			if(DataCollection.INSTANCE.getLuminance(i) > 0 ) {
				DataCollection.INSTANCE.setWshPoint(i, 255);
                DataCollection.INSTANCE.addWaterShedPoint(i);
            }
			else {
				DataCollection.INSTANCE.setWshPoint(i, 0);
			}
		}
	}


}
