package com.thefirstday.widgetsync;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private static final String tag = "star" + MainActivity.class.getSimpleName();

    private ServiceConnection mServiceConnection;

    private ImageView activityImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(tag, "onCreate");
        setContentView(R.layout.activity_main);

        activityImage = (ImageView) findViewById(R.id.activity_image);

        activityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean newState = MyStar.getInstance(MainActivity.this).toggleStarState();
                wishForStar(newState);
            }
        });
        Log.v(tag, "onCreate/done");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(tag, "onResume");
        startService(false);

        boolean resumedState = MyStar.getInstance(MainActivity.this).getStarState();
        wishForStar(resumedState);
        Log.v(tag, "onResume/done");
    }

    @Override
    protected void onPause() {
        Log.v(tag, "onPause");
        startService(true);
        Log.v(tag, "onPause/done");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.v(tag, "onDestroy");
        MyStar.getInstance(MainActivity.this).setStarState(false);
        Log.v(tag, "onDestroy/done");
        super.onDestroy();
    }

    private void setImage(boolean state) {
        int imageId;
        if (state) {
            imageId = MyStar.STAR_ON;
        } else {
            imageId =  MyStar.STAR_OFF;
        }

        activityImage.setBackgroundResource(imageId);
    }

    private void wishForStar(boolean state) {
        setImage(state);
        MyStar.broadcastWidgetUpdate(MainActivity.this, MyWidget.BCAST_ACT);
    }

    private void startService(boolean start) {
        Intent serviceIntent = new Intent(MainActivity.this, MyService.class);

        if (start) {
            startService(serviceIntent);

            if (mServiceConnection == null) {
                mServiceConnection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        MyService.MyLocalBinder myBinder = (MyService.MyLocalBinder) iBinder;
                        //myService = myBinder.getService();
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {
                        //myService = null;
                    }
                };
            }

            bindService(serviceIntent, mServiceConnection, 0);
        } else {
            stopService(serviceIntent);

            if (mServiceConnection != null) {
                unbindService(mServiceConnection);
                mServiceConnection = null;
            }
        }
    }
}
