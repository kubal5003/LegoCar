package pl.earduino.rclimoble.Events;


public class SteeringPositionEvent extends java.util.EventObject {

    public SteeringPositionEvent(Object source, int position) {
        super(source);
        this.setPosition(position);
    }

    private int Position;

    public int getPosition() {
        return Position;
    }

    private void setPosition(int position) {
        Position = position;
    }
}
