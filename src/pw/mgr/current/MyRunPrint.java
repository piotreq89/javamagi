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

/**
 * Created by piotrek on 2017-02-11.
 */
public class MyRunPrint extends Thread {


    private List<Point> myPoints = new ArrayList<>();

    public MyRunPrint(List<Point> myPoints ) {
        this.myPoints = myPoints ;
    }

    @Override
    public void run() {
        System.out.println("print");
        BufferedImage image = new BufferedImage(1280, 960, BufferedImage.TYPE_INT_RGB);

        Graphics2D cg = image.createGraphics();
        int width = 10;
        int height = 10;

        for (Point mp: myPoints) {
            System.out.println(mp);
            cg.drawOval((int) mp.y * 2, (int) mp.x * 2, width, height);
        }

        File output = new File("result/wykryty_ruch6.jpg");
        output.mkdirs();
        try {
            ImageIO.write( image, "jpg", output);
//            startClass.setStart(true);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }}
