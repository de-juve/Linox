package gui.menu;


import gui.Linox;
import plugins.DataCollection;
import plugins.MyPlugInFilter;

import javax.swing.*;
import java.awt.*;

public class PluginRunner implements Runnable {
    MyPlugInFilter plugin;

    public void setPlugin(MyPlugInFilter plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            Linox.getInstance().getImageStore().setCursor(new Cursor(Cursor.WAIT_CURSOR));
            Linox.getInstance().getImageStore().setCursorType(Cursor.WAIT_CURSOR);
            ((LinoxMenuStore) Linox.getInstance().getMenuStore()).setEnableEditToolsItems(false);
            DataCollection.INSTANCE.clearHistory();
            plugin.initProcessor(DataCollection.INSTANCE.getImageOriginal().getProcessor().convertToRGB().duplicate());
            plugin.run();
            if(plugin.exit()) {
                JOptionPane.showMessageDialog(Linox.getInstance(), "plugin " + plugin.getTitle() + " stoped. Becouse: " + plugin.getErrMessage());
                return;
            }
            DataCollection.INSTANCE.setImageResult(plugin.getResult(true));
            DataCollection.INSTANCE.setMaxLuminance(255);

            (Linox.getInstance().getImageStore()).addImageTab(DataCollection.INSTANCE.getImageResult().getTitle(), DataCollection.INSTANCE.getImageResult());
            Linox.getInstance().setPreferredSize(new Dimension(Math.min(640, plugin.getResult(false).getBufferedImage().getWidth() + 3), Math.min(480, plugin.getResult(false).getBufferedImage().getHeight() + 3)));
        } finally {
            ((LinoxMenuStore) Linox.getInstance().getMenuStore()).setEnableEditToolsItems(true);
            Linox.getInstance().getImageStore().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Linox.getInstance().getImageStore().setCursorType(Cursor.DEFAULT_CURSOR);
        }


    }
}
