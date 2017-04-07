package com.caine.plugin;

import com.caine.ui.SearchController;

import static java.lang.Thread.sleep;

/**
 * Abstract thread based plugin implementation.
 */
public abstract class AbstractPluginImpl implements Runnable, Plugin {
    private static final long QUERY_INPUT_DELAY_IN_MS = 100;

    protected final SearchController searchController;

    private String queryString;
    private boolean queryUpdate;

    private Object synchronizeObject = new Object();

    public AbstractPluginImpl(SearchController searchController) {
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

            delayForInput();

            String query = getQueryString();
            queryPluginImpl(query);
        }
    }

    private void queryPluginImpl(String query) {
        int pageNumber = 0;
        do {
            pollQueryResult(query, pageNumber ++);
        } while (hasMoreQueryResult());
    }

    protected abstract void pollQueryResult(String query, int pageNumber);
    protected abstract boolean hasMoreQueryResult();

    public abstract void cancelQuery();

    // TODO: delay until no input for certain time
    private void delayForInput() {
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
