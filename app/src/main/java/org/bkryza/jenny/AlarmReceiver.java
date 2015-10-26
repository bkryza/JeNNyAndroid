package org.bkryza.jenny;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by bartek on 25/10/15.
 */
public class AlarmReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        //
        // Check if we are on the home wifi
        //
        String wifiName = getWifiName(context);
        if(wifiName==null || !wifiName.contains("stl")) {
            Toast.makeText(context, "We ain't home :-(", Toast.LENGTH_SHORT).show();
            //super.onReceive(context, intent);
        }

        //
        // Prepare a retrofit request to server
        //
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.99:8080")
                .addConverterFactory(new ToStringConverterFactory())
                .build();

        OpenHabItemService service = retrofit.create(OpenHabItemService.class);

        Call call = null;

        //
        // Change the status of PresenceBartek switch in openhab
        //
        call = service.updateStatus("PresenceBartek", "ON");

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

    }

    public String getWifiName(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.getSSID();
                }
            }
        }
        return null;
    }

}
