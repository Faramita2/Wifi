package com.lgzzzz.wifi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

public class EditActivity extends AppCompatActivity {

    // SETTING
    private static final String WIFI_SETTINGS = "WIFI SETTINGS";

    // TextViews
    private EditText ssid;
    private EditText password;
    private EditText ip;
    private EditText port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Set the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display last Wifi configuration
        displayLastConfiguration();
    }

    private void displayLastConfiguration() {
        // Get the last Wifi configuration
        Map configs = getSharedPreferences(WIFI_SETTINGS, 0).getAll();
        String ssidText = (String) configs.get("ssid");
        String passwordText = (String) configs.get("password");
        String ipText = (String) configs.get("ip");
        String portText = (String) configs.get("port");

        // Set text
        ssid = findViewById(R.id.wifi_name);
        ssid.setText(ssidText);
        password = findViewById(R.id.wifi_password);
        password.setText(passwordText);
        ip = findViewById(R.id.wifi_ip);
        ip.setText(ipText);
        port = findViewById(R.id.wifi_port);
        port.setText(portText);

    }

    private void saveConfiguration() {
        // Save the Wifi configuration
        SharedPreferences.Editor editor = getSharedPreferences(WIFI_SETTINGS, 0).edit();

        editor.putString("ssid", ssid.getText().toString());
        editor.putString("password", password.getText().toString());
        editor.putString("ip", ip.getText().toString());
        editor.putString("port", port.getText().toString());
        editor.apply();

        // Show success
        Toast.makeText(this, "Save configuration successfully!", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_config:
                // After click save button
                // need check the input information then back to MainActivity
                // TODO: check info and save
                saveConfiguration();
                super.onBackPressed();
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }
}
