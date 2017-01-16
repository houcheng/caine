package com.caine.core;

import com.google.inject.Singleton;
import lombok.Setter;

/**
 * Handles query string updating to plugin and aggregate results from plugins.
 */
@Singleton
public class QueryClient {
    @Setter
    private QueryService plugin;

    public void updateQuery(String query) {
        plugin.updateQuery(query);
    }

    void cancelQuery() {
    }

}
