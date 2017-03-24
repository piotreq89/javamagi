package pw.mgr.old;

/**
 * Created by piotrek on 2017-03-01.
 */
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class JavaCVPrjt01 {
    static {
        System.loadLibrary("opencv_java310");
    }

    static Mat imag = null;

    public static void main(String[] args) {
        JFrame jframe = new JFrame("HUMAN MOTION DETECTOR FPS");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel vidpanel = new JLabel();
        jframe.setContentPane(vidpanel);
        jframe.setSize(840, 680);
        jframe.setVisible(true);

        Mat frame = new Mat();
        Mat outerBox = new Mat();
        Mat diff_frame = null;
        Mat finall_frame = null;
        Mat tempon_frame = null;
        ArrayList<Rect> array = new ArrayList<Rect>();
        VideoCapture camera = new VideoCapture(
                "D:\\moje\\magi\\test_film1.mp4");
        Size sz = new Size(840, 680);
        int i = 0;

        int k = 0;
        while (true) {

            k ++ ;
//            System.out.println("k out " + k);
            if (camera.read(frame)) {
                Imgproc.resize(frame, frame, sz);
                imag = frame.clone();
                outerBox = new Mat(frame.size(), CvType.CV_8UC1);
                Imgproc.cvtColor(frame, outerBox, Imgproc.COLOR_BGR2GRAY);
                Imgproc.GaussianBlur(outerBox, outerBox, new Size(3, 3), 0);

                if (i == 0) {
                    jframe.setSize(frame.width(), frame.height());
                    diff_frame = new Mat(outerBox.size(), CvType.CV_8UC1);
                    tempon_frame = new Mat(outerBox.size(), CvType.CV_8UC1);

                    finall_frame = diff_frame.clone();
                    diff_frame = outerBox.clone();
                }

                if (i == 1) {
//                    if(k % 8 == 0) {
//                        System.out.println("k in " + k);
                        Core.absdiff(outerBox, tempon_frame, diff_frame);

                        Imgproc.adaptiveThreshold(diff_frame, finall_frame, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
//                    Imgproc.threshold(diff_frame, finall_frame, 1, 255,  Imgproc.THRESH_BINARY_INV);
                        array = detection_contours(finall_frame);
//                        if (array.size() > 0) {
//
//                            Iterator<Rect> it2 = array.iterator();
//                            while (it2.hasNext()) {
//                                Rect obj = it2.next();
//                                Imgproc.rectangle(imag, obj.br(), obj.tl(),
//                                        new Scalar(0, 255, 0), 1);
//                            }
//
//                        }
//                    }
                }

                i = 1;

//                if(k % 8 == 0) {
                    ImageIcon beforeImage = new ImageIcon(Mat2bufferedImage(finall_frame));

//                ImageIcon image = new ImageIcon(Mat2bufferedImage(imag));
//                vidpanel.setIcon(image);
                    vidpanel.setIcon(beforeImage);
                    vidpanel.repaint();
//                }
//                if(k % 3 == 0) {
                    tempon_frame = outerBox.clone();
//                }
            }
        }
    }

    public static BufferedImage Mat2bufferedImage(Mat image) {
        MatOfByte bytemat = new MatOfByte();
        Imgcodecs.imencode(".png", image, bytemat);
//        Highgui.imencode(".jpg", image, bytemat);
        byte[] bytes = bytemat.toArray();
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage img = null;
        try {
            img = ImageIO.read(in);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return img;
    }

    public static ArrayList<Rect> detection_contours(Mat outmat) {
        Mat v = new Mat();
        Mat vv = outmat.clone();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(vv, contours, v, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        double maxArea = 100;
        int maxAreaIdx = -1;
        Rect r = null;
        ArrayList<Rect> rect_array = new ArrayList<Rect>();

        for (int idx = 0; idx < contours.size(); idx++) { Mat contour = contours.get(idx); double contourarea = Imgproc.contourArea(contour); if (contourarea > maxArea) {
            // maxArea = contourarea;
            maxAreaIdx = idx;
            r = Imgproc.boundingRect(contours.get(maxAreaIdx));
            rect_array.add(r);
            Imgproc.drawContours(imag, contours, maxAreaIdx, new Scalar(0,0, 255));
        }

        }

        v.release();

        return rect_array;

    }
}