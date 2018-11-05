package com.lgzzzz.wifi;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

class Connection {
    
    private WifiManager manager;
    private WifiConfiguration wifiConfiguration = new WifiConfiguration();
    
    Connection(WifiManager manager) {
        this.manager = manager;
    }
    
    
     void setWifiConfiguration(String ssidText, String passwordText) {
        // Check wifi if is enabled
        if (manager == null)
            return;
        if (!manager.isWifiEnabled())
            manager.setWifiEnabled(true);

        // find if a same wifi exists
        // remove it
        wifiConfiguration.SSID = String.format("\"%s\"", ssidText);
        wifiConfiguration.preSharedKey = String.format("\"%s\"", passwordText);

        delDuplicates(manager, wifiConfiguration);

    }

    boolean connect() {
        int netId = manager.addNetwork(wifiConfiguration);
        manager.disconnect();
        manager.enableNetwork(netId, true);
        manager.reconnect();
        return true;
    }

    private void delDuplicates(WifiManager manager, WifiConfiguration conf) {
        for (WifiConfiguration c : manager.getConfiguredNetworks())
            if (conf.SSID.equals(c.SSID))
                manager.removeNetwork(c.networkId);
    }

}
