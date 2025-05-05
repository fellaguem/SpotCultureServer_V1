package com.spotculture.tasks;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimerTask;

public class ParisImportTask extends TimerTask {

    @Override
    public void run() {
        try {
            URL url = new URL("http://localhost:8080/SpotCultureServer_V1/import-paris-events");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.getInputStream().close(); // déclenche la requête
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
