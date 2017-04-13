package com.caine.ui;

import com.caine.config.AppConfiguration;
import com.caine.exception.WindowNotFoundException;

import javax.swing.*;

import static com.caine.ui.MainApplication.APPLICATION_WINDOW_NAME;

class HotKeyActivateWindowTask implements Runnable {

    private SearchController searchController;
    private final KeyStroke hotKey;

    HotKeyActivateWindowTask(SearchController searchController, KeyStroke hotKey) {
        this.searchController = searchController;
        this.hotKey = hotKey;
    }

    @Override
    public void run() {
        searchController.showWindowOnHotKey(hotKey);
        activateWindowInOwnThread();
    }

    private void activateWindowInOwnThread() {
        new Thread(createActivateWindowJob()).start();
    }

    private Runnable createActivateWindowJob() {

        return new Runnable() {
            private int MAX_RETRY_COUNT = 20;
            private int ACTIVATE_WINDOW_DELAY_TIME_IN_MS = 50;

            @Override
            public void run() {

                int retryCount = 0;
                while (retryCount < MAX_RETRY_COUNT) {
                    try {
                        activateApplicationWindow();
                        break;
                    } catch (WindowNotFoundException ex) {
                        retryCount++;
                        continue;
                    }
                }
            }

            private void activateApplicationWindow() {
                waitUiThread(ACTIVATE_WINDOW_DELAY_TIME_IN_MS);
                (new ActivateWindowJni()).callActivateWindow(APPLICATION_WINDOW_NAME);
            }
        };
    }

    private void waitUiThread(int milliSecond) {
        try {
            Thread.sleep(milliSecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
