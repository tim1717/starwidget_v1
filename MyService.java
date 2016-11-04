package com.thefirstday.widgetsync;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
    private static final String tag = "star" + MyService.class.getSimpleName();

    private final IBinder myBinder = new MyLocalBinder();

    private MyStar.StarLightListener starLightListener;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(tag, "onCreate");
        if (starLightListener == null) {
            starLightListener = new MyStar.StarLightListener() {
                @Override
                public void onChange(boolean state) {
                    Log.i(tag, "StarLight onChange " + state);
                    Toast.makeText(getApplicationContext(), "HI! " + state, Toast.LENGTH_SHORT).show();
                }
            };
            MyStar.getInstance(MyService.this).setStarLightListener(starLightListener);
        }
        Log.v(tag, "onCreate/done");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(tag, "onStartCommand");
        return START_NOT_STICKY;  // will not re-onCreate after onTaskRemoved
        //return START_STICKY;  // will re-onCreate after onTaskRemoved
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.v(tag, "onTaskRemoved");
        MyStar.broadcastWidgetUpdate(MyService.this, MyWidget.BCAST_ACT);
        this.stopSelf();
        Log.v(tag, "onTaskRemoved/done");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.v(tag, "onDestroy");
        starLightListener = null;
        Log.v(tag, "onDestroy/done");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class MyLocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
