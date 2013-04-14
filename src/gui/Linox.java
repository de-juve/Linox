package gui;

import gui.menu.LinoxImageStore;
import gui.menu.LinoxMenuStore;
import javax.swing.*;
import java.awt.*;



public class Linox extends JFrame {
    private volatile static Linox linox;
    //GUI
    private JPanel mainPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final Linox frame = new Linox();
                frame.pack();
                // Mark for display in the center of the screen
                frame.setLocationRelativeTo(null);
                frame.setContentPane(Linox.getInstance().$$$getRootComponent$$$());
                frame.setSize(640, 480);
                // Exit application when frame is closed.
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

    private Linox() throws HeadlessException {
        super("Linox");
        $$$setupUI$$$();
        createUIComponents();
        linox = this;
    }

    public static Linox getInstance() {
        if (linox == null) {
            synchronized (Linox.class) {
                if (linox == null) {
                    linox = new Linox();
                }
            }
        }
        return linox;
    }

    public JMenuBar getMenuStore() {
        return (JMenuBar) mainPanel.getComponent(0);
    }

    public LinoxImageStore getImageStore() {
        return (LinoxImageStore) mainPanel.getComponent(1);
    }

    public StatusBar getStatusBar() {
        return (StatusBar) mainPanel.getComponent(2);
    }

    private void createUIComponents() {
        mainPanel.add(new LinoxMenuStore(), BorderLayout.NORTH, 0);

        mainPanel.add(new LinoxImageStore(), BorderLayout.CENTER, 1);

        mainPanel.add(new StatusBar(), BorderLayout.SOUTH, 2);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMaximumSize(new Dimension(1024, 978));
        mainPanel.setMinimumSize(new Dimension(200, 100));
       // mainPanel.setPreferredSize(new Dimension(200, 100));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    public void showStatus(String s) {
       getStatusBar().setStatus(s);
    }

}

