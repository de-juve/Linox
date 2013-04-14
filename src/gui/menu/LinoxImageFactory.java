package gui.menu;

import ij.ImagePlus;

import java.util.ArrayList;

public class LinoxImageFactory {
    private final ArrayList<ImageJPanel> items = new ArrayList<>();

    public ImageJPanel addImage(ImagePlus image) {
        final ImageJPanel panel = new ImageJPanel(image);
        items.add(panel);
        return panel;
    }

    public ArrayList<ImageJPanel> getItems() {
        return items;
    }

    LinoxImageFactory() {
        items.add(new ImageJPanel());
    }
}

