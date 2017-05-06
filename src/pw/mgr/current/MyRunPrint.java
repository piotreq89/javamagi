package pw.mgr.current;

import org.opencv.core.*;
import org.opencv.core.Point;

import javax.imageio.ImageIO;
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


    private List<Rect> myPoints = new ArrayList<>();
    private List<DetectedObject> detectedObjects = new ArrayList<>();

    public MyRunPrint(List<Rect> myPoints ) {
        this.myPoints = myPoints ;
    }

    public MyRunPrint(List<DetectedObject> detectedObjects, int i ) {
        this.detectedObjects = detectedObjects ;
    }



    @Override
    public void run() {
        System.out.println("print");
        BufferedImage image = new BufferedImage(910, 600, BufferedImage.TYPE_INT_RGB);

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
        int width = 10;
        int height = 10;

//        for (Rect mp: myPoints) {
//            System.out.println(mp);
//            cg.drawOval( mp.y * 2, mp.x * 2, width, height);
//        }

        List<Integer> integers = detectedObjects.stream()
                .map(d -> d.getGroup())
                .distinct()
                .collect(Collectors.toList());

        integers.stream()
                .forEach(i -> {
                    List<DetectedObject> collect = detectedObjects.stream()
                            .filter(d -> d.getGroup().equals(i))
                            .sorted(((o1, o2) -> o1.getSeq().compareTo(o2.getSeq())))
                            .collect(Collectors.toList());

                    for (DetectedObject objects: collect ) {

                        if(collect.size() > (collect.indexOf(objects) + 1)){
                            cg.setColor(integerColorMap.get(i));
                            cg.setStroke(new BasicStroke(5));
                            cg.drawLine(objects.getRect().x,objects.getRect().y, collect.get(collect.indexOf(objects) +1 ).getRect().x, collect.get(collect.indexOf(objects) +1 ).getRect().y );

                        }
                    }

                });


        File output = new File("result/wykryty_ruch6.jpg");
        output.mkdirs();
        try {
            ImageIO.write( image, "jpg", output);
//            startClass.setStart(true);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }}
