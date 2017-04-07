package com.caine.plugin;

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
    private final List<Plugin> pluginList = new LinkedList();
    private final SearchController searchController;

    @Inject
    public PluginManager(SearchController searchController) {

        this.searchController = searchController;

        tryToLoadAllPlugins();
    }

    public void updateQuery(String query) {
        for (Plugin plugin : pluginList) {
            plugin.updateQuery(query);
        }
    }

    private void tryToLoadAllPlugins() {
        try {
            loadAllPlugins();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    // TODO: load only specified plugins
    // TODO: Add a history entries search plugin for accelerating search
    private void loadAllPlugins() throws IllegalAccessException, InstantiationException {
        pluginList.add(loadRubyPlugin(FileSearchPlugin.class));
    }


    private RubyPluginImpl loadRubyPlugin(Class rubyPluginClass) throws IllegalAccessException, InstantiationException {

        RubyPluginImpl targetPlugin = new RubyPluginImpl(searchController, rubyPluginClass);
        new Thread(targetPlugin).start();

        return targetPlugin;
    }
}
