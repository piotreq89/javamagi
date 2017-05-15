package pw.mgr.current;

import org.opencv.core.Rect;

/**
 * Created by piotrek on 2017-05-06.
 */
public class SortedObject {

    private Integer sortedId;
    private Integer sortedIdIn;
    private Rect rect;

    public SortedObject(Integer sortedId, Integer sortedIdIn, Rect rect) {
        this.sortedId = sortedId;
        this.sortedIdIn = sortedIdIn;
        this.rect = rect;
    }

    public SortedObject(Integer sortedId, Rect rect) {
        this.rect = rect;
        this.sortedId = sortedId;
    }

    public SortedObject() {
    }

    public Integer getSortedId() {
        return sortedId;
    }

    public void setSortedId(Integer sortedId) {
        this.sortedId = sortedId;
    }

    public Integer getSortedIdIn() {
        return sortedIdIn;
    }

    public void setSortedIdIn(Integer sortedIdIn) {
        this.sortedIdIn = sortedIdIn;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SortedObject that = (SortedObject) o;

        if (sortedId != null ? !sortedId.equals(that.sortedId) : that.sortedId != null) return false;
        if (sortedIdIn != null ? !sortedIdIn.equals(that.sortedIdIn) : that.sortedIdIn != null) return false;
        return rect != null ? rect.equals(that.rect) : that.rect == null;

    }

    @Override
    public int hashCode() {
        int result = sortedId != null ? sortedId.hashCode() : 0;
        result = 31 * result + (sortedIdIn != null ? sortedIdIn.hashCode() : 0);
        result = 31 * result + (rect != null ? rect.hashCode() : 0);
        return result;
    }
}
