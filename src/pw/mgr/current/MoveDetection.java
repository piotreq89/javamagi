package pw.mgr.current;

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
    private static ArrayList<Rect> old_rect_array = new ArrayList<>();
    private static Mat oldFrame ;
    private static MatOfPoint2f oldMOPf;

    private static Map<Integer, MyPoint> myCustomPoints = new HashMap<>();

    private static List<MyPoint> obiects = new ArrayList<>();

    private static Mat previousFrame = null;
    private static Mat diffFrame = null;


    private static int slider = 1;
    private static int slider2 = 1;
    private static int slider3 = 1;
    private static int slider4 = 9;

    private static Mat frame = new Mat();
    private static String videoAddress = "C:\\Users\\piotrek\\Desktop\\test\\VIDEO0386.mp4";
//    private static String videoAddress = "C:\\Users\\piotrek\\Desktop\\test\\magi_new.mp4";
    private static VideoCapture video = new VideoCapture(videoAddress);

    private static Map<Integer, Rect> detectedObjectMap;

    private static List<DetectedObject> detectedObjectList;
    public static void main(String[] args) {
        StartClass startClass = new StartClass();

//        Mat frame = new Mat();

        // kalasa z funkcjami do obrobki video
//        VideoCapture video = new VideoCapture("D:\\moje\\magi\\test_film1.mp4");
//        VideoCapture video = new VideoCapture("C:\\Users\\piotrek\\Desktop\\test\\test_film1.mp4");



        Mat morphOutput = new Mat();

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

        myFrame.getjSlider1().addChangeListener(e -> {
            System.out.println("Change jSlider " + myFrame.getjSlider1().getValue());
//            if(myFrame.getjSlider1().getValue() % 2 == 1){
                slider = myFrame.getjSlider1().getValue();

//            }
        });

        myFrame.getjSlider2().addChangeListener(e -> {
            System.out.println("Change jSlider 2 " + myFrame.getjSlider2().getValue());
//            if(myFrame.getjSlider2().getValue() % 2 == 1){
                slider2 = myFrame.getjSlider2().getValue();

//            }
        });

        myFrame.getjSlider3().addChangeListener(e -> {
            System.out.println("Change jSlider 3 " + myFrame.getjSlider3().getValue());
//            if(myFrame.getjSlider3().getValue() % 2 == 1){
                slider3 = myFrame.getjSlider3().getValue();

//            }
        });

        myFrame.getjSlider4().addChangeListener(e -> {
            System.out.println("Change jSlider 4 " + myFrame.getjSlider4().getValue());
//            if(myFrame.getjSlider4().getValue() % 2 == 1){
                slider4 = myFrame.getjSlider4().getValue();

//            }
        });
    }

    private static  Integer i = 1 ;
    private static  Integer k = 0;
    private static  Integer seq = 1;
    private static  Integer group = 1;

    private static void showframe(Mat frame, VideoCapture video) {

        /**pierwszy ekran*/

//        frame = new Mat();

        Mat secondFrame;
        video.read(frame);
//        Imgproc.resize(frame, frame,new Size(710, 400));
        Imgproc.resize(frame, frame,new Size(710, 400));
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

        Mat subtractedFrame = new Mat(secondFrame.size(), CvType.CV_8UC1);
        Mat fgmask =  new Mat(secondFrame.size(), CvType.CV_8UC1);



        //BS oblicza maskę pierwszego planu, wykonując odejmowanie między bieżącą ramką a modelem tła,
        // zawierającą statyczną część sceny lub ogólnie wszystko, co można uznać za tło,
        // biorąc pod uwagę charakterystykę obserwowanej sceny

        //Modelowanie tła składa się z dwóch głównych etapów: Inicjalizacja tła; Aktualizacja w tle.
        // W pierwszym kroku obliczany jest początkowy model tła, podczas gdy w drugim etapie model
        // jest aktualizowany w celu dostosowania się do możliwych zmian w scenie.

//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
        //BackgroundSubtractor zostaną użyte do wygenerowania masek pierwszego planu.
        // Zapewnia lepszą adaptowalność do zmieniających się scen dzięki zmianom oświetlenia itp.
        BackgroundSubtractorMOG2 mog2 = Video.createBackgroundSubtractorMOG2(500, 16, true);
        //Oblicza maskę pierwszego planu
        //Następna klatka wideo, Maska pierwszoplanowa wyjściowa jako 8-bitowy obraz binarny.,
        //Każda ramka jest używana zarówno do obliczania nowej warstwy, jak i do uaktualniania tła.
//        System.out.println(
//                " \nmog2.getHistory() " + mog2.getHistory() +
//                        " \nmog2.getBackgroundRatio()  " + mog2.getBackgroundRatio() +
//                        " \nmog2.getDetectShadows() " + mog2.getDetectShadows() +
//                        " \nmog2.getVarThreshold() " + mog2.getVarThreshold() +
//                        "\n-------------------------------------------------"
//        );



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

        mog2.apply(secondFrame, fgmask);
//        Mat erode=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(8,8));
//        Mat dilate=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(8,8));
//        Mat openElem=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(slider,slider),new Point(slider2,slider2));
//        Mat closeElem=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(slider3,slider3),new Point(slider4,slider4));

//        Imgproc.morphologyEx(fgmask,fgmask,Imgproc.MORPH_OPEN,erode);
//        Imgproc.morphologyEx(fgmask,fgmask,Imgproc.MORPH_OPEN,dilate);
//        Imgproc.morphologyEx(fgmask,fgmask,Imgproc.MORPH_OPEN,openElem);
//        Imgproc.morphologyEx(fgmask,fgmask,Imgproc.MORPH_CLOSE,closeElem);

//        Imgproc.morphologyEx(fgmask, subtractedFrame, Imgproc.MORPH_OPEN, kernel);

        subtractedFrame =fgmask;

        Mat flow = new Mat(secondFrame.size(),CvType.CV_8UC1);
//        Video.calcOpticalFlowFarneback(subtractedFrame, subtractedFrame, flow ,0.5,1, 1, 1, 7, 1.5,1);
////


        thirdScreen = diffFrame;

        MatOfPoint corners  = new MatOfPoint();
        Imgproc.goodFeaturesToTrack(secondFrame, corners, 3000 , 0.5, 10);

        List<Point> points1 = corners.toList();

        points1.stream()
                .forEach(p -> {

                    Imgproc.rectangle(firstScreen, p, p , new Scalar(0, 0, 255), 2);
                });

//
        if(oldFrame == null){
            oldFrame = new Mat(secondFrame.size(),CvType.CV_8UC1);
        }

        Mat gray = secondFrame.clone();
////
        MatOfPoint2f mp2f = new MatOfPoint2f(corners.toArray());
        if(oldMOPf == null){
            oldMOPf = mp2f;
        }
            MatOfByte status = new MatOfByte();
            MatOfFloat err = new MatOfFloat();
//
            Video.calcOpticalFlowPyrLK(oldFrame,gray ,oldMOPf , mp2f,  status, err);


        oldFrame = secondFrame.clone();
        mp2f.copyTo(oldMOPf);

//        thirdScreen = gray.clone();

        /**czwarty ekran*/

        Mat finalFrame = new Mat(secondFrame.size(), CvType.CV_8UC1);

        //new
        Imgproc.threshold(diffFrame, finalFrame, 10 , 500, Imgproc.THRESH_BINARY );
//        Imgproc.adaptiveThreshold(diffFrame, finalFrame, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, slider2, slider3);

        // Adaptacyjne progi, metoda oblicza wartości progowe, usuwa szumy, czyli filtruje piksele z za małą albo za dużą wartością
        // src, dst, wartośc pixela jaka nalezy podać jeżeli wartość jest większa niż wartość progowa,
        // zmienna reprezentujaca metode adaptacyjna która ma być użyta(sa 2 możiwe)
        // - ADAPTIVE_THRESH_MEAN_C - wartość progowa jest wartością obszaru sąsiedztwa.
        // - ADAPTIVE_THRESH_GAUSSIAN_C - wartość progowa jest ważoną sumą wartości sąsiedztwa, gdzie wagi są oknami Gaussa. ,
        // wielkość pikseli sąsiedztwa używana do obliczania wartości progowej. ,
        // stałą używaną w obu metodach (odejmowana od średniej lub ważonej średniej).

        //old
//        Imgproc.adaptiveThreshold(subtractedFrame, finalFrame, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 5, 2);



//        Mat erodeClose=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(1,1));
//        Mat dilateClose=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(9,9));
        Mat erodeClose=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(slider,slider));
//        Mat dilateClose=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(slider2,slider2));

//        Imgproc.morphologyEx(finalFrame,finalFrame,Imgproc.MORPH_CLOSE, dilateClose);
        Imgproc.morphologyEx(finalFrame,finalFrame,Imgproc.MORPH_CLOSE, erodeClose);

//        Mat erodeOpen=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(slider3,slider3));
        Mat dilateOpen=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(slider4,slider4));

//        Imgproc.morphologyEx(finalFrame,finalFrame,Imgproc.MORPH_OPEN, erodeOpen);
        Imgproc.morphologyEx(finalFrame,finalFrame,Imgproc.MORPH_OPEN, dilateOpen);


//        Imgproc.erode(finalFrame, finalFrame, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(slider3, slider3)));
//
//        Imgproc.dilate(finalFrame, finalFrame, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(slider4, slider4)));

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
//        array.stream().forEach(a -> System.out.println(a));
//        Imgproc.matchShapes()
//        System.out.println("------------------------");
        if (array.size() > 0) {
            Iterator<Rect> it2 = array.iterator();
//            int i = 0 ;
            while (it2.hasNext()) {
//                i++;
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

//        Imgproc.matchShapes(old_rect_array, array, 1, 1);

        old_rect_array = array;
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


//        System.out.println(contours);
//        Imgproc.

        double maxArea = 50;
        int maxAreaIdx = -1;
        Rect r = null;
        ArrayList<Rect> rect_array = new ArrayList<Rect>();

//        int i = 1 ;

        if(detectedObjectList == null){
            detectedObjectList = new ArrayList<>();
        }

        if(detectedObjectMap == null){
            detectedObjectMap = new HashMap<>();
        }

        for (int idx = 0; idx < contours.size(); idx++) {
            Mat contour = contours.get(idx);
//            System.out.println("contour " + contour);

            double contourarea = Imgproc.contourArea(contour);
            if (contourarea > maxArea && contourarea < 2500) {
//                if (true) {
                maxAreaIdx = idx;
                r = Imgproc.boundingRect(contours.get(maxAreaIdx));
//                System.out.println(maxAreaIdx + " " + r + " r.tl() " +r.tl() + " r.br() " + r.br());
                myPoints.add(r);
                rect_array.add(r);
                Imgproc.rectangle(firstScreen, r.br(), r.tl(), new Scalar(0, 255, 0), 1);
//                Imgproc.drawContours(firstScreen, contours, maxAreaIdx, new Scalar(0,0, 255));



                MyIter myIter = new MyIter(i, maxAreaIdx);

                DetectedObject detectedObject = new DetectedObject(i, maxAreaIdx, r, seq);
//                detectedObjectList.sget(i -1)

                List<DetectedObject> collect = detectedObjectList.stream()
                        .filter(doa -> doa.getIterationId().equals(i - 1))
                        .collect(Collectors.toList());

                int numer = 20 ;
                Optional<DetectedObject> first = collect.stream()
//                        .filter(doa -> (doa.getRect().br().x <= ((int) detectedObject.getRect().br().x + numer)
//                                && doa.getRect().br().x >= ((int) detectedObject.getRect().br().x - numer)
//                                && doa.getRect().br().y <= ((int) detectedObject.getRect().br().y + numer)
//                                && doa.getRect().br().y >= ((int) detectedObject.getRect().br().y - numer))
//
//                                && (doa.getRect().tl().x <= ((int) detectedObject.getRect().tl().x + numer)
//                                && doa.getRect().tl().x >= ((int) detectedObject.getRect().tl().x - numer)
//                                && doa.getRect().tl().y <= ((int) detectedObject.getRect().tl().y + numer)
//                                && doa.getRect().tl().y >= ((int) detectedObject.getRect().tl().y - numer)))
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
//                detectedObject.put(myIter, r );
                System.out.println(maxAreaIdx + " rect " + detectedObject.getRect() + " r.tl() " +detectedObject.getRect().tl() + " r.br() "
                        + detectedObject.getRect().br() + " seq " + detectedObject.getSeq() + " group " + detectedObject.getGroup());

                seq++ ;


            }


        }
        i++ ;
        System.out.println("---------------------------");

        v.release();

        printRectangle(rect_array, outmat);

//     old_rect_array = rect_array;

//        return rect_array;
    }
}