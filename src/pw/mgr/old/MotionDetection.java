//package pw.mgr.old;
//
//import org.opencv.core.*;
//import org.opencv.core.Point;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.videoio.VideoCapture;
//
//import javax.imageio.ImageIO;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
////import org.opencv.highgui.Highgui;
////import org.opencv.highgui.VideoCapture;
//
//public class MoveDetection {
//    static {
//        System.loadLibrary("opencv_java310");
//    }
//
//
//    static Mat imag = null;
//    private static List<Point> myPoints = new ArrayList<>();
//
//    public static void main(String[] args) {
//
//
//        StartClass startClass = new StartClass();
//        int width = 860;
//        int height = 680;
////        int width = 1350;
////        int height = 850;
//
//        JFrame jframe = new JFrame("Detektor ruchu");
//        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        JButton startButton = new JButton("Start");
//        JButton stopButton = new JButton("Stop");
//        JButton drawMoveButton = new JButton("Narysuj ruch");
//
//        GridBagLayout gBLayout = new GridBagLayout();
//        JPanel buttonsPanel = new JPanel();
//        buttonsPanel.setLayout(gBLayout);
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.insets = new Insets(10, 25, 0, 0);
//
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        buttonsPanel.add(startButton, gbc);
//
//        gbc.gridx = 1;
//        gbc.gridy = 0;
//        buttonsPanel.add(stopButton, gbc);
//
//        gbc.gridx = 2;
//        gbc.gridy = 0;
//        buttonsPanel.add(drawMoveButton, gbc);
//
//        JPanel videoPanel = new JPanel();
//        JLabel videoLabel = new JLabel();
//        videoPanel.add(videoLabel);
//
//        JPanel generalPanel = new JPanel();
//        generalPanel.add(buttonsPanel);
//        generalPanel.add(videoPanel);
//
//        jframe.setSize(width, height);
//        jframe.setVisible(true);
//        jframe.setLocationRelativeTo(null);
//        jframe.setContentPane(generalPanel);
//
//
//        Mat frame = new Mat();
//        Mat outerBox = new Mat();
//        Mat diff_frame = null;
//        Mat tempon_frame = null;
//
////        ArrayList<Rect> array = new ArrayList<Rect>();
//        VideoCapture camera = new VideoCapture("C:\\PIOTREK\\magi\\magi_test_pw2.mp4");
//
//        Mat mask = new Mat();
//        Mat morphOutput = new Mat();
//        ArrayList<Rect> array = new ArrayList<Rect>();
//        Size sz = new Size(640, 480);
////        Size sz = new Size(1024, 768);
//
//        int i = 0;
//
//
//        class MyRun extends Thread {
//            @Override
//            public void run() {
//                System.out.println("show");
//                while (startClass.isStart()){
//                    showframe(width, height, morphOutput, array, outerBox,jframe, videoLabel, frame, diff_frame, tempon_frame, camera, sz, i);
//                }
//            }
//        }
//
//        class MyRunPrint extends Thread {
//            @Override
//            public void run() {
//                System.out.println("print");
//                BufferedImage image = new BufferedImage(1280, 960, BufferedImage.TYPE_INT_RGB);
//
//                Graphics2D cg = image.createGraphics();
//                int width = 20;
//                int height = 20;
//
//                for (Point mp: myPoints) {
//                    System.out.println(mp);
//                    cg.drawOval((int) mp.y * 2, (int) mp.x * 2, width, height);
//                }
//
//                File output = new File("result/wykryty_ruch6.jpg");
//                output.mkdirs();
//                try {
//                    ImageIO.write( image, "jpg", output);
//                    startClass.setStart(true);
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        }
//
//        startButton.addActionListener(e -> {
//            System.out.println("Start");
//            startClass.setStart(true);
//            MyRun myRun = new MyRun();
//            myRun.start();
//        });
//
//        stopButton.addActionListener(e -> {
//            System.out.println("Stop");
//            startClass.setStart(false);
//        });
//
//    drawMoveButton.addActionListener(e -> {
////        startClass.setStart(false);
////        BufferedImage image = new BufferedImage(1280, 960, BufferedImage.TYPE_INT_RGB);
////        int width = 20;
////        int height = 20;
////
////        for (Point mp: myPoints) {
////            System.out.println(mp);
////            cg.drawOval((int) mp.y, (int) mp.x, width, height);
////        }
////        generateDetectedPoint(image);
////
////        File output = new File("result/wykryty_ruch6.jpg");
////        output.mkdirs();
////                try {
////                    ImageIO.write( image, "jpg", output);
////                    startClass.setStart(true);
////                } catch (IOException e1) {
////                    e1.printStackTrace();
////                }finally {
////                }
//        System.out.println("priont point");
////        my
//        MyRunPrint myRunPrint = new MyRunPrint();
//        myRunPrint.start();
//    });
//    }
//    private static void generateDetectedPoint(BufferedImage image){
//        System.out.println("Print point");
//        Graphics2D cg = image.createGraphics();
//
//        int width = 20;
//        int height = 20;
//
//        for (Point mp: myPoints) {
//            System.out.println(mp);
//            cg.drawOval((int) mp.y, (int) mp.x, width, height);
//        }
//    }
//
//    private static void showframe(int height, int width, Mat morphOutput, ArrayList<Rect> array , Mat outerBox, JFrame jframe, JLabel vidpanel, Mat frame, Mat diff_frame, Mat tempon_frame, VideoCapture camera, Size sz, int i) {
//        camera.read(frame);
//
//            Imgproc.resize(frame, frame, sz);
//            imag = frame.clone();
//            outerBox = new Mat(frame.size(), CvType.CV_8UC1);
//            Imgproc.cvtColor(frame, outerBox, Imgproc.COLOR_BGR2GRAY);
//            Imgproc.blur(outerBox, outerBox, new Size(3, 3));
//
//            if (i == 0) {
//                jframe.setSize(height, width);
//                diff_frame = new Mat(outerBox.size(), CvType.CV_8UC1);
//                tempon_frame = new Mat(outerBox.size(), CvType.CV_8UC1);
//                diff_frame = outerBox.clone();
//            }
//
////            if (i == 1) {
//                Core.subtract(outerBox, tempon_frame, diff_frame);
////                Imgproc.adaptiveThreshold(diff_frame, diff_frame, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 2);
//
//                    Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
//                    Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
////
////                    Imgproc.erode(mask, morphOutput, erodeElement);
//////					Imgproc.erode(mask, morphOutput, erodeElement);
////
//////					Imgproc.dilate(mask, morphOutput, dilateElement);
////                    Imgproc.dilate(mask, morphOutput, dilateElement);
////
////                    Mat2bufferedImage(morphOutput);
//                    array = detection_contours(diff_frame);
//
//        printRectangle(array);
////
////            i = 1;
//
//            ImageIcon image = new ImageIcon(Mat2bufferedImage(imag));
//            vidpanel.setIcon(image);
//            vidpanel.repaint();
//            tempon_frame = outerBox.clone();
//
//    }
//
//    private static void printRectangle(ArrayList<Rect> array) {
//        if (array.size() > 0) {
//            Iterator<Rect> it2 = array.iterator();
//            while (it2.hasNext()) {
//                Rect obj = it2.next();
//                Imgproc.rectangle(imag, obj.br(), obj.tl(), new Scalar(0, 255, 0), 1);
//            }
//        }
//    }
//
//    public static BufferedImage Mat2bufferedImage(Mat frame) {
//
//        // create a temporary buffer
//        MatOfByte buffer = new MatOfByte();
//        // encode the frame in the buffer, according to the PNG format
//        Imgcodecs.imencode(".png", frame, buffer);
//        // build and return an Image created from the image encoded in the
//
//        BufferedImage img = null;
//
//        try {
//            img = ImageIO.read(new ByteArrayInputStream(buffer.toArray()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return img;
//    }
//
//
//    public static ArrayList<Rect> detection_contours(Mat outmat) {
//        Mat v = new Mat();
//        Mat vv = outmat.clone();
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Imgproc.findContours(vv, contours, v, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        double maxArea = 140;
//        int maxAreaIdx = -1;
//        Rect r = null;
//        ArrayList<Rect> rect_array = new ArrayList<Rect>();
//
//        for (int idx = 0; idx < contours.size(); idx++) {
//            Mat contour = contours.get(idx);
//
//            double contourarea = Imgproc.contourArea(contour);
//            if (contourarea > maxArea && contourarea < 250) {
//                maxAreaIdx = idx;
//                r = Imgproc.boundingRect(contours.get(maxAreaIdx));
//                myPoints.add(new Point(r.y, r.x));
//                rect_array.add(r);
////                Imgproc.rectangle(imag, r.br(), r.tl(), new Scalar(0, 255, 0), 1);
////                Imgproc.drawContours(imag, contours, maxAreaIdx, new Scalar(0,0, 255));
//
//            }
//        }
//
//        v.release();
//
//        return rect_array;
//    }
//}