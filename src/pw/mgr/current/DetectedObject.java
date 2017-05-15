package pw.mgr.current;

import org.opencv.core.Rect;

import java.awt.*;

/**
 * Created by piotrek on 2017-05-06.
 */
public class DetectedObject {

    private Integer iterationId;
    private Integer contourId;
    private Rect rect;
    private Integer seq ;
    private Integer group;
    private ColorMap colorMap ;
    private Color color ;

    public DetectedObject(Integer iterationId, Integer contourId, Rect rect, Integer seq, Integer group) {
        this.iterationId = iterationId;
        this.contourId = contourId;
        this.rect = rect;
        this.seq = seq;
        this.group = group;
    }

    public DetectedObject(Integer iterationId, Integer contourId, Rect rect, Integer seq) {
        this.iterationId = iterationId;
        this.contourId = contourId;
        this.rect = rect;
        this.seq = seq;
    }

    public DetectedObject() {
    }

    public Integer getIterationId() {
        return iterationId;
    }

    public void setIterationId(Integer iterationId) {
        this.iterationId = iterationId;
    }

    public Integer getContourId() {
        return contourId;
    }

    public void setContourId(Integer contourId) {
        this.contourId = contourId;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public ColorMap getColorMap() {
        return colorMap;
    }

    public void setColorMap(ColorMap colorMap) {
        this.colorMap = colorMap;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DetectedObject that = (DetectedObject) o;

        if (iterationId != null ? !iterationId.equals(that.iterationId) : that.iterationId != null) return false;
        if (contourId != null ? !contourId.equals(that.contourId) : that.contourId != null) return false;
        if (rect != null ? !rect.equals(that.rect) : that.rect != null) return false;
        if (seq != null ? !seq.equals(that.seq) : that.seq != null) return false;
        return group != null ? group.equals(that.group) : that.group == null;

    }

    @Override
    public int hashCode() {
        int result = iterationId != null ? iterationId.hashCode() : 0;
        result = 31 * result + (contourId != null ? contourId.hashCode() : 0);
        result = 31 * result + (rect != null ? rect.hashCode() : 0);
        result = 31 * result + (seq != null ? seq.hashCode() : 0);
        result = 31 * result + (group != null ? group.hashCode() : 0);
        return result;
    }
}
