package pl.earduino.rclimoble;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.earduino.rclimoble.Shapes.GLText;
import pl.earduino.rclimoble.Shapes.Pads;
import pl.earduino.rclimoble.Shapes.SteeringWheel;

public class MyGLRenderer implements GLSurfaceView.Renderer {


    private final Context context;
    private final Pads mPads;
    private SteeringWheel mSteeringWheel;
    private float mAngle;
    private boolean mAutoReturn = true;
    private GLText glText;
    private MainActivity activity;

    public MyGLRenderer(Context context){
        this.context = context;
        activity = (MainActivity)context;
        mSteeringWheel = new SteeringWheel();
        mPads = new Pads();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);

        mSteeringWheel.loadGLTexture(gl, context);
        mPads.loadGLTexture(gl,context);

        gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
        gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
        gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
        gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
        gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do

        //Really Nice Perspective Calculations
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        // Create the GLText
        glText = new GLText( gl, context.getAssets() );

        // Load the font from file (set size + padding), creates the texture
        // NOTE: after a successful call to this the font is ready for rendering!
        glText.load( "Roboto-Regular.ttf", 14, 2, 2 );  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        // Draw background color
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);


        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();   // reset the matrix to its default state

        GLU.gluLookAt(gl, 0, 0, -4, 0f, 0f, 0f, 0f, 1.0f, 0.0f);


        //processSteeringWheelAutoReturn();

        gl.glTranslatef(1.5f, 0.0f, 0.0f);
        gl.glRotatef(mAngle, 0.0f, 0.0f, 1.0f);
        mSteeringWheel.draw(gl);

        gl.glLoadIdentity();
        GLU.gluLookAt(gl, 0, 0, -4, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        gl.glTranslatef(-1.5f, 0.0f, 0.0f);
        mPads.draw(gl);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Adjust the viewport based on geometry changes
        // such as screen rotations
        gl.glViewport(0, 0, width, height);

        // make adjustments for screen ratio
        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);        // set matrix to projection mode
        gl.glLoadIdentity();                        // reset the matrix to its default state
        gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);  // apply the projection matrix
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    public void setAutoReturn(boolean autoReturn){
        mAutoReturn = autoReturn;
    }

    private void processSteeringWheelAutoReturn(){
        if (mAutoReturn){
            final float step = 1.4f;
            final float margin = 0.2f;
            if (mAngle > step + margin){
                mAngle -= step;
            }
            else if (mAngle < -step -margin){
                mAngle += step;
            } else if(mAngle >= -margin && mAngle <= margin){
                mAngle = 0;
            }
        }


        int wheelPosition = (int)Math.round(mAngle / 11);
        if (wheelPosition > 0)
        {
            wheelPosition = Math.min(wheelPosition, 7);
        }
        else
        {
            wheelPosition = Math.max(wheelPosition, -7);
        }

        activity.setSteeringWheelPosition(wheelPosition);

        //Log.i("WHEEL","mAngle: " + Float.toString(mAngle) + " position: " + wheelPosition);
    }
}
