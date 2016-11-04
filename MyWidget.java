package com.thefirstday.widgetsync;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

public class MyWidget extends AppWidgetProvider {
    private static final String tag = "star" + MyWidget.class.getSimpleName();

    public static final String BCAST_ACT = "CASTACT";
    public static final String BCAST_WGT = "CASTWGT";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(tag, "onReceive");
        if (intent != null) {
            String castFromAct = intent.getStringExtra(BCAST_ACT);
            String castFromWgt = intent.getStringExtra(BCAST_WGT);
            Log.d(tag, "castFrom: " + castFromAct + " " + castFromWgt);

            // toggled from widget, otherwise do nothing, let image state refresh
            if (!TextUtils.isEmpty(castFromWgt) && castFromWgt.equals(BCAST_WGT)) {
                MyStar.getInstance(context).toggleStarState();
            }
        }
        Log.v(tag, "onReceive/done");
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(tag, "onUpdate");
        // may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, appWidgetIds);
        }
        Log.v(tag, "onUpdate/done");
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_widget);

        setImage(MyStar.getInstance(context).getStarState(), remoteViews);

        Intent intent = new Intent(context, MyWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        intent.putExtra(MyWidget.BCAST_WGT, MyWidget.BCAST_WGT);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_image, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    private void setImage(boolean state, RemoteViews remoteViews) {
        int imageId;
        if (state) {
            imageId = MyStar.STAR_ON;
        } else {
            imageId =  MyStar.STAR_OFF;
        }

        remoteViews.setImageViewResource(R.id.widget_image, imageId);
    }
}

