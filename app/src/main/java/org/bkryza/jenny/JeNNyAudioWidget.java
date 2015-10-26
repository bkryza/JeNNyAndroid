package org.bkryza.jenny;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by bartek on 26/10/15.
 */
public class JeNNyAudioWidget extends AppWidgetProvider {

    private static final String EXTRA_ITEM = "EXTRAARG";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //
        // There may be multiple widgets active, so update all of them
        //
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    // Called when the BroadcastReceiver receives an Intent broadcast.
    // Checks to see whether the intent's action is TOAST_ACTION. If it is, the app widget
    // displays a Toast message for the current item.
    @Override
    public void onReceive(final Context context, Intent intent) {

        String wifiName = WifiHelper.getWifiName(context);
        if(wifiName==null || !wifiName.contains("stl")) {
            Toast.makeText(context, "We ain't home :-(", Toast.LENGTH_SHORT).show();
            super.onReceive(context, intent);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.99:8080")
                .addConverterFactory(new ToStringConverterFactory())
                .build();

        OpenHabItemService service = retrofit.create(OpenHabItemService.class);

        Call call = null;
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);

        if (intent.getAction().equals("ROTEL_ON")) {
            call = service.updateStatus("Rotel_Power", "ON");
        }
        else if (intent.getAction().equals("ROTEL_OFF")) {
            call = service.updateStatus("Rotel_Power", "OFF");
        }
        else if (intent.getAction().equals("RADIO_ON")) {
            call = service.updateStatus("MPlayerSwitch", "ON");
        }
        else if (intent.getAction().equals("RADIO_OFF")) {
            call = service.updateStatus("MPlayerSwitch", "OFF");
        }
        else if (intent.getAction().equals("MUSIC_ON")) {
            call = service.updateStatus("MopidySwitch", "ON");
        }
        else if (intent.getAction().equals("MUSIC_OFF")) {
            call = service.updateStatus("MopidySwitch", "OFF");
        }
        else if (intent.getAction().equals("VOLUME_UP")) {
            call = service.updateStatus("RotelVolume", "UP");
        }
        else if (intent.getAction().equals("VOLUME_DOWN")) {
            call = service.updateStatus("RotelVolume", "DOWN");
        }

        if(call != null) {
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Response response, Retrofit retrofit) {
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(context, "Openhab request failed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        super.onReceive(context, intent);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //
        // check if this is the right widget
        //
        CharSequence widgetText = context.getString(R.string.appwidget_audio_text);

        //
        // Update Rotel status and button handler
        //
        RemoteViews remoteViews;
        ComponentName watchWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.je_nny_audio_widget);

        watchWidget = new ComponentName(context, JeNNyAudioWidget.class);
        remoteViews.setOnClickPendingIntent(R.id.rotel_on, getPendingSelfIntent(context, "ROTEL_ON"));
        remoteViews.setOnClickPendingIntent(R.id.rotel_off, getPendingSelfIntent(context, "ROTEL_OFF"));
        remoteViews.setOnClickPendingIntent(R.id.radio_on, getPendingSelfIntent(context, "RADIO_ON"));
        remoteViews.setOnClickPendingIntent(R.id.radio_off, getPendingSelfIntent(context, "RADIO_OFF"));
        remoteViews.setOnClickPendingIntent(R.id.music_on, getPendingSelfIntent(context, "MUSIC_ON"));
        remoteViews.setOnClickPendingIntent(R.id.music_off, getPendingSelfIntent(context, "MUSIC_OFF"));
        remoteViews.setOnClickPendingIntent(R.id.volume_up, getPendingSelfIntent(context, "VOLUME_UP"));
        remoteViews.setOnClickPendingIntent(R.id.volume_down, getPendingSelfIntent(context, "VOLUME_DOWN"));


        appWidgetManager.updateAppWidget(watchWidget, remoteViews);

    }

    protected static PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, JeNNyAudioWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

}


