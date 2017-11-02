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
    private double totalFrames;

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

        Graphics2D cg = image.createGraphics();
        Graphics2D cgBase = imageBase.createGraphics();

        int width = 30;
        int height = 50;

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
//            objects.setCount(objects.getCount() + 25);
            objects.setCount((int)((objects.getCount() + 25) / (totalFrames/(12 * 4)) * 100));
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
            System.out.println("object " + objects);

            cg.setStroke(new BasicStroke(1));
            cg.fillOval(objects.getRect().x, objects.getRect().y , width, height);
            cgBase.setStroke(new BasicStroke(1));
            cgBase.fillOval(objects.getRect().x, objects.getRect().y , width, height);
        });


        if(i % 10 == 0){
            File output = new File("result/wykryty_ruch6.jpg");
            File outputBase = new File("result/wykryty_ruchBase.jpg");

            try {
                ImageIO.write( image, "jpg", output);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                ImageIO.write( imageBase, "jpg", outputBase);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public double getTotalFrames() {
        return totalFrames;
    }

    public void setTotalFrames(double totalFrames) {
        this.totalFrames = totalFrames;
    }
}
