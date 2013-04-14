package plugins;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import plugins.morfology.AreaEqualing;
import workers.MassiveWorker;
import workers.PixelsMentor;
import workers.ShedWorker;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;


public class Watershed extends MyAPlugin implements DialogListener {
	MassiveWorker worker;

	TreeMap<Integer, ArrayList<Integer>> steepestNeighboures;

	boolean[] maximum;

	Color[] colors;

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            result = new ImagePlus("watershed " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if(addToStack) {
                ImageProcessor ip = imageProcessor.duplicate();
                for(int i = 0; i < DataCollection.INSTANCE.getShedLabels().length; i++) {
                    colors[i] = ShedWorker.getInstance().getShedColor(DataCollection.INSTANCE.getShedLabel(i));
                }
                create(ip, colors);
                DataCollection.INSTANCE.addtoHistory(new ImagePlus("watershed colors " + criteria + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), ip));
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }
        return result;
    }

	@Override
	public void run() {

        showDialog("watershed");

		if(exit) {
			return;
		}

        if(DataCollection.INSTANCE.getMaxLuminance() < 255) {
            LuminanceRedirector luminanceRedirector = new LuminanceRedirector();
            luminanceRedirector.initProcessor(imageProcessor);
            luminanceRedirector.run();
            imageProcessor = luminanceRedirector.getResult(false).getProcessor();
        }

        int length =   width*height;
        DataCollection.INSTANCE.newWshPoints(length);
        colors = new Color[length];
        maximum = new boolean[length];

       // LuminanceCalculator luminanceCalculator = new LuminanceCalculator();

		LowerCompletion lowerCompletionPlugin = new LowerCompletion();

      /*  ImagePlus image;
        ImageProcessor imPr = ip.duplicate();*/

		//for(int iter = 0; iter < iterations; iter++) {
         //   if(iter > 0) {
                ShedWorker.getInstance().clear();
        //    }
            AreaEqualing areaEqualing = new AreaEqualing();
            areaEqualing.initProcessor(imageProcessor);
            areaEqualing.run();
            imageProcessor = areaEqualing.getResult(true).getProcessor();

            //luminanceCalculator.run(imageProcessor);
            lowerCompletionPlugin.initProcessor(imageProcessor);
			lowerCompletionPlugin.run();

			constructDAG();
			flood();

			create(imageProcessor, DataCollection.INSTANCE.getWshPoints());
           /* DataCollection.INSTANCE.addtoHistory(new ImagePlus("watershed colors " + criteria + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor.duplicate()));


            ImageProcessor ipExtansion = extansion(imageProcessor);

			ipExtansion = binaryProcessing(ipExtansion, iter);

            imageProcessor = shrinkage(ipExtansion);

            ipExtansion.reset();*/

	//	}
        //AreaEqualing areaEqualing = new AreaEqualing();
        areaEqualing.initProcessor(imageProcessor);
        areaEqualing.run();
        imageProcessor = areaEqualing.getResult(false).getProcessor();
	}

    private void constructDAG()
    {
        ImageStack stack = new ImageStack(width, height);
        ImageProcessor ip = imageProcessor.duplicate();
        Color[] colors = new Color[width*height];
        for(int i = 0; i < colors.length; i++) {
            colors[i] = Color.BLACK;
        }
        int count = 0;

        steepestNeighboures = new TreeMap<>();
        worker = new MassiveWorker();

        worker.sort(DataCollection.INSTANCE.getLuminances(), DataCollection.INSTANCE.getLowerCompletions());

        //start from pixels with min lower completion
        ArrayList<Integer> ids  = worker.getIds();

        //for(int i = 0; i < ids.size(); i++) {
        for(int i = ids.size()-1; i > -1; i--) {
            int id = ids.get(i);
            boolean isMinimum = true;


            ArrayList<Integer> neighboures = PixelsMentor.defineNeighboursIdsWithLowerValue(id, DataCollection.INSTANCE.getLuminances(), width, height);
            if(neighboures.size() > 0) {
                isMinimum = false;
                ArrayList<Integer> nids = new ArrayList<>();
                for(Integer ni : neighboures) {
                    maximum[ni] = false;
                    if(!steepestNeighboures.containsKey(ni)) {
                        nids.add(ni);
                    }
                    else {
                        for(Integer nid : steepestNeighboures.get(ni)) {
                            nids.add(nid);
                        }
                    }
                }
                if(nids.size() > 1) {
                    HashSet<Integer> hs = new HashSet<>();
                    hs.addAll(nids);
                    nids.clear();
                    nids.addAll(hs);
                }
                for(Integer nid : nids) {
                    maximum[nid] = false;
                }
                steepestNeighboures.remove(id);
                steepestNeighboures.put(id,nids);

            }
            else {
                neighboures.clear();
                //берем соседей с уровень яркости, как и самого пиксела
                neighboures = PixelsMentor.defineNeighboursIdsWithSameValue(id, DataCollection.INSTANCE.getLuminances(), width, height);
                //берем соседей с меньшим уровнем lowerCompletion
                ArrayList<Integer> eqneighboures = new ArrayList<>();
                for(Integer eq : neighboures) {
                    if(DataCollection.INSTANCE.getLowerCompletion(eq) < DataCollection.INSTANCE.getLowerCompletion(id)) {
                        eqneighboures.add(eq);
                    }
                }
                neighboures.clear();
                neighboures.addAll(eqneighboures);
                if(neighboures.size() > 0) {
                    isMinimum = false;

                    ArrayList<Integer> nids = new ArrayList<>();
                    for(Integer ni : neighboures) {
                        maximum[ni] = false;
                        if(!steepestNeighboures.containsKey(ni)) {
                            nids.add(ni);
                            continue;
                        }
                        for(Integer nid : steepestNeighboures.get(ni)) {
                            nids.add(nid);
                        }
                    }
                    if(nids.size() > 1) {
                        HashSet<Integer> hs = new HashSet<>();
                        hs.addAll(nids);
                        nids.clear();
                        nids.addAll(hs);
                    }
                    if(nids.size() > 0) {
                        maximum[i] = true;
                        isMinimum = false;
                        for(Integer nid : nids) {
                            maximum[nid] = false;
                        }
                        steepestNeighboures.remove(id);
                        steepestNeighboures.put(id,nids);
                    }
                }
            }

            if(isMinimum) {
                maximum[id] = false;
                colors[id] = Color.BLUE;
            } else {
                maximum[id] = true;
                colors[id] = Color.RED;
            }

            if(count ==  width*height/25) {
               for(int ii = ids.size()-1; ii > -1; ii--) {
                    if(maximum[ids.get(ii)]) {
                        colors[ids.get(ii)] = Color.RED;
                    } else if(!colors[ids.get(ii)].equals(Color.BLACK)) {
                        colors[ids.get(ii)] = Color.CYAN;
                    }
                }
                create(ip, colors);
                stack.addSlice(ip.duplicate());
                count = -1;
            }
            count++;

            //define canonical element of min region if it need and if we can
            if(isMinimum) {
                int canonical = ShedWorker.getInstance().getCanonical(DataCollection.INSTANCE.getShedLabel(id));
                //if canonical doesn't init
                if(canonical == -1) {
                    canonical = id;
                    ShedWorker.getInstance().setCanonical(DataCollection.INSTANCE.getShedLabel(id), canonical);
                }
                ArrayList<Integer> ar = new ArrayList<>(1);
                ar.add(0, canonical);
                steepestNeighboures.remove(id);
                steepestNeighboures.put(id, ar);
            }
        }
        create(ip, colors);
        stack.addSlice(ip.duplicate());
        new ImagePlus("DAG", stack).show();
    }

    private void flood() {
        ArrayList<Integer> ids  = worker.getIds();
        //start from pixels with min property
        for(int i =  ids.size() -1; i >= 0; i--) {
            int p = ids.get(i);
            int rep = resolve(p);

            if(rep == -1 /*&& maximum[p]*/ ) {
                DataCollection.INSTANCE.setWshPoint(p, 255);
            } else {
                DataCollection.INSTANCE.setWshPoint(p, 0);
            }
        }
    }

    //Recursive function for resolving the downstream paths of the lower complete graph
    //Returns representative element of pixel p, or W if p is a watershed pixel
    private int resolve(int p)
    {
        int i = 0;
        int rep =  -2;
        ArrayList<Integer> stN = steepestNeighboures.get(p);
        if(stN == null)
            return rep;
        int con = stN.size();
        while( i < con && rep != -1) {
            int sln = stN.get(i);
            if(sln != p && sln != -1) {
                sln = resolve(sln);
                stN.set(i, sln);
            }
            if(i == 0) {
                rep = stN.get(i);
            }
            else if(sln != rep) {
                rep = -1;
                stN.clear();
                stN.add(-1);
            }
            i++;
        }
        /*if( steepestNeighboures.get(p).size() > 1) {
            HashSet<Integer> hs = new HashSet<>();
            hs.addAll(steepestNeighboures.get(p));
            steepestNeighboures.get(p).clear();
            steepestNeighboures.get(p).addAll(hs);
        }*/
        return rep;
    }

	private ImageProcessor binaryProcessing(ImageProcessor imageProcessor, int iter) {
		BinaryProcessor bp =  new BinaryProcessor((ByteProcessor)(imageProcessor.convertToByte(false)));
		bp.invert();
        //new MyImagePlus("binary "+ iter, bp.convertToRGB().duplicate()).show();

        bp.dilate();
       // new MyImagePlus("binary dilate "+ iter, bp.convertToRGB().duplicate()).show();

		bp.skeletonize();
		bp.invert();
        //new MyImagePlus("binary skelet "+ iter, bp.convertToRGB().duplicate()).show();

		return bp.convertToRGB().duplicate();
	}

	private ImageProcessor extansion(ImageProcessor imageProcessor) {
		ImageProcessor result = imageProcessor.createProcessor(width+10, height+10);
		Rectangle rectangle = imageProcessor.getRoi();
		int offset, i, offset2, i2;
		for (int y=rectangle.y; y<(rectangle.y+rectangle.height); y++) {
			offset = y*width;
			offset2 = (y+5) * (width+10);
			for (int x=rectangle.x; x<(rectangle.x+rectangle.width); x++) {
				i = offset + x;
				i2 = offset2 + x+5;
				int value =  imageProcessor.get(i);
				result.set(i2,value);
                if (y != 0 && x != 0 && y != height-1 && x != width-1) {
                    continue;
                }
                if(y == 0)  {
                    int z = y;
                    while (z <= y+5) {
                        result.set(x+5,z,value);
                        z++;
                    }
                }
                if(y == height-1 ) {
                    int z = y+5;
                    while (z <= y+10) {
                        result.set(x+5,z,value);
                        z++;
                    }
                }
                if( x== 0) {
                    int z = x;
                    while (z < x+5) {
                        result.set(z,y+5,value);
                        z++;
                    }
                }
                if( x == width-1) {
                    int z = x+5;
                    while (z <= x+10) {
                        result.set(z,y+5,value);
                        z++;
                    }
                }
            }
		}
		return result;
	}

	private ImageProcessor shrinkage(ImageProcessor imageProcessor) {
		ImageProcessor result = imageProcessor.createProcessor(width, height);
		Rectangle rectangle = result.getRoi();
		int offset, i, offset2, i2;

		for (int y=rectangle.y; y<(rectangle.y+rectangle.height); y++) {
			offset = y*width;
			offset2 = (y+5) * (width+10);
			for (int x=rectangle.x; x<(rectangle.x+rectangle.width); x++) {
				i = offset + x;
				i2 = offset2 + x+5;
				int value =  imageProcessor.get(i2);
				result.set(i,value);
			}
		}

		return result;
	}

	protected void myRecoverWatershedLines() {
		recoverWatershedLines();
		/*watersheds.clear();
		for(Integer i = 0; i < DataCollection.INSTANCE.getLuminances().length; i++) {
			if(DataCollection.INSTANCE.getWshPoint(i) > 0 ) {
				watersheds.add(i);
			}
		}
		*/
	}

	protected void showDialog(String name) {
		GenericDialog gd = new GenericDialog(name, IJ.getInstance());
      //  gd.addSlider("Iterations", 1, 5, 1);
        gd.addSlider("Luminance of road", 0, DataCollection.INSTANCE.getMaxLuminance(), DataCollection.INSTANCE.getMaxLuminance());

        gd.addDialogListener(this);

		gd.showDialog();
		if (gd.wasCanceled()) {
			exit = true;
			return;
		}
	}

    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
     //   iterations = (int) gd.getNextNumber();
        DataCollection.INSTANCE.setMaxLuminance((int) gd.getNextNumber());
        return true;
    }
}
