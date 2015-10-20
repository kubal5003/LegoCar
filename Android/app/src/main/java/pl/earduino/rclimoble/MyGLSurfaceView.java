package pl.earduino.rclimoble;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    public static final String TOUCH = "TOUCH";
    private final MyGLRenderer mRenderer;
    private MainActivity mainActivity;

    public MyGLSurfaceView(Context context) {
        super(context);
        mRenderer = new MyGLRenderer(context);
        mainActivity = (MainActivity)context;
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private float mPreviousX;
    private float mPreviousY;
    private int steeringWheelPointerId = 0;
    private boolean isSteeringWheelPointerDown = false;

    @Override
    public boolean onTouchEvent(MotionEvent e) {

            int i = e.getActionIndex();
            float x = normalizeX(e.getX(i));
            float y = normalizeY(e.getY(i));

            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    //USER TOUCHED THE LEFT SIDE OF THE SCREEN - input for steering wheel
                    if (x < 0.0f && !isSteeringWheelPointerDown){
                        //REMEMBER THE TOUCH ID FOR USE WITH MOVE
                        isSteeringWheelPointerDown = true;
                        steeringWheelPointerId = e.getPointerId(i);
                        mRenderer.setAutoReturn(false);
                        mPreviousX = x;
                        mPreviousY = y;
                        Log.i(TOUCH,"Starting TOUCH (Steering Wheel)");
                    }
                    if(x> 0.0f && x < 0.5f ) {
                        Log.i(TOUCH, "Starting TOUCH (Reverse)");
                        mainActivity.driveBackward();
                        Log.i(TOUCH,"X:" + x);
                    }
                    else if(x > 0.5f){
                        Log.i(TOUCH, "Starting TOUCH (Forward)");
                        mainActivity.driveForward();
                        Log.i(TOUCH,"X:" + x);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    //STEERING WHEEL TOUCH ENDED
                    if (isSteeringWheelPointerDown && e.getPointerId(i) == steeringWheelPointerId){
                        isSteeringWheelPointerDown = false;
                        steeringWheelPointerId = 0;
                        mRenderer.setAutoReturn(true);
                        Log.i(TOUCH,"Finishing TOUCH (Steering Wheel)");
                    }
                    else {
                        Log.i(TOUCH, "Finishing TOUCH (Break)");
                        mainActivity.stopEngine();
                        Log.i(TOUCH,"X:" + x);
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    //WE SHOULD PROCESS THE MOVE ONLY IF WE'RE STEERING THE WHEEL
                    if (isSteeringWheelPointerDown && e.getPointerId(i) == steeringWheelPointerId) {

                        float dx = x - mPreviousX;
                        float dy = y - mPreviousY;

                        if (y > 0.0f) {
                            dx = dx * -1;
                        }

                        if (x > -0.5f) {
                            dy = dy * -1;
                        }

                        mRenderer.setAngle(mRenderer.getAngle() + ((dx + dy) * 100.0f));
                        mPreviousX = x;
                        mPreviousY = y;
                        requestRender();
                    }
                    break;
            }
        return true;
    }

    private float normalizeX(float rawX){
        return (rawX/getWidth())*2.0f - 1.0f;
    }

    private float normalizeY(float rawY){
        return  (rawY/getHeight())*-2.0f + 1.0f;
    }

}
