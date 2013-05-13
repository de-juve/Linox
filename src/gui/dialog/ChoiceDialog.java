package gui.dialog;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ChoiceDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private ArrayList<ParameterSlider> parameterSliders;
    private ArrayList<ParameterComboBox> parameterComboBoxes;

    public boolean cancle = false;

    public ChoiceDialog() {
        this.setLayout(new MigLayout());

        parameterSliders = new ArrayList<>();
        parameterComboBoxes = new ArrayList<>();

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
        cancle = true;
        dispose();
    }

    public void addParameterSlider(ParameterSlider slider) {
        parameterSliders.add(slider);
        this.add(slider);
    }

    public int getValueSlider(ParameterSlider slider) {
        return parameterSliders.get(parameterSliders.lastIndexOf(slider)).getValue();
    }

    public void addParameterComboBox(ParameterComboBox comboBox) {
        parameterComboBoxes.add(comboBox);
        this.add(comboBox);
    }

    public String getValueComboBox(ParameterComboBox comboBox) {
        return parameterComboBoxes.get(parameterComboBoxes.lastIndexOf(comboBox)).getValue();
    }

    public static void main(String[] args) {
        ChoiceDialog dialog = new ChoiceDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
