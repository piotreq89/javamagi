package pw.mgr.current;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MoveDetection {

    static {
        System.loadLibrary("opencv_java310");
    }
    private static MyFrame myFrame = new MyFrame();
    private static List<Rect> myPoints = new ArrayList<>();

    private static Mat firstScreen = null;
    private static Mat secondScreen = null;
    private static Mat thirdScreen = null;
    private static Mat fourthScreen = null;
    private static Mat previousFrame = null;
    private static Mat diffFrame = null;
    private static Mat baseFrame = null;
    private static Mat frame;
    private static Mat finalFrame;

    private static  Integer i = 1 ;
    private static  Integer k = 0;
    private static  Integer seq = 1;
    private static  Integer group = 1;
    private static  Integer y = 0;
    private static int morphOpenSlider = 25;
    private static int morphCloseSlider = 4;
    private static double totalFrames;
    private static int progress;

    private static String oldMovieLocation;
    private static Movie movie = new Movie() ;

    private static VideoCapture video;
    private static List<DetectedObject> detectedObjectList;
    private static StartClass startClass = new StartClass();

    private static List<DetectedObject> detectedObjectListOld;

    public static void main(String[] args) {
        addActionListenerToButtons(startClass);
        addChangeListenerForSlider();
    }

    private static void addActionListenerToButtons(final StartClass startClass) {
        class MyRun extends Thread {

            @Override
            public void run() {
                if(!startClass.isStart()){
                    startClass.setStart(true);
                    System.out.println("show");
                    k = 0;
                    while (startClass.isStart()){
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
                video = new VideoCapture(movieLocationBaseOnName);
                clearApplicationData();
            }
            oldMovieLocation = movieLocationBaseOnName;
            startClass.setStartMode(StartMode.NORMAL);
            MyRun myRun = new MyRun();
            myRun.start();
        });

        myFrame.getStopButton().addActionListener(e -> {
            System.out.println("Stop");
            startClass.setStart(false);
        });

        myFrame.getReloadButton().addActionListener(e -> {
            System.out.println("reload video");
            System.out.println("selected " + movie.getMovieLocationBaseOnName(myFrame.getSelectedMovie()));
            k = 0;
            video = new VideoCapture(movie.getMovieLocationBaseOnName(myFrame.getSelectedMovie()));
            clearApplicationData();
        });

        myFrame.getBackgroundProcessButton().addActionListener(e -> {
            System.out.println("Processing in background");
            startClass.setStartMode(StartMode.BACKGROUND);
            video = new VideoCapture(movie.getMovieLocationBaseOnName(myFrame.getSelectedMovie()));
            clearApplicationData();
            k = 0;
            MyRun myRun = new MyRun();
            myRun.start();
        });
    }

    private static void clearApplicationData() {
        baseFrame = null;
        diffFrame = null;
        previousFrame = null;
        myPoints = new ArrayList<>();
        detectedObjectListOld = new ArrayList<>();
        detectedObjectList = new ArrayList<>();
        progress = 0;
        frame = new Mat();
        group = 1;
        seq = 1 ;
    }

    private static void addChangeListenerForSlider() {
        myFrame.getMorphOpenJSlider().addChangeListener(e -> {
            System.out.println("Zmiana wartośc dla otwarcia : " + myFrame.getMorphOpenJSlider().getValue());
                morphOpenSlider = myFrame.getMorphOpenJSlider().getValue();
        });

        myFrame.getMorphCloseJSlider().addChangeListener(e -> {
            System.out.println("Zmiana wartośc dla zamknięcia : " + myFrame.getMorphCloseJSlider().getValue());
                morphCloseSlider = myFrame.getMorphCloseJSlider().getValue();
        });
    }




    private static synchronized void showframe(Mat frame, VideoCapture video, StartClass startClass) {
        k++ ;

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
        }


        if((frame.dataAddr() != 0 || k == 1) && k < totalFrames){ //k == 1) {
            video.read(frame);

            if (k % 4 == 0) {
                Imgproc.resize(frame, frame, new Size(800, 600));
                firstScreen = frame;

                if (baseFrame == null) {
                    baseFrame = frame.clone();
                }

                /**drugi ekran*/

                Mat secondFrame = new Mat(frame.size(), CvType.CV_8UC1);
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

                Core.absdiff(previousFrame, secondFrame, diffFrame);
                previousFrame = secondFrame.clone();

                thirdScreen = diffFrame;

                /**czwarty ekran*/

                finalFrame = new Mat(secondFrame.size(), CvType.CV_8UC1);

                //new
                Imgproc.threshold(diffFrame, finalFrame, 20, 500, Imgproc.THRESH_BINARY);

                Mat erodeClose = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(morphOpenSlider, morphOpenSlider));
                Imgproc.morphologyEx(finalFrame, finalFrame, Imgproc.MORPH_CLOSE, erodeClose);

                Mat dilateOpen = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(morphCloseSlider, morphCloseSlider));

                Imgproc.morphologyEx(finalFrame, finalFrame, Imgproc.MORPH_OPEN, dilateOpen);

                fourthScreen = finalFrame.clone();

                detection_contours(fourthScreen);

                if (k % 3 == 0 && StartMode.NORMAL.equals(startClass.getStartMode())) {
                    ImageIcon baseImage;
                    if(baseFrame != null) {
                        baseImage = new ImageIcon(Mat2bufferedImage(baseFrame));

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
                            myFrame.getVideoLabelThirdScreen().setIcon(image);

                        }

                        y++;

                        myRunPrint.start();
                    }

                putScreenInVideoLabel();
                }
            }
        }else if(k > totalFrames){
            if (StartMode.BACKGROUND.equals(startClass.getStartMode())) {
                ImageIcon baseImage = new ImageIcon(Mat2bufferedImage(baseFrame));
                MyRunPrint myRunPrint = new MyRunPrint(detectedObjectList, 10, baseImage);
                myRunPrint.setTotalFrames(totalFrames);
                myRunPrint.start();

                while(!myRunPrint.currentThread().isInterrupted()){
                    System.out.println("koniec ?");
                    // do stuff
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
                        myFrame.getVideoLabelThirdScreen().setIcon(image);

                    }
                    break;
                }

            }
            JOptionPane.showMessageDialog(null, "Przetwarzanie zakończone.","Informacja", JOptionPane.INFORMATION_MESSAGE);
            startClass.setStart(false);
        }
    }

    private static void putScreenInVideoLabel() {

        ImageIcon firstImage = new ImageIcon(Mat2bufferedImage(firstScreen));
        ImageIcon fourthImage = new ImageIcon(Mat2bufferedImage(finalFrame));

        myFrame.getVideoLabelFirstScreen().setIcon(firstImage);
        myFrame.getResultLabel().setIcon(fourthImage);
    }

    public static BufferedImage Mat2bufferedImage(Mat frame) {

        BufferedImage img = null;
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        if(frame != null) {
            Imgcodecs.imencode(".png", frame, buffer);

            // build and return an Image created from the image encoded in the

            try {
                return ImageIO.read(new ByteArrayInputStream(buffer.toArray()));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                r = new Rect((int)r.tl().x, (int)r.tl().y, 30 , 60);


                myPoints.add(r);
                rect_array.add(r);

                if((r.br().y > 20 && r.br().y < 490) && (r.br().x > 90 && r.br().x < 760) && (r.tl().y > 20 && r.tl().y < 480) && (r.tl().x > 30 && r.tl().x < 740)&& r.area() < 3000){
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
                                            &&(detectedObject.getRect().tl().x + detectedObject.getRect().width <= (doa.getRect().tl().x + doa.getRect().width + numer)
                                            && detectedObject.getRect().tl().x + detectedObject.getRect().width >= (doa.getRect().tl().x + doa.getRect().width  - numer)
                                            && detectedObject.getRect().tl().y <= (doa.getRect().tl().y + numer)
                                            && detectedObject.getRect().tl().y >= (doa.getRect().tl().y - numer)
                                            &&(detectedObject.getRect().br().x - detectedObject.getRect().width <= (doa.getRect().br().x - doa.getRect().width + numer)
                                            && detectedObject.getRect().br().x - detectedObject.getRect().width >= (doa.getRect().br().x - doa.getRect().width - numer)
                                            && detectedObject.getRect().br().y <= (doa.getRect().br().y + numer)
                                            && detectedObject.getRect().br().y >= (doa.getRect().br().y - numer)))
                                ).findFirst();

                        if (first.isPresent()) {
                            detectedObject.setGroup(first.get().getGroup());
                            detectedObjectList.add(detectedObject);
                        } else {
                            detectedObject.setGroup(group);
                            detectedObjectList.add(detectedObject);
                            group++;

                        }
                        seq++;
                }
            }
        }

        int numer = 40 ;

        detectedObjectListOld.stream()
                .filter(co -> (co.getRect().br().y > 50 && co.getRect().br().y < 470)
                        && (co.getRect().br().x > 110 && co.getRect().br().x < 730)
                        && (co.getRect().tl().y > 50 && co.getRect().tl().y < 460)
                        && (co.getRect().tl().x > 50 && co.getRect().tl().x < 680))
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
                            ).findFirst();

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
                    Imgproc.putText(firstScreen, "tl " + d.getRect().tl() ,
                            new Point( d.getRect().tl().x - 20 , d.getRect().tl().y - 5), 1, 1, new Scalar(255, 255, 0), 1);

                    Imgproc.putText(firstScreen, "br " + d.getRect().br() ,
                            new Point(d.getRect().br().x - 20 ,d.getRect().br().y + 15) , 1, 1, new Scalar(255, 255, 0), 1);
                });
        i++ ;

        v.release();

        Point pt1 = new Point(80, 40);
        Point pt2 = new Point(750, 450);
        Imgproc.rectangle(firstScreen, pt1, pt2, new Scalar(0, 255, 255), 1);

        Imgproc.putText(firstScreen, "pt1 x " + pt1.x + " pt1 y " + pt1.y , new Point(75, 35) , 1, 1, new Scalar(255, 255, 255), 1);
        Imgproc.putText(firstScreen, "pt2 x " + pt2.x + " pt2 y " + pt2.y , new Point(pt2.x - 200, pt2.y + 15) , 1, 1, new Scalar(255, 255, 255), 1);
        Imgproc.putText(firstScreen, "pt3 x " + pt1.x + " pt3 y " + (pt2.y - pt1.y) , new Point(pt1.x - 5, pt2.y + 15) , 1, 1, new Scalar(255, 255, 255), 1);
        Imgproc.putText(firstScreen, "pt4 x " + (pt2.x - pt1.x) + " pt4 y " + pt1.y , new Point(pt2.x - 190, pt1.y - 5) , 1, 1, new Scalar(255, 255, 255), 1);


    }
}