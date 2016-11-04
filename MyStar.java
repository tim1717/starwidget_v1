package com.thefirstday.widgetsync;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyStar {
    private static final String tag = MyStar.class.getSimpleName();

    public static final int STAR_ON = android.R.drawable.btn_star_big_on;
    public static final int STAR_OFF = android.R.drawable.btn_star_big_off;

    private static MyStar instance;
    private boolean starState = false;

    private StarLightListener starLightListener;

    private MyStar(Context context) {
        Log.d(tag, "NEW MyStar << " + context.getClass().getSimpleName());

    }

    public static MyStar getInstance(Context context) {
        if (instance == null) {
            instance = new MyStar(context);
        }
        return instance;
    }

    public boolean getStarState() {
        Log.d(tag, "get << " + starState);
        return starState;
    }

    public void setStarState(boolean state) {
        Log.d(tag, "set >> " + starState + " >> " + state);
        if (starState != state) {
            starState = state;
            starLightListenerOnChange(starState);
        }
    }

    public boolean toggleStarState() {
        Log.d(tag, "toggle >> " + starState + " >> " + !starState);
        starState = !starState;
        starLightListenerOnChange(starState);
        return starState;
    }

    public static void broadcastWidgetUpdate(Context context, String castFrom) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context.getPackageName(), MyWidget.class.getName()));

        Intent intent = new Intent(context, MyWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        intent.putExtra(castFrom, castFrom);
        context.sendBroadcast(intent);
    }

    public interface StarLightListener {
        void onChange(boolean state);
    }

    public void setStarLightListener(StarLightListener listener) {
        Log.d(tag, "setting StarLightListener");
        if (starLightListener == null) {
            starLightListener = listener;
        }
    }

    public void removeStarLightListener() {
        starLightListener = null;
    }

    public void starLightListenerOnChange(boolean state) {
        if (starLightListener != null) {
            starLightListener.onChange(state);
        }
    }

}
