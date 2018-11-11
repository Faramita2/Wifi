package com.lgzzzz.wifi;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final String WIFI_SETTINGS = "SETTINGS";

    private WifiManager manager;

    // Show connection information
    private TextView connectLog;

    // configuration
    private Map configs;

    // Connect Listener
    private View.OnClickListener connectClickListener = v -> connectButtonClicked();

    // Set Listener
    private View.OnClickListener setClickListener = v -> setButtonClicked();

    // Edit Listener
    private View.OnClickListener editClickListener = v -> editButtonClicked();

    // Edit
    private void editButtonClicked() {
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
        // waste resource
//        this.finish();
    }

    // Set proxy operation
    private void setButtonClicked() {

        ProxyManager proxyManager = new ProxyManager(manager);
        String ipText = (String) configs.get("ip");
        String portText = (String) configs.get("port");

        proxyManager.setWifiProxySettings(ipText, portText);
        new ShowCurrentIpTask(this).execute();

    }

    // Connect wifi operation
    private void connectButtonClicked() {
        String ssidText = (String) configs.get("ssid");
        String passwordText = (String) configs.get("password");

        // connect wifi
        Connection conn = new Connection(manager);
        conn.setWifiConfiguration(ssidText, passwordText);
        conn.connect();


        // check if connected
//        ConnectivityManager cm =
//                (ConnectivityManager) getSystemService(Context
//                        .CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        boolean isConnected = activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting();
//        if (isConnected)
//            connectLog.setText("connect successfully.");
        new ShowCurrentIpTask(this).execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ready
        loadConf();
        if (savedInstanceState != null) {
            String connectLogText = savedInstanceState.getString("connect_log");
            connectLog.setText(connectLogText);
        }

    }

    private void loadConf() {
        manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        configs = getSharedPreferences(WIFI_SETTINGS, 0).getAll();

        // set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button connectBtn = findViewById(R.id.connect);
        connectBtn.setOnClickListener(connectClickListener);

        Button setBtn = findViewById(R.id.set);
        setBtn.setOnClickListener(setClickListener);

        FloatingActionButton editBtn = findViewById(R.id.edit);
        editBtn.setOnClickListener(editClickListener);

        connectLog = findViewById(R.id.log_text);
        connectLog.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String connectLogText = connectLog.getText().toString();
        outState.putString("connect_log", connectLogText);
    }
}
