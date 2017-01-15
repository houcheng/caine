package com.caine.core;

import com.caine.ui.SearchController;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Setter;

/**
 * Handles query string updating to plugin and aggregate results from plugins.
 */
@Singleton
public class QueryClient {
    private final SearchController searchController;

    @Setter
    private QueryServiceInterface plugin;

    @Inject
    public QueryClient(SearchController searchController) {
        this.searchController = searchController;
    }

    public void updateQuery(String query) {
        plugin.updateQuery(query);
    }

    void cancelQuery() {

    }

}
