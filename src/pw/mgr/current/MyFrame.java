package pw.mgr.current;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

    private JButton startButton = new JButton("Start");
    private JButton stopButton = new JButton("Stop");
    private JButton drawMoveButton = new JButton("Narysuj ruch");

    private MyFrame generateMyFrame(){

        this.setTitle("Detektor ruchu");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.setSize(1620, 1060);
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

        gbc.gridx = 1;
        gbc.gridy = 0;
        buttonsPanel.add(stopButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        buttonsPanel.add(drawMoveButton, gbc);

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

        gbcScreens.gridx = 0;
        gbcScreens.gridy = 1;

        videoPanel.add(videoLabelSecondScreen, gbcScreens);

        gbcScreens.gridx = 1;
        gbcScreens.gridy = 0;
        videoPanel.add(videoLabelThirdScreen, gbcScreens);

        gbcScreens.gridx = 1;
        gbcScreens.gridy = 1;
        videoPanel.add(videoLabelFourthScreen, gbcScreens);

        JPanel generalPanel = new JPanel();
        generalPanel.add(buttonsPanel);
        generalPanel.add(videoPanel);


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

    public JLabel getVideoLabelFirstScreen() {
        if (videoLabelFirstScreen == null){
            videoLabelFirstScreen = new JLabel();
        }
        return videoLabelFirstScreen;
    }

    public JLabel getVideoLabelThirdScreen() {
        if (videoLabelSecondScreen == null){
            videoLabelSecondScreen = new JLabel();
        }
        return videoLabelSecondScreen;
    }

    public JLabel getVideoLabelSecondScreen() {
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

    public void setVideoLabelFourthScreen(JLabel videoLabelFourthScreen) {
        this.videoLabelFourthScreen = videoLabelFourthScreen;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }

    public JButton getDrawMoveButton() {
        return drawMoveButton;
    }
}
