package org.bkryza.jenny;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by bartek on 25/10/15.
 */
public class ConnectivityChangeReceiver
        extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //
        // Check the current network
        // If we're on home network, start alarm to notify
        // openhab about the phone presence, otherwise cancel the alarm
        //
        String wifiName = WifiHelper.getWifiName(context);
        if(wifiName == null || !wifiName.contains("stl")) {
            // we're not home anymore
            AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent2 = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent2, 0);
            alarmManager.cancel(pendingIntent);
        }
        else {
            // we're home
            AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent2 = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent2, 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+1000,60000,pendingIntent);
        }
    }

}
