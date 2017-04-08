package com.caine.plugin;

public interface PluginProxy extends Runnable {

    void updateQuery(String queryString);
    void cancelQuery();
}
