package com.lgzzzz.wifi;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

class Configuration {

    static WifiConfiguration config(WifiManager manager, String ssidText, String passwordText) {
        // Check wifi if is enabled
        if (manager == null)
            return null;
        if (!manager.isWifiEnabled()) {
            manager.setWifiEnabled(true);
        }

        // find if a same wifi exists
        // remove it
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + ssidText + "\"";
        conf.preSharedKey = "\"" + passwordText + "\"";

        checkExists(manager, conf);

        return conf;
    }

    private static void checkExists(WifiManager manager, WifiConfiguration conf) {
        for (WifiConfiguration c : manager.getConfiguredNetworks())
            if (conf.SSID.equals(c.SSID))
                manager.removeNetwork(c.networkId);
    }

}
