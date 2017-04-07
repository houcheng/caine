package com.caine.plugin;

import com.caine.core.QueryClient;
import com.caine.core.QueryService;
import com.caine.ui.FileSearchPlugin;
import com.caine.ui.SearchController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.LinkedList;
import java.util.List;

/**
 * Manages plugins based on configuration file.
 */
@Singleton
public class PluginManager {
    private final QueryClient queryClient;
    private final SearchController searchController;

    @Inject
    public PluginManager(SearchController searchController, QueryClient queryClient) {

        this.searchController = searchController;
        this.queryClient = queryClient;

        tryToLoadAllPlugins();
    }

    public void tryToLoadAllPlugins() {
        try {
            loadAllPlugins();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    // TODO: load only specified plugins
    private void loadAllPlugins() throws IllegalAccessException, InstantiationException {

        List<QueryService> queryServiceList = new LinkedList();
        queryServiceList.add(loadRubyPlugin(FileSearchPlugin.class));

        configureAllPlugins(queryServiceList);
    }

    private void configureAllPlugins(List<QueryService> queryServiceList) {

        for (QueryService queryService : queryServiceList) {
            queryClient.setPlugin(queryService);
        }
    }

    private RubyPlugin loadRubyPlugin(Class rubyPluginClass) throws IllegalAccessException, InstantiationException {

        RubyPlugin targetPlugin = new RubyPlugin(searchController, rubyPluginClass);
        new Thread(targetPlugin).start();

        return targetPlugin;
    }
}
