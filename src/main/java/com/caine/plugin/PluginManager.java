package com.caine.plugin;

import com.caine.config.AppConfiguration;
import com.caine.ui.SearchController;
import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.util.*;

/**
 * Manages plugins based on configuration file.
 */
@Singleton
public class PluginManager {

    private final Map<String, Class> classMap = new HashMap<>();
    private final Map<String, PluginProxy> pluginMap = new HashMap<>();
    private final SearchController searchController;

    AppConfiguration appConfiguration;

    @Inject
    public PluginManager(SearchController searchController, AppConfiguration appConfiguration) {

        this.searchController = searchController;
        this.appConfiguration = appConfiguration;

        tryToLoadPluginClasses();
        loadPluginsByConfiguration();
    }

    public void updateQuery(String query) {
        for (PluginProxy pluginProxy : pluginMap.values()) {
            pluginProxy.updateQuery(query);
        }
    }

    private void tryToLoadPluginClasses() {
        try {

            ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
            loadPluginClasses(cp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadPluginClasses(ClassPath cp) throws ClassNotFoundException {

        for(ClassPath.ClassInfo info : cp.getTopLevelClasses("com.caine.plugin.pluginstore")) {
            System.out.println("Load plugin: " + info.getSimpleName());
            classMap.put(info.getSimpleName(), Class.forName(info.getName()));
        }
    }

    private void loadPluginsByConfiguration() {
        for (String hotKeys : appConfiguration.getHotKeys()) {
            loadPluginsByHotKey(hotKeys);
        }
    }

    private void loadPluginsByHotKey(String hotKeys) {
        for (String instanceName : appConfiguration.getPluginListByHotKeys(hotKeys)) {
            String className = appConfiguration.getPluginType(instanceName);
            tryToLoadPlugin(instanceName, className);
        }
    }

    private void tryToLoadPlugin(String instanceName, String className) {

        Class pluginClass = classMap.get(className);
        try {
            loadPlugin(instanceName, pluginClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    // TODO: Add a history entries search plugin for accelerating search
    private void loadPlugin(String instanceName, Class pluginClass) throws IllegalAccessException, InstantiationException {

        Plugin plugin = (Plugin) pluginClass.newInstance();
        plugin.load(instanceName);

        PluginProxy proxyPlugin  = loadPluginAndProxy(plugin);
        pluginMap.put(instanceName, proxyPlugin);
    }

    private PluginProxy loadPluginAndProxy(Plugin plugin) {

        PluginProxy proxyPlugin = new PluginProxyImpl(searchController, plugin);
        new Thread(proxyPlugin).start();
        return proxyPlugin;
    }
}
