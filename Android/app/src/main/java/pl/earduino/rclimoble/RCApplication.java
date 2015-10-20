package pl.earduino.rclimoble;

import android.app.Application;
import android.util.Log;

/**
 * Created by jlesniak on 2015-01-08.
 */
public class RCApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.e("RCApplication", "Uncaught exception", ex);
            }
        });
    }
}
