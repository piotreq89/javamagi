package pw.mgr.current;

import javax.swing.*;

/**
 * Created by piotrek on 2017-06-16.
 */
public class App {
    private JButton startButton;
    private JButton stopButton;
    private JButton drawMoveButton;
    private JButton reloadButton;
    private JComboBox comboBox1;
    private JSlider slider1;
    private JSlider slider2;
    private JLabel erozjon;
    private JLabel dylatation;
    private JPanel mainPanel;

    public JButton getStartButton() {
        return startButton;
    }

    public void setStartButton(JButton startButton) {
        this.startButton = startButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }

    public void setStopButton(JButton stopButton) {
        this.stopButton = stopButton;
    }

    public JButton getDrawMoveButton() {
        return drawMoveButton;
    }

    public void setDrawMoveButton(JButton drawMoveButton) {
        this.drawMoveButton = drawMoveButton;
    }

    public JButton getReloadButton() {
        return reloadButton;
    }

    public void setReloadButton(JButton reloadButton) {
        this.reloadButton = reloadButton;
    }

    public JComboBox getComboBox1() {
        return comboBox1;
    }

    public void setComboBox1(JComboBox comboBox1) {
        this.comboBox1 = comboBox1;
    }

    public JSlider getSlider1() {
        return slider1;
    }

    public void setSlider1(JSlider slider1) {
        this.slider1 = slider1;
    }

    public JSlider getSlider2() {
        return slider2;
    }

    public void setSlider2(JSlider slider2) {
        this.slider2 = slider2;
    }

    public JLabel getErozjon() {
        return erozjon;
    }

    public void setErozjon(JLabel erozjon) {
        this.erozjon = erozjon;
    }

    public JLabel getDylatation() {
        return dylatation;
    }

    public void setDylatation(JLabel dylatation) {
        this.dylatation = dylatation;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }
}
