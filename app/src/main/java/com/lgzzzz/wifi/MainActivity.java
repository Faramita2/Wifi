package com.lgzzzz.wifi;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private static final String WIFI_SETTINGS = "WIFI SETTINGS";
    private static final String CHECK_IP = "http://checkip.amazonaws.com";

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


    // connect wifi
    public void onClickConnect(View view) {
        // Check wifi if is enabled
        if (manager == null)
            return;
        if (!manager.isWifiEnabled()) {
            manager.setWifiEnabled(true);
        }

        connectWithoutProxy();
        new ShowCurrentIpTask().execute();

    }

    public void onClickSet(View view) {
        reconnectWithProxy();
        new ShowCurrentIpTask().execute();
    }


    private void reconnectWithProxy() {
        ProxyManager proxyManager = new ProxyManager(manager);
        String ipText = (String) configs.get("ip");
        String portText = (String) configs.get("port");
        proxyManager.setWifiProxySettings(ipText, portText);
    }

    private void connectWithoutProxy() {

        String ssidText = (String) configs.get("ssid");
        String passwordText = (String) configs.get("password");

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + ssidText + "\"";
        conf.preSharedKey = "\"" + passwordText + "\"";

        // find if a same wifi exists
        // remove it

        isExists(conf);

        int netId = manager.addNetwork(conf);
        manager.disconnect();
        manager.enableNetwork(netId, true);
        manager.reconnect();

        // show connect success
        if (manager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
            Toast.makeText(this, R.string.connect_successfully, Toast.LENGTH_SHORT).show();

    }


    private void isExists(WifiConfiguration conf) {
        for (WifiConfiguration c : manager.getConfiguredNetworks())
            if (conf.SSID.equals(c.SSID))
                manager.removeNetwork(c.networkId);

    }


    public void onClickEdit(View view) {
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }


    private class ShowCurrentIpTask extends AsyncTask<Void, Integer, String> {
        protected String doInBackground(Void... voids) {
            String ip = null;
                try {
                    sleep(10000);
                    URL url = new URL(CHECK_IP);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    ip = reader.readLine();
                } catch (Exception e) {
                    e.printStackTrace();

                }

            return ip;
        }



        protected void onPostExecute(String result) {
            Toast.makeText(MainActivity.this, "Now ip: " + result, Toast.LENGTH_SHORT).show();;
        }
    }

}
