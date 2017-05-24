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
    private static int slider3 = 20;

    private static int slider4 = 500;
    private static  Integer i = 1 ;
    private static  Integer k = 0;

    private static  Integer seq = 1;
    private static  Integer group = 1;

//    private static String videoAddress = "C:\\Users\\piotrek\\Desktop\\test\\VIDEO0376.mp4";
//    private static String videoAddress = "C:\\Users\\piotrek\\Desktop\\test\\magi_new.mp4";
//    private static String videoAddress = "C:\\Users\\piotrek\\Desktop\\test\\magi_new9.mp4";
//    private static String videoAddress = "C:\\Users\\piotrek\\Desktop\\test\\test_film1.mp4";
//    private static String videoAddress = "C:\\Users\\piotrek\\Desktop\\test\\magi_new2.mp4";
//      private static String videoAddress = "E:\\magi\\film\\YDXJ0571.MP4";
//    private static String videoAddress = "C:\\Users\\piotrek\\Desktop\\test\\magi_new3.mp4";

    private static String videoAddress = "D:\\moje\\magi\\magi_new6.mp4";



    private static VideoCapture video = new VideoCapture(videoAddress);
    private static List<DetectedObject> detectedObjectList;

    private static List<DetectedObject> detectedObjectListOld  = new ArrayList<>();

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
        k++ ;
        Mat secondFrame;
        video.read(frame);

        if(k % 4 == 0) {

//        Imgproc.resize(frame, frame,new Size(640, 480));
//        Imgproc.resize(frame, frame,new Size(1024, 768));
//        Imgproc.resize(frame, frame,new Size(890, 500));
            Imgproc.resize(frame, frame, new Size(800, 600));
//        Imgproc.resize(frame, frame,new Size(1260, 800));
//        Imgproc.resize(frame, frame,new Size(1600, 1200));
//        Imgproc.resize(frame, frame,new Size(1860, 1400));
            firstScreen = frame;

            /**drugi ekran*/

            secondFrame = new Mat(frame.size(), CvType.CV_8UC1);
            //Konwertuje obraz do skali szarości
            Imgproc.cvtColor(frame, secondFrame, Imgproc.COLOR_BGR2GRAY);
            //Rozmycie filtrem Gaussa, usuwa on detale i eliminuje zakłócenia
            // src, dst, wielkośc jądra gausa, odchylenie standardowe jądra w kierunku X
            Imgproc.GaussianBlur(secondFrame, secondFrame, new Size(3, 3), 1.0, 1.0, 0);
            secondScreen = secondFrame;


            HOGDescriptor hogDescriptor = new HOGDescriptor();

            hogDescriptor.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());


//            double ratio = 0.5;
            int width = 800;
            int height = 600 ;
//            int w = (int) (width * ratio);
//            int h = (int) (height * ratio);

            Mat m3 = new Mat(height, width, CvType.CV_8UC1);

//            MatOfRect found = new MatOfRect();
//            MatOfDouble weight = new MatOfDouble();
//
//            hogDescriptor.detectMultiScale(m3, found, weight, 0, new Size(1, 1), new Size(1, 1), 1.05, 2, false);
//
//            List<Rect> rects = found.toList();
//            if (rects.size()> 0) {
//                for (int i=0; i<rects.size(); i++) {
////                    rect(rects[i].x/ratio, rects[i].y/ratio, rects[i].width/ratio, rects[i].height/ratio);
//                    Imgproc.rectangle(secondScreen, new Point(rects.get(i).x, rects.get(i).y),
//                            new Point(rects.get(i).width, rects.get(i).height), new Scalar(0, 255, 0), 1);
//                }
//            }
//            text("Frame Rate: " + round(frameRate), 500, 50);

            /**trzeci ekran*/

            if (previousFrame == null) {
                previousFrame = new Mat(secondFrame.size(), CvType.CV_8UC1);
            }

            if (diffFrame == null) {
                diffFrame = new Mat(secondFrame.size(), CvType.CV_8UC1);
            }


            if (k % 4 == 0) {
                Core.absdiff(previousFrame, secondFrame, diffFrame);
//                Core.subtract(previousFrame, secondFrame, diffFrame);
                previousFrame = secondFrame.clone();
            }


            if (flow == null) {
                flow = new Mat(secondFrame.size(), CvType.CV_8UC1);
            }

            Imgproc.applyColorMap(secondFrame, flow, Imgproc.COLORMAP_JET);
            Imgproc.GaussianBlur(flow, flow, new Size(5, 5), 0, 0);


            thirdScreen = diffFrame;


            /**czwarty ekran*/

            Mat finalFrame = new Mat(secondFrame.size(), CvType.CV_8UC1);

            //new
            Imgproc.threshold(diffFrame, finalFrame, slider3, slider4, Imgproc.THRESH_BINARY);

            Mat erodeClose = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(slider, slider));
            Imgproc.morphologyEx(finalFrame, finalFrame, Imgproc.MORPH_CLOSE, erodeClose);

            Mat dilateOpen = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(slider2, slider2));

            Imgproc.morphologyEx(finalFrame, finalFrame, Imgproc.MORPH_OPEN, dilateOpen);

            fourthScreen = finalFrame.clone();
//        if(k % 4 == 0) {
            detection_contours(fourthScreen);
//        }

            putScreenInVideoLabel();
        }
    }

    private static void putScreenInVideoLabel() {
        ImageIcon firstImage = new ImageIcon(Mat2bufferedImage(firstScreen));
//        System.out.println(" secondScreen.size() " + secondScreen.size());
//        Imgproc.resize(secondScreen, secondScreen,new Size(800, 768));
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
//                Imgproc.putText(firstScreen, "obiekt " , new Point(700, 10), 2, 1, new Scalar(0, 255, 0), 1);
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

                r.width= 20 ;
                r.height= 40 ;

//                double[] ints = {20, 40};
//                r.set(ints);
//                r.tl();
                 r = new Rect((int)r.tl().x, (int)r.tl().y, 20 , 40);


                myPoints.add(r);
                rect_array.add(r);

                if (r.br().y > 60) {
                    if(r.br().y < 480 && r.br().x > 80 && r.tl().y <= 770 && r.tl().y >= 30 &&
                            r.area() < 3000){

                        DetectedObject detectedObject = new DetectedObject(i, maxAreaIdx, r, seq);

                        List<DetectedObject> collect = detectedObjectList.stream()
                                .filter(doa -> doa.getIterationId().equals(i - 1))
                                .collect(Collectors.toList());

                        int numer = 40;
                        Optional<DetectedObject> first = collect.stream()
                                .filter(doa -> {
                                            double doaTlX = detectedObject.getRect().tl().x;
                                    double doaTlY = detectedObject.getRect().tl().y;
                                    double doaBrX = detectedObject.getRect().br().x;
                                    double doaBrY = detectedObject.getRect().br().y;
                                    double dTlX = doa.getRect().tl().x;
                                    double dBrX = doa.getRect().br().x;
                                    int width = doa.getRect().width;
                                    int doaWidth = detectedObject.getRect().width;
                                    return (dTlX <= (doaTlX + numer)
                                                    && dTlX >= (doaTlX - numer)
                                                    && doa.getRect().tl().y <= (doaTlY + numer)
                                                    && doa.getRect().tl().y >= (doaTlY - numer)
                                                    && (dBrX <= (doaBrX + numer)
                                                    && dBrX >= (doaBrX - numer)
                                                    && doa.getRect().br().y <= (doaBrY + numer)
                                                    && doa.getRect().br().y >= (doaBrY - numer))
                                            )
                                                    && (doaTlX + doaWidth <= (dTlX + width + numer)
                                                    && doaTlX + doaWidth >= (dTlX + width - numer)
                                                    && doaTlY <= (doa.getRect().tl().y + numer)
                                                    && doaTlY >= (doa.getRect().tl().y - numer)
                                                    && (doaBrX - doaWidth <= (dBrX - width + numer)
                                                    && doaBrX - doaWidth >= (dBrX - width - numer)
                                                    && doaBrY <= (doa.getRect().br().y + numer)
                                                    && doaBrY >= (doa.getRect().br().y - numer)));
                                        }

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

                        System.out.println(maxAreaIdx + " rect " + detectedObject.getRect() + " r.tl() " + detectedObject.getRect().tl() + " r.br() "
                                + detectedObject.getRect().br() + " seq " + detectedObject.getSeq() + " group " + detectedObject.getGroup());
                        seq++;
                    }

                }
            }
        }

        int numer = 40 ;

        detectedObjectListOld.stream()
                .filter(co -> co.getRect().br().y < 440 && co.getRect().br().x > 105 && co.getRect().tl().y <= 740 && co.getRect().tl().y >= 40)
                .filter(co -> co.getRect().area() < 2500)
                .forEach( co -> {
                    List<DetectedObject> collect2 = detectedObjectList.stream()
                            .filter(dol -> dol.getIterationId().equals(i)).collect(Collectors.toList());
                    Optional<DetectedObject> first1 = collect2.stream()
                            .filter(doa -> {
                                        double doaTlX = doa.getRect().tl().x;
                                        double doaTlY = doa.getRect().tl().y;
                                        double doaBrX = doa.getRect().br().x;
                                        double doaBrY = doa.getRect().br().y;
                                        int width = doa.getRect().width;
                                double coBrX = co.getRect().br().x;
                                double coTlX = co.getRect().tl().x;
                                int coWidth = co.getRect().width;

                                return (coTlX <= (doaTlX + numer)
                                                && coTlX >= (doaTlX - numer)
                                                && co.getRect().tl().y <= (doaTlY + numer)
                                                && co.getRect().tl().y >= (doaTlY - numer)
                                                && (coBrX <= (doaBrX + numer)
                                                && coBrX >= (doaBrX - numer)
                                                && co.getRect().br().y <= (doaBrY + numer)
                                                && co.getRect().br().y >= (doaBrY - numer)))

                                                && (coTlX + coWidth <= (doaTlX + width + numer)
                                                && coTlX + coWidth >= (doaTlX + width - numer)
                                                && co.getRect().tl().y <= (doaTlY + numer)
                                                && co.getRect().tl().y >= (doaTlY - numer)
                                                && (coBrX - coWidth <= (doaBrX - width + numer)
                                                && coBrX - coWidth >= (doaBrX - width - numer)
                                                && co.getRect().br().y <= (doaBrY + numer)
                                                && co.getRect().br().y >= (doaBrY - numer)));
                                    }
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
//                    Imgproc.putText(firstScreen, "br x " + d.getRect().br().x + " y " + d.getRect().br().y + " area " + d.getRect().area() ,
//                            d.getRect().br(), 1, 1, new Scalar(255, 255, 0), 2);

                    Imgproc.putText(firstScreen, "br",
                            d.getRect().br(), 1, 1, new Scalar(255, 255, 0), 2);

                    Imgproc.putText(firstScreen, "tl" ,
                            d.getRect().tl(), 1, 1, new Scalar(255, 255, 0), 2);

                    Imgproc.putText(firstScreen, "1" ,
                            new Point(d.getRect().tl().x + d.getRect().width, d.getRect().tl().y) , 1, 1, new Scalar(255, 255, 0), 2);

                    Imgproc.putText(firstScreen, "2" ,
                            new Point(d.getRect().br().x - d.getRect().width, d.getRect().br().y), 1, 1, new Scalar(255, 255, 0), 2);
                });


        i++ ;

            System.out.println("---------------------------");

            v.release();

            printRectangle(rect_array, outmat);
//                }

        Point pt1 = new Point(80, 30);
        Point pt2 = new Point(750, 450);
        Imgproc.rectangle(firstScreen, pt1,
                pt2, new Scalar(0, 255, 255), 1);

        Imgproc.putText(firstScreen, "pt1 x " + pt1.x + " pt1 y " + pt1.y , pt1 , 1, 1, new Scalar(255, 255, 255), 1);
        Imgproc.putText(firstScreen, "pt2 x " + pt2.x + " pt2 y " + pt2.y , new Point(pt2.x - 50, pt2.y) , 1, 1, new Scalar(255, 255, 255), 1);


    }
}