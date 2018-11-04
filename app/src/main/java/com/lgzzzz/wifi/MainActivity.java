package com.lgzzzz.wifi;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final String WIFI_SETTINGS = "WIFI SETTINGS";

    private WifiManager manager;

    // Show connection information
    private TextView connectLog;

    // configuration
    private Map configs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        configs = getSharedPreferences(WIFI_SETTINGS, 0).getAll();

        // set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    // Connect wifi
    public void onClickConnect(View view) {
        String ssidText = (String) configs.get("ssid");
        String passwordText = (String) configs.get("password");

        // get config
        WifiConfiguration conf = Configuration.config(manager, ssidText, passwordText);

        int netId = manager.addNetwork(conf);
        manager.disconnect();
        manager.enableNetwork(netId, true);
        manager.reconnect();

        // show connect success
        if (manager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
            Toast.makeText(this, R.string.connect_successfully, Toast.LENGTH_SHORT).show();

        new ShowCurrentIpTask(this).execute();

    }

    // Set proxy
    public void onClickSet(View view) {
        ProxyManager proxyManager = new ProxyManager(manager);
        String ipText = (String) configs.get("ip");
        String portText = (String) configs.get("port");
        proxyManager.setWifiProxySettings(ipText, portText);
        new ShowCurrentIpTask(this).execute();
    }

    // Edit configuration
    public void onClickEdit(View view) {
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }


}
