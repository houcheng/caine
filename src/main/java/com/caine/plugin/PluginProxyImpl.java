package com.caine.plugin;

import com.caine.core.QueryResultGenerator;
import com.caine.ui.SearchController;

import static java.lang.Thread.sleep;

/**
 * A thread based plugin proxy implementation.
 */
public class PluginProxyImpl implements Runnable, PluginProxy {
    private static final long QUERY_INPUT_DELAY_IN_MS = 100;

    protected final SearchController searchController;

    private String queryString;

    private boolean queryUpdateFlag;
    private boolean cancelQueryFlag;
    private boolean shutdownFlag;

    private Object synchronizeObject = new Object();

    private Plugin plugin;

    public PluginProxyImpl(SearchController searchController, Plugin plugin) {
        this.searchController = searchController;
        this.plugin = plugin;
    }

    @Override
    public synchronized void updateQuery(String queryString) {
        this.queryString = queryString;
        queryUpdateFlag = true;
        cancelQueryFlag = false;
        notifyWaitingThread();
    }

    @Override
    public synchronized void cancelQuery() {
        queryUpdateFlag = false;
        cancelQueryFlag = true;
    }

    public void shutdown() {
        shutdownFlag = true;
        notifyWaitingThread();
    }

    public void run() {
        while(! shutdownFlag) {
            if (!queryUpdateFlag) {
                waitQueryUpdate();
            }

            delayForInput();

            if(shutdownFlag) {
                break;
            }
            String query = getQueryString();
            queryPlugin(query);
        }
    }

    private void notifyWaitingThread() {
        synchronized (synchronizeObject) {
            synchronizeObject.notify();
        }
    }

    private void queryPlugin(String query) {
        int pageNumber = 0;
        do {
            queryPluginByPage(query, pageNumber);

            if (cancelQueryFlag) {
                cancelQueryFlag = false;
                break;
            }
        } while (plugin.hasMorePage(pageNumber ++));
    }

    private void queryPluginByPage(String query, int pageNumber) {
        if (queryString.length() == 0) {
            return;
        }

       Object[] resultEntries = plugin.queryByPage(queryString, pageNumber);
       QueryResultGenerator queryResults = new RubyQueryResultGenerator(resultEntries);
       searchController.appendSearchResult(queryString, queryResults);
    }

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
        queryUpdateFlag = false;
        return queryString;
    }
}
