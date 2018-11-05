package com.lgzzzz.wifi;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class ProxyManager {

    private WifiManager wifiManager;

    ProxyManager(WifiManager m) {
        this.wifiManager = m;
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
        if (!manager.isWifiEnabled())
            return null;

        // get all wifi configurations
        List<WifiConfiguration> configurationList = manager.getConfiguredNetworks();
        WifiConfiguration configuration = null;
        // get current networkId
        int cur = manager.getConnectionInfo().getNetworkId();
        // iterate all wifi network
        // if current networkId in these
        // return it
        for (WifiConfiguration c : configurationList) {
            if (c.networkId == cur)
                configuration = c;
        }
        return configuration;
    }

    public void setWifiProxySettings(String ipText, String portText)
    {
        // get the current wifi configuration
        WifiConfiguration config = getCurrentWifiConfiguration(wifiManager);
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
            wifiManager.updateNetwork(config);
            wifiManager.disconnect();
            wifiManager.reconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void unsetWifiProxySettings()
    {
        WifiConfiguration config = getCurrentWifiConfiguration(wifiManager);
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
            wifiManager.updateNetwork(config);
            wifiManager.disconnect();
            wifiManager.reconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
