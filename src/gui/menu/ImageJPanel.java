package gui.menu;

import gui.Linox;
import ij.ImagePlus;
import net.miginfocom.swing.MigLayout;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ImageJPanel extends JPanel {
    JScrollPane imageScrollPane;
    JLabel imageView = new JLabel();
    ImagePlus image;
    JButton button;
    MouseMotionAdapter mouseMotionAdapter;
    MouseAdapter mouseAdapter;
    private boolean mousePressed = false;

    ImageJPanel(ImagePlus _image) {
        this.setLayout(new MigLayout());
        image = _image;//new opencv_core.IplImage(_image);

        imageView.setIcon(new ImageIcon(image.getBufferedImage()));

        mouseMotionAdapter =  new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if(e.getX() < image.getWidth() && e.getX() > -1 && e.getY() < image.getHeight() && e.getY() > -1) {
                    int pixel = image.getProcessor().get(e.getX(), e.getY());
                    int r = (pixel & 0xff0000) >> 16;
                    int g = (pixel & 0x00ff00) >> 8;
                    int b = (pixel & 0x0000ff);
                    int lum = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                    int id = e.getX() + e.getY() * image.getWidth();
                    Linox.getInstance().getStatusBar().setStatus("(" + e.getX() + ", " + e.getY() + ") id = " + id + " RGB(" + r + ", " + g + ", " + b + ") lum(" + lum + ")");
                }
            }

        };
        imageView.addMouseMotionListener( mouseMotionAdapter);

        imageScrollPane = new JScrollPane(imageView, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        imageScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        imageScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));

        this.add(imageScrollPane);
    }

    ImageJPanel() {
        this.setLayout(new MigLayout());
        imageView.setText("No image");
        image = null;
        final JScrollPane imageScrollPane = new JScrollPane(imageView);
        // imageScrollPane.setPreferredSize(new Dimension(90, 30));
        add(imageScrollPane);
    }

    void setButton(Action action, boolean visible) {
        if(button != null) {
            this.remove(button);
        }
        button = new JButton(action);
        button.setVisible(visible);
        this.add(button);
    }

    void removeButton() {
        if(button != null) {
            this.remove(button);
        }
    }

    void setMouseListener(MouseAdapter adapter) {
        if(mouseAdapter != null) {
            imageView.removeMouseListener(mouseAdapter);
            imageView.removeMouseMotionListener(mouseAdapter);
        } else {
            imageView.removeMouseMotionListener(mouseMotionAdapter);
        }

        mouseAdapter = adapter;
        imageView.addMouseListener(mouseAdapter);
        imageView.addMouseMotionListener(mouseAdapter);

    }

    void resetMouseMotionListener() {
        imageView.removeMouseListener(mouseAdapter);
        imageView.removeMouseMotionListener(mouseAdapter);
        mouseAdapter = null;

        imageView.addMouseMotionListener(mouseMotionAdapter);
    }

    ImagePlus getImage() {
        return image;
    }

    public void setMousePressed(boolean value) {
        mousePressed = value;
    }

    public boolean getMousePressed() {
        return mousePressed;
    }

}
