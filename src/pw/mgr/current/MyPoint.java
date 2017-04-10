package pw.mgr.current;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrek on 2017-04-08.
 */
public class MyPoint{

    private Integer id ;
    private List<Point> points;
    private Point point;


    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void addToPoints(Point point){
        if(points == null){
            points = new ArrayList<>();
        }
        points.add(point);
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
