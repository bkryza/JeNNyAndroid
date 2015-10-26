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
 * Implementation of App Widget functionality.
 */
public class JeNNyWidget extends AppWidgetProvider {

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

        if (intent.getAction().equals("LIGHTBOX_ON")) {
            call = service.updateStatus("Light_Living_Lightbox", "ON");
        }
        else if (intent.getAction().equals("LIGHTBOX_OFF")) {
            call = service.updateStatus("Light_Living_Lightbox", "OFF");
        }
        else if (intent.getAction().equals("SLEEP_ON")) {
            call = service.updateStatus("SleepTimer", "ON");
        }
        else if (intent.getAction().equals("SLEEP_OFF")) {
            call = service.updateStatus("SleepTimer", "OFF");
        }
        else if(intent.getAction().startsWith("BLIND_")) {
            String action = intent.getAction();
            String[] action_tokens = action.split("_");

            if(action_tokens.length == 3) {

                String blindId = action_tokens[2];
                String blindCommand = action_tokens[1];

                call = service.updateStatus("Roller_"+blindId, blindCommand);
            }
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

        CharSequence widgetText = context.getString(R.string.appwidget_text);

        //
        // Update Rotel status and button handler
        //
        RemoteViews remoteViews;
        ComponentName watchWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.je_nny_widget);

        watchWidget = new ComponentName(context, JeNNyWidget.class);
        remoteViews.setOnClickPendingIntent(R.id.lightbox_on, getPendingSelfIntent(context, "LIGHTBOX_ON"));
        remoteViews.setOnClickPendingIntent(R.id.lightbox_off, getPendingSelfIntent(context, "LIGHTBOX_OFF"));
        remoteViews.setOnClickPendingIntent(R.id.sleep_on, getPendingSelfIntent(context, "SLEEP_ON"));
        remoteViews.setOnClickPendingIntent(R.id.sleep_off, getPendingSelfIntent(context, "SLEEP_OFF"));

        remoteViews.setOnClickPendingIntent(R.id.blind_down_a, getPendingSelfIntent(context, "BLIND_DOWN_A"));
        remoteViews.setOnClickPendingIntent(R.id.blind_down_b, getPendingSelfIntent(context, "BLIND_DOWN_B"));
        remoteViews.setOnClickPendingIntent(R.id.blind_down_c, getPendingSelfIntent(context, "BLIND_DOWN_C"));
        remoteViews.setOnClickPendingIntent(R.id.blind_down_d, getPendingSelfIntent(context, "BLIND_DOWN_D"));
        remoteViews.setOnClickPendingIntent(R.id.blind_down_e, getPendingSelfIntent(context, "BLIND_DOWN_E"));
        remoteViews.setOnClickPendingIntent(R.id.blind_down_f, getPendingSelfIntent(context, "BLIND_DOWN_F"));
        remoteViews.setOnClickPendingIntent(R.id.blind_down_g, getPendingSelfIntent(context, "BLIND_DOWN_G"));
        remoteViews.setOnClickPendingIntent(R.id.blind_down_z, getPendingSelfIntent(context, "BLIND_DOWN_Z"));

        remoteViews.setOnClickPendingIntent(R.id.blind_up_a, getPendingSelfIntent(context, "BLIND_UP_A"));
        remoteViews.setOnClickPendingIntent(R.id.blind_up_b, getPendingSelfIntent(context, "BLIND_UP_B"));
        remoteViews.setOnClickPendingIntent(R.id.blind_up_c, getPendingSelfIntent(context, "BLIND_UP_C"));
        remoteViews.setOnClickPendingIntent(R.id.blind_up_d, getPendingSelfIntent(context, "BLIND_UP_D"));
        remoteViews.setOnClickPendingIntent(R.id.blind_up_e, getPendingSelfIntent(context, "BLIND_UP_E"));
        remoteViews.setOnClickPendingIntent(R.id.blind_up_f, getPendingSelfIntent(context, "BLIND_UP_F"));
        remoteViews.setOnClickPendingIntent(R.id.blind_up_g, getPendingSelfIntent(context, "BLIND_UP_G"));
        remoteViews.setOnClickPendingIntent(R.id.blind_up_z, getPendingSelfIntent(context, "BLIND_UP_Z"));

        remoteViews.setOnClickPendingIntent(R.id.blind_stop_a, getPendingSelfIntent(context, "BLIND_STOP_A"));
        remoteViews.setOnClickPendingIntent(R.id.blind_stop_b, getPendingSelfIntent(context, "BLIND_STOP_B"));
        remoteViews.setOnClickPendingIntent(R.id.blind_stop_c, getPendingSelfIntent(context, "BLIND_STOP_C"));
        remoteViews.setOnClickPendingIntent(R.id.blind_stop_d, getPendingSelfIntent(context, "BLIND_STOP_D"));
        remoteViews.setOnClickPendingIntent(R.id.blind_stop_e, getPendingSelfIntent(context, "BLIND_STOP_E"));
        remoteViews.setOnClickPendingIntent(R.id.blind_stop_f, getPendingSelfIntent(context, "BLIND_STOP_F"));
        remoteViews.setOnClickPendingIntent(R.id.blind_stop_g, getPendingSelfIntent(context, "BLIND_STOP_G"));
        remoteViews.setOnClickPendingIntent(R.id.blind_stop_z, getPendingSelfIntent(context, "BLIND_STOP_Z"));

        appWidgetManager.updateAppWidget(watchWidget, remoteViews);

    }

    protected static PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, JeNNyWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

}

