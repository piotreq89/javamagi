package pw.mgr.current;

//import com.github.matthewbeckler.heatmap.HeatMap;

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

    public static final int IPADX = 30;
    public static final int FONT_SIZE = 18;

    public MyFrame() {

        generateMyFrame();
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
    private JButton backgroundProcessButton = new JButton("Przetwarzaj w tle");
    private JSlider morphOpenJSlider = new JSlider(JSlider.CENTER, 0, 30, 1);
    private JSlider morphCloseJSlider = new JSlider(JSlider.CENTER, 0, 30, 1);

    private JPanel scalaPanel = new JPanel();
    private JLabel jLabelScaleTop;
    private JLabel jLabelScaleCenter;
    private JLabel jLabelScaleDown;
    private JLabel progressLabel;
    private Font font = new Font("Serif", Font.PLAIN, FONT_SIZE);

    private String selectedMovie;

    private BufferedImage paintImage = new BufferedImage(800, 600, BufferedImage.TYPE_3BYTE_BGR);
    private BufferedImage scalaImage = new BufferedImage(100, 50, BufferedImage.TYPE_3BYTE_BGR);


//    HeatMap panel;
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
        gbc.insets = new Insets(15, 35, 0, 0);
        gbc.gridwidth= 1;

        String [] movies = new String[] {
                "Wybierz film"
                ,"Film numer 1"
                ,"Film numer 2"
                ,"Film numer 3"
                ,"Film numer 4"
                ,"Film numer 5"
                ,"Film numer 6"
                ,"Film numer 7"
                ,"Film numer 8"};

        JComboBox<String> movieList = new JComboBox<>(movies);
        movieList.setFont(font);

        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonsPanel.add(movieList, gbc);

        movieList.addActionListener(e -> {
            selectedMovie = (String) movieList.getSelectedItem();
            System.out.println("selectedMovie " + selectedMovie);
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        startButton.setFont(font);
        buttonsPanel.add(startButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        stopButton.setFont(font);
        buttonsPanel.add(stopButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        reloadButton.setFont(font);
        buttonsPanel.add(reloadButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        backgroundProcessButton.setFont(font);
        buttonsPanel.add(backgroundProcessButton, gbc);

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

        gbc.gridx = 0;
        gbc.gridy = 7;
//        progressLabel = new JLabel("postęp przetwarzania : 0 %");
        progressLabel = new JLabel("Postęp przetwarzania : 0 %");
        progressLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        buttonsPanel.add(progressLabel, gbc);

        prepareScalaPanel();

        gbc.gridx = 0;
        gbc.gridy = 8;
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

    private void prepareScalaPanel() {
        GridBagLayout gBLayoutScale = new GridBagLayout();
        GridBagConstraints gbcScale = new GridBagConstraints();
        gbcScale.anchor = GridBagConstraints.LINE_START;
//        gbcScale.fill = GridBagConstraints.VERTICAL;
//        gbcScale.insets = new Insets(10, 25, 0, 0);
//        gbcScale.gridwidth= 1;
        scalaPanel.setLayout(gBLayoutScale);

        BufferedImage scalaImage = null;
        try {
            scalaImage = ImageIO.read(new File("resources/skala3.jpg"));
        } catch (IOException er) {
            er.printStackTrace();
        }
        ImageIcon image = new ImageIcon(scalaImage);
        scalaLabel.setIcon(image);
        gbcScale.gridx = 0;
        gbcScale.gridy = 0;
//        gbcScale.anchor = GridBagConstraints.FIRST_LINE_START;
        scalaPanel.add(scalaLabel, gbcScale);

        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.anchor = GridBagConstraints.PAGE_START;
        jLabelScaleTop = new JLabel(" 00.0 sec ");
        jLabelScaleTop.setFont(font);
        gbcTop.gridx = 1;
        gbcTop.gridy = 0;
        scalaPanel.add(jLabelScaleTop, gbcTop);

        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.anchor = GridBagConstraints.CENTER;
        jLabelScaleCenter = new JLabel(" 00.0 sec ");
        jLabelScaleCenter.setFont(font);
        gbcCenter.gridx = 1;
        gbcCenter.gridy = 0;
        scalaPanel.add(jLabelScaleCenter, gbcCenter);

        GridBagConstraints gbcDown = new GridBagConstraints();
        gbcDown.anchor = GridBagConstraints.PAGE_END;
        jLabelScaleDown = new JLabel(" 00.0 sec ");
        jLabelScaleDown.setFont(font);
        gbcDown.gridx = 1;
        gbcDown.gridy = 0;
        scalaPanel.add(jLabelScaleDown, gbcDown);

    }

    private JPanel getSliderPanel() {
        GridBagLayout gBLayout = new GridBagLayout();

        JPanel slidersPanel = new JPanel();
        slidersPanel.setLayout(gBLayout);

        GridBagConstraints gbcSlider = new GridBagConstraints();
        gbcSlider.fill = GridBagConstraints.VERTICAL;
//        gbcSlider.insets = new Insets(10, 25, 0, 0);

        JLabel jTextField = new JLabel("Zamknięcie :");
//        gbcSlider.ipadx = IPADX;
        gbcSlider.gridx = 0;
        gbcSlider.gridy = 0;
        jTextField.setFont(font);
        slidersPanel.add(jTextField, gbcSlider);

        JLabel jTextField2 = new JLabel("Otwarcie :");
        gbcSlider.gridx = 0;
        gbcSlider.gridy = 2;
        jTextField2.setFont(font);
        slidersPanel.add(jTextField2, gbcSlider);

        setSliderParams(morphOpenJSlider);
//        gbcSlider.ipadx = IPADX;
        gbcSlider.gridx = 0;
        gbcSlider.gridy = 1;
        morphOpenJSlider.setValue(25);
        slidersPanel.add(morphOpenJSlider, gbcSlider);


        setSliderParams(morphCloseJSlider);
        gbcSlider.gridx = 0;
        gbcSlider.gridy = 3;
        morphCloseJSlider.setValue(4);
        slidersPanel.add(morphCloseJSlider, gbcSlider);

        return slidersPanel;
    }

    private void setSliderParams(JSlider jSlider) {
        jSlider.setMinorTickSpacing(10);
        jSlider.setPaintTicks(true);
        jSlider.setLabelTable(jSlider.createStandardLabels(10));
        jSlider.setPaintLabels(true);
        jSlider.setFont(font);
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

    public JButton getBackgroundProcessButton() {
        return backgroundProcessButton;
    }

    public JSlider getMorphOpenJSlider() {
        return morphOpenJSlider;
    }

    public JSlider getMorphCloseJSlider() {
        return morphCloseJSlider;
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

//    public HeatMap getPanel() {
//        return panel;
//    }
//
//    public void setPanel(HeatMap panel) {
//        this.panel = panel;
//    }

    public JPanel getScalaPanel() {
        return scalaPanel;
    }

    public void setScalaPanel(JPanel scalaPanel) {
        this.scalaPanel = scalaPanel;
    }

    public JLabel getjLabelScaleTop() {
        return jLabelScaleTop;
    }

    public void setjLabelScaleTopText(String jLabelScaleTopText) {
        this.jLabelScaleTop.setText(jLabelScaleTopText);
    }

    public void setjLabelScaleCenterText(String jLabelScaleCenterText) {
        this.jLabelScaleCenter.setText(jLabelScaleCenterText);
    }

    public void setjLabelScaleDownText(String jLabelScaleDownText) {
        this.jLabelScaleDown.setText(jLabelScaleDownText);
    }
    public JLabel getProgressLabel() {
        return progressLabel;
    }

    public void setProgressLabel(String progress) {
        this.progressLabel.setText(progress);
    }
}
