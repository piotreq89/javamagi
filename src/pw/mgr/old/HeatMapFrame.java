package pw.mgr.old;

/**
 * Created by piotrek on 2017-06-12.
 */
import com.github.matthewbeckler.heatmap.Gradient;
import com.github.matthewbeckler.heatmap.HeatMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class HeatMapFrame extends JFrame
{
    public static HeatMap panel;

    public HeatMapFrame() throws Exception
    {

        super("Heat Map Frame");
        preparePanel();

//        panel.set

        this.getContentPane().add(panel);


//        BufferedImage bi = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g = bi.createGraphics();
//        panel.paintComponent(g);
//        g.dispose();


    }

    private static HeatMap preparePanel() {
//        double[][] data = {
//                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0}};

//        double[][] data = HeatMap.generateSinCosData(50);
        double[][] data = HeatMap.generatePyramidData(100);
//                ,{6.0, 2.0, 0.0, 10.0, 13.0, 7.0, 3.0, 8.0,1.0, 6.0},
//                {1.0, 5.0, 3.0, 14.0, 5.0, 6.0, 2.0, 8.0, 9.0, 10.0},{1.0, 5.0, 3.0, 14.0, 5.0, 6.0, 2.0, 8.0, 9.0, 10.0},
//                {6.0, 2.0, 0.0, 10.0, 13.0, 7.0, 3.0, 8.0,1.0, 6.0},{1.0, 8.0, 3.0, 3.0, 5.0, 0.0, 7.0, 8.0, 1.0, 10.0}};
//                HeatMap.generateSinCosData(100);
        boolean useGraphicsYAxis = true;

        // you can use a pre-defined gradient:

        panel = new HeatMap(data, useGraphicsYAxis, Gradient.GRADIENT_BLUE_TO_RED);

        // or you can also make a custom gradient:
//        panel.updateData(data, true );

        Color[] gradientColors = new Color[]{Color.blue,Color.green,Color.yellow, Color.red};
        Color[] customGradient = Gradient.createMultiGradient(gradientColors, 500);
        panel.updateGradient(customGradient);

        // set miscellaneous settings

        panel.setDrawLegend(true);

        panel.setTitle("Height (m)");
        panel.setDrawTitle(true);

        panel.setXAxisTitle("X-Distance (m)");
        panel.setDrawXAxisTitle(true);

        panel.setYAxisTitle("Y-Distance (m)");
        panel.setDrawYAxisTitle(true);

        panel.setCoordinateBounds(0, 6.28, 0, 6.28);

        panel.setDrawXTicks(true);
        panel.setDrawYTicks(true);
        panel.setSize(800, 600);

        return panel;
    }

    // this function will be run from the EDT

    private static HeatMapFrame createAndShowGUI() throws Exception
    {

        HeatMapFrame hmf = new HeatMapFrame();
        hmf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        hmf.setSize(800,600);
        hmf.setVisible(true);

        return hmf;

    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
//                    HeatMapFrame andShowGUI = createAndShowGUI();

                    File output = new File("result/wykryty_ruch7.png");
//                    panel = preparePanel();
//                    Container content = andShowGUI.getContentPane();
//
                    BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
//                    Graphics2D g2d = img.createGraphics();
//                    content.printAll(g2d);
//
//                    g2d.dispose();

                    Graphics2D g2 = img.createGraphics();

                    int x = 300;
                    int y = 300;
                    // fill RoundRectangle2D.Double
                    GradientPaint redtowhite = new GradientPaint(x, y, Color.red, 330, y,
                            Color.blue);
                    g2.setPaint(redtowhite);
//                    g2.fill(new RoundRectangle2D.Double(x, y, 200, 200, 10, 10));
                    g2.fillOval(x, y , 30, 30);
                    g2.setPaint(Color.black);
                    g2.drawString("Filled RoundRectangle2D", x, 250);
                    output.mkdirs();
                    try {
                        ImageIO.write( img, "png", output);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
                catch (Exception e)
                {
                    System.err.println(e);
                    e.printStackTrace();
                }
            }
        });
    }
}