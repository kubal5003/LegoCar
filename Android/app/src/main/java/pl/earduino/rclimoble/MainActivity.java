package pl.earduino.rclimoble;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {
    private static final  String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRAS_DEVICE_NAME = "1";
    public static final String EXTRAS_DEVICE_ADDRESS = "2";
    private String mDeviceName = "RCLIMO";
    private String mDeviceAddress;
    private boolean mConnected = false;
    private GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
        setTitle("Remote Control Lego");
        final Intent intent = getIntent();

        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

      //  final TextView textView = (TextView)findViewById(R.id.DeviceNameTextView);
       // textView.setText(mDeviceName);

        Intent bluetoothServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(bluetoothServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_BLUETOOTH_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_BLUETOOTH_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                //clearUI();
            } else if (BluetoothLeService.ACTION_BLUETOOTH_DATA_AVAILABLE.equals(action)) {
                //displayData(intent.getStringExtra(mBluetoothLeService.EXTRA_DATA));
            }
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                getmBluetoothLeService().connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                getmBluetoothLeService().disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private BluetoothLeService mBluetoothLeService;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            //Automatically connects to the device upon successful start-up initialization.
            //mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            setmBluetoothLeService(null);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (getmBluetoothLeService() != null) {
            final boolean result = getmBluetoothLeService().connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        mGLView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        setmBluetoothLeService(null);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //   mConnectionState.setText(resourceId);
            }
        });
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_BLUETOOTH_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_BLUETOOTH_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_BLUETOOTH_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_BLUETOOTH_DATA_AVAILABLE);
        return intentFilter;
    }


    public BluetoothLeService getmBluetoothLeService() {
        return mBluetoothLeService;
    }

    private void setmBluetoothLeService(BluetoothLeService mBluetoothLeService) {
        this.mBluetoothLeService = mBluetoothLeService;
    }

    private int previousValue = 0;
    public void setSteeringWheelPosition(int value){
        if (value != previousValue) {
            String v = Integer.toString(value);
            Log.i("WHEEL", v);
            sendBluetoothCommand(v);
            previousValue = value;
        }
    }

    public void driveForward()
    {
        sendBluetoothCommand("FORWARD");
    }

    public void driveBackward()
    {
        sendBluetoothCommand("BACKWARD");
    }

    public void stopEngine()
    {
        sendBluetoothCommand("STOP");
    }

    private void sendBluetoothCommand(String cmd){
        if (getmBluetoothLeService() != null) {
            getmBluetoothLeService().sendText(cmd + ";");
            Log.i("BLUETOOTH",cmd);
        }
    }
}
