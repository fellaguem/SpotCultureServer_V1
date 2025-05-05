package com.spotculture.tasks;


import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.Timer;

@WebListener
public class StartupListener implements ServletContextListener {
    private Timer timer;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        timer = new Timer(true);
        // Lance toutes les 12h (en millisecondes)
        timer.scheduleAtFixedRate(new ParisImportTask(), 0, 1000 * 60 * 60 * 12);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        timer.cancel();
    }
}
