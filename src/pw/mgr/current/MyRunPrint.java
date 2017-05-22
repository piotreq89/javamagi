package pw.mgr.current;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.tc33.jheatchart.HeatChart;

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

/*
                    for (DetectedObject objects: collect ) {

                        if(collect.size() > (collect.indexOf(objects) + 1)){

                            Optional<DetectedObject> first = detectedObjects.stream()
                                    .filter(d -> !d.equals(objects))
                                    .filter(d -> d.getSeq() < objects.getSeq())
                                    .filter(d -> d.getRect().x == objects.getRect().x && d.getRect().y == objects.getRect().y)
                                    .findFirst();

                            if(first.isPresent()){
                                DetectedObject detectedObject = first.get();

//                                if(detectedObject.getColorMap() != null){
//                                    objects.setColorMap(colorMaps.get(detectedObject.getColorMap().getId() + 1));
//                                }else{
//                                    objects.setColorMap(colorMaps.get(1));
//                                }

                                if(detectedObject.getColor() != null){
                                    objects.setColor(new Color(i + 10, 255, 255));
                                }else{
                                    objects.setColor(Color.white);
                                }

                            }else {
                                objects.setColor(Color.white);

                            }

//                            if(objects.getColorMap() == null){
//                                objects.setColorMap(colorMaps.get(1));
//                            }else{
//                                objects.setColorMap(colorMaps.get(objects.getColorMap().getId() + 1));
//                            }

                            List<Rect> detectedPointToPrint = detectedObjects.stream()
                                    .sorted(((o1, o2) -> o1.getSeq().compareTo(o2.getSeq())))
                                    .map(o -> o.getRect())
                                    .collect(Collectors.toList());

                            List<SortedObject> sortedObjects = new ArrayList<>();

                            int o = 1 ;

                            for (Rect rect: detectedPointToPrint ) {
                                if (sortedObjects.isEmpty()) {
                                    SortedObject sortedObject = new SortedObject(o, 1, rect);
                                    sortedObjects.add(sortedObject);
                                    o++;

                                }else {
                                    Optional<SortedObject> first1 = sortedObjects.stream()
                                            .filter(s -> s.getRect().x == rect.x && s.getRect().y == rect.y)
                                            .findFirst();

                                    if(first1.isPresent()){
                                        SortedObject sortedObject = first1.get();
                                        sortedObject.setSortedIdIn(sortedObject.getSortedIdIn() + 1);
                                        sortedObjects.add(sortedObject);
                                    }else{
                                        SortedObject sortedObject = new SortedObject(o, 1, rect);
                                        sortedObjects.add(sortedObject);
                                        o++;
                                    }
                                }
                            }

                            int colour = 0 ;

                            List<Integer> numbers = sortedObjects.stream()
                                    .map(d -> d.getSortedId())
                                    .distinct()
                                    .collect(Collectors.toList());

                            numbers.stream()
                                    .forEach(n -> {
                                        List<SortedObject> collect1 = sortedObjects.stream()
                                                .filter(s -> s.getSortedId().equals(n))
                                                .collect(Collectors.toList());

                                        int size = collect1.size();
                                        SortedObject sortedObject = collect1.stream()
                                                .findFirst()
                                                .get();

//                                        System.out.println("rect.x " + rect.getRect().x + " rect.y " + rect.getRect().y + " rect.getSortedIdIn() " + rect.getSortedIdIn());
//                                        int sortedIdInColour = rect.getSortedIdIn();
//                                        rect.setSortedIdIn(sortedIdInColour);
                                        size= size* 63 ;
//                                        System.out.println("size " + size);
                                        if(size <= 255 && size > 0){
                                            cg.setColor(new Color(size,255, 255));
                                        }else if(size <= 255 && size >= 510){
                                            cg.setColor(new Color(255, size/2 , 255));
                                        }else if(size <= 765 && size > 0){
                                            cg.setColor(new Color(255, 255, size/3));
                                        }
                                        cg.fillOval(sortedObject.getRect().x, sortedObject.getRect().y, 5, 5);


                                    });

//                            sortedObjects.stream()
//                                            .forEach(rect -> {
//
//                                                System.out.println("rect.x " + rect.getRect().x + " rect.y " + rect.getRect().y + " rect.getSortedIdIn() " + rect.getSortedIdIn());
//                                                int sortedIdInColour = rect.getSortedIdIn();
//                                                rect.setSortedIdIn(sortedIdInColour);
//                                                if(rect.getSortedIdIn() <= 255 && rect.getSortedIdIn() > 0){
//                                                    cg.setColor(new Color(rect.getSortedIdIn(),255, 255));
//                                                }else if(rect.getSortedIdIn() <= 255 && rect.getSortedIdIn() >= 510){
//                                                    cg.setColor(new Color(255, rect.getSortedIdIn()/2 , 255));
//                                                }else if(rect.getSortedIdIn() <= 765 && rect.getSortedIdIn() > 0){
//                                                    cg.setColor(new Color(255, 255, rect.getSortedIdIn()/3));
//                                                }
//                                                cg.fillOval(rect.getRect().x, rect.getRect().y, 10, 10);
//
//                                            });

//                            cg.setColor(objects.getColor());
////                            cg.setColor(Color.WHITE);

//                            cg.setColor(integerColorMap.get(i));
//                            cg.setStroke(new BasicStroke(5));
//                            cg.fillOval(objects.getRect().x, objects.getRect().y , width, height);

//                            cg.drawOval( objects.getRect().x, objects.getRect().y , width, height);
//                            cg.fillRect(objects.getRect().x,objects.getRect().y, 4 ,4);
//                            cg.drawLine(objects.getRect().x,objects.getRect().y, collect.get(collect.indexOf(objects) +1 ).getRect().x, collect.get(collect.indexOf(objects) +1 ).getRect().y );

                                }



                    }
*/

                    for (DetectedObject objects: collect ) {

                        if(collect.size() > (collect.indexOf(objects) + 1)){

                            Optional<DetectedObject> first = detectedObjects.stream()
                                    .filter(d -> !d.equals(objects))
                                    .filter(d -> d.getSeq() < objects.getSeq())
                                    .filter(d -> d.getRect().x == objects.getRect().x && d.getRect().y == objects.getRect().y)
                                    .findFirst();

//                            if(first.isPresent()){
//                                DetectedObject detectedObject = first.get();
//
//                                objects.setColorMap(colorMaps.get(detectedObject.getColorMap().getId() + 1));
//
//                            }else {
//                                objects.setColorMap(colorMaps.get(1));
//
//                            }

//                            if(objects.getColorMap() == null){
//                                objects.setColorMap(colorMaps.get(1));
//                            }else{
//                                objects.setColorMap(colorMaps.get(objects.getColorMap().getId() + 1));
//                            }
//                            cg.setColor(objects.getColorMap().getColor());
////                            cg.setColor(Color.WHITE);

                            cg.setColor(integerColorMap.get(i));
                            cg.setStroke(new BasicStroke(5));
//                            cg.fillOval(objects.getRect().x, objects.getRect().y , width, height);

//                            cg.drawOval( objects.getRect().x, objects.getRect().y , width, height);
//                            cg.fillRect(objects.getRect().x,objects.getRect().y, 4 ,4);
                            cg.drawLine(objects.getRect().x,objects.getRect().y, collect.get(collect.indexOf(objects) +1 ).getRect().x, collect.get(collect.indexOf(objects) +1 ).getRect().y );

                        }
                    }


                });




//        try {
//            for (int x = 0; x < data.length; x++)
//            {
//                for (int y = 0; y < data[0].length; y++)
//                {
//                    int colorIndex = dataColorIndices[x][y];
//                    if (colorIndex != NA) {
//                        cg.setColor(integerColorMap.get((colorIndex)));
//                        cg.fillRect(x, y, 1, 1);
//                    }
//// Alternate flow, if you really want the pixels to be white
////                else {
////                    bufferedGraphics.setColor(Color.WHITE);
////                    bufferedGraphics.fillRect(x, y, 1, 1);
////                }
//                }
//            }
//        }
//        finally {
//            bufferedGraphics.dispose();
//        }

// Create some dummy data.
//        double[][] data = new double[][]{{3,2,3,4,5,6,3,2,3,4,5,6},
//                {2,3,4,5,6,7,3,2,3,4,5,6},
//                {3,4,5,6,7,6,3,2,3,4,5,6},
//                {4,5,6,7,6,5,3,2,3,4,5,6}};
//
//// Step 1: Create our heat map chart using our data.
//        HeatChart map = new HeatChart(data);
////
////// Step 2: Customise the chart.
//        map.setTitle("This is my heat chart title");
//        map.setXAxisLabel("X aaa");
//        map.setYAxisLabel("Y bbbb");
////
////// Step 3: Output the chart to a file.
//        try {
//            map.saveToFile(new File("result/java-heat-chart.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        System.out.println("finish print");
        File output = new File("result/wykryty_ruch6.jpg");
        output.mkdirs();
        try {
            ImageIO.write( image, "jpg", output);
//            startClass.setStart(true);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }}
