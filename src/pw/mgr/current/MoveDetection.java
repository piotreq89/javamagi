package pw.mgr.current;

import javafx.scene.effect.GaussianBlur;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.objdetect.Objdetect;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class MoveDetection {

    static {
        System.loadLibrary("opencv_java310");
    }

    private static List<Rect> myPoints = new ArrayList<>();
    private static MyFrame myFrame = new MyFrame();
    private static Mat firstScreen = null;

    private static Mat secondScreen = null;
    private static Mat thirdScreen = null;
    private static Mat fourthScreen = null;

    private static Mat previousFrame = null;
    private static Mat diffFrame = null;

    private static Mat frame;
private static Mat finalFrame;
    private static int slider = 25;
    private static int slider2 = 4;
    private static int slider3 = 20;
    private static int slider4 = 500;
    private static  Integer i = 1 ;
    private static  Integer k = 0;
    private static  Integer seq = 1;
    private static  Integer group = 1;
    private static  Integer y = 0;
    private static  Mat baseFrame = null;
    private static double totalFrames;
    private static int progress;
    static long summary = 0;


    private static String oldMovieLocation;
    private static Movie movie = new Movie() ;

    private static VideoCapture video;
    private static List<DetectedObject> detectedObjectList;

    private static List<DetectedObject> detectedObjectListOld;

    public static void main(String[] args) {
        StartClass startClass = new StartClass();
        StartClass backgroundProcessing = new StartClass();

        addActionListenerToButtons(startClass, backgroundProcessing);

        addChangeListenerForSlider();
    }

    private static void addActionListenerToButtons(final StartClass startClass, final StartClass backgroundProcessing ) {
        class MyRun extends Thread {

            @Override
            public void run() {
                if(!startClass.isStart()){
                    startClass.setStart(true);
                    System.out.println("show");
                    while (startClass.isStart()){
                        boolean set = video.set(5, 10.0);
                        boolean set1 = video.set(Videoio.CAP_PROP_FRAME_WIDTH, 800);
                        boolean set2 = video.set(Videoio.CAP_PROP_FRAME_HEIGHT, 600);

                        showframe(frame, video, startClass);
                    }
                    System.out.println("after while");
                }
            }
        }


        myFrame.getStartButton().addActionListener(e -> {
            String movieLocationBaseOnName = movie.getMovieLocationBaseOnName(myFrame.getSelectedMovie());
            System.out.println("Start");
            if(!movieLocationBaseOnName.equals(oldMovieLocation)){
                frame = new Mat();
                video = new VideoCapture(movieLocationBaseOnName);
                detectedObjectListOld  = new ArrayList<>();
                detectedObjectList = new ArrayList<>();
                group = 1;
                seq = 1 ;
            }
            oldMovieLocation = movieLocationBaseOnName;

            MyRun myRun = new MyRun();
            myRun.start();
        });

        myFrame.getStopButton().addActionListener(e -> {
            System.out.println("Stop");
            startClass.setStart(false);
        });

        myFrame.getReloadButton().addActionListener(e -> {
            System.out.println("reload video");
            baseFrame = null;
            System.out.println("selected " + movie.getMovieLocationBaseOnName(myFrame.getSelectedMovie()));
            detectedObjectListOld  = new ArrayList<>();
            frame = new Mat();
            video = new VideoCapture(movie.getMovieLocationBaseOnName(myFrame.getSelectedMovie()));
            detectedObjectList = new ArrayList<>();
            group = 1;
            seq = 1 ;

        });

        myFrame.getBackgroundProcessButton().addActionListener(e -> {
            System.out.println("Przetwarzanie w tle");
            backgroundProcessing.setStart(false);

        });

        myFrame.getDrawMoveButton().addActionListener(e -> {
            System.out.println("print points");
//            MyRunPrint myRunPrint = new MyRunPrint(myPoints);

            MyRunPrint myRunPrint = new MyRunPrint(detectedObjectList, 1, null);
            myRunPrint.start();



            BufferedImage paintImage = null;
            try {
                paintImage = ImageIO.read(new File("result/wykryty_ruch6.jpg"));
            } catch (IOException er) {
                er.printStackTrace();
            }
            ImageIcon image = new ImageIcon(paintImage);

            myFrame.getResultLabel().repaint();
            myFrame.getResultLabel().setIcon(image);
//            myFrame.setData(myRunPrint.getData());
//            myFrame.getPanel().repaint();
            myFrame.repaint();

            startClass.setStart(true);
        });
    }

    private static void addChangeListenerForSlider() {
        myFrame.getjSlider1().addChangeListener(e -> {
            System.out.println("Change jSlider " + myFrame.getjSlider1().getValue());
                slider = myFrame.getjSlider1().getValue();
        });

        myFrame.getjSlider2().addChangeListener(e -> {
            System.out.println("Change jSlider 2 " + myFrame.getjSlider2().getValue());
                slider2 = myFrame.getjSlider2().getValue();
        });
    }

    private static void showframe(Mat frame, VideoCapture video, StartClass startClass) {

        Date startAll = new Date();
        /**pierwszy ekran*/

//        frame = new Mat();
        k++ ;
        Mat secondFrame;
        video.isOpened();

        if(k == 1) {
            double fps = video.get(5);
            totalFrames = video.get(7);
            double time = (int)totalFrames/(int)fps;
            myFrame.setjLabelScaleTopText(" " + time/4 + " sec");
            myFrame.setjLabelScaleCenterText(" " + time/8 + " sec");
            myFrame.setjLabelScaleDownText(" 00.0 sec");

            System.out.println("Fps = " + fps + " liczba klatek = " + totalFrames + " długość filmu = " + time/60);

        }

        if(progress < (int) (k / totalFrames * 100)){
            progress = (int) (k / totalFrames * 100);
            myFrame.setProgressLabel(" Postęp przetwarzania : " + progress + " %");
//            myFrame.setProgressLabel("postęp : " + progress + " %");
        }


        if((frame.dataAddr() != 0 || k == 1) && k < totalFrames){ //k == 1) {
            Date start = new Date();

            video.read(frame);
            boolean set = video.set(Videoio.CAP_PROP_FPS, 60);

            if (k % 4 == 0) {
                Imgproc.resize(frame, frame, new Size(800, 600));
                firstScreen = frame;

                if (baseFrame == null) {
                    baseFrame = frame.clone();
                }

                /**drugi ekran*/

                secondFrame = new Mat(frame.size(), CvType.CV_8UC1);
                //Konwertuje obraz do skali szarości
                Imgproc.cvtColor(frame, secondFrame, Imgproc.COLOR_BGR2GRAY);
                //Rozmycie filtrem Gaussa, usuwa on detale i eliminuje zakłócenia
                // src, dst, wielkośc jądra gausa, odchylenie standardowe jądra w kierunku X
                Imgproc.GaussianBlur(secondFrame, secondFrame, new Size(3, 3), 1.0, 1.0, 0);
                secondScreen = secondFrame;

                if (previousFrame == null) {
                    previousFrame = new Mat(secondFrame.size(), CvType.CV_8UC1);
                }

                if (diffFrame == null) {
                    diffFrame = new Mat(secondFrame.size(), CvType.CV_8UC1);
                }

//            if (k % 4 == 0) {
                Core.absdiff(previousFrame, secondFrame, diffFrame);
//                Core.subtract(previousFrame, secondFrame, diffFrame);
                previousFrame = secondFrame.clone();
//            }

                thirdScreen = diffFrame;

                /**czwarty ekran*/

                finalFrame = new Mat(secondFrame.size(), CvType.CV_8UC1);

                //new
                Imgproc.threshold(diffFrame, finalFrame, slider3, slider4, Imgproc.THRESH_BINARY);

                Mat erodeClose = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(slider, slider));
                Imgproc.morphologyEx(finalFrame, finalFrame, Imgproc.MORPH_CLOSE, erodeClose);

                Mat dilateOpen = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(slider2, slider2));

                Imgproc.morphologyEx(finalFrame, finalFrame, Imgproc.MORPH_OPEN, dilateOpen);

                fourthScreen = finalFrame.clone();
//                System.out.println("time processing przetwarzanie tee" + (new Date().getTime() - start.getTime()) + " ramka " + k);
//        if(k % 4 == 0) {
                Date startDetectionContour = new Date();
                detection_contours(fourthScreen);
                long l = new Date().getTime() - startAll.getTime();
//                System.out.println("time processing detekcja konturów " + l + " ramka " + k);
                 summary += l;
//                System.out.println("w sumie czas "  + summary);
//        }

                if (k % 3 == 0) {
                    ImageIcon baseImage = new ImageIcon(Mat2bufferedImage(baseFrame));

                    MyRunPrint myRunPrint = new MyRunPrint(detectedObjectList, y, baseImage);
                    myRunPrint.setTotalFrames(totalFrames);


                    BufferedImage paintImage = null;
                    try {
                        paintImage = ImageIO.read(new File("result/wykryty_ruch6.jpg"));
                    } catch (Exception er) {
                        er.printStackTrace();
                        paintImage = null;
                    }
                    if (paintImage != null) {
                        ImageIcon image = new ImageIcon(paintImage);


                        myFrame.getVideoLabelFourthScreen().repaint();
                        myFrame.getVideoLabelFourthScreen().setIcon(image);

                    }

                    BufferedImage baseBuffImage = null;
                    try {
                        baseBuffImage = ImageIO.read(new File("result/wykryty_ruchBase.jpg"));
                    } catch (Exception er) {
                        er.printStackTrace();
                        baseBuffImage = null;
                    }
                    if (baseBuffImage != null) {
                        ImageIcon image = new ImageIcon(baseBuffImage);


//                myFrame.getVideoLabelThirdScreen())
                        myFrame.getVideoLabelThirdScreen().setIcon(image);

                    }

                    y++;

                    myRunPrint.start();

                putScreenInVideoLabel();
                }
            }
        }else if(k > totalFrames){
//            if (k % 3 == 0) {
//                ImageIcon baseImage = new ImageIcon(Mat2bufferedImage(baseFrame));
//
//                MyRunPrint myRunPrint = new MyRunPrint(detectedObjectList, y, baseImage);
//                myRunPrint.setTotalFrames(totalFrames);
//
//
//                BufferedImage paintImage = null;
//                try {
//                    paintImage = ImageIO.read(new File("result/wykryty_ruch6.jpg"));
//                } catch (Exception er) {
//                    er.printStackTrace();
//                    paintImage = null;
//                }
//                if (paintImage != null) {
//                    ImageIcon image = new ImageIcon(paintImage);
//
//
//                    myFrame.getVideoLabelFourthScreen().repaint();
//                    myFrame.getVideoLabelFourthScreen().setIcon(image);
//
//                }
//
//                BufferedImage baseBuffImage = null;
//                try {
//                    baseBuffImage = ImageIO.read(new File("result/wykryty_ruchBase.jpg"));
//                } catch (Exception er) {
//                    er.printStackTrace();
//                    baseBuffImage = null;
//                }
//                if (baseBuffImage != null) {
//                    ImageIcon image = new ImageIcon(baseBuffImage);
//
//
////                myFrame.getVideoLabelThirdScreen())
//                    myFrame.getVideoLabelThirdScreen().setIcon(image);
//
//                }
//
//                y++;
//
//                myRunPrint.start();
//
//                putScreenInVideoLabel();
//            }
            JOptionPane.showMessageDialog(null, "Przetwarzanie zakończone.","Informacja", JOptionPane.INFORMATION_MESSAGE);
            startClass.setStart(false);
        }
    }

    private static void putScreenInVideoLabel() {

        ImageIcon firstImage = new ImageIcon(Mat2bufferedImage(firstScreen));
//        System.out.println(" secondScreen.size() " + secondScreen.size());
//        Imgproc.resize(secondScreen, secondScreen,new Size(800, 768));
//        ImageIcon secondImage = new ImageIcon(Mat2bufferedImage(secondScreen));
//        ImageIcon thirdImage = new ImageIcon(Mat2bufferedImage(thirdScreen));
        ImageIcon fourthImage = new ImageIcon(Mat2bufferedImage(finalFrame));

        myFrame.getVideoLabelFirstScreen().setIcon(firstImage);
        myFrame.getResultLabel().setIcon(fourthImage);
//        myFrame.getVideoLabelThirdScreen().setIcon(thirdImage);
//        myFrame.getVideoLabelFourthScreen().setIcon(fourthImage);



    }

//    private static void printRectangle(ArrayList<Rect> array, Mat img) {
//        if (array.size() > 0) {
//            Iterator<Rect> it2 = array.iterator();
////            while (it2.hasNext()) {
////                Rect obj = it2.next();
//////                obj.height += 20;
//////                obj.width +=10;
//////                Imgproc.rectangle(firstScreen, obj.br(), new Point(obj.br().x - 30, obj.br().y - 50), new Scalar(0, 255, 0), 1);
//////                System.out.println(" .br() " + obj.br() + " .tl() " + obj.tl());
//////                Imgproc.putText(firstScreen, "obiekt " , new Point(700, 10), 2, 1, new Scalar(0, 255, 0), 1);
////                if(array.indexOf(obj) -1 > 1){
//////                    Imgproc.line(firstScreen,obj.br(), array.get(array.indexOf(obj) -1).br(),  new Scalar(0, 0, 255), 2);
////                }
////            }
//        }
//
//    }

    public static BufferedImage Mat2bufferedImage(Mat frame) {

        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", frame, buffer);
        // build and return an Image created from the image encoded in the

        BufferedImage img = null;

        try {
            return ImageIO.read(new ByteArrayInputStream(buffer.toArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }


    public static void detection_contours(Mat outmat) {
        Mat v = new Mat();
        Mat vv = outmat.clone();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(vv, contours, v, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        double maxArea = 120;
        int maxAreaIdx = -1;
        Rect r = null;
        ArrayList<Rect> rect_array = new ArrayList<Rect>();

        if(detectedObjectList == null){
            detectedObjectList = new ArrayList<>();
        }
        for (int idx = 0; idx < contours.size(); idx++) {
            Mat contour = contours.get(idx);

            double contourarea = Imgproc.contourArea(contour);
            if (contourarea > maxArea && contourarea < 3500) {
                maxAreaIdx = idx;
                r = Imgproc.boundingRect(contours.get(maxAreaIdx));
//                System.out.println(maxAreaIdx + " " + r + " r.tl() " +r.tl() + " r.br() " + r.br());
//                if(r.br().x)

//                r.width= 40 ;
//                r.height= 60 ;

//                double[] ints = {20, 40};
//                r.set(ints);
//                r.tl();
//                System.out.println("r " + r + " area " + r.area());
                    r = new Rect((int)r.tl().x, (int)r.tl().y, 30 , 60);


                myPoints.add(r);
                rect_array.add(r);

//                if (r.br().y > 20) {
                    if((r.br().y > 20 && r.br().y < 490)
                            && (r.br().x > 90 && r.br().x < 760)
                            && (r.tl().y > 20 && r.tl().y < 480)
                            && (r.tl().x > 30 && r.tl().x < 740)&& r.area() < 3000){

//                            && r.br().x > 80 && r.tl().y <= 790 && r.tl().y >= 10 && r.area() < 3000){
//                        if(r.br().y < 480 && r.br().x > 80 && r.tl().y <= 790 && r.tl().y >= 10 && r.area() < 3000){

                        DetectedObject detectedObject = new DetectedObject(i, maxAreaIdx, r, seq);

                        List<DetectedObject> collect = detectedObjectList.stream()
                                .filter(doa -> doa.getIterationId().equals(i - 1))
                                .collect(Collectors.toList());

                        int numer = 35;
                        Optional<DetectedObject> first = collect.stream()
                                .filter(doa ->
                                        (doa.getRect().tl().x <= (detectedObject.getRect().tl().x + numer)
                                        && doa.getRect().tl().x >= (detectedObject.getRect().tl().x - numer)
                                        && doa.getRect().tl().y <= (detectedObject.getRect().tl().y + numer)
                                        && doa.getRect().tl().y >= (detectedObject.getRect().tl().y - numer)
                                        &&(doa.getRect().br().x <= (detectedObject.getRect().br().x + numer)
                                        && doa.getRect().br().x >= (detectedObject.getRect().br().x - numer)
                                        && doa.getRect().br().y <= (detectedObject.getRect().br().y + numer)
                                        && doa.getRect().br().y >= (detectedObject.getRect().br().y - numer))
                                )
                                 &&          (detectedObject.getRect().tl().x + detectedObject.getRect().width <= (doa.getRect().tl().x + doa.getRect().width + numer)
                                                && detectedObject.getRect().tl().x + detectedObject.getRect().width >= (doa.getRect().tl().x + doa.getRect().width  - numer)
                                                && detectedObject.getRect().tl().y <= (doa.getRect().tl().y + numer)
                                                && detectedObject.getRect().tl().y >= (doa.getRect().tl().y - numer)
                                                &&(detectedObject.getRect().br().x - detectedObject.getRect().width <= (doa.getRect().br().x - doa.getRect().width + numer)
                                                && detectedObject.getRect().br().x - detectedObject.getRect().width >= (doa.getRect().br().x - doa.getRect().width - numer)
                                                && detectedObject.getRect().br().y <= (doa.getRect().br().y + numer)
                                                && detectedObject.getRect().br().y >= (doa.getRect().br().y - numer)))

                                )
                                .findFirst();

                        if (first.isPresent()) {
                            detectedObject.setGroup(first.get().getGroup());
                            detectedObjectList.add(detectedObject);
                        } else {
                            detectedObject.setGroup(group);
                            detectedObjectList.add(detectedObject);
                            group++;

                        }

//                        System.out.println(maxAreaIdx + " rect " + detectedObject.getRect() + " r.tl() " + detectedObject.getRect().tl() + " r.br() "
//                                + detectedObject.getRect().br() + " seq " + detectedObject.getSeq() + " group " + detectedObject.getGroup());
                        seq++;
                    }

//                }
            }
        }

        int numer = 40 ;

        detectedObjectListOld.stream()
                .filter(co -> (co.getRect().br().y > 50 && co.getRect().br().y < 470)
                        && (co.getRect().br().x > 110 && co.getRect().br().x < 730)
                        && (co.getRect().tl().y > 50 && co.getRect().tl().y < 460)
                        && (co.getRect().tl().x > 50 && co.getRect().tl().x < 680))
//                .filter(co -> co.getRect().br().y < 440 && co.getRect().br().x > 105 && co.getRect().tl().y <= 440 && co.getRect().tl().y >= 0)
                .filter(co -> co.getRect().area() < 2500)
                .forEach( co -> {

                    List<DetectedObject> collect2 = detectedObjectList.stream()
                            .filter(dol -> dol.getIterationId().equals(i)).collect(Collectors.toList());
                    Optional<DetectedObject> first1 = collect2.stream()
                            .filter(doa ->

                                            (co.getRect().tl().x <= (doa.getRect().tl().x + numer)
                                                    && co.getRect().tl().x >= (doa.getRect().tl().x - numer)
                                                    && co.getRect().tl().y <= (doa.getRect().tl().y + numer)
                                                    && co.getRect().tl().y >= (doa.getRect().tl().y - numer)
                                                    &&(co.getRect().br().x <= (doa.getRect().br().x + numer)
                                                    && co.getRect().br().x >= (doa.getRect().br().x - numer)
                                                    && co.getRect().br().y <= (doa.getRect().br().y + numer)
                                                    && co.getRect().br().y >= (doa.getRect().br().y - numer)))

                                    &&          (co.getRect().tl().x + co.getRect().width <= (doa.getRect().tl().x + doa.getRect().width + numer)
                                                    && co.getRect().tl().x + co.getRect().width >= (doa.getRect().tl().x + doa.getRect().width  - numer)
                                                    && co.getRect().tl().y <= (doa.getRect().tl().y + numer)
                                                    && co.getRect().tl().y >= (doa.getRect().tl().y - numer)
                                                    &&(co.getRect().br().x - co.getRect().width <= (doa.getRect().br().x - doa.getRect().width + numer)
                                                    && co.getRect().br().x - co.getRect().width >= (doa.getRect().br().x - doa.getRect().width - numer)
                                                    && co.getRect().br().y <= (doa.getRect().br().y + numer)
                                                    && co.getRect().br().y >= (doa.getRect().br().y - numer)))
                            )



//                                    co.getRect().x <= (doa.getRect().x + numer)
//                                    && co.getRect().x >= (doa.getRect().x - numer)
//                                    && co.getRect().y <= (doa.getRect().y + numer)
//                                    && co.getRect().y >= (doa.getRect().y - numer))
                            .findFirst();
                    if(!first1.isPresent()){
                        co.setIterationId(i);
                        co.setSeq(seq);
                        detectedObjectList.add(co);
                    }
                });

        detectedObjectListOld = new ArrayList<>();

        detectedObjectList.stream()
                .filter(dol -> dol.getIterationId().equals(i -1 ))
                .forEach( c -> detectedObjectListOld.add(c));


        detectedObjectList.stream()
                .filter(d -> d.getIterationId().equals(i))
                .forEach(d -> {

                    Imgproc.rectangle(firstScreen, d.getRect().br(), d.getRect().tl(), new Scalar(0, 255, 0), 1);

//                    Imgproc.rectangle(firstScreen, d.getRect().tl(), d.getRect().br() , new Scalar(0, 255, 0, 255), 50);
                    Imgproc.putText(firstScreen, "tl " + d.getRect().tl() ,
                            new Point( d.getRect().tl().x - 20 , d.getRect().tl().y - 5), 1, 1, new Scalar(255, 255, 0), 1);

                    Imgproc.putText(firstScreen, "br " + d.getRect().br() ,
                            new Point(d.getRect().br().x - 20 ,d.getRect().br().y + 15) , 1, 1, new Scalar(255, 255, 0), 1);

//                    Imgproc.putText(firstScreen, "br",
//                            d.getRect().br(), 1, 1, new Scalar(255, 255, 0), 2);


//                    Imgproc.putText(firstScreen, "1" ,
//                            new Point(d.getRect().tl().x + d.getRect().width, d.getRect().tl().y) , 1, 1, new Scalar(255, 255, 0), 2);
//
//                    Imgproc.putText(firstScreen, "2" ,
//                            new Point(d.getRect().br().x - d.getRect().width, d.getRect().br().y), 1, 1, new Scalar(255, 255, 0), 2);
                });


        i++ ;

//            System.out.println("---------------------------");

            v.release();

//            printRectangle(rect_array, outmat);
//                }

        Point pt1 = new Point(80, 40);
        Point pt2 = new Point(750, 450);
        Imgproc.rectangle(firstScreen, pt1, pt2, new Scalar(0, 255, 255), 1);

        Imgproc.putText(firstScreen, "pt1 x " + pt1.x + " pt1 y " + pt1.y , new Point(75, 35) , 1, 1, new Scalar(255, 255, 255), 1);
        Imgproc.putText(firstScreen, "pt2 x " + pt2.x + " pt2 y " + pt2.y , new Point(pt2.x - 200, pt2.y + 15) , 1, 1, new Scalar(255, 255, 255), 1);
        Imgproc.putText(firstScreen, "pt3 x " + pt1.x + " pt3 y " + (pt2.y - pt1.y) , new Point(pt1.x - 5, pt2.y + 15) , 1, 1, new Scalar(255, 255, 255), 1);
        Imgproc.putText(firstScreen, "pt4 x " + (pt2.x - pt1.x) + " pt4 y " + pt1.y , new Point(pt2.x - 190, pt1.y - 5) , 1, 1, new Scalar(255, 255, 255), 1);


    }
}