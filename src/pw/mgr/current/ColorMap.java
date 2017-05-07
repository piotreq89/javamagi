package pw.mgr.current;

import java.awt.*;

/**
 * Created by piotrek on 2017-05-07.
 */
public class ColorMap {

    private Integer id ;
    private Color color;

    public ColorMap(Integer id, Color color) {
        this.id = id;
        this.color = color;
    }

    public ColorMap() {
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
