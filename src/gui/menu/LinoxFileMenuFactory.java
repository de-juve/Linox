package gui.menu;

import gui.Linox;
import ij.ImagePlus;
import plugins.DataCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class LinoxFileMenuFactory {
    private final ArrayList<JMenuItem> items = new ArrayList<>();

    public ArrayList<JMenuItem> getItems() {
        return items;
    }

    public LinoxFileMenuFactory() {
        final Action openImage = new AbstractAction("Open Image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    // Load image and update display. If new image was not loaded do nothing.
                    final ImagePlus img = ((LinoxMenuStore)Linox.getInstance().getMenuStore()).openImage();
                    if(img != null) {
                        DataCollection.INSTANCE.setImageOriginal(img);
                        Linox.getInstance().getStatusBar().setStatus(img.getTitle() + ": width = " + img.getWidth() + " height = " + img.getHeight());
                    }
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        final Action saveImage = new AbstractAction("Save Image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    if(DataCollection.INSTANCE.getImageOriginal() != null) {
                        ((LinoxMenuStore)Linox.getInstance().getMenuStore()).saveImage(DataCollection.INSTANCE.getImageOriginal());
                    }
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        final Action closeImage = new AbstractAction("Close Image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    ((LinoxMenuStore)Linox.getInstance().getMenuStore()).closeImage();
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        final Action exit = new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    ((LinoxMenuStore)Linox.getInstance().getMenuStore()).exit();
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        items.add(new JMenuItem(openImage));
        items.add(new JMenuItem(saveImage));
        items.add(new JMenuItem(closeImage));
        items.add(new JMenuItem(exit));
        // items.add(new JMenuItem(test));

    }
}
