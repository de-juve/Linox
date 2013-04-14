import ij.*;
import ij.plugin.*;
import ij.process.*;
import ij.gui.*;

/*
IsoPhotContour_ by Gabriel Landini G.Landini at bham. ac. uk
This plugin creates a number of contour level curves equally separated in the
greyscale space
20 Oct 2003 Released 1.0
30 Nov 2003 version 1.1, changed equality test for strings.
3 Feb 2007 version 1.2 supports stacks, added 'none'


*/
public class IsoPhotContour2_ implements PlugIn {

	public void run(String arg) {

		ImagePlus img = WindowManager.getCurrentImage();
		if (img==null){
			IJ.error("Error","No image!.\nPlease open an  8-bit image.");
			return;
		}

		if (img.getType()!=ImagePlus.GRAY8){
			IJ.error("Error","8 bit images only!");
			return;
		}

		ImageProcessor ip = img.getProcessor();
		int stk = img.getStackSize();

		int i, x, y, v, j=0, bck;
		int xe=ip.getWidth(), ye=ip.getHeight();
		int [] lev = {28,56,84,112,140,168,196,224};
		int [] col = new  int[8];
		int [][] p = new  int[xe][ye];
		boolean done=false, to8bit=false, contr=false, bb=false;
		String [] coverOption={"red","orange","yellow","green","cyan","blue","magenta","black","none"};
		String [] selcol = {"red","orange","yellow","green","cyan","blue","magenta","black","none"};


		GenericDialog gd = new GenericDialog("IsoPhot 2 v1.2");
		for (i=0;i<8;i++){
			j=8-i;
			gd.addNumericField ("Level_"+j,  lev[j-1], 0);
			gd.addChoice("Colour_"+j, coverOption, selcol[j-1]);
		}

			gd.addCheckbox ("Contours only",contr);
			gd.addCheckbox ("Black background",bb);
			gd.addCheckbox ("Convert to 8bit colour", false);

			gd.showDialog();
			if (gd.wasCanceled()){
				done=true;
				return;
			}

			for (i=0;i<8;i++){
				j=7-i;
				lev[j]=(int) gd.getNextNumber();
				selcol[j] = gd.getNextChoice ();
				if (selcol[j].equals("none"))
					lev[j] =100000;
				if (selcol[j].equals("red"))
					col[j]=((255 & 0xff)<<16)+((0&0xff)<<8)+(0&0xff);
				if (selcol[j].equals("blue"))
					col[j]=((0 & 0xff)<<16)+((0&0xff)<<8)+(255&0xff);
				if (selcol[j].equals("magenta"))
					col[j]=((255 & 0xff)<<16)+((0&0xff)<<8)+(255&0xff);
				if (selcol[j].equals("orange"))
					col[j]=((255 & 0xff)<<16)+((128&0xff)<<8)+(0&0xff);
				if (selcol[j].equals("green"))
					col[j]=((0 & 0xff)<<16)+((255&0xff)<<8)+(0&0xff);
				if (selcol[j].equals("cyan"))
					col[j]=((0 & 0xff)<<16)+((255&0xff)<<8)+(255&0xff);
				if (selcol[j].equals("yellow"))
					col[j]=((255 & 0xff)<<16)+((255&0xff)<<8)+(0&0xff);
				if (selcol[j].equals("black"))
					col[j]=((0 & 0xff)<<16)+((0 & 0xff)<<8)+(0&0xff);
			}

			contr= gd.getNextBoolean();
			bb= gd.getNextBoolean();
			to8bit= gd.getNextBoolean();

		IJ.run("Duplicate...", "title=IsoPhot duplicate");
		IJ.run("RGB Color");
		//IJ.selectWindow("IsoPhot");
		ImagePlus img2 = WindowManager.getCurrentImage();
		ImageProcessor ip2 = img2.getProcessor();

		if(bb==true)
			bck=0;
		else
			bck=((255 & 0xff)<<16)+((255 & 0xff)<<8)+(255 & 0xff);

		for (i=1; i<=stk; i++) {

			img.setSlice(i);

			for (y=0;y<ye; y++) {
				for (x=0; x<xe; x++) {
					p[x][y]=ip.getPixel(x,y);
				}
			}

			img2.setSlice(i);

			if (contr==true){
				for (y=0;y<ye; y++) {
					for (x=0; x<xe; x++)
						ip2.putPixel(x,y,bck);
				}
			}
			img.updateAndDraw();

			for (y=1;y<ye-1;y++) {
				for (x=1;x<xe-1;x++) {
					for (v=0;v<8;v++)
						if(p[x][y] <= lev[v] && (p[x-1][y-1] > lev[v] || p[x][y-1] > lev[v] || p[x+1][y-1] > lev[v]
						|| p[x-1][y]> lev[v] || p[x+1][y] > lev[v] || p[x-1][y+1]>lev[v] || p[x][y+1]>lev[v] || p[x+1][y+1]> lev[v]))
						ip2.putPixel(x,y,col[v]);
						//ip2.putPixel(x,y,((255 & 0xff)<<16)+((0&0xff)<<8)+(0&0xff));
				}
			}
			// top, bottom, left & right borders
			y=0;
			for (x=1;x<xe-1;x++) {
				for (v=0;v<8;v++)
					if(p[x][y] <= lev[v] && (p[x-1][y]> lev[v] || p[x+1][y] > lev[v] ||
					 p[x-1][y+1]>lev[v] || p[x][y+1]>lev[v] || p[x+1][y+1]> lev[v]))
					ip2.putPixel(x,y,col[v]);
			}
			y=ye-1;
			for (x=1;x<xe-1;x++) {
				for (v=0;v<8;v++)
					if(p[x][y] <= lev[v] && (p[x-1][y-1] > lev[v] || p[x][y-1] > lev[v] ||
					p[x+1][y-1] > lev[v] || p[x-1][y]> lev[v] || p[x+1][y] > lev[v]))
						ip2.putPixel(x,y,col[v]);
			}
			x=0;
			for (y=1;y<ye-1;y++) {
				for (v=0;v<8;v++)
					if(p[x][y] <= lev[v] && (p[x][y-1] > lev[v] || p[x+1][y-1] > lev[v] ||
					p[x+1][y] > lev[v] || p[x][y+1]>lev[v] || p[x+1][y+1]> lev[v]))
						ip2.putPixel(x,y,col[v]);
			}
			x=xe-1;
			for (y=1;y<ye-1;y++) {
				for (v=0;v<8;v++)
					if(p[x][y] <= lev[v] && (p[x-1][y-1] > lev[v] || p[x][y-1] > lev[v] ||
					p[x-1][y]> lev[v] || p[x-1][y+1]>lev[v] || p[x][y+1]>lev[v]))
					ip2.putPixel(x,y,col[v]);
			}
			img2.updateAndDraw();
		}
		if (to8bit==true)
			IJ.run("8-bit Color", "number=256");

	}
}
