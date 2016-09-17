package io.github.ivanryuu.tadaima;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by Ivan on 9/13/2016.
 */
public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (info != null && info.isConnected()) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            Log.d("WifiReceiver", wifiInfo.getBSSID());
            sendSmsIfHomeNetwork(context, wifiInfo.getBSSID());
        }
    }

    private void sendSmsIfHomeNetwork(Context context, String BSSID) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String[] homeNetworkValue = sharedPref.getString("home_network", context.getString(R.string.default_home_network))
                        .split(context.getString(R.string.delimiter_home_network));

        if(BSSID.equals(homeNetworkValue[1])) {
            String phoneNumber = sharedPref.getString("phone_number", "");
            String textMessage = sharedPref.getString("text_message", "I'm home!");

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, textMessage, null, null);
            sendNotifiedNotification(context, sharedPref);
        }
    }

    private void sendNotifiedNotification(Context context, SharedPreferences preferences) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_info_black_24dp)
                .setContentTitle("Tadaima!")
                .setContentText("User notified!");
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0,mBuilder.build());
    }
}
