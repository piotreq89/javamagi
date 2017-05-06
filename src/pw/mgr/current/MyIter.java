package pw.mgr.current;

/**
 * Created by piotrek on 2017-05-06.
 */
public class MyIter {

    private int a;
    private int b;

    public MyIter(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public MyIter() {
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyIter myIter = (MyIter) o;

        if (a != myIter.a) return false;
        return b == myIter.b;

    }

    @Override
    public int hashCode() {
        int result = a;
        result = 31 * result + b;
        return result;
    }
}
