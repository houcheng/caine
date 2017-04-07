package com.caine.plugin;

import com.caine.ui.SearchController;

import static java.lang.Thread.sleep;

/**
 * Abstract thread based plugin implementation.
 */
public abstract class ThreadPluginImpl implements Runnable, Plugin {
    private static final long QUERY_INPUT_DELAY_IN_MS = 100;

    protected final SearchController searchController;

    private String queryString;
    private boolean queryUpdate;

    private Object synchronizeObject = new Object();

    public ThreadPluginImpl(SearchController searchController) {
        this.searchController = searchController;
    }

    @Override
    public synchronized void updateQuery(String queryString) {
        this.queryString = queryString;
        queryUpdate = true;
        synchronized (synchronizeObject) {
            synchronizeObject.notify();
        }
    }


    public void run() {
        while(true) {
            if (! queryUpdate) {
                waitQueryUpdate();
            }

            queryInputDelay();
            String query = getQueryString();
            performQuery(query);
        }
    }

    protected abstract void performQuery(String query);
    public abstract void cancelQuery();

    private void queryInputDelay() {
        try {
            sleep(QUERY_INPUT_DELAY_IN_MS);
        } catch (InterruptedException e) {
        }
    }

    private void waitQueryUpdate() {
        try {
            synchronized(synchronizeObject) {
                synchronizeObject.wait();
            }
        } catch (InterruptedException e) {
            System.out.println("EXCEPTION" + e.toString());
        }
    }

    private synchronized  String getQueryString() {
        queryUpdate = false;
        return queryString;
    }
}
