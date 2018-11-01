package com.lgzzzz.wifi;

import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String WIFI_SETTING = "wifi_setting";
    private EditText ssid;
    private EditText password;
    private EditText ip;
    private EditText port;
    private WifiManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        // display last
        displayLastConfigs();

    }

    private void displayLastConfigs() {
        // Get the last Wifi setting
        Map configs = getSharedPreferences(WIFI_SETTING, 0).getAll();
        String ssidText = (String) configs.get("ssid");
        String passwordText = (String) configs.get("password");
        String ipText = (String) configs.get("ip");
        String portText = (String) configs.get("port");

        // Then setting those
        ssid = findViewById(R.id.wifi_name);
        ssid.setText(ssidText);
        password = findViewById(R.id.wifi_password);
        password.setText(passwordText);
        ip = findViewById(R.id.wifi_ip);
        ip.setText(ipText);
        port = findViewById(R.id.wifi_port);
        port.setText(portText);

    }

    // set proxy
    public void onClickSetProxy(View view) {
        setWifiProxySettings();
    }

    // unset proxy
    public void onClickUnsetProxy(View view) {
        unsetWifiProxySettings();
    }

    // save info
    public void onClickSave(View view) {

        // Get the input configs
        String ssidText = ssid.getText().toString();
        String passwordText = password.getText().toString();
        String ipText = ip.getText().toString();
        String portText = port.getText().toString();

        // Save the Wifi information
        SharedPreferences.Editor editor = getSharedPreferences(WIFI_SETTING, 0).edit();

        editor.putString("ssid", ssidText);
        editor.putString("password", passwordText);
        editor.putString("ip", ipText);
        editor.putString("port", portText);
        editor.apply();

        // show save successfully info
        Toast.makeText(this,
                "Your wifi information has been saved successfully! :)",
                Toast.LENGTH_SHORT).show();

    }

    // connect wifi
    public void onClickConnect(View view) {
        // Check wifi if is enabled
        if (manager == null)
            return ;
        if (!manager.isWifiEnabled()) {
            manager.setWifiEnabled(true);
        }

        String ssidText = ssid.getText().toString();
        String passwordText = password.getText().toString();


        connectToWifi(ssidText, passwordText);
        Toast.makeText(this, "Connect successfully.", Toast.LENGTH_SHORT).show();

    }

    private void connectToWifi(String ssid, String password) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + ssid + "\"";
        conf.preSharedKey = "\"" + password + "\"";

        // find if a same wifi exists
        // and update it or add it
        int netId = isExists(conf) ? manager.updateNetwork(conf) : manager.addNetwork(conf);

        manager.disconnect();
        manager.enableNetwork(netId, true);
        manager.reconnect();
    }

    private boolean isExists(WifiConfiguration c) {
        for (WifiConfiguration conf : manager.getConfiguredNetworks())
        {
            if (conf.SSID.equals(c.SSID))
                return true;
        }
        return false;
    }

    // Reflection
    private static Object getField(Object obj, String name)
        throws SecurityException, NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException{
            Field f = obj.getClass().getField(name);
        return f.get(obj);
    }

    private static Object getDeclaredField(Object obj, String name)
        throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(obj);
    }

    private static void setEnumField(Object obj, String value, String name)
        throws SecurityException, NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException{
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }

    private static void setProxySettings(String assign, WifiConfiguration wifiConfiguration)
        throws SecurityException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException{
        setEnumField(wifiConfiguration, assign, "proxySettings");
    }

    private WifiConfiguration getCurrentWifiConfiguration(WifiManager manager) {
        // Check wifi if is Enabled, if not, return
        if (!manager.isWifiEnabled()) {
            return null;
        }
        // get all wifi configurations
        List<WifiConfiguration> configurationList = manager.getConfiguredNetworks();
        WifiConfiguration configuration = null;
        // get current networkId
        int cur = manager.getConnectionInfo().getNetworkId();
        // iterate all wifi network
        // if current networkId in these
        // return it
        for (int i = 0; i < configurationList.size(); ++i) {
            WifiConfiguration wifiConfiguration = configurationList.get(i);
            if (wifiConfiguration.networkId == cur) {
                configuration = wifiConfiguration;
            }
        }
        return configuration;
    }

    private void setWifiProxySettings()
    {
        // get the current wifi configuration
        WifiConfiguration config = getCurrentWifiConfiguration(manager);
        if (null == config)
            return;
        try
        {
            // get the link properties from the wifi configuration
            Object linkProperties = getField(config, "linkProperties");
            if (null == linkProperties)
                return;
            // get the setHttpProxy method for LinkProperties
            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            Class[] setHttpProxyParams = new Class[1];
            setHttpProxyParams[0] = proxyPropertiesClass;
            Class lpClass = Class.forName("android.net.LinkProperties");
            Method setHttpProxy = lpClass.getDeclaredMethod("setHttpProxy", setHttpProxyParams);
            setHttpProxy.setAccessible(true);

            // get ProxyProperties constructor
            Class[] proxyPropertiesConstructorParamTypes = new Class[3];
            proxyPropertiesConstructorParamTypes[0] = String.class;
            proxyPropertiesConstructorParamTypes[1] = int.class;
            proxyPropertiesConstructorParamTypes[2] = String.class;

            Constructor proxyPropertiesConstructor = proxyPropertiesClass.getConstructor(proxyPropertiesConstructorParamTypes);

            // create the parameters for the constructor
            Object[] proxyPropertiesConstructorParams = new Object[3];

            // TODO:input params
            String ipText = ip.getText().toString();
            String portText = port.getText().toString();
            proxyPropertiesConstructorParams[0] = ipText;
            proxyPropertiesConstructorParams[1] = Integer.parseInt(portText);
            proxyPropertiesConstructorParams[2] = null;

            // create a new object using the params
            Object proxySettings = proxyPropertiesConstructor.newInstance(proxyPropertiesConstructorParams);

            // pass the new object to setHttpProxy
            Object[] params = new Object[1];
            params[0] = proxySettings;
            setHttpProxy.invoke(linkProperties, params);

            setProxySettings("STATIC", config);

            // save the settings
            manager.updateNetwork(config);
            manager.disconnect();
            manager.reconnect();

            Toast.makeText(this, "Set proxy successfully.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Set proxy failed.", Toast.LENGTH_SHORT).show();
        }

    }

    private void unsetWifiProxySettings()
    {
        WifiConfiguration config = getCurrentWifiConfiguration(manager);
        if (null == config)
            return;

        try
        {

            // get the link properties from the wifi configuration
            Object linkProperties = getField(config, "linkProperties");
            if (null == linkProperties)
                return;

            // get the setHttpProxy method for LinkProperties
            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            Class[] setHttpProxyParams = new Class[1];
            setHttpProxyParams[0] = proxyPropertiesClass;
            Class lpClass = Class.forName("android.net.LinkProperties");
            Method setHttpProxy = lpClass.getDeclaredMethod("setHttpProxy", setHttpProxyParams);
            setHttpProxy.setAccessible(true);

            // pass null as the proxy
            Object[] params = new Object[1];
            params[0] = null;
            setHttpProxy.invoke(linkProperties, params);

            setProxySettings("NONE", config);

            // save the config
            manager.updateNetwork(config);
            manager.disconnect();
            manager.reconnect();

            Toast.makeText(this, "Unset proxy successfully.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Unset proxy failed.", Toast.LENGTH_SHORT).show();
        }

    }

}
