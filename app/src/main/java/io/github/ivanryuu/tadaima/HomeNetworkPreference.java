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

    private final int SSID = 0;
    private final int BSSID = 1;

    public HomeNetworkPreference(final Context context, AttributeSet attrs) {
        super(context, attrs);

        WifiManager wifiManager = (WifiManager) getContext().getSystemService(getContext().WIFI_SERVICE);
        List<ScanResult> scanResults = wifiManager.getScanResults();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        final SharedPreferences.Editor editor = sharedPref.edit();

        String[] homeNetworkValue =
                sharedPref.getString("home_network", context.getString(R.string.default_home_network))
                .split(context.getString(R.string.delimiter_home_network));

        ArrayList<String> ssidList = new ArrayList<>();
        ArrayList<String> bssidList = new ArrayList<>();

        initializeLists(scanResults, ssidList, bssidList);

        int index = 0;
        if(!homeNetworkValue[BSSID].equals("bssid")) {
            index = initializeIndex(homeNetworkValue[BSSID], bssidList);

            if(index == -1) {
                ssidList.add(0, homeNetworkValue[SSID]);
                bssidList.add(0, homeNetworkValue[BSSID]);
                index = 0;
            }
            setSummary(homeNetworkValue[SSID]);
        }

        setEntries(ssidList.toArray(new String[ssidList.size()]));
        setEntryValues(bssidList.toArray(new String[bssidList.size()]));

        setValueIndex(index);

        setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object bssid) {
                int index = findIndex((String)bssid);
                String ssid = getEntries()[index].toString();
                editor.putString("home_network", ssid + context.getString(R.string.delimiter_home_network) + bssid)
                        .apply();

                setSummary(ssid);
                return true;
            }
        });
    }

    private int findIndex(String bssid) {
        int i = Arrays.asList(getEntryValues()).indexOf(bssid);
        return i != -1 ? i : 0;
    }

    private int initializeIndex(String bssid, ArrayList<String> bssidList) {
        return bssidList.indexOf(bssid);
    }

    private void initializeLists(List<ScanResult> scanResults, List<String> ssidList, List<String> bssidList) {
        for(ScanResult result : scanResults) {
            ssidList.add(result.SSID);
            bssidList.add(result.BSSID);
        }
    }
}
