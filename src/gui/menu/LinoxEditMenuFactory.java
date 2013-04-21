package gui.menu;

import gui.Linox;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.Histogram;
import ij.plugin.Scaler;
import plugins.*;
import plugins.gradationConversions.GradationConverter;
import plugins.histogramChanging.HistogramChanger;
import plugins.imageOperations.ImageOperator;
import plugins.morfology.AreaClosing;
import plugins.morfology.AreaEqualing;
import plugins.morfology.AreaOpening;
import plugins.morfology.MorfologyCompilation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

public class LinoxEditMenuFactory {
    private final ArrayList<JMenuItem> items = new ArrayList<>();

    public ArrayList<JMenuItem> getItems() {
        return items;
    }

    public LinoxEditMenuFactory() {
        final PluginRunner pluginRunner = new PluginRunner();

        final Action gradientCalculator = new AbstractAction("Gradient") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new GradientCalculator());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action laplasianCalculator = new AbstractAction("Laplasian") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new Laplasian());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action lowerCompletion = new AbstractAction("Lower completion") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new LowerCompletion());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action gradationCoversion = new AbstractAction("Gradation conversion") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new GradationConverter());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action histChanger = new AbstractAction("Histogram chaniging") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new HistogramChanger());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action imageOperator = new AbstractAction("Image operations") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new ImageOperator());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action opening = new AbstractAction("Morfology opening") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new AreaOpening());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action closing = new AbstractAction("Morfology closing") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new AreaClosing());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action equaling = new AbstractAction("Morfology equaling") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new AreaEqualing());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };


        final Action morfologyCompilation = new AbstractAction("Morfology compilation") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new MorfologyCompilation());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action shedColoring = new AbstractAction("Sheds coloring") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new ShedColoring());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };


        final Action luminance = new AbstractAction("Luminance") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new LuminanceCalculator());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action watershed = new AbstractAction("Watershed") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new Watershed());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action watershed2 = new AbstractAction("Watershed2") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new Watershed2());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action watershed3 = new AbstractAction("Watershed3") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new Watershed3());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action edgeDetector = new AbstractAction("Edge Detector") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new DetectEdges());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action curveAnalizer = new AbstractAction("Curve Analyzer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new CurveAnalyzer());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action gac = new AbstractAction("Build GAC") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new GACBuilder());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action homotopy = new AbstractAction("Homotopy") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new HomotopyFilter());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action luminaceRedirector = new AbstractAction("Luminance redirector") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new LuminanceRedirector());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action luminaceDiscretizator = new AbstractAction("Luminance discretization") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new LuminanceDiscretizator());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };

        final Action medianFilter = new AbstractAction("Median filter") {
            @Override
            public void actionPerformed(ActionEvent e) {
                pluginRunner.setPlugin(new MedianFilter());
                Thread myThready = new Thread(pluginRunner);
                myThready.start();
            }
        };



        final Action linePainter = new AbstractAction("Line extrapolation") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final ImageJPanel panel = Linox.getInstance().getImageStore().getSelectedTab();
                final ImagePlus image = panel.image.duplicate();

                panel.setButton(new AbstractAction("Paint line") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        panel.setMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                if (panel.getMousePressed()) {
                                    image.getProcessor().set(e.getX(), e.getY(), Color.RED.getRGB());
                                    panel.imageView.setIcon(new ImageIcon(image.getBufferedImage()));
                                    int id = e.getX() + e.getY() * image.getWidth();
                                    DataCollection.INSTANCE.addPointToLine(id);
                                }
                            }

                            @Override
                            public void mousePressed(MouseEvent e) {
                                panel.setMousePressed(true);
                                DataCollection.INSTANCE.newLine();
                            }

                            @Override
                            public void mouseReleased(MouseEvent e) {
                                panel.setMousePressed(false);
                                panel.resetMouseMotionListener();

                                panel.setButton(new AbstractAction("Extrapolation line") {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        pluginRunner.setPlugin(new LineExtrapolation());
                                        Thread myThready = new Thread(pluginRunner);
                                        myThready.start();
                                        panel.removeButton();
                                        panel.imageView.setIcon(new ImageIcon(panel.image.getBufferedImage()));
                                        (Linox.getInstance().getImageStore()).addImageTab(image.getTitle(), image.duplicate());
                                        image.close();
                                    }
                                }, true);
                            }
                        });
                    }
                }, true);

            }
        };

        final Action thickening = new AbstractAction("Line Thickening") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final ImageJPanel panel = Linox.getInstance().getImageStore().getSelectedTab();
                final ImagePlus image = panel.image.duplicate();

                panel.setButton(new AbstractAction("Paint line") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        panel.setMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                if (panel.getMousePressed()) {
                                    image.getProcessor().set(e.getX(), e.getY(), Color.RED.getRGB());
                                    panel.imageView.setIcon(new ImageIcon(image.getBufferedImage()));
                                    int id = e.getX() + e.getY() * image.getWidth();
                                    DataCollection.INSTANCE.addPointToLine(id);
                                }
                            }

                            @Override
                            public void mousePressed(MouseEvent e) {
                                panel.setMousePressed(true);
                                DataCollection.INSTANCE.newLine();
                            }

                            @Override
                            public void mouseReleased(MouseEvent e) {
                                panel.setMousePressed(false);
                                panel.resetMouseMotionListener();

                                panel.setButton(new AbstractAction("Line thickening") {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        pluginRunner.setPlugin(new LineThickening());
                                        Thread myThready = new Thread(pluginRunner);
                                        myThready.start();
                                        panel.removeButton();
                                        panel.imageView.setIcon(new ImageIcon(panel.image.getBufferedImage()));
                                        (Linox.getInstance().getImageStore()).addImageTab(image.getTitle(), image.duplicate());
                                        image.close();
                                    }
                                }, true);
                            }
                        });
                    }
                }, true);

            }
        };

        final Action test1 = new AbstractAction("Test closing and opening") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    String dir =  System.getProperty("user.dir") + "/resource/test_1";
                    File[] list = new File(dir).listFiles();
                    String dir_r =  dir + "/morf/";;
                    if (list != null) {
                        for (int i = 0; i < list.length; i++) {
                            if(!list[i].isFile() || list[i].getName().contains("r_o") || list[i].getName().contains("r_c") || list[i].getName().contains(".DS_Store"))
                                continue;

                            System.out.println(list[i].getAbsolutePath());
                            String name = list[i].getName().substring(0, list[i].getName().indexOf('.'));
                            String extension = list[i].getName().substring(list[i].getName().indexOf('.'));
                            loadImage(list[i]);

                            int criteria = 5000;
                            int size = DataCollection.INSTANCE.getImageOriginal().getWidth() *  DataCollection.INSTANCE.getImageOriginal().getHeight();

                            AreaOpening plugin = new AreaOpening();
                            plugin.setCriteria(Math.min(size, criteria));
                            plugin.initProcessor(DataCollection.INSTANCE.getImageOriginal().getProcessor().duplicate());
                            plugin.run();
                            if(plugin.exit()) {
                                return;
                            }
                            DataCollection.INSTANCE.setImageResult(plugin.getResult(true));
                            IJ.save(DataCollection.INSTANCE.getImageResult(), dir_r + name + "_opening_" + criteria + extension);

                            AreaClosing plugin2 = new AreaClosing();
                            plugin2.setCriteria(Math.min(size, criteria));
                            plugin2.initProcessor(DataCollection.INSTANCE.getImageOriginal().getProcessor());
                            plugin2.run();
                            if(plugin2.exit()) {
                                return;
                            }
                            DataCollection.INSTANCE.setImageResult(plugin2.getResult(true));
                            IJ.save(DataCollection.INSTANCE.getImageResult(), dir_r + name + "_closing_" + criteria + extension);

                            DataCollection.INSTANCE.getImageOriginal().close();
                            DataCollection.INSTANCE.getImageResult().close();
                        }
                    }
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        final Action test2 = new AbstractAction("Test homotopy") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    String dir =  System.getProperty("user.dir") + "/resource/test_1/morf";
                    File[] list = new File(dir).listFiles();
                    String dir_r =   System.getProperty("user.dir") + "/resource/test_1/homotopy/";
                    if (list != null) {
                        for (int i = 0; i < list.length; i++) {

                            if(!list[i].isFile() || list[i].getName().contains(".DS_Store"))
                                continue;

                            String name = list[i].getName().substring(0, list[i].getName().indexOf('.'));
                            String extension = list[i].getName().substring(list[i].getName().indexOf('.'));
                            loadImage(list[i]);

                            HomotopyFilter plugin = new HomotopyFilter();
                            plugin.setAreaSizeX(13);
                            plugin.setAreaSizeY(13);
                            plugin.setDeviation(10);
                            plugin.initProcessor(DataCollection.INSTANCE.getImageOriginal().getProcessor().duplicate());
                            plugin.run();
                            if(plugin.exit()) {
                                return;
                            }

                            DataCollection.INSTANCE.setImageResult(plugin.getResult(true));
                            IJ.save(DataCollection.INSTANCE.getImageResult(), dir_r + name + "_hom" + extension);


                            DataCollection.INSTANCE.getImageOriginal().close();
                            DataCollection.INSTANCE.getImageResult().close();
                        }
                    }
                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        final Action scaler = new AbstractAction("Scaler") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    WindowManager.setTempCurrentImage(DataCollection.INSTANCE.getImageOriginal());
                    Scaler scaler = new Scaler();
                    scaler.run("scaler");
                    if( IJ.getImage() != null && WindowManager.getCurrentWindow() != null)  {
                        DataCollection.INSTANCE.setImageResult((ImagePlus) IJ.getImage().clone());

                        ((LinoxImageStore) Linox.getInstance().getImageStore()).addImageTab(WindowManager.getCurrentWindow().getTitle(), DataCollection.INSTANCE.getImageResult());
                        WindowManager.closeAllWindows();
                    }

                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        final Action histogram = new AbstractAction("Histogram") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    WindowManager.setTempCurrentImage(DataCollection.INSTANCE.getImageOriginal());
                    Histogram histogram = new Histogram();
                    histogram.run("");
                    if( IJ.getImage() != null && WindowManager.getCurrentWindow() != null)  {
                        DataCollection.INSTANCE.setImageResult((ImagePlus) IJ.getImage().clone());

                        (Linox.getInstance().getImageStore()).addImageTab(WindowManager.getCurrentWindow().getTitle(), DataCollection.INSTANCE.getImageResult());
                        WindowManager.closeAllWindows();
                    }

                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        final Action color_deconvolution = new AbstractAction("Color deconvolution") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Linox.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    WindowManager.setTempCurrentImage(DataCollection.INSTANCE.getImageOriginal());
                    Colour_Deconvolution plugin = new Colour_Deconvolution();
                    plugin.run("");
                    if( IJ.getImage() != null && WindowManager.getCurrentWindow() != null)  {
                        DataCollection.INSTANCE.setImageResult((ImagePlus) IJ.getImage().clone());

                        ((LinoxImageStore) Linox.getInstance().getImageStore()).addImageTab(WindowManager.getCurrentWindow().getTitle(), DataCollection.INSTANCE.getImageResult());
                        WindowManager.closeAllWindows();
                    }

                } finally {
                    Linox.getInstance().setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        items.add(new JMenuItem(gradientCalculator));
        items.add(new JMenuItem(laplasianCalculator));
        items.add(new JMenuItem(lowerCompletion));
        items.add(new JMenuItem(gradationCoversion));
        items.add(new JMenuItem(histChanger));
        items.add(new JMenuItem(imageOperator));
        items.add(new JMenuItem(luminance));
        items.add(new JMenuItem(luminaceRedirector));
        items.add(new JMenuItem(luminaceDiscretizator));
        items.add(new JMenuItem(opening));
        items.add(new JMenuItem(closing));
        items.add(new JMenuItem(equaling));
        items.add(new JMenuItem(morfologyCompilation));
        items.add(new JMenuItem(shedColoring));

        items.add(new JMenuItem(watershed));
        items.add(new JMenuItem(watershed2));
        items.add(new JMenuItem(watershed3));
        items.add(new JMenuItem(edgeDetector));
        items.add(new JMenuItem(gac));
        items.add(new JMenuItem(curveAnalizer));
        items.add(new JMenuItem(homotopy));
        items.add(new JMenuItem(scaler));
        items.add(new JMenuItem(histogram));
        items.add(new JMenuItem(color_deconvolution));
        items.add(new JMenuItem(linePainter));
        items.add(new JMenuItem(thickening));
        items.add(new JMenuItem(medianFilter));

        for(JMenuItem item : items) {
            item.setEnabled(false);
        }

        /*JMenuItem item = new JMenuItem(test1);
        item.setEnabled(true);
        items.add(item);

        item = new JMenuItem(test2);
        item.setEnabled(true);
        items.add(item);*/

    }

    private void loadImage (File file) {
        ij.ImagePlus img =   IJ.openImage(file.getPath());
        DataCollection.INSTANCE.setImageOriginal(new ImagePlus(file.getName(), img.getProcessor().convertToRGB()));
    }
}
