package pw.mgr.current;

import com.github.matthewbeckler.heatmap.Gradient;
import com.github.matthewbeckler.heatmap.HeatMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by piotrek on 2017-01-28.
 */
public class MyFrame extends JFrame{

    public static final int IPADX = 30;

    public MyFrame() {

        generateMyFrame();
//        this.setTitle("Detektor ruchu");
//        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//
//        this.setSize(1920, 1060);
//
//        this.setVisible(true);
//        this.setLocationRelativeTo(null);
//
//        this.setContentPane(new App().getMainPanel());


    }

    private JLabel videoLabelFirstScreen = new JLabel();
    private JLabel videoLabelSecondScreen = new JLabel();
    private JLabel videoLabelThirdScreen = new JLabel();
    private JLabel videoLabelFourthScreen = new JLabel();
    private JLabel resultLabel = new JLabel();
    private JLabel scalaLabel = new JLabel();

    private JButton startButton = new JButton("Start");
    private JButton stopButton = new JButton("Stop");
    private JButton reloadButton = new JButton("Przeładuj");
    private JButton drawMoveButton = new JButton("Narysuj ruch");
    private JButton backgroundProcessButton = new JButton("Przetwarzaj w tle");
    private JSlider jSlider1 = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private JSlider jSlider2 = new JSlider(JSlider.HORIZONTAL, 0, 30, 1);
    private JSlider jSlider3 = new JSlider(JSlider.HORIZONTAL, 0, 100, 1);
    private JSlider jSlider4 = new JSlider(JSlider.HORIZONTAL, 0, 1000, 1);

    private String selectedMovie;

    private BufferedImage paintImage = new BufferedImage(800, 600, BufferedImage.TYPE_3BYTE_BGR);
    private BufferedImage scalaImage = new BufferedImage(100, 50, BufferedImage.TYPE_3BYTE_BGR);


    HeatMap panel;
    double[][] data = new double[800][600];

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

        this.setVisible(true);
        this.setLocationRelativeTo(null);

        GridBagLayout gBLayout = new GridBagLayout();
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(gBLayout);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 25, 0, 0);
        gbc.gridwidth= 1;

        String [] movies = new String[] {
                ""
                ,"Film numer 1"
                ,"Film numer 2"
                ,"Film numer 3"
                ,"Film numer 4"
                ,"Film numer 5"
                ,"Film numer 6"
                ,"Film numer 7"
                ,"Film numer 8"};

        JComboBox<String> movieList = new JComboBox<>(movies);

        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonsPanel.add(movieList, gbc);

        movieList.addActionListener(e -> {
            selectedMovie = (String) movieList.getSelectedItem();
            System.out.println("selectedMovie " + selectedMovie);
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        buttonsPanel.add(startButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        buttonsPanel.add(stopButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        buttonsPanel.add(reloadButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        buttonsPanel.add(backgroundProcessButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        buttonsPanel.add(drawMoveButton, gbc);

        JPanel slidersPanel = getSliderPanel();

        gbc.gridx = 0;
        gbc.gridy = 6;
        buttonsPanel.add(slidersPanel, gbc);

        BufferedImage emptyBufferImage = null;
        try {
            emptyBufferImage = ImageIO.read(new File("resources/empty.jpg"));
        } catch (IOException er) {
            er.printStackTrace();
        }

        ImageIcon emptyImage = new ImageIcon(emptyBufferImage);


        JPanel scalaPanel = new JPanel();
        scalaPanel.setLayout(gBLayout);

        BufferedImage scalaImage = null;
        try {
            scalaImage = ImageIO.read(new File("resources/skala3.jpg"));
        } catch (IOException er) {
            er.printStackTrace();
        }

        ImageIcon image = new ImageIcon(scalaImage);
        scalaLabel.setIcon(image);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        scalaPanel.add(scalaLabel, gbc);

//        JPanel scalaTextPanel = new JPanel();
//        scalaTextPanel.setLayout(gBLayout);
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.anchor = GridBagConstraints.PAGE_START;
        JLabel jTextField = new JLabel(" AAAA :");
        gbc3.gridx = 1;
        gbc3.gridy = 0;
        scalaPanel.add(jTextField, gbc3);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.anchor = GridBagConstraints.CENTER;
        JLabel jTextField1 = new JLabel(" AAAB :");
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        scalaPanel.add(jTextField1, gbc2);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.anchor = GridBagConstraints.PAGE_END;
        JLabel jTextField3 = new JLabel(" CCCB :");
        gbc4.gridx = 1;
        gbc4.gridy = 0;
        scalaPanel.add(jTextField3, gbc4);

//
//        gbc.gridx = 1;
//        gbc.gridy = 0;
//        scalaPanel.add(scalaTextPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        buttonsPanel.add(scalaPanel, gbc);

        JPanel videoPanel = new JPanel();
//        BorderLayout borderLayout = new BorderLayout();
        videoPanel.setLayout(gBLayout);
//        JLabel videoLabelFourthScreen = new JLabel();

        GridBagConstraints gbcScreens = new GridBagConstraints();
        gbcScreens.insets = new Insets(10, 10, 10, 10);

        if (videoLabelFourthScreen == null){
            videoLabelFourthScreen = new JLabel();
        }
        if (videoLabelFirstScreen == null){
            videoLabelFirstScreen = new JLabel();
        }
        gbcScreens.gridx = 0;
        gbcScreens.gridy = 0;
        videoLabelFirstScreen.setIcon(emptyImage);
        videoPanel.add(videoLabelFirstScreen, gbcScreens);

        gbcScreens.gridx = 1;
        gbcScreens.gridy = 0;

//        resultLabel.setSize(800, 600);
//        resultLabel.setBackground(new Color(0,0,255));

        resultLabel.setIcon(emptyImage);
        videoPanel.add(resultLabel, gbcScreens);

        gbcScreens.gridx = 0;
        gbcScreens.gridy = 1;
        videoLabelThirdScreen.setIcon(emptyImage);
        videoPanel.add(videoLabelThirdScreen, gbcScreens);

        gbcScreens.gridx = 1;
        gbcScreens.gridy = 1;
        videoLabelFourthScreen.setIcon(emptyImage);
        videoPanel.add(videoLabelFourthScreen, gbcScreens);

        gbcScreens.gridx = 2;
        gbcScreens.gridy = 0;
//        videoPanel.add(slidersPanel, gbcScreens);


        JPanel generalPanel = new JPanel();
        GridBagLayout gBGeneralLayout = new GridBagLayout();
        generalPanel.setLayout(gBGeneralLayout);

        GridBagConstraints gbcGeneral = new GridBagConstraints();
        gbcGeneral.anchor = GridBagConstraints.NORTHWEST;
        gbcGeneral.insets = new Insets(10, 10, 10, 10);

        gbcGeneral.gridx = 0;
        gbcGeneral.gridy = 0;
//        gbcGeneral.ipadx = IPADX ;
        gbcGeneral.anchor = GridBagConstraints.FIRST_LINE_START;
        generalPanel.add(buttonsPanel,gbcGeneral);

        gbcGeneral.gridx = 1;
        gbcGeneral.gridy = 0;
        generalPanel.add(videoPanel, gbcGeneral);

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

        JLabel jTextField = new JLabel("Dylacja :");
        gbc.ipadx = IPADX;
        gbc.gridx = 0;
        gbc.gridy = 0;
        slidersPanel.add(jTextField, gbc);

        JLabel jTextField2 = new JLabel("Erozja :");
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
        gbc.ipadx = IPADX;
        gbc.gridx = 0;
        gbc.gridy = 1;
        jSlider1.setValue(25);
        slidersPanel.add(jSlider1, gbc);


        setSliderParams(jSlider2);
        gbc.gridx = 0;
        gbc.gridy = 3;
        jSlider2.setValue(4);
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

    public JButton getBackgroundProcessButton() {
        return backgroundProcessButton;
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

    public String getSelectedMovie() {
        return selectedMovie;
    }

    public void setSelectedMovie(String selectedMovie) {
        this.selectedMovie = selectedMovie;
    }

    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public HeatMap getPanel() {
        return panel;
    }

    public void setPanel(HeatMap panel) {
        this.panel = panel;
    }
}
