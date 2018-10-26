package com.lgzzzz.wifi;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String WIFI_SETTING = "wifi_setting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the last Wifi information
        SharedPreferences wifiSetting = getSharedPreferences(WIFI_SETTING, 0);
        String nameText = wifiSetting.getString("name", "");
        String passwordText = wifiSetting.getString("password", "");
        String ipText = wifiSetting.getString("ip", "");
        String portText = wifiSetting.getString("port", "");

        // Then setting those
        EditText name = findViewById(R.id.wifi_name);
        name.setText(nameText);
        EditText password = findViewById(R.id.wifi_password);
        password.setText(passwordText);
        EditText ip = findViewById(R.id.wifi_ip);
        ip.setText(ipText);
        EditText port = findViewById(R.id.wifi_port);
        port.setText(portText);

    }

    public void onClick(View view) {
        // Save the wifi info to database
        SharedPreferences wifiSetting = getSharedPreferences(WIFI_SETTING, 0);
        SharedPreferences.Editor editor = wifiSetting.edit();

        // Get the input Wifi-name
        EditText name = findViewById(R.id.wifi_name);
        String nameText = name.getText().toString();

        // Get the input Wifi-password
        EditText password = findViewById(R.id.wifi_password);
        String passwordText = password.getText().toString();

        // Get the input proxy-ip
        EditText ip = findViewById(R.id.wifi_ip);
        String ipText = ip.getText().toString();

        // Get the input proxy-port
        EditText port = findViewById(R.id.wifi_port);
        String portText = ip.getText().toString();

        // Save the Wifi information
        editor.putString("name", nameText);
        editor.putString("password", passwordText);
        editor.putString("ip", ipText);
        editor.putString("port", portText);
        editor.apply();

        Toast toast = Toast.makeText(this,"Your wifi information has been saved successfully! :)",
                Toast
                .LENGTH_SHORT);
        toast.show();
    }
}
