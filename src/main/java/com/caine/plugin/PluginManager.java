package com.caine.plugin;

import com.caine.core.QueryClient;
import com.caine.ui.FileSearchPlugin;
import com.caine.ui.SearchController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Manages plugins based on configuration file.
 */
@Singleton
public class PluginManager {
    private final QueryClient queryClient;
    private final SearchController seachController;

    @Inject
    public PluginManager(SearchController searchController, QueryClient queryClient) {
        this.seachController = searchController;
        this.queryClient = queryClient;
    }

    public void load() {
        try {
            RubyPlugin targetPlugin = new RubyPlugin(seachController, FileSearchPlugin.class);
            new Thread(targetPlugin).start();
            queryClient.setPlugin(targetPlugin);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
