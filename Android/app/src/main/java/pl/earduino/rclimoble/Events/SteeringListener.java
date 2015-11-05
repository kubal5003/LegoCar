package pl.earduino.rclimoble.Events;

import java.util.EventListener;

import pl.earduino.rclimoble.Events.SteeringPositionEvent;

public interface SteeringListener extends EventListener {
    void steeringPositionChange(SteeringPositionEvent event);
}
