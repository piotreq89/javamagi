package pw.mgr.current;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MoveDetection {

    static {
        System.loadLibrary("opencv_java310");
    }

    private static int i = 0 ;
    private static  int k = 0 ;
    private static Mat previousFrame = null;


    private static List<Point> myPoints = new ArrayList<>();
    private static MyFrame myFrame = new MyFrame();
    private static Mat firstScreen = null;

    private static Mat secondScreen = null;
    private static Mat thirdScreen = null;

    private static Mat fourthScreen = null;

    public static void main(String[] args) {
        StartClass startClass = new StartClass();

        Mat frame = new Mat();

        // kalasa z funkcjami do obrobki video
        VideoCapture video = new VideoCapture("D:\\moje\\magi\\test_film1.mp4");


        Mat morphOutput = new Mat();

        class MyRun extends Thread {
            @Override
            public void run() {
                System.out.println("show");
                while (startClass.isStart()){
                    showframe(previousFrame, morphOutput, frame, video);
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

    myFrame.getDrawMoveButton().addActionListener(e -> {
        System.out.println("print points");
        MyRunPrint myRunPrint = new MyRunPrint(myPoints);
        myRunPrint.start();
        startClass.setStart(true);
    });
    }


    private static void showframe(Mat previousFrame,  Mat morphOutput, Mat frame, VideoCapture video) {

        Mat tempon_frame ;
        Mat diff_frame;
        Mat secondFrame;

        video.read(frame);
        Imgproc.resize(frame, frame,new Size(710, 400));
        firstScreen = frame;

        secondFrame = new Mat(frame.size(), CvType.CV_8UC1);
        //Konwertuje obraz do skali szarości
        Imgproc.cvtColor(frame, secondFrame, Imgproc.COLOR_BGR2GRAY);
        //Rozmycie filtrem Gaussa, usuwa on detale i eliminuje zakłócenia
        // src, dst, wielkośc jądra gausa, odchylenie standardowe jądra w kierunku X
        Imgproc.GaussianBlur(secondFrame, secondFrame, new Size(3, 3), 0);
        secondScreen = secondFrame ;

        if(MoveDetection.i == 0){
            previousFrame = new Mat(frame.size(), CvType.CV_8UC1);
            MoveDetection.i = 1 ;
        }

        diff_frame = new Mat(secondFrame.size(), CvType.CV_8UC1);

        // Oblicza różnicę między dwiema macieżami
        Core.subtract(previousFrame, secondFrame, diff_frame);

        //Oblicza różnicę bezwzględną między poszczególnymi elementami tablicy
//        Core.absdiff(previousFrame, secondFrame, diff_frame);

//        Imgproc.Laplacian(previousFrame, previousFrame, previousFrame.depth());
//        thirdScreen = diff_frame.clone();

        Mat finalFrame = diff_frame.clone();

//        Imgproc.adaptiveThreshold(diff_frame, diff_frame, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 2);
        // Adaptacyjne progi, metoda oblicza wartości progowe, usuwa szumy, czyli filtruje piksele z za małą albo za dużą wartością
        // src, dst, wartośc pixela jaka nalezy podać jeżeli wartość jest większa niż wartość progowa,
        // zmienna reprezentujaca metode adaptacyjna która ma być użyta(sa 2 możiwe)
        // - ADAPTIVE_THRESH_MEAN_C - wartość progowa jest wartością obszaru sąsiedztwa.
        // - ADAPTIVE_THRESH_GAUSSIAN_C - wartość progowa jest ważoną sumą wartości sąsiedztwa, gdzie wagi są oknami Gaussa. ,
        // wielkość pikseli sąsiedztwa używana do obliczania wartości progowej. ,
        // stałą używaną w obu metodach (odejmowana od średniej lub ważonej średniej).
        Imgproc.adaptiveThreshold(diff_frame, finalFrame, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 5, 2);

        thirdScreen = finalFrame.clone();

        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
        Imgproc.erode(finalFrame, finalFrame, erodeElement);

        Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
        Imgproc.dilate(finalFrame, finalFrame, dilateElement);
        Imgproc.dilate(finalFrame, finalFrame, dilateElement);
        Imgproc.dilate(finalFrame, finalFrame, dilateElement);
        Imgproc.dilate(finalFrame, finalFrame, dilateElement);


//
        fourthScreen = finalFrame.clone();


//
//        Scalar minValues = new Scalar(0, 0, 0);
//        Scalar maxValues = new Scalar(180, 255, 148);

//        Imgproc.dilate(diff_frame, morphOutput, dilateElement);

        // bierze tylko obiekty z tego zakresu kolorow
//        Core.inRange(diff_frame, minValues, maxValues, mask);
//        Imgproc.erode(diff_frame, morphOutput, erodeElement);
////					Imgproc.erode(mask, morphOutput, erodeElement);
//
////					Imgproc.dilate(mask, morphOutput, dilateElement);
//                    Imgproc.dilate(mask, morphOutput, dilateElement);
//
//                    Mat2bufferedImage(morphOutput);



        detection_contours(fourthScreen);

        MoveDetection.k++ ;

//        if(k % 10 == 0 || i == 0){
            MoveDetection.previousFrame = secondFrame.clone();

//        }

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
                Imgproc.rectangle(firstScreen, obj.br(), obj.tl(), new Scalar(0, 255, 0), 1);
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
        Imgproc.findContours(vv, contours, v, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE, new Point(0, 0));

        double maxArea = 50;
        int maxAreaIdx = -1;
        Rect r = null;
        ArrayList<Rect> rect_array = new ArrayList<Rect>();

        for (int idx = 0; idx < contours.size(); idx++) {
            Mat contour = contours.get(idx);

            double contourarea = Imgproc.contourArea(contour);
            if (contourarea > maxArea && contourarea < 2500) {
//                if (true) {
                maxAreaIdx = idx;
                r = Imgproc.boundingRect(contours.get(maxAreaIdx));
                myPoints.add(new Point(r.y, r.x));
                rect_array.add(r);
//                Imgproc.rectangle(fourthScreen, r.br(), r.tl(), new Scalar(0, 255, 0), 1);
//                Imgproc.drawContours(fourthScreen, contours, maxAreaIdx, new Scalar(0,0, 255));

            }
        }

        v.release();

        printRectangle(rect_array, outmat);

//        return rect_array;
    }
}