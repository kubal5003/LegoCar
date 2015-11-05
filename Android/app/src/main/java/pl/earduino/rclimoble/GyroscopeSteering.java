package pl.earduino.rclimoble;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Arrays;
import java.util.EventListener;

import pl.earduino.rclimoble.Events.SteeringListener;
import pl.earduino.rclimoble.Events.SteeringPositionEvent;

public class GyroscopeSteering implements SensorEventListener{
    private final SensorManager mSensorManager;
    private final Sensor mGameRotationSensor;
    private final SteeringListener mListener;

    public GyroscopeSteering(Context context, SteeringListener listener) throws Exception {
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        mGameRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        if (mGameRotationSensor == null){
            throw  new Exception("There's no game rotation sensor.");
        }

        mListener = listener;
    }

    public void start(){
        mSensorManager.registerListener(this, mGameRotationSensor, 100000);
    }

    public void stop(){
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
       if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {

           float[] mRotationMatrix = new float[16];
           SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);

           float[] orientationValues = new float[3];
           SensorManager.getOrientation(mRotationMatrix, orientationValues);

           long rotation = Math.round(Math.toDegrees(orientationValues[1]) / 12) * -1;

           int wheelPosition = (int) rotation;

           if (wheelPosition > 0)
           {
               wheelPosition = Math.min(wheelPosition, 7);
           }
           else
           {
               wheelPosition = Math.max(wheelPosition, -7);
           }

           if (mListener != null){
               mListener.steeringPositionChange(new SteeringPositionEvent(this,wheelPosition));
           }
           //Log.i("SENSOR", "Rotation: " + Long.toString(rotation));


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

