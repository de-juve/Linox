package gui.dialog;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ParameterSlider extends JPanel implements ChangeListener {
    private JLabel label;
    private JSlider slider;
    private int value;

    ParameterSlider(String name, int min, int max, int defValue) {
        this.setLayout(new MigLayout());

        slider = new JSlider(JSlider.HORIZONTAL, min, max);
        slider.setValue(defValue);
        value = defValue;
        label.setText(name);
        slider.addChangeListener(this);

        this.add(label);
        this.add(slider);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
            value = source.getValue();
        }
    }

    public int getValue() {
        return value;
    }
}
