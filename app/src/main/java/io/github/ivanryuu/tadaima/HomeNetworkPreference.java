package io.github.ivanryuu.tadaima;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ivan on 9/14/2016.
 */
public class HomeNetworkPreference extends ListPreference {

    public HomeNetworkPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        WifiManager wifiManager = (WifiManager) getContext().getSystemService(getContext().WIFI_SERVICE);


        setEntries(entries(wifiManager));
        setEntryValues(entryValues(wifiManager));
        int index = initializeIndex();
        setValueIndex(index);
        setSummary(initializeSummary(index));
        setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setSummary(getEntries()[findIndex((String)newValue)]);
                return true;
            }
        });
    }

    public HomeNetworkPreference(Context context) {
        this(context, null);
    }

    private CharSequence[] entries(WifiManager wifiManager) {

        ArrayList<String> ssidList = new ArrayList<>();
        for(ScanResult result : wifiManager.getScanResults()) {
            ssidList.add(result.SSID);
        }
        return ssidList.toArray(new String[ssidList.size()]);
    }

    private CharSequence[] entryValues(WifiManager wifiManager) {
        ArrayList<String> ssidList = new ArrayList<>();
        for(ScanResult result : wifiManager.getScanResults()) {
            ssidList.add(result.BSSID);
        }
        return ssidList.toArray(new String[ssidList.size()]);
    }

    private int initializeIndex() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String bssid = sharedPref.getString("home_network", "");
        return findIndex(bssid);
    }

    private int findIndex(String bssid) {
        int i = Arrays.asList(getEntryValues()).indexOf(bssid);
        return i != -1 ? i : 0;
    }

    private CharSequence initializeSummary(int index) {
        return getEntries()[index];
    }
}
