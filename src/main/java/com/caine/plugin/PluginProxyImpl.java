package com.caine.plugin;

import com.caine.core.QueryResultGenerator;
import com.caine.ui.SearchController;
import org.jruby.RubyArray;

import static java.lang.Thread.sleep;

/**
 * A thread based plugin proxy implementation.
 */
public class PluginProxyImpl implements Runnable, PluginProxy {
    private static final long QUERY_INPUT_DELAY_IN_MS = 100;

    protected final SearchController searchController;

    private String queryString;
    private boolean queryUpdate;
    private boolean cancelQueryFlag;

    private Object synchronizeObject = new Object();

    private Plugin plugin;

    public PluginProxyImpl(SearchController searchController, Plugin plugin) {
        this.searchController = searchController;
        this.plugin = plugin;
    }

    @Override
    public synchronized void updateQuery(String queryString) {
        this.queryString = queryString;
        queryUpdate = true;
        cancelQueryFlag = false;
        synchronized (synchronizeObject) {
            synchronizeObject.notify();
        }
    }

    @Override
    public synchronized void cancelQuery() {
        queryUpdate = false;
        cancelQueryFlag = true;
    }

    public void run() {
        while(true) {
            if (! queryUpdate) {
                waitQueryUpdate();
            }

            delayForInput();

            String query = getQueryString();
            queryPlugin(query);
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
        queryUpdate = false;
        return queryString;
    }
}
