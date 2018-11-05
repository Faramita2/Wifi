package com.lgzzzz.wifi;

import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

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
            sleep(15000);
            URL url = new URL(CHECK_IP);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            ip = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();

        }

        return ip;
    }

    protected void onPostExecute(String result) {
        TextView connectLog = mainActivityWeakReference.get().findViewById(R.id
                .log_text);


        String info;
        Date currentTime = Calendar.getInstance().getTime();
        if (result != null)
            info = String.format("- %s\n\tconnect successfully, now ip: " +
                    "%s.\n\n", currentTime, result);

        else
            info = String.format("- %s\n\tconnect failed.\n", currentTime);
        Toast.makeText(mainActivityWeakReference.get(), result, Toast
                .LENGTH_SHORT).show();
        connectLog.append(info);
    }
}



