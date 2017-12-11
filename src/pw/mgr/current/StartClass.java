package pw.mgr.current;

/**
 * Created by piotrek on 2017-01-28.
 */
public class StartClass {

    private boolean start ;
    private StartMode startMode;

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public StartMode getStartMode() {
        return startMode;
    }

    public void setStartMode(StartMode startMode) {
        this.startMode = startMode;
    }
}
