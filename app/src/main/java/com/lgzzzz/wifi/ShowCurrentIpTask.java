package com.lgzzzz.wifi;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;

import static java.lang.Thread.sleep;

// Check ip
public  class ShowCurrentIpTask extends AsyncTask<Void, Integer, String> {
    private static final String CHECK_IP = "http://checkip.amazonaws.com";
    private final WeakReference<MainActivity> mainActivityWeakReference;

    ShowCurrentIpTask(MainActivity activity) {
        mainActivityWeakReference = new WeakReference<>(activity);
    }
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
        Toast.makeText(mainActivityWeakReference.get(), "Now ip: " + result, Toast.LENGTH_SHORT)
                .show();
    }
}



