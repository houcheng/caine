package com.caine.plugin;

import com.caine.pluginProxy.pluginstore.FileSearchPlugin;
import com.caine.ui.SearchController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages plugins based on configuration file.
 */
@Singleton
public class PluginManager {

    private final Map<String, PluginProxy> pluginMap = new HashMap<>();
    private final SearchController searchController;

    @Inject
    public PluginManager(SearchController searchController) {

        this.searchController = searchController;

        tryToLoadAllPlugins();
    }

    public PluginProxy getPluginByName() {
        return null;
    }

    public List<PluginProxy> getAllPlugins() {
        return null;
    }

    public void updateQuery(String query) {
        for (PluginProxy pluginProxy : pluginMap.values()) {
            pluginProxy.updateQuery(query);
        }
    }

    private void tryToLoadAllPlugins() {
        try {
            registerAllPlugins();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    // TODO: load only specified plugins
    // TODO: Add a history entries search plugin for accelerating search
    private void registerAllPlugins() throws IllegalAccessException, InstantiationException {

        registerPlugin(FileSearchPlugin.class);
    }

    private void registerPlugin(Class rubyPluginClass) throws IllegalAccessException, InstantiationException {

        Plugin plugin = (Plugin) rubyPluginClass.newInstance();
        PluginProxy proxyPlugin  = loadPluginAndProxy(plugin);
        pluginMap.put(plugin.getName(), proxyPlugin);
    }

    private PluginProxy loadPluginAndProxy(Plugin plugin) {

        PluginProxy proxyPlugin = new PluginProxyImpl(searchController, plugin);
        new Thread(proxyPlugin).start();
        return proxyPlugin;
    }
}
