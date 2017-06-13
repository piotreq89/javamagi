package pw.mgr.old;

/**
 * Created by piotrek on 2017-06-12.
 */
import com.github.matthewbeckler.heatmap.Gradient;
import com.github.matthewbeckler.heatmap.HeatMap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class HeatMapFrame extends JFrame
{
    HeatMap panel;

    public HeatMapFrame() throws Exception
    {

        super("Heat Map Frame");
        double[][] data = {{1.0,2.0,3.0,4.0,5.0, 6.0,7.0,8.0,9.0,10.0}, {10.0,9.0,8.0,7.0,6.0,5.0,4.0,3.0,2.0,1.0}, {1.0,2.0,3.0,4.0,5.0, 6.0,7.0,8.0,9.0,10.0}, {10.0,9.0,8.0,7.0,6.0,5.0,4.0,3.0,2.0,1.0}};
//                HeatMap.generateSinCosData(100);
        boolean useGraphicsYAxis = true;

        // you can use a pre-defined gradient:

        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        panel = new HeatMap(data, useGraphicsYAxis, Gradient.GRADIENT_BLUE_TO_RED);
        // or you can also make a custom gradient:

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

        this.getContentPane().add(panel);
    }

    // this function will be run from the EDT

    private static void createAndShowGUI() throws Exception
    {

        HeatMapFrame hmf = new HeatMapFrame();
        hmf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        hmf.setSize(800,600);
        hmf.setVisible(true);

    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    createAndShowGUI();
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