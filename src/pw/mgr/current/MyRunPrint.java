package pw.mgr.current;

import com.github.matthewbeckler.heatmap.Gradient;
import com.github.matthewbeckler.heatmap.HeatMap;
import org.opencv.core.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by piotrek on 2017-02-11.
 */
public class MyRunPrint extends Thread {

    private static HeatMap panel;
    private List<Rect> myPoints = new ArrayList<>();
    private List<DetectedObject> detectedObjects = new ArrayList<>();
    double[][] data = new double[740][540];
    ImageIcon baseImage;

    private int i ;

    public MyRunPrint(List<Rect> myPoints ) {
        this.myPoints = myPoints ;
    }

    public MyRunPrint(List<DetectedObject> detectedObjects, int i ,  ImageIcon baseImage ) {
        this.detectedObjects = detectedObjects ;
        this.i = i ;
        this.baseImage = baseImage ;
    }


    static class PointToDraw{
        Rect rect;
        Integer count ;

        public PointToDraw(Rect rect, int count) {
            this.rect = rect;
            this.count = count;
        }

        public Rect getRect() {
            return rect;
        }

        public void setRect(Rect rect) {
            this.rect = rect;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return "PointToDraw{" +
                    "rect=" + rect +
                    ", count=" + count +
                    '}';
        }
    }

    List<PointToDraw> pointToDrawList = new ArrayList<>();

    @Override
    public void run() {
        System.out.println("print");
        BufferedImage imageBase = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
//        BufferedImage image = new BufferedImage(1600, 1200, BufferedImage.TYPE_INT_RGB);

        List<ColorMap> colorMaps = new ArrayList<>();
        colorMaps.add(new ColorMap(1, Color.WHITE)) ;
        colorMaps.add(new ColorMap(2, Color.PINK)) ;
        colorMaps.add(new ColorMap(3, Color.ORANGE)) ;
        colorMaps.add(new ColorMap(4, Color.RED)) ;


        Map<Integer, Color> integerColorMap = new HashMap<>();
        integerColorMap.put(1, Color.cyan);
        integerColorMap.put(2, Color.ORANGE);
        integerColorMap.put(3, Color.MAGENTA);
        integerColorMap.put(4, Color.GREEN);
        integerColorMap.put(5, Color.pink);
        integerColorMap.put(6, Color.WHITE);
        integerColorMap.put(7, Color.white);
        integerColorMap.put(8, Color.yellow);
        integerColorMap.put(9, Color.cyan);
        integerColorMap.put(10, Color.ORANGE);
        integerColorMap.put(11, Color.GREEN);
        integerColorMap.put(12, Color.pink);
        integerColorMap.put(13, Color.MAGENTA);
        integerColorMap.put(14, Color.GREEN);
        integerColorMap.put(15, Color.pink);
        integerColorMap.put(16, Color.ORANGE);
        integerColorMap.put(17, Color.white);
        integerColorMap.put(18, Color.yellow);
        integerColorMap.put(19, Color.GREEN);
        integerColorMap.put(20, Color.ORANGE);
        integerColorMap.put(21, Color.cyan);
        integerColorMap.put(22, Color.ORANGE);
        integerColorMap.put(23, Color.MAGENTA);
        integerColorMap.put(24, Color.GREEN);
        integerColorMap.put(25, Color.pink);
        integerColorMap.put(26, Color.WHITE);
        integerColorMap.put(27, Color.white);
        integerColorMap.put(28, Color.yellow);





        Graphics2D cg = image.createGraphics();
        Graphics2D cgBase = imageBase.createGraphics();

        int width = 30;
        int height = 50;




//        for (Rect mp: myPoints) {
//            System.out.println(mp);
//            cg.drawOval( mp.y * 2, mp.x * 2, width, height);
//        }

        List<PointToDraw> pointToDraws = detectedObjects.stream()
                .map(DetectedObject::getRect)
                .distinct()
                .map(d -> new PointToDraw(d, (int) detectedObjects.stream()
                        .filter(r -> {
                            int area = 20;
                            return (r.getRect().x <= d.x + area && r.getRect().x >= d.x - area
                                    && r.getRect().y <= d.y + area && r.getRect().y >= d.y - area);
                        }).count()))
                .sorted((o1, o2) -> o1.getCount().compareTo(o2.getCount()))
                .collect(Collectors.toList());

        cg.setColor(new Color(0, 0, 255));
        cg.fillRect(0,0, 800, 600);

        cgBase.drawImage(baseImage.getImage(), 10, 10, null );


        pointToDraws.stream().forEach(objects -> {
            objects.setCount(objects.getCount() + 25);
            int alfa = 15;
            if(objects.getCount() < 255){
                cg.setColor(new Color(0, objects.getCount(), 255 - objects.getCount()));
                cgBase.setColor(new Color(0, objects.getCount(), 255 - objects.getCount(), alfa));
            }else if (objects.getCount() >= 255 && objects.getCount() < 510){
                cg.setColor(new Color(objects.getCount() - 255 , 255 - (objects.getCount() - 255), 0));
                cgBase.setColor(new Color(objects.getCount() - 255 , 255 - (objects.getCount() - 255), 0 , alfa -5));
            }else{
                cg.setColor(new Color(255, 0, 0));
                cgBase.setColor(new Color(255, 0, 0 , alfa + 20));
            }
//            int x = 20;
//            int y = 7;
//            GradientPaint redtowhite = new GradientPaint(objects.getRect().x + 20, objects.getRect().y + 20, cg.getColor(), objects.getRect().x + height, objects.getRect().y + width, new Color(0, 0, 255));

//            data[objects.getRect().x][objects.getRect().y] = objects.getCount() ;

//            cg.setPaint(redtowhite);
//            int variable = 1;
//            if(objects.getCount() > 0){
//                for(int a = 0; a < variable; a++ ){
//                    for( int b = 0; b < variable; b ++ ){
//                        data[objects.getRect().x][objects.getRect().y - b] = objects.getCount() ;
//                        data[objects.getRect().x][objects.getRect().y + b] = objects.getCount() ;
//                        data[objects.getRect().x - a][objects.getRect().y] = objects.getCount() ;
//                        data[objects.getRect().x + a][objects.getRect().y] = objects.getCount() ;
//                        data[objects.getRect().x + a][objects.getRect().y + b] = objects.getCount() ;
//                        data[objects.getRect().x + a][objects.getRect().y - b] = objects.getCount() ;
//                    }
//                }
//            }



////            data[objects.getRect().x/ variable][objects.getRect().y/ variable] = objects.getCount() ;
//            data[objects.getRect().x/ variable + 1][objects.getRect().y/ variable + 1] = objects.getCount() ;
//            data[objects.getRect().x/ variable + 1][objects.getRect().y/ variable -  1] = objects.getCount() ;
//            data[objects.getRect().x/ variable + 1][objects.getRect().y/ variable] = objects.getCount() ;
//            data[objects.getRect().x/ variable - 1][objects.getRect().y/ variable] = objects.getCount() ;
//            data[objects.getRect().x/ variable - 1][objects.getRect().y/ variable -1] = objects.getCount() ;
//            data[objects.getRect().x/ variable - 1][objects.getRect().y/ variable + 1] = objects.getCount() ;
//            data[objects.getRect().x/ variable][objects.getRect().y/ variable -1 ] = objects.getCount() ;
//            data[objects.getRect().x/ variable][objects.getRect().y/ variable + 1] = objects.getCount() ;
////
////
//            data[objects.getRect().x/ variable][objects.getRect().y/ variable - 2] = objects.getCount() ;
//            data[objects.getRect().x/ variable][objects.getRect().y/ variable + 2] = objects.getCount() ;
//
//            data[objects.getRect().x/ variable - 2][objects.getRect().y/ variable - 2] = objects.getCount() ;
//            data[objects.getRect().x/ variable - 2][objects.getRect().y/ variable - 1] = objects.getCount() ;
//            data[objects.getRect().x/ variable - 2][objects.getRect().y/ variable + 1] = objects.getCount() ;
//            data[objects.getRect().x/ variable - 2][objects.getRect().y/ variable + 2] = objects.getCount() ;
//            data[objects.getRect().x/ variable - 2][objects.getRect().y/ variable] = objects.getCount() ;
//
//            data[objects.getRect().x/ variable - 1][objects.getRect().y/ variable + 2] = objects.getCount() ;
//            data[objects.getRect().x/ variable - 1][objects.getRect().y/ variable - 2] = objects.getCount() ;
//
//            data[objects.getRect().x/ variable + 1][objects.getRect().y/ variable - 2] = objects.getCount() ;
//            data[objects.getRect().x/ variable + 1][objects.getRect().y/ variable + 2] = objects.getCount() ;
//
//            data[objects.getRect().x/ variable + 2][objects.getRect().y/ variable] = objects.getCount() ;
//            data[objects.getRect().x/ variable + 2][objects.getRect().y/ variable - 1] = objects.getCount() ;
//            data[objects.getRect().x/ variable + 2][objects.getRect().y/ variable + 1] = objects.getCount() ;
//            data[objects.getRect().x/ variable + 2][objects.getRect().y/ variable + 2] = objects.getCount() ;
//            data[objects.getRect().x/ variable + 2][objects.getRect().y/ variable - 2] = objects.getCount() ;


//            System.out.println(data[objects.getRect().y][objects.getRect().x] + " " + objects.getRect().y + " " +  objects.getRect().x);

//            System.out.println(" -> objects " + objects );


            cg.setStroke(new BasicStroke(1));
//            cg.fillRect(objects.getRect().x, objects.getRect().y , width, height);
            cg.fillOval(objects.getRect().x, objects.getRect().y , width, height);
            cgBase.setStroke(new BasicStroke(1));
//            cg.fillRect(objects.getRect().x, objects.getRect().y , width, height);
            cgBase.fillOval(objects.getRect().x, objects.getRect().y , width, height);
//

        });


        if(i % 10 == 0){


            boolean useGraphicsYAxis = true;

//            panel = new HeatMap(data, useGraphicsYAxis, Gradient.GRADIENT_BLUE_TO_RED);
//            // or you can also make a custom gradient:
//
//            Color[] gradientColors = new Color[]{Color.blue,Color.green,Color.yellow, Color.red};
//            Color[] customGradient = Gradient.createMultiGradient(gradientColors, 10);
//            panel.updateGradient(customGradient);
//
//            // set miscellaneous settings
//
//            panel.setDrawLegend(true);
//
//            panel.setTitle("Height (m)");
//            panel.setDrawTitle(true);
//
//            panel.setXAxisTitle("X-Distance (m)");
//            panel.setDrawXAxisTitle(true);
//
//            panel.setYAxisTitle("Y-Distance (m)");
//            panel.setDrawYAxisTitle(true);
//
//            panel.setCoordinateBounds(0, 6.28, 0, 6.28);
//
//            panel.setDrawXTicks(true);
//            panel.setDrawYTicks(true);
//            panel.setSize(800, 600);

//        hmf.getContentPane().add(panel);

//        hmf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        hmf.setSize(800,600);
//        hmf.setVisible(true);

            File output = new File("result/wykryty_ruch6.jpg");
            File outputBase = new File("result/wykryty_ruchBase.jpg");
//            Container content = panel;//andShowGUI.getContentPane();

//            BufferedImage img = new BufferedImage(content.getWidth(), content.getHeight(), BufferedImage.TYPE_INT_RGB);
//            Graphics2D g2d = img.createGraphics();
//            content.printAll(g2d);

//            g2d.dispose();

//            output.mkdirs();
            try {// dziala dla jhearmaps
//                ImageIO.write( img, "jpg", output);
                ImageIO.write( image, "jpg", output);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {// dziala dla jhearmaps
//                ImageIO.write( img, "jpg", output);
                ImageIO.write( imageBase, "jpg", outputBase);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }



//        System.out.println(" data from print" );
//        for(int i =0 ; i < 540 ; i++){
//            for(int j =0 ; j < 740 ; j++){
//
//                double v = data[j][i];
//                if(v > 0){
//                    System.out.println(" v " + v + " j " + j + " i " + i);
//                }
//
//            }
//        }
//        System.out.println("finish print");
    }

//    void convert_to_rgb(minval, maxval, val, colors):
//    max_index = len(colors)-1
//    v = float(val-minval) / float(maxval-minval) * max_index
//    i1, i2 = int(v), min(int(v)+1, max_index)
//            (r1, g1, b1), (r2, g2, b2) = colors[i1], colors[i2]
//    f = v - i1
//    return int(r1 + f*(r2-r1)), int(g1 + f*(g2-g1)), int(b1 + f*(b2-b1))


    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
    }
}
