package pw.mgr.current;

import javafx.scene.effect.GaussianBlur;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
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

    private static Mat frame = new Mat();
    private static  Mat flow ;

    private static int slider = 1;
    private static int slider2 = 9;
    private static int slider3 = 1;

    private static int slider4 = 9;
    private static  Integer i = 1 ;
    private static  Integer k = 0;

    private static  Integer seq = 1;
    private static  Integer group = 1;

    private static String videoAddress = "C:\\Users\\piotrek\\Desktop\\test\\VIDEO0386.mp4";
//    private static String videoAddress = "C:\\Users\\piotrek\\Desktop\\test\\magi_new.mp4";

    private static VideoCapture video = new VideoCapture(videoAddress);
    private static List<DetectedObject> detectedObjectList;

    public static void main(String[] args) {
        StartClass startClass = new StartClass();

        addActionListenerToButtons(startClass);

        addChangeListenerForSlider();
    }

    private static void addActionListenerToButtons(final StartClass startClass) {
        class MyRun extends Thread {
            @Override
            public void run() {
                System.out.println("show");
                while (startClass.isStart()){
                    showframe(frame, video);
                }
            }
        }

        myFrame.getStartButton().addActionListener(e -> {
            System.out.println("Start");
            startClass.setStart(true);
            MyRun myRun = new MyRun();
            myRun.start();
        });

        myFrame.getStopButton().addActionListener(e -> {
            System.out.println("Stop");
            startClass.setStart(false);
        });

        myFrame.getReloadButton().addActionListener(e -> {
            System.out.println("reload video");
            video = new VideoCapture(videoAddress);
            detectedObjectList = new ArrayList<>();
            group = 1;
            seq = 1 ;

        });

        myFrame.getDrawMoveButton().addActionListener(e -> {
            System.out.println("print points");
//            MyRunPrint myRunPrint = new MyRunPrint(myPoints);

            MyRunPrint myRunPrint = new MyRunPrint(detectedObjectList, 1);
            myRunPrint.start();
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

        myFrame.getjSlider3().addChangeListener(e -> {
            System.out.println("Change jSlider 3 " + myFrame.getjSlider3().getValue());
                slider3 = myFrame.getjSlider3().getValue();
        });

        myFrame.getjSlider4().addChangeListener(e -> {
            System.out.println("Change jSlider 4 " + myFrame.getjSlider4().getValue());
                slider4 = myFrame.getjSlider4().getValue();
        });
    }

    private static void showframe(Mat frame, VideoCapture video) {

        /**pierwszy ekran*/

//        frame = new Mat();

        Mat secondFrame;
        video.read(frame);
        Imgproc.resize(frame, frame,new Size(710, 400));
//        Imgproc.resize(frame, frame,new Size(890, 500));
//        Imgproc.resize(frame, frame,new Size(1060, 600));
        firstScreen = frame;

        /**drugi ekran*/

        secondFrame = new Mat(frame.size(), CvType.CV_8UC1);
        //Konwertuje obraz do skali szarości
        Imgproc.cvtColor(frame, secondFrame, Imgproc.COLOR_BGR2GRAY);
        //Rozmycie filtrem Gaussa, usuwa on detale i eliminuje zakłócenia
        // src, dst, wielkośc jądra gausa, odchylenie standardowe jądra w kierunku X
        Imgproc.GaussianBlur(secondFrame, secondFrame, new Size(3, 3), 1.0, 1.0, 0);
        secondScreen = secondFrame ;

        /**trzeci ekran*/

        if(previousFrame == null){
            previousFrame = new Mat(secondFrame.size(), CvType.CV_8UC1);
        }

        if(diffFrame == null){
            diffFrame = new Mat(secondFrame.size(), CvType.CV_8UC1);
        }


        k++ ;
        if(k % 3 == 0){
            Core.absdiff(previousFrame, secondFrame, diffFrame);
            previousFrame =secondFrame.clone();
        }


        if(flow == null){
            flow = new Mat(secondFrame.size(),CvType.CV_8UC1);
        }

        Imgproc.applyColorMap(secondFrame, flow, Imgproc.COLORMAP_JET);
        Imgproc.GaussianBlur(flow, flow, new Size(5, 5) , 0, 0);


        thirdScreen = diffFrame;


        /**czwarty ekran*/

        Mat finalFrame = new Mat(secondFrame.size(), CvType.CV_8UC1);

        //new
        Imgproc.threshold(diffFrame, finalFrame, 10 , 500, Imgproc.THRESH_BINARY );

        Mat erodeClose=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(slider,slider));
        Imgproc.morphologyEx(finalFrame,finalFrame,Imgproc.MORPH_CLOSE, erodeClose);

        Mat dilateOpen=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(slider2,slider2));

        Imgproc.morphologyEx(finalFrame,finalFrame,Imgproc.MORPH_OPEN, dilateOpen);

        fourthScreen = finalFrame.clone();
        detection_contours(fourthScreen);

        putScreenInVideoLabel();
    }

    private static void putScreenInVideoLabel() {
        ImageIcon firstImage = new ImageIcon(Mat2bufferedImage(firstScreen));
        ImageIcon secondImage = new ImageIcon(Mat2bufferedImage(secondScreen));
        ImageIcon thirdImage = new ImageIcon(Mat2bufferedImage(thirdScreen));
        ImageIcon fourthImage = new ImageIcon(Mat2bufferedImage(fourthScreen));

        myFrame.getVideoLabelFirstScreen().setIcon(firstImage);
        myFrame.getVideoLabelSecondScreen().setIcon(secondImage);
        myFrame.getVideoLabelThirdScreen().setIcon(thirdImage);
        myFrame.getVideoLabelFourthScreen().setIcon(fourthImage);
        myFrame.getVideoLabelFourthScreen().repaint();
    }

    private static void printRectangle(ArrayList<Rect> array, Mat img) {
        if (array.size() > 0) {
            Iterator<Rect> it2 = array.iterator();
            while (it2.hasNext()) {
                Rect obj = it2.next();
                obj.height += 20;
                obj.width +=10;
//                Imgproc.rectangle(firstScreen, obj.br(), new Point(obj.br().x - 30, obj.br().y - 50), new Scalar(0, 255, 0), 1);
//                System.out.println(" .br() " + obj.br() + " .tl() " + obj.tl());
                Imgproc.putText(firstScreen, "obiekt " , new Point(700, 10), 2, 1, new Scalar(0, 255, 0), 1);
                if(array.indexOf(obj) -1 > 1){
//                    Imgproc.line(firstScreen,obj.br(), array.get(array.indexOf(obj) -1).br(),  new Scalar(0, 0, 255), 2);
                }
            }
        }

    }

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

        double maxArea = 50;
        int maxAreaIdx = -1;
        Rect r = null;
        ArrayList<Rect> rect_array = new ArrayList<Rect>();

        if(detectedObjectList == null){
            detectedObjectList = new ArrayList<>();
        }

        for (int idx = 0; idx < contours.size(); idx++) {
            Mat contour = contours.get(idx);

            double contourarea = Imgproc.contourArea(contour);
            if (contourarea > maxArea && contourarea < 2500) {
                maxAreaIdx = idx;
                r = Imgproc.boundingRect(contours.get(maxAreaIdx));
//                System.out.println(maxAreaIdx + " " + r + " r.tl() " +r.tl() + " r.br() " + r.br());
                myPoints.add(r);
                rect_array.add(r);

//                if()
//


                Imgproc.rectangle(firstScreen, r.br(), r.tl(), new Scalar(0, 255, 0), 1);
//                Imgproc.drawContours(firstScreen, contours, maxAreaIdx, new Scalar(0,0, 255));

                DetectedObject detectedObject = new DetectedObject(i, maxAreaIdx, r, seq);

                List<DetectedObject> collect = detectedObjectList.stream()
                        .filter(doa -> doa.getIterationId().equals(i - 1))
                        .collect(Collectors.toList());

                int numer = 20 ;
                Optional<DetectedObject> first = collect.stream()
                        .filter(doa -> doa.getRect().x <= (detectedObject.getRect().x + numer)
                                && doa.getRect().x >= (detectedObject.getRect().x - numer)
                                && doa.getRect().y <= (detectedObject.getRect().y + numer)
                                && doa.getRect().y >= (detectedObject.getRect().y - numer))
                        .findFirst();

                if(first.isPresent()){
                    detectedObject.setGroup(first.get().getGroup());
                    detectedObjectList.add(detectedObject);

                }else {
                    detectedObject.setGroup(group);
                    detectedObjectList.add(detectedObject);
                    group++;

                }
                System.out.println(maxAreaIdx + " rect " + detectedObject.getRect() + " r.tl() " +detectedObject.getRect().tl() + " r.br() "
                        + detectedObject.getRect().br() + " seq " + detectedObject.getSeq() + " group " + detectedObject.getGroup());

                seq++ ;
            }
        }
        i++ ;

        System.out.println("---------------------------");

        v.release();

        printRectangle(rect_array, outmat);
    }
}