package pw.mgr.current;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by piotrek on 2017-01-28.
 */
public class MyFrame extends JFrame{

    public MyFrame() {

        generateMyFrame();
    }

    private JLabel videoLabelFirstScreen = new JLabel();
    private JLabel videoLabelSecondScreen = new JLabel();
    private JLabel videoLabelThirdScreen = new JLabel();
    private JLabel videoLabelFourthScreen = new JLabel();
    private JLabel resultLabel = new JLabel();

    private JButton startButton = new JButton("Start");
    private JButton stopButton = new JButton("Stop");
    private JButton reloadButton = new JButton("Przeładuj");
    private JButton drawMoveButton = new JButton("Narysuj ruch");
    private JSlider jSlider1 = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private JSlider jSlider2 = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private JSlider jSlider3 = new JSlider(JSlider.HORIZONTAL, 0, 100, 1);
    private JSlider jSlider4 = new JSlider(JSlider.HORIZONTAL, 0, 1000, 1);

    private BufferedImage paintImage = new BufferedImage(800, 600, BufferedImage.TYPE_3BYTE_BGR);


    public BufferedImage getPaintImage() {
        return paintImage;
    }

    public void setPaintImage(BufferedImage paintImage) {
        this.paintImage = paintImage;
    }

    private MyFrame generateMyFrame(){

        this.setTitle("Detektor ruchu");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.setSize(1920, 1060);
//        this.setSize(4096, 2304 );

        this.setVisible(true);
        this.setLocationRelativeTo(null);


        GridBagLayout gBLayout = new GridBagLayout();
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(gBLayout);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 25, 0, 0);

        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonsPanel.add(startButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        buttonsPanel.add(stopButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        buttonsPanel.add(reloadButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        buttonsPanel.add(drawMoveButton, gbc);

        JPanel slidersPanel = getSliderPanel();

        gbc.gridx = 0;
        gbc.gridy = 4;
        buttonsPanel.add(slidersPanel, gbc);

        JPanel videoPanel = new JPanel();
//        BorderLayout borderLayout = new BorderLayout();
        videoPanel.setLayout(gBLayout);
//        JLabel videoLabelFourthScreen = new JLabel();

        GridBagConstraints gbcScreens = new GridBagConstraints();
        gbcScreens.insets = new Insets(25, 25, 0, 0);

        if (videoLabelFourthScreen == null){
            videoLabelFourthScreen = new JLabel();
        }
        if (videoLabelFirstScreen == null){
            videoLabelFirstScreen = new JLabel();
        }
        gbcScreens.gridx = 0;
        gbcScreens.gridy = 0;
        videoPanel.add(videoLabelFirstScreen, gbcScreens);

        gbcScreens.gridx = 1;
        gbcScreens.gridy = 0;

//        ImageIcon image = new ImageIcon(paintImage);
        // update panel with new paint image

        resultLabel.setSize(800, 600);
//        resultLabel.repaint();
//        resultLabel.setIcon(image);
//        JPanel panel = new JPanel(new BorderLayout());
        videoPanel.add(resultLabel, gbcScreens);
//        repaint();

//        videoPanel.add(videoLabelSecondScreen, gbcScreens);

        gbcScreens.gridx = 0;
        gbcScreens.gridy = 1;
        videoPanel.add(videoLabelThirdScreen, gbcScreens);

        gbcScreens.gridx = 1;
        gbcScreens.gridy = 1;
        videoPanel.add(videoLabelFourthScreen, gbcScreens);

        gbcScreens.gridx = 2;
        gbcScreens.gridy = 0;
//        videoPanel.add(slidersPanel, gbcScreens);


        JPanel generalPanel = new JPanel();
        GridBagLayout gBGeneralLayout = new GridBagLayout();
        generalPanel.setLayout(gBGeneralLayout);

        GridBagConstraints gbcGeneral = new GridBagConstraints();
        gbcGeneral.anchor = GridBagConstraints.WEST;
        gbcGeneral.insets = new Insets(10, 25, 0, 0);

        gbcGeneral.gridx = 0;
        gbcGeneral.gridy = 0;
        generalPanel.add(buttonsPanel,gbcGeneral);

        gbcGeneral.gridx = 1;
        gbcGeneral.gridy = 0;
        generalPanel.add(videoPanel, gbcGeneral);

//        gbcGeneral.gridx = 0;
//        gbcGeneral.gridy = 1;
//        generalPanel.add(slidersPanel, gbcGeneral);


        this.setContentPane(generalPanel);


        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
//                int i=JOptionPane.showConfirmDialog(null, "Zamknąć apikacje?");
//                if(i==0)
                    System.exit(0);
            }
        });

        return this;
    }

    private JPanel getSliderPanel() {
        GridBagLayout gBLayout = new GridBagLayout();

        JPanel slidersPanel = new JPanel();
        slidersPanel.setLayout(gBLayout);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 25, 0, 0);

        JTextField jTextField = new JTextField("Dylacja :");
        gbc.ipadx = 40;
        gbc.gridx = 0;
        gbc.gridy = 0;
        slidersPanel.add(jTextField, gbc);

        JTextField jTextField2 = new JTextField("Erozja :");
        gbc.gridx = 0;
        gbc.gridy = 2;
        slidersPanel.add(jTextField2, gbc);

//        JTextField jTextField3 = new JTextField("slider3");
//        gbc.gridx = 0;
//        gbc.gridy = 4;
//        slidersPanel.add(jTextField3, gbc);
//
//        JTextField jTextField4 = new JTextField("slider4");
//        gbc.gridx = 0;
//        gbc.gridy = 6;
//        slidersPanel.add(jTextField4, gbc);

        setSliderParams(jSlider1);
        gbc.ipadx = 100;
        gbc.gridx = 0;
        gbc.gridy = 1;
        slidersPanel.add(jSlider1, gbc);


        setSliderParams(jSlider2);
        gbc.gridx = 0;
        gbc.gridy = 3;
        slidersPanel.add(jSlider2, gbc);
//
//        setSliderParams(jSlider3);
//        gbc.gridx = 0;
//        gbc.gridy = 5;
//        slidersPanel.add(jSlider3, gbc);
//
//        setSliderParams(jSlider4);
//        gbc.gridx = 0;
//        gbc.gridy = 7;
//        slidersPanel.add(jSlider4, gbc);
        return slidersPanel;
    }

    private void setSliderParams(JSlider jSlider) {
        jSlider.setMinorTickSpacing(5);
        jSlider.setPaintTicks(true);
        jSlider.setLabelTable(jSlider.createStandardLabels(5));
        jSlider.setPaintLabels(true);
    }

    public JLabel getVideoLabelFirstScreen() {
        if (videoLabelFirstScreen == null){
            videoLabelFirstScreen = new JLabel();
        }
        return videoLabelFirstScreen;
    }

    public JLabel getVideoLabelSecondScreen() {
        if (videoLabelSecondScreen == null){
            videoLabelSecondScreen = new JLabel();
        }
        return videoLabelSecondScreen;
    }

    public JLabel getVideoLabelThirdScreen() {
        if (videoLabelThirdScreen == null){
            videoLabelThirdScreen = new JLabel();
        }
        return videoLabelThirdScreen;
    }

    public JLabel getVideoLabelFourthScreen() {
        if (videoLabelFourthScreen == null){
            videoLabelFourthScreen = new JLabel();
        }
        return videoLabelFourthScreen;
    }

    public JLabel getResultLabel() {
        return resultLabel;
    }

    public void setVideoLabelFourthScreen(JLabel videoLabelFourthScreen) {
        this.videoLabelFourthScreen = videoLabelFourthScreen;
    }

    public JButton getStartButton() {

        return startButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }

    public JButton getReloadButton() {
        return reloadButton;
    }

    public JButton getDrawMoveButton() {

        return drawMoveButton;
    }

    public JSlider getjSlider1() {
        return jSlider1;
    }

    public JSlider getjSlider2() {
        return jSlider2;
    }

    public JSlider getjSlider3() {
        return jSlider3;
    }

    public JSlider getjSlider4() {
        return jSlider4;
    }
}
